const API_VALES = "/api/vales-salida";
const API_ORDENES = "/api/ordenes-servicio";
const API_INSUMOS = "/api/insumos";
const API_REPORTE_PRODUCCION = "/api/reportes/produccion/pdf";

const modalOverlayVale = document.getElementById("modalOverlayVale");
const btnNuevoVale = document.getElementById("btnNuevoVale");
const btnReporteProduccion = document.getElementById("btnReporteProduccion");
const btnCerrarModalVale = document.getElementById("btnCerrarModalVale");
const btnCancelarModalVale = document.getElementById("btnCancelarModalVale");
const formVale = document.getElementById("formVale");
const formErrorVale = document.getElementById("formErrorVale");
const modalTituloVale = document.getElementById("modalTituloVale");

const campoIdVale = document.getElementById("campoIdVale");
const campoOrdenServicio = document.getElementById("campoOrdenServicio");
const campoNumeroVale = document.getElementById("campoNumeroVale");
const campoFechaEmision = document.getElementById("campoFechaEmision");

const valesTbody = document.getElementById("vales-tbody");
const valesMensajeVacio = document.getElementById("vales-mensaje-vacio");
const valesCount = document.getElementById("vales-count");
const kpiValesTotal = document.getElementById("kpi-vales-total");
const kpiValesHoy = document.getElementById("kpi-vales-hoy");
const kpiStockBajo = document.getElementById("kpi-stock-bajo");

const modalOverlayDetalles = document.getElementById("modalOverlayDetalles");
const btnCerrarModalDetalles = document.getElementById("btnCerrarModalDetalles");
const btnCerrarModalDetalles2 = document.getElementById("btnCerrarModalDetalles2");
const modalTituloDetalles = document.getElementById("modalTituloDetalles");
const formDetalle = document.getElementById("formDetalle");
const formErrorDetalle = document.getElementById("formErrorDetalle");
const campoInsumo = document.getElementById("campoInsumo");
const campoCantidadDetalle = document.getElementById("campoCantidadDetalle");
const detallesTbody = document.getElementById("detalles-tbody");
const detallesMensajeVacio = document.getElementById("detalles-mensaje-vacio");

let ordenesCache = [];
let insumosCache = [];
let idValeActivo = null;

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

async function cargarSelects() {

    const [respOrdenes, respInsumos] = await Promise.all([
        fetch(API_ORDENES),
        fetch(API_INSUMOS)
    ]);

    ordenesCache = respOrdenes.ok ? await respOrdenes.json() : [];
    insumosCache = respInsumos.ok ? await respInsumos.json() : [];

    campoOrdenServicio.innerHTML = ordenesCache.map(function (o) {
        return '<option value="' + o.idOrden + '">' + escaparHtml(o.numeroOrden) + "</option>";
    }).join("");

    campoInsumo.innerHTML = insumosCache.map(function (i) {
        return '<option value="' + i.idInsumo + '">' + escaparHtml(i.codigo + " — " + i.descripcion) +
            " (stock: " + i.stockActual + ")</option>";
    }).join("");

}


function nombreOrden(idOrden) {
    const orden = ordenesCache.find(function (o) { return o.idOrden === idOrden; });
    return orden ? orden.numeroOrden : "Orden #" + idOrden;
}


function nombreInsumo(idInsumo) {
    const insumo = insumosCache.find(function (i) { return i.idInsumo === idInsumo; });
    return insumo ? insumo.codigo + " — " + insumo.descripcion : "Insumo #" + idInsumo;
}

function abrirReporteProduccion() {
    window.open(API_REPORTE_PRODUCCION, "_blank");
}

async function cargarVales() {

    try {

        const respuesta = await fetch(API_VALES);

        if (!respuesta.ok) {
            throw new Error("No se pudo cargar la lista de vales");
        }

        const vales = await respuesta.json();

        pintarVales(vales);
        pintarKpis(vales);

    } catch (error) {
        console.error(error);
        valesTbody.innerHTML = '<tr><td colspan="4">Error al cargar los vales.</td></tr>';
    }

}


function pintarVales(vales) {

    valesTbody.innerHTML = "";
    valesMensajeVacio.style.display = vales.length === 0 ? "block" : "none";
    valesCount.textContent = vales.length;

    vales.forEach(function (v) {

        const fila = document.createElement("tr");

        fila.innerHTML =
            "<td>" + escaparHtml(v.numeroVale) + "</td>" +
            "<td>" + escaparHtml(nombreOrden(v.idOrdenServicio)) + "</td>" +
            "<td>" + formatearFecha(v.fechaEmision) + "</td>" +
            "<td>" +
                '<button type="button" class="btn-secundario btn-detalles-vale" data-id="' + v.idVale + '" data-numero="' + escaparHtml(v.numeroVale) + '">Ver detalles</button> ' +
                '<button type="button" class="btn-secundario btn-editar-vale" data-id="' + v.idVale + '">Editar</button> ' +
                '<button type="button" class="btn-secundario btn-eliminar-vale" data-id="' + v.idVale + '">Eliminar</button>' +
            "</td>";

        valesTbody.appendChild(fila);

    });

    document.querySelectorAll(".btn-detalles-vale").forEach(function (boton) {
        boton.addEventListener("click", function () {
            abrirModalDetalles(boton.dataset.id, boton.dataset.numero);
        });
    });

    document.querySelectorAll(".btn-editar-vale").forEach(function (boton) {
        boton.addEventListener("click", function () {
            abrirModalEdicionVale(boton.dataset.id, vales);
        });
    });

    document.querySelectorAll(".btn-eliminar-vale").forEach(function (boton) {
        boton.addEventListener("click", function () {
            eliminarVale(boton.dataset.id);
        });
    });

}


function pintarKpis(vales) {

    kpiValesTotal.textContent = vales.length;

    const hoy = new Date().toISOString().slice(0, 10);
    const valesHoy = vales.filter(function (v) { return v.fechaEmision === hoy; }).length;
    kpiValesHoy.textContent = valesHoy;

    const stockBajo = insumosCache.filter(function (i) {
        return Number(i.stockActual) <= Number(i.stockMinimo);
    }).length;
    kpiStockBajo.textContent = stockBajo;

}


async function abrirModalNuevoVale() {

    formVale.reset();
    campoIdVale.value = "";
    modalTituloVale.textContent = "Nuevo vale de salida";
    ocultarErrorVale();

    if (ordenesCache.length === 0) {
        await cargarSelects();
    }

    modalOverlayVale.classList.add("show");

}


function abrirModalEdicionVale(id, vales) {

    const vale = vales.find(function (v) { return String(v.idVale) === String(id); });
    if (!vale) return;

    campoIdVale.value = vale.idVale;
    campoOrdenServicio.value = vale.idOrdenServicio;
    campoNumeroVale.value = vale.numeroVale;
    campoFechaEmision.value = vale.fechaEmision || "";

    modalTituloVale.textContent = "Editar vale de salida";
    ocultarErrorVale();
    modalOverlayVale.classList.add("show");

}


function cerrarModalVale() {
    modalOverlayVale.classList.remove("show");
}


function mostrarErrorVale(mensaje) {
    formErrorVale.textContent = mensaje;
    formErrorVale.style.display = "block";
}


function ocultarErrorVale() {
    formErrorVale.style.display = "none";
    formErrorVale.textContent = "";
}


btnNuevoVale.addEventListener("click", abrirModalNuevoVale);
btnReporteProduccion.addEventListener("click", abrirReporteProduccion);

btnCerrarModalVale.addEventListener("click", cerrarModalVale);
btnCancelarModalVale.addEventListener("click", cerrarModalVale);


formVale.addEventListener("submit", async function (evento) {

    evento.preventDefault();
    ocultarErrorVale();

    const dto = {
        idOrdenServicio: Number(campoOrdenServicio.value),
        numeroVale: campoNumeroVale.value.trim(),
        fechaEmision: campoFechaEmision.value || null
    };

    const id = campoIdVale.value;
    const esEdicion = Boolean(id);

    try {

        const respuesta = await fetch(
            esEdicion ? API_VALES + "/" + id : API_VALES,
            {
                method: esEdicion ? "PUT" : "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dto)
            }
        );

        if (!respuesta.ok) {
            const cuerpo = await respuesta.json().catch(function () { return null; });
            throw new Error((cuerpo && cuerpo.error) || "No se pudo guardar el vale");
        }

        cerrarModalVale();
        await cargarVales();

    } catch (error) {
        mostrarErrorVale(error.message);
    }

});


async function eliminarVale(id) {

    const confirmado = await mostrarModalConfirmacion("¿Eliminar este vale de salida? Esto no devuelve el stock de sus insumos automáticamente si ya tiene detalles.");

    if (!confirmado) {
        return;
    }

    try {

        const respuesta = await fetch(API_VALES + "/" + id, { method: "DELETE" });

        if (!respuesta.ok) {
            const cuerpo = await respuesta.json().catch(function () { return null; });
            throw new Error((cuerpo && cuerpo.error) || "No se pudo eliminar el vale");
        }

        await cargarVales();

    } catch (error) {
        alert(error.message);
    }

}

// DETALLES DE UN VALE //

async function abrirModalDetalles(idVale, numeroVale) {

    idValeActivo = idVale;
    modalTituloDetalles.textContent = "Detalle del vale " + numeroVale;
    formDetalle.reset();
    ocultarErrorDetalle();

    if (insumosCache.length === 0) {
        await cargarSelects();
    }

    await cargarDetalles();

    modalOverlayDetalles.classList.add("show");

}


async function cargarDetalles() {

    try {

        const respuesta = await fetch(API_VALES + "/" + idValeActivo + "/detalles");

        if (!respuesta.ok) {
            throw new Error("No se pudo cargar el detalle del vale");
        }

        const detalles = await respuesta.json();
        pintarDetalles(detalles);

    } catch (error) {
        console.error(error);
        detallesTbody.innerHTML = '<tr><td colspan="3">Error al cargar el detalle.</td></tr>';
    }

}


function pintarDetalles(detalles) {

    detallesTbody.innerHTML = "";
    detallesMensajeVacio.style.display = detalles.length === 0 ? "block" : "none";

    detalles.forEach(function (d) {

        const fila = document.createElement("tr");

        fila.innerHTML =
            "<td>" + escaparHtml(nombreInsumo(d.idInsumo)) + "</td>" +
            "<td>" + Number(d.cantidad) + "</td>" +
            "<td>" +
                '<button type="button" class="btn-secundario btn-eliminar-detalle" data-insumo="' + d.idInsumo + '">Quitar</button>' +
            "</td>";

        detallesTbody.appendChild(fila);

    });

    document.querySelectorAll(".btn-eliminar-detalle").forEach(function (boton) {
        boton.addEventListener("click", function () {
            eliminarDetalle(boton.dataset.insumo);
        });
    });

}


function cerrarModalDetalles() {
    modalOverlayDetalles.classList.remove("show");
    idValeActivo = null;
    cargarVales();
}


function mostrarErrorDetalle(mensaje) {
    formErrorDetalle.textContent = mensaje;
    formErrorDetalle.style.display = "block";
}


function ocultarErrorDetalle() {
    formErrorDetalle.style.display = "none";
    formErrorDetalle.textContent = "";
}


btnCerrarModalDetalles.addEventListener("click", cerrarModalDetalles);
btnCerrarModalDetalles2.addEventListener("click", cerrarModalDetalles);


formDetalle.addEventListener("submit", async function (evento) {

    evento.preventDefault();
    ocultarErrorDetalle();

    const dto = {
        idInsumo: Number(campoInsumo.value),
        cantidad: Number(campoCantidadDetalle.value)
    };

    try {

        const respuesta = await fetch(API_VALES + "/" + idValeActivo + "/detalles", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(dto)
        });

        if (!respuesta.ok) {
            const cuerpo = await respuesta.json().catch(function () { return null; });
            throw new Error((cuerpo && cuerpo.error) || "No se pudo agregar el insumo (si ya está en el vale, quítalo primero para cambiar la cantidad)");
        }

        formDetalle.reset();
        await cargarDetalles();
        await cargarSelects();

    } catch (error) {
        mostrarErrorDetalle(error.message);
    }

});


async function eliminarDetalle(idInsumo) {

    const confirmado = await mostrarModalConfirmacion("¿Quitar este insumo del vale? Esto devuelve la cantidad al stock.");

    if (!confirmado) {
        return;
    }

    try {

        const respuesta = await fetch(API_VALES + "/" + idValeActivo + "/detalles/" + idInsumo, {
            method: "DELETE"
        });

        if (!respuesta.ok) {
            const cuerpo = await respuesta.json().catch(function () { return null; });
            throw new Error((cuerpo && cuerpo.error) || "No se pudo quitar el insumo");
        }

        await cargarDetalles();
        await cargarSelects();

    } catch (error) {
        alert(error.message);
    }

}


(async function iniciar() {
    await cargarSelects();
    await cargarVales();
})();