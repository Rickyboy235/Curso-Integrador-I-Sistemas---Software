const API_VEHICULOS = "/api/vehiculos";
const API_CONDUCTORES = "/api/conductores";

const modalOverlayVehiculo = document.getElementById("modalOverlayVehiculo");
const btnNuevoVehiculo = document.getElementById("btnNuevoVehiculo");
const btnCerrarModalVehiculo = document.getElementById("btnCerrarModalVehiculo");
const btnCancelarModalVehiculo = document.getElementById("btnCancelarModalVehiculo");
const formVehiculo = document.getElementById("formVehiculo");
const formErrorVehiculo = document.getElementById("formErrorVehiculo");
const modalTituloVehiculo = document.getElementById("modalTituloVehiculo");

const mensajeModalOverlayVehiculo = document.getElementById("mensajeModalOverlayVehiculo");
const mensajeModalTextoVehiculo = document.getElementById("mensajeModalTextoVehiculo");
const btnCerrarMensajeModalVehiculo = document.getElementById("btnCerrarMensajeModalVehiculo");
const btnAceptarMensajeModalVehiculo = document.getElementById("btnAceptarMensajeModalVehiculo");
const confirmarEliminarVehiculoOverlay = document.getElementById("confirmarEliminarVehiculoOverlay");
const btnCerrarConfirmarEliminarVehiculo = document.getElementById("btnCerrarConfirmarEliminarVehiculo");
const btnCancelarEliminarVehiculo = document.getElementById("btnCancelarEliminarVehiculo");
const btnConfirmarEliminarVehiculo = document.getElementById("btnConfirmarEliminarVehiculo");
let vehiculoAEliminar = null;

const mensajeModalOverlayConductor = document.getElementById("mensajeModalOverlayConductor");
const mensajeModalTextoConductor = document.getElementById("mensajeModalTextoConductor");
const btnCerrarMensajeModalConductor = document.getElementById("btnCerrarMensajeModalConductor");
const btnAceptarMensajeModalConductor = document.getElementById("btnAceptarMensajeModalConductor");
const confirmarEliminarConductorOverlay = document.getElementById("confirmarEliminarConductorOverlay");
const btnCerrarConfirmarEliminarConductor = document.getElementById("btnCerrarConfirmarEliminarConductor");
const btnCancelarEliminarConductor = document.getElementById("btnCancelarEliminarConductor");
const btnConfirmarEliminarConductor = document.getElementById("btnConfirmarEliminarConductor");
let conductorAEliminar = null;

const campoIdVehiculo = document.getElementById("campoIdVehiculo");
const campoPlacaTracto = document.getElementById("campoPlacaTracto");
const campoPlacaCarreta = document.getElementById("campoPlacaCarreta");
const campoSoat = document.getElementById("campoSoat");
const campoCitv = document.getElementById("campoCitv");

const vehiculosTbody = document.getElementById("vehiculos-tbody");
const vehiculosMensajeVacio = document.getElementById("vehiculos-mensaje-vacio");
const vehiculosCount = document.getElementById("vehiculos-count");
const kpiVehiculosTotal = document.getElementById("kpi-vehiculos-total");
const kpiVehiculosAptos = document.getElementById("kpi-vehiculos-aptos");

const modalOverlayConductor = document.getElementById("modalOverlayConductor");
const btnNuevoConductor = document.getElementById("btnNuevoConductor");
const btnCerrarModalConductor = document.getElementById("btnCerrarModalConductor");
const btnCancelarModalConductor = document.getElementById("btnCancelarModalConductor");
const formConductor = document.getElementById("formConductor");
const formErrorConductor = document.getElementById("formErrorConductor");
const modalTituloConductor = document.getElementById("modalTituloConductor");

const campoIdConductor = document.getElementById("campoIdConductor");
const campoNombresConductor = document.getElementById("campoNombresConductor");
const campoApellidosConductor = document.getElementById("campoApellidosConductor");
const campoNumDoc = document.getElementById("campoNumDoc");
const campoLicencia = document.getElementById("campoLicencia");
const campoCategoria = document.getElementById("campoCategoria");
const campoVencimientoLicencia = document.getElementById("campoVencimientoLicencia");

const conductoresTbody = document.getElementById("conductores-tbody");
const conductoresMensajeVacio = document.getElementById("conductores-mensaje-vacio");
const conductoresCount = document.getElementById("conductores-count");
const kpiConductoresTotal = document.getElementById("kpi-conductores-total");
const kpiLicenciasVigentes = document.getElementById("kpi-licencias-vigentes");


function estaVencida(fechaIso) {
    if (!fechaIso) return true;
    const hoy = new Date().toISOString().slice(0, 10);
    return fechaIso < hoy;
}


async function cargarVehiculos() {

    try {

        const respuesta = await fetch(API_VEHICULOS);

        if (!respuesta.ok) {
            throw new Error("No se pudo cargar la lista de vehículos");
        }

        const vehiculos = await respuesta.json();

        pintarVehiculos(vehiculos);
        pintarKpisVehiculos(vehiculos);

    } catch (error) {
        console.error(error);
        vehiculosTbody.innerHTML = '<tr><td colspan="6">Error al cargar los vehículos.</td></tr>';
    }

}


function pintarVehiculos(vehiculos) {

    vehiculosTbody.innerHTML = "";
    vehiculosMensajeVacio.style.display = vehiculos.length === 0 ? "block" : "none";
    vehiculosCount.textContent = vehiculos.length;

    vehiculos.forEach(function (v) {

        const apto = !estaVencida(v.soatVencimiento) && !estaVencida(v.citvVencimiento);
        const estadoTexto = apto ? "Apto" : "Documentos vencidos";
        const estadoClase = apto ? "stage-badge is-ganada" : "stage-badge";

        const fila = document.createElement("tr");

        fila.innerHTML =
            "<td>" + escaparHtml(v.placaTracto) + "</td>" +
            "<td>" + escaparHtml(v.placaCarreta || "-") + "</td>" +
            "<td>" + formatearFecha(v.soatVencimiento) + "</td>" +
            "<td>" + formatearFecha(v.citvVencimiento) + "</td>" +
            "<td><span class=\"" + estadoClase + "\">" + estadoTexto + "</span></td>" +
            "<td>" +
                '<button type="button" class="btn-secundario btn-editar-vehiculo" data-id="' + v.idVehiculo + '">Editar</button> ' +
                '<button type="button" class="btn-secundario btn-eliminar-vehiculo" data-id="' + v.idVehiculo + '">Eliminar</button>' +
            "</td>";

        vehiculosTbody.appendChild(fila);

    });

    document.querySelectorAll(".btn-editar-vehiculo").forEach(function (boton) {
        boton.addEventListener("click", function () {
            abrirModalEdicionVehiculo(boton.dataset.id, vehiculos);
        });
    });

    document.querySelectorAll(".btn-eliminar-vehiculo").forEach(function (boton) {
        boton.addEventListener("click", function () {
            eliminarVehiculo(boton.dataset.id);
        });
    });

}


function pintarKpisVehiculos(vehiculos) {

    kpiVehiculosTotal.textContent = vehiculos.length;

    const aptos = vehiculos.filter(function (v) {
        return !estaVencida(v.soatVencimiento) && !estaVencida(v.citvVencimiento);
    }).length;

    kpiVehiculosAptos.textContent = aptos;

}


function abrirModalNuevoVehiculo() {
    formVehiculo.reset();
    campoIdVehiculo.value = "";
    modalTituloVehiculo.textContent = "Nuevo vehículo";
    ocultarErrorVehiculo();
    modalOverlayVehiculo.classList.add("show");
}


function abrirModalEdicionVehiculo(id, vehiculos) {

    const vehiculo = vehiculos.find(function (v) { return String(v.idVehiculo) === String(id); });
    if (!vehiculo) return;

    campoIdVehiculo.value = vehiculo.idVehiculo;
    campoPlacaTracto.value = vehiculo.placaTracto;
    campoPlacaCarreta.value = vehiculo.placaCarreta || "";
    campoSoat.value = vehiculo.soatVencimiento;
    campoCitv.value = vehiculo.citvVencimiento;

    modalTituloVehiculo.textContent = "Editar vehículo";
    ocultarErrorVehiculo();
    modalOverlayVehiculo.classList.add("show");

}


function cerrarModalVehiculo() {
    modalOverlayVehiculo.classList.remove("show");
}


function mostrarErrorVehiculo(mensaje) {
    formErrorVehiculo.textContent = mensaje;
    formErrorVehiculo.style.display = "block";
}


function ocultarErrorVehiculo() {
    formErrorVehiculo.style.display = "none";
    formErrorVehiculo.textContent = "";
}

function mostrarModalMensajeConductor(mensaje) {
    mensajeModalTextoConductor.textContent = mensaje;
    mensajeModalOverlayConductor.classList.add("show");
}

function cerrarModalMensajeConductor() {
    mensajeModalOverlayConductor.classList.remove("show");
}

function mostrarConfirmarEliminarConductor(id) {
    conductorAEliminar = id;
    confirmarEliminarConductorOverlay.classList.add("show");
}

function cerrarConfirmarEliminarConductor() {
    conductorAEliminar = null;
    confirmarEliminarConductorOverlay.classList.remove("show");
}

btnNuevoVehiculo.addEventListener("click", abrirModalNuevoVehiculo);
btnCerrarModalVehiculo.addEventListener("click", cerrarModalVehiculo);
btnCancelarModalVehiculo.addEventListener("click", cerrarModalVehiculo);


formVehiculo.addEventListener("submit", async function (evento) {

    evento.preventDefault();
    ocultarErrorVehiculo();

    const dto = {
        placaTracto: campoPlacaTracto.value.trim().toUpperCase(),
        placaCarreta: campoPlacaCarreta.value.trim().toUpperCase() || null,
        soatVencimiento: campoSoat.value,
        citvVencimiento: campoCitv.value
    };

    const id = campoIdVehiculo.value;
    const esEdicion = Boolean(id);

    try {

        const respuesta = await fetch(
            esEdicion ? API_VEHICULOS + "/" + id : API_VEHICULOS,
            {
                method: esEdicion ? "PUT" : "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dto)
            }
        );

        if (!respuesta.ok) {
            const cuerpo = await respuesta.json().catch(function () { return null; });
            throw new Error((cuerpo && cuerpo.error) || "No se pudo guardar el vehículo");
        }

        cerrarModalVehiculo();
        await cargarVehiculos();

    } catch (error) {
        mostrarErrorVehiculo(error.message);
    }

});


function eliminarVehiculo(id) {
    mostrarConfirmarEliminarVehiculo(id);
}

async function ejecutarEliminacionVehiculo(id) {

    try {

        const respuesta = await fetch(
            API_VEHICULOS + "/" + id,
            {
                method: "DELETE"
            }
        );

        if (!respuesta.ok) {

            const cuerpo = await respuesta.json().catch(function () {
                return null;
            });

            throw new Error(
                (cuerpo && cuerpo.error) ||
                "No se pudo eliminar el vehículo"
            );
        }

        await cargarVehiculos();

    } catch (error) {

        mostrarModalMensajeVehiculo(error.message);
    }
}

async function cargarConductores() {

    try {

        const respuesta = await fetch(API_CONDUCTORES);

        if (!respuesta.ok) {
            throw new Error("No se pudo cargar la lista de conductores");
        }

        const conductores = await respuesta.json();

        pintarConductores(conductores);
        pintarKpisConductores(conductores);

    } catch (error) {
        console.error(error);
        conductoresTbody.innerHTML = '<tr><td colspan="7">Error al cargar los conductores.</td></tr>';
    }

}


function pintarConductores(conductores) {

    conductoresTbody.innerHTML = "";
    conductoresMensajeVacio.style.display = conductores.length === 0 ? "block" : "none";
    conductoresCount.textContent = conductores.length;

    conductores.forEach(function (c) {

        const vigente = !estaVencida(c.vencimientoLicencia);
        const estadoTexto = vigente ? "Vigente" : "Vencida";
        const estadoClase = vigente ? "stage-badge is-ganada" : "stage-badge";

        const fila = document.createElement("tr");

        fila.innerHTML =
            "<td>" + escaparHtml(c.nombres + " " + c.apellidos) + "</td>" +
            "<td>" + escaparHtml(c.numDoc) + "</td>" +
            "<td>" + escaparHtml(c.licencia) + "</td>" +
            "<td>" + escaparHtml(c.categoria) + "</td>" +
            "<td>" + formatearFecha(c.vencimientoLicencia) + "</td>" +
            "<td><span class=\"" + estadoClase + "\">" + estadoTexto + "</span></td>" +
            "<td>" +
                '<button type="button" class="btn-secundario btn-editar-conductor" data-id="' + c.idConductor + '">Editar</button> ' +
                '<button type="button" class="btn-secundario btn-eliminar-conductor" data-id="' + c.idConductor + '">Eliminar</button>' +
            "</td>";

        conductoresTbody.appendChild(fila);

    });

    document.querySelectorAll(".btn-editar-conductor").forEach(function (boton) {
        boton.addEventListener("click", function () {
            abrirModalEdicionConductor(boton.dataset.id, conductores);
        });
    });

    document.querySelectorAll(".btn-eliminar-conductor").forEach(function (boton) {
        boton.addEventListener("click", function () {
            eliminarConductor(boton.dataset.id);
        });
    });

}


function pintarKpisConductores(conductores) {

    kpiConductoresTotal.textContent = conductores.length;

    const vigentes = conductores.filter(function (c) {
        return !estaVencida(c.vencimientoLicencia);
    }).length;

    kpiLicenciasVigentes.textContent = vigentes;

}


function abrirModalNuevoConductor() {
    formConductor.reset();
    campoIdConductor.value = "";
    modalTituloConductor.textContent = "Nuevo conductor";
    ocultarErrorConductor();
    modalOverlayConductor.classList.add("show");
}


function abrirModalEdicionConductor(id, conductores) {

    const conductor = conductores.find(function (c) { return String(c.idConductor) === String(id); });
    if (!conductor) return;

    campoIdConductor.value = conductor.idConductor;
    campoNombresConductor.value = conductor.nombres;
    campoApellidosConductor.value = conductor.apellidos;
    campoNumDoc.value = conductor.numDoc;
    campoLicencia.value = conductor.licencia;
    campoCategoria.value = conductor.categoria;
    campoVencimientoLicencia.value = conductor.vencimientoLicencia;

    modalTituloConductor.textContent = "Editar conductor";
    ocultarErrorConductor();
    modalOverlayConductor.classList.add("show");

}


function cerrarModalConductor() {
    modalOverlayConductor.classList.remove("show");
}


function mostrarErrorConductor(mensaje) {
    formErrorConductor.textContent = mensaje;
    formErrorConductor.style.display = "block";
}


function ocultarErrorConductor() {
    formErrorConductor.style.display = "none";
    formErrorConductor.textContent = "";
}

function mostrarModalMensajeVehiculo(mensaje) {
    mensajeModalTextoVehiculo.textContent = mensaje;
    mensajeModalOverlayVehiculo.classList.add("show");
}

function cerrarModalMensajeVehiculo() {
    mensajeModalOverlayVehiculo.classList.remove("show");
}

function mostrarConfirmarEliminarVehiculo(id) {
    vehiculoAEliminar = id;
    confirmarEliminarVehiculoOverlay.classList.add("show");
}

function cerrarConfirmarEliminarVehiculo() {
    vehiculoAEliminar = null;
    confirmarEliminarVehiculoOverlay.classList.remove("show");
}

btnNuevoConductor.addEventListener("click", abrirModalNuevoConductor);
btnCerrarModalConductor.addEventListener("click", cerrarModalConductor);
btnCancelarModalConductor.addEventListener("click", cerrarModalConductor);
btnCerrarMensajeModalVehiculo.addEventListener("click", cerrarModalMensajeVehiculo);
btnAceptarMensajeModalVehiculo.addEventListener("click", cerrarModalMensajeVehiculo);
btnCerrarConfirmarEliminarVehiculo.addEventListener("click", cerrarConfirmarEliminarVehiculo);
btnCancelarEliminarVehiculo.addEventListener("click", cerrarConfirmarEliminarVehiculo);
btnConfirmarEliminarVehiculo.addEventListener("click", async function () {

    if (vehiculoAEliminar === null) {
        return;
    }

    const id = vehiculoAEliminar;

    cerrarConfirmarEliminarVehiculo();

    await ejecutarEliminacionVehiculo(id);
});

btnCerrarMensajeModalConductor.addEventListener("click", cerrarModalMensajeConductor);
btnAceptarMensajeModalConductor.addEventListener("click", cerrarModalMensajeConductor);
btnCerrarConfirmarEliminarConductor.addEventListener("click", cerrarConfirmarEliminarConductor);
btnCancelarEliminarConductor.addEventListener("click", cerrarConfirmarEliminarConductor);
btnConfirmarEliminarConductor.addEventListener(
    "click",
    async function () {

        if (conductorAEliminar === null) {
            return;
        }

        const id = conductorAEliminar;

        cerrarConfirmarEliminarConductor();

        await ejecutarEliminacionConductor(id);
    }
);

btnConfirmarEliminarConductor.addEventListener(
    "click",
    async function () {

        if (conductorAEliminar === null) {
            return;
        }

        const id = conductorAEliminar;

        cerrarConfirmarEliminarConductor();

        await ejecutarEliminacionConductor(id);
    }
);

formConductor.addEventListener("submit", async function (evento) {

    evento.preventDefault();
    ocultarErrorConductor();

    const dto = {
        nombres: campoNombresConductor.value.trim(),
        apellidos: campoApellidosConductor.value.trim(),
        numDoc: campoNumDoc.value.trim(),
        licencia: campoLicencia.value.trim().toUpperCase(),
        categoria: campoCategoria.value.trim().toUpperCase(),
        vencimientoLicencia: campoVencimientoLicencia.value
    };

    const id = campoIdConductor.value;
    const esEdicion = Boolean(id);

    try {

        const respuesta = await fetch(
            esEdicion ? API_CONDUCTORES + "/" + id : API_CONDUCTORES,
            {
                method: esEdicion ? "PUT" : "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dto)
            }
        );

        if (!respuesta.ok) {
            const cuerpo = await respuesta.json().catch(function () { return null; });
            throw new Error((cuerpo && cuerpo.error) || "No se pudo guardar el conductor");
        }

        cerrarModalConductor();
        await cargarConductores();

    } catch (error) {
        mostrarErrorConductor(error.message);
    }

});


function eliminarConductor(id) {
    mostrarConfirmarEliminarConductor(id);
}

async function ejecutarEliminacionConductor(id) {

    try {

        const respuesta = await fetch(
            API_CONDUCTORES + "/" + id,
            {
                method: "DELETE"
            }
        );

        if (!respuesta.ok) {

            const cuerpo = await respuesta.json().catch(function () {
                return null;
            });

            throw new Error(
                (cuerpo && cuerpo.error) ||
                "No se pudo eliminar el conductor"
            );
        }

        await cargarConductores();

    } catch (error) {

        mostrarModalMensajeConductor(error.message);
    }
}


const API_REPORTE_LOGISTICA = "/api/reportes/logistica/pdf";

const btnReporteLogistica =
    document.getElementById("btnReporteLogistica");

function abrirReporteLogistica() {
    window.open(API_REPORTE_LOGISTICA, "_blank");
}

if (btnReporteLogistica) {
    btnReporteLogistica.addEventListener(
        "click",
        abrirReporteLogistica
    );
}

cargarVehiculos();
cargarConductores();