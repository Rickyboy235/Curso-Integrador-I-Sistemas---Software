const API_LIQUIDACIONES = "/api/liquidaciones-tributos";
const API_EXPEDIENTES = "/api/expedientes-dam";

const API_REPORTE_MARKETING = "/api/reportes/tributos/pdf";

const btnReporteMarketing =
    document.getElementById("btnReporteMarketing");

if (btnReporteMarketing) {
    btnReporteMarketing.addEventListener("click", function () {
        window.open(API_REPORTE_MARKETING, "_blank");
    });
}

const modalOverlayLiquidacion = document.getElementById("modalOverlayLiquidacion");
const btnNuevaLiquidacion = document.getElementById("btnNuevaLiquidacion");
const btnCerrarModalLiquidacion = document.getElementById("btnCerrarModalLiquidacion");
const btnCancelarModalLiquidacion = document.getElementById("btnCancelarModalLiquidacion");
const formLiquidacion = document.getElementById("formLiquidacion");
const formErrorLiquidacion = document.getElementById("formErrorLiquidacion");
const modalTituloLiquidacion = document.getElementById("modalTituloLiquidacion");

const campoIdLiquidacion = document.getElementById("campoIdLiquidacion");
const campoExpediente = document.getElementById("campoExpediente");
const campoAdValorem = document.getElementById("campoAdValorem");
const campoIgv = document.getElementById("campoIgv");
const campoIpm = document.getElementById("campoIpm");
const campoEstadoPago = document.getElementById("campoEstadoPago");

const liqTbody = document.getElementById("liq-tbody");
const liqMensajeVacio = document.getElementById("liq-mensaje-vacio");
const liqCount = document.getElementById("liq-count");
const kpiLiqTotal = document.getElementById("kpi-liq-total");
const kpiLiqPendientes = document.getElementById("kpi-liq-pendientes");
const kpiLiqMonto = document.getElementById("kpi-liq-monto");

let expedientesCache = [];

const confirmModalOverlay = document.getElementById("confirmModalOverlay");
const confirmModalTexto = document.getElementById("confirmModalTexto");
const btnCerrarConfirmModal = document.getElementById("btnCerrarConfirmModal");
const btnCancelarConfirmModal = document.getElementById("btnCancelarConfirmModal");
const btnAceptarConfirmModal = document.getElementById("btnAceptarConfirmModal");

let resolverConfirmacion = null;

function mostrarModalConfirmacion(mensaje) {
    confirmModalTexto.textContent = mensaje;
    confirmModalOverlay.classList.add("show");

    return new Promise(function (resolve) {
        resolverConfirmacion = resolve;
    });
}

function cerrarModalConfirmacion(resultado) {
    confirmModalOverlay.classList.remove("show");

    if (resolverConfirmacion) {
        resolverConfirmacion(resultado);
        resolverConfirmacion = null;
    }
}

btnCerrarConfirmModal.addEventListener("click", function () {
    cerrarModalConfirmacion(false);
});

btnCancelarConfirmModal.addEventListener("click", function () {
    cerrarModalConfirmacion(false);
});

btnAceptarConfirmModal.addEventListener("click", function () {
    cerrarModalConfirmacion(true);
});

async function cargarExpedientes() {

    const respuesta = await fetch(API_EXPEDIENTES);
    expedientesCache = respuesta.ok ? await respuesta.json() : [];

    campoExpediente.innerHTML = expedientesCache.map(function (e) {
        const etiqueta = e.numeroDam ? e.numeroDam : "Expediente #" + e.idExpediente;
        return '<option value="' + e.idExpediente + '">' + escaparHtml(etiqueta) + "</option>";
    }).join("");

}

function nombreExpediente(idExpediente) {
    const exp = expedientesCache.find(function (e) { return e.idExpediente === idExpediente; });
    if (!exp) return "Expediente #" + idExpediente;
    return exp.numeroDam ? exp.numeroDam : "Expediente #" + exp.idExpediente;
}

async function cargarLiquidaciones() {

    try {

        const respuesta = await fetch(API_LIQUIDACIONES);

        if (!respuesta.ok) {
            throw new Error("No se pudo cargar la lista de liquidaciones");
        }

        const liquidaciones = await respuesta.json();

        pintarTabla(liquidaciones);

        pintarKpis(liquidaciones);

    } catch (error) {
        console.error(error);
        liqTbody.innerHTML = '<tr><td colspan="7">Error al cargar las liquidaciones.</td></tr>';
    }

}


function pintarTabla(liquidaciones) {

    liqTbody.innerHTML = "";
    liqMensajeVacio.style.display = liquidaciones.length === 0 ? "block" : "none";
    liqCount.textContent = liquidaciones.length;

    liquidaciones.forEach(function (l) {

        const estadoClase = l.estadoPago === "PAGADO" ? "stage-badge is-ganada" : "stage-badge";

        const fila = document.createElement("tr");

        fila.innerHTML =
            "<td>" + escaparHtml(nombreExpediente(l.idExpedienteDam)) + "</td>" +
            "<td>" + formatearMonto(l.adValorem) + "</td>" +
            "<td>" + formatearMonto(l.igv) + "</td>" +
            "<td>" + formatearMonto(l.ipm) + "</td>" +
            "<td>" + formatearMonto(l.totalTributos) + "</td>" +
            "<td><span class=\"" + estadoClase + "\">" + escaparHtml(l.estadoPago) + "</span></td>" +
            "<td>" +
                '<button type="button" class="btn-secundario btn-editar-liq" data-id="' + l.idLiquidacion + '">Editar</button> ' +
                '<button type="button" class="btn-secundario btn-eliminar-liq" data-id="' + l.idLiquidacion + '">Eliminar</button>' +
            "</td>";

        liqTbody.appendChild(fila);

    });

    document.querySelectorAll(".btn-editar-liq").forEach(function (boton) {
        boton.addEventListener("click", function () {
            abrirModalEdicion(boton.dataset.id, liquidaciones);
        });
    });

    document.querySelectorAll(".btn-eliminar-liq").forEach(function (boton) {
        boton.addEventListener("click", function () {
            eliminarLiquidacion(boton.dataset.id);
        });
    });

}


function pintarKpis(liquidaciones) {

    kpiLiqTotal.textContent = liquidaciones.length;

    const pendientes = liquidaciones.filter(function (l) {
        return l.estadoPago !== "PAGADO";
    }).length;
    kpiLiqPendientes.textContent = pendientes;

    const montoTotal = liquidaciones.reduce(function (acumulado, l) {
        return acumulado + Number(l.totalTributos || 0);
    }, 0);
    kpiLiqMonto.textContent = formatearMonto(montoTotal).replace("US$", "S/");

}


async function abrirModalNuevo() {

    formLiquidacion.reset();
    campoIdLiquidacion.value = "";
    modalTituloLiquidacion.textContent = "Nueva liquidación";
    ocultarError();

    if (expedientesCache.length === 0) {
        await cargarExpedientes();
    }

    modalOverlayLiquidacion.classList.add("show");

}


function abrirModalEdicion(id, liquidaciones) {

    const liq = liquidaciones.find(function (l) { return String(l.idLiquidacion) === String(id); });
    if (!liq) return;

    campoIdLiquidacion.value = liq.idLiquidacion;
    campoExpediente.value = liq.idExpedienteDam;
    campoAdValorem.value = liq.adValorem;
    campoIgv.value = liq.igv;
    campoIpm.value = liq.ipm;
    campoEstadoPago.value = liq.estadoPago;

    modalTituloLiquidacion.textContent = "Editar liquidación";
    ocultarError();
    modalOverlayLiquidacion.classList.add("show");

}


function cerrarModal() {
    modalOverlayLiquidacion.classList.remove("show");
}


function mostrarError(mensaje) {
    formErrorLiquidacion.textContent = mensaje;
    formErrorLiquidacion.style.display = "block";
}


function ocultarError() {
    formErrorLiquidacion.style.display = "none";
    formErrorLiquidacion.textContent = "";
}


btnNuevaLiquidacion.addEventListener("click", abrirModalNuevo);
btnCerrarModalLiquidacion.addEventListener("click", cerrarModal);
btnCancelarModalLiquidacion.addEventListener("click", cerrarModal);


formLiquidacion.addEventListener("submit", async function (evento) {

    evento.preventDefault();
    ocultarError();

    const dto = {
        idExpedienteDam: Number(campoExpediente.value),
        adValorem: Number(campoAdValorem.value),
        igv: Number(campoIgv.value),
        ipm: Number(campoIpm.value),
        estadoPago: campoEstadoPago.value
    };

    const id = campoIdLiquidacion.value;
    const esEdicion = Boolean(id);

    try {

        const respuesta = await fetch(
            esEdicion ? API_LIQUIDACIONES + "/" + id : API_LIQUIDACIONES,
            {
                method: esEdicion ? "PUT" : "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dto)
            }
        );

        if (!respuesta.ok) {
            const cuerpo = await respuesta.json().catch(function () { return null; });
            throw new Error((cuerpo && cuerpo.error) || "No se pudo guardar la liquidación");
        }

        cerrarModal();
        await cargarLiquidaciones();

    } catch (error) {
        mostrarError(error.message);
    }

});


async function eliminarLiquidacion(id) {

    const confirmado = await mostrarModalConfirmacion("¿Eliminar esta liquidación?");

    if (!confirmado) {
        return;
    }

    try {

        const respuesta = await fetch(API_LIQUIDACIONES + "/" + id, { method: "DELETE" });

        if (!respuesta.ok) {
            const cuerpo = await respuesta.json().catch(function () { return null; });
            throw new Error((cuerpo && cuerpo.error) || "No se pudo eliminar la liquidación");
        }

        await cargarLiquidaciones();

    } catch (error) {
        alert(error.message);
    }

}

if (btnReporteMarketing) {
    btnReporteMarketing.addEventListener("click", function () {
        window.open(API_REPORTE_MARKETING, "_blank");
    });
}

(async function iniciar() {
    await cargarExpedientes();
    await cargarLiquidaciones();
})();