const API_ORDENES = "/api/ordenes-servicio";
const API_COTIZACIONES = "/api/cotizaciones";
const API_VEHICULOS = "/api/vehiculos";
const API_CONDUCTORES = "/api/conductores";
const API_VEHICULOS_DISPONIBLES = "/api/vehiculos/disponibles";
const API_CONDUCTORES_DISPONIBLES = "/api/conductores/disponibles";
const API_REPORTE_OPERACIONES = "/api/reportes/operaciones/pdf";

const ESTADOS = ["pendiente", "programada", "ejecucion", "incidencia", "finalizada"];

const ETIQUETAS_ESTADO = {
    pendiente: "Pendiente",
    programada: "Programada",
    ejecucion: "En ejecución",
    incidencia: "Con incidencia",
    finalizada: "Finalizada"
};

const mensajeModalOverlay = document.getElementById("mensajeModalOverlay");
const mensajeModalTexto = document.getElementById("mensajeModalTexto");
const btnCerrarMensajeModal = document.getElementById("btnCerrarMensajeModal");
const btnAceptarMensajeModal = document.getElementById("btnAceptarMensajeModal");

const confirmarEliminarOverlay = document.getElementById("confirmarEliminarOverlay");
const btnCerrarConfirmarEliminar = document.getElementById("btnCerrarConfirmarEliminar");
const btnCancelarEliminar = document.getElementById("btnCancelarEliminar");
const btnConfirmarEliminar = document.getElementById("btnConfirmarEliminar");

let ordenAEliminar = null;

const modalOverlay = document.getElementById("modalOverlay");
const btnNuevaOrden = document.getElementById("btnNuevaOrden");
const btnReporteOperaciones = document.getElementById("btnReporteOperaciones");
const btnCerrarModal = document.getElementById("btnCerrarModal");
const btnCancelarModal = document.getElementById("btnCancelarModal");
const formOrden = document.getElementById("formOrden");
const formError = document.getElementById("formError");
const modalTitulo = document.getElementById("modalTitulo");

const campoId = document.getElementById("campoId");
const campoNumeroOrden = document.getElementById("campoNumeroOrden");
const campoCotizacion = document.getElementById("campoCotizacion");
const campoVehiculo = document.getElementById("campoVehiculo");
const campoConductor = document.getElementById("campoConductor");
const campoFecha = document.getElementById("campoFecha");
const campoEstado = document.getElementById("campoEstado");

const ordenesTbody = document.getElementById("ordenes-tbody");
const mensajeVacio = document.getElementById("mensaje-vacio");
const otCount = document.getElementById("ot-count");

const kpiTotal = document.getElementById("kpi-total");
const kpiEjecucion = document.getElementById("kpi-ejecucion");
const kpiFinalizada = document.getElementById("kpi-finalizada");
const kpiIncidencia = document.getElementById("kpi-incidencia");

let cotizacionesCache = [];
let vehiculosCache = [];
let conductoresCache = [];
let vehiculosDisponiblesCache = [];
let conductoresDisponiblesCache = [];

async function cargarSelects() {

    const [respCotizaciones, respVehiculos, respConductores, respVehiculosDisp, respConductoresDisp] = await Promise.all([
        fetch(API_COTIZACIONES),
        fetch(API_VEHICULOS),
        fetch(API_CONDUCTORES),
        fetch(API_VEHICULOS_DISPONIBLES),
        fetch(API_CONDUCTORES_DISPONIBLES)
    ]);

    cotizacionesCache = respCotizaciones.ok ? await respCotizaciones.json() : [];
    vehiculosCache = respVehiculos.ok ? await respVehiculos.json() : [];
    conductoresCache = respConductores.ok ? await respConductores.json() : [];
    vehiculosDisponiblesCache = respVehiculosDisp.ok ? await respVehiculosDisp.json() : [];
    conductoresDisponiblesCache = respConductoresDisp.ok ? await respConductoresDisp.json() : [];

    campoCotizacion.innerHTML = cotizacionesCache.map(function (c) {
        const etiqueta = (c.codigoCotizacion || ("COT-" + c.idCotizacion)) + " · " + c.clienteRazonSocial;
        return '<option value="' + c.idCotizacion + '">' + escaparHtml(etiqueta) + "</option>";
    }).join("");

    campoVehiculo.innerHTML = '<option value="">Sin asignar</option>' + vehiculosDisponiblesCache.map(function (v) {
        return '<option value="' + v.idVehiculo + '">' + escaparHtml(v.placaTracto) + "</option>";
    }).join("");

    campoConductor.innerHTML = '<option value="">Sin asignar</option>' + conductoresDisponiblesCache.map(function (c) {
        return '<option value="' + c.idConductor + '">' + escaparHtml(c.nombres + " " + c.apellidos) + "</option>";
    }).join("");

}

function buscarCotizacion(idCotizacion) {
    return cotizacionesCache.find(function (c) {
        return String(c.idCotizacion) === String(idCotizacion);
    });
}

function buscarVehiculo(idVehiculo) {
    return vehiculosCache.find(function (v) {
        return String(v.idVehiculo) === String(idVehiculo);
    });
}

function buscarConductor(idConductor) {
    return conductoresCache.find(function (c) {
        return String(c.idConductor) === String(idConductor);
    });
}

async function cargarOrdenes() {

    try {

        const respuesta = await fetch(API_ORDENES);

        if (!respuesta.ok) {
            throw new Error("No se pudo cargar la lista de ordenes de servicio");
        }

        const ordenes = await respuesta.json();

        pintarTabla(ordenes);
        pintarPipeline(ordenes);
        pintarKpis(ordenes);

    } catch (error) {
        console.error(error);
        ordenesTbody.innerHTML =
            '<tr><td colspan="8">Error al cargar las ordenes de servicio.</td></tr>';
    }

}


function pintarTabla(ordenes) {

    ordenesTbody.innerHTML = "";

    mensajeVacio.style.display = ordenes.length === 0 ? "block" : "none";
    otCount.textContent = ordenes.length;

    ordenes.forEach(function (orden) {

        const cotizacion = buscarCotizacion(orden.idCotizacion);
        const vehiculo = buscarVehiculo(orden.idVehiculo);
        const conductor = buscarConductor(orden.idConductor);

        const etiquetaCotizacion = cotizacion
            ? (cotizacion.codigoCotizacion || ("COT-" + cotizacion.idCotizacion))
            : ("COT-" + orden.idCotizacion);

        const cliente = cotizacion ? cotizacion.clienteRazonSocial : "-";
        const placaVehiculo = vehiculo ? vehiculo.placaTracto : "-";
        const nombreConductor = conductor ? (conductor.nombres + " " + conductor.apellidos) : "-";
        const etiquetaEstado = ETIQUETAS_ESTADO[orden.estadoServicio] || orden.estadoServicio;

        const fila = document.createElement("tr");

        fila.innerHTML =
            "<td>" + escaparHtml(orden.numeroOrden) + "</td>" +
            "<td>" + escaparHtml(etiquetaCotizacion) + "</td>" +
            "<td>" + escaparHtml(cliente) + "</td>" +
            "<td>" + escaparHtml(placaVehiculo) + "</td>" +
            "<td>" + escaparHtml(nombreConductor) + "</td>" +
            "<td>" + formatearFecha(orden.fechaProgramada) + "</td>" +
            "<td><span class=\"stage-badge\">" + escaparHtml(etiquetaEstado) + "</span></td>" +
            "<td>" +
                "<button type=\"button\" class=\"btn-secundario btn-editar\" data-id=\"" + orden.idOrden + "\">Editar</button> " +
                "<button type=\"button\" class=\"btn-secundario btn-eliminar\" data-id=\"" + orden.idOrden + "\">Eliminar</button>" +
            "</td>";

        ordenesTbody.appendChild(fila);

    });

    document.querySelectorAll(".btn-editar").forEach(function (boton) {
        boton.addEventListener("click", function () {
            abrirModalEdicion(boton.dataset.id, ordenes);
        });
    });

    document.querySelectorAll(".btn-eliminar").forEach(function (boton) {
        boton.addEventListener("click", function () {
            eliminarOrden(boton.dataset.id);
        });
    });

}


function pintarPipeline(ordenes) {

    ESTADOS.forEach(function (estado) {

        const contenedor = document.getElementById("pipeline-" + estado);
        const contador = document.getElementById("count-" + estado);

        const delEstado = ordenes.filter(function (o) {
            return o.estadoServicio === estado;
        });

        contador.textContent = delEstado.length;

        contenedor.innerHTML = delEstado.map(function (orden) {

            const cotizacion = buscarCotizacion(orden.idCotizacion);
            const cliente = cotizacion ? cotizacion.clienteRazonSocial : "Cliente no encontrado";

            return (
                '<div class="pipeline-card" data-id="' + orden.idOrden + '">' +
                    "<strong>" + escaparHtml(orden.numeroOrden) + "</strong>" +
                    "<span>" + escaparHtml(cliente) + "</span>" +
                    '<div class="pipeline-card-foot">' +
                        '<span class="pipeline-amount">' + formatearFecha(orden.fechaProgramada) + "</span>" +
                        '<div class="pipeline-owner">TF</div>' +
                    "</div>" +
                "</div>"
            );

        }).join("");

    });

    document.querySelectorAll(".pipeline-card").forEach(function (tarjeta) {
        tarjeta.addEventListener("click", function () {
            abrirModalEdicion(tarjeta.dataset.id, ordenes);
        });
    });

}


function pintarKpis(ordenes) {

    kpiTotal.textContent = ordenes.length;

    kpiEjecucion.textContent = ordenes.filter(function (o) {
        return o.estadoServicio === "ejecucion";
    }).length;

    kpiFinalizada.textContent = ordenes.filter(function (o) {
        return o.estadoServicio === "finalizada";
    }).length;

    kpiIncidencia.textContent = ordenes.filter(function (o) {
        return o.estadoServicio === "incidencia";
    }).length;

}

async function abrirModalNuevo() {

    formOrden.reset();
    campoId.value = "";
    modalTitulo.textContent = "Nueva orden de servicio";
    ocultarError();

    await cargarSelects();
    campoEstado.value = "pendiente";

    modalOverlay.classList.add("show");

}


async function abrirModalEdicion(id, ordenes) {

    const orden = ordenes.find(function (item) {
        return String(item.idOrden) === String(id);
    });

    if (!orden) {
        return;
    }

    await cargarSelects();

    campoId.value = orden.idOrden;
    campoNumeroOrden.value = orden.numeroOrden;
    campoCotizacion.value = orden.idCotizacion;
    campoVehiculo.value = orden.idVehiculo || "";
    campoConductor.value = orden.idConductor || "";
    campoFecha.value = orden.fechaProgramada;
    campoEstado.value = orden.estadoServicio;

    modalTitulo.textContent = "Editar orden de servicio";
    ocultarError();
    modalOverlay.classList.add("show");

}


function cerrarModal() {
    modalOverlay.classList.remove("show");
}


function mostrarError(mensaje) {
    formError.textContent = mensaje;
    formError.style.display = "block";
}


function ocultarError() {
    formError.style.display = "none";
    formError.textContent = "";
}

function mostrarModalMensaje(mensaje) {

    mensajeModalTexto.textContent = mensaje;

    mensajeModalOverlay.classList.add("show");
}


function cerrarModalMensaje() {

    mensajeModalOverlay.classList.remove("show");

}

function abrirReporteOperaciones() {
    window.open(API_REPORTE_OPERACIONES, "_blank");
}

btnNuevaOrden.addEventListener("click", abrirModalNuevo);
btnReporteOperaciones.addEventListener("click", abrirReporteOperaciones);
btnCerrarModal.addEventListener("click", cerrarModal);
btnCancelarModal.addEventListener("click", cerrarModal);
btnCerrarMensajeModal.addEventListener("click", cerrarModalMensaje);
btnAceptarMensajeModal.addEventListener("click", cerrarModalMensaje);
btnCerrarConfirmarEliminar.addEventListener("click", cerrarConfirmarEliminar);
btnCancelarEliminar.addEventListener("click", cerrarConfirmarEliminar);

btnConfirmarEliminar.addEventListener("click", async function () {

    if (ordenAEliminar === null) {
        return;
    }

    const id = ordenAEliminar;

    cerrarConfirmarEliminar();

    await ejecutarEliminacionOrden(id);

});

formOrden.addEventListener("submit", async function (evento) {

    evento.preventDefault();
    ocultarError();

    const dto = {
        numeroOrden: campoNumeroOrden.value.trim(),
        idCotizacion: Number(campoCotizacion.value),
        idVehiculo: campoVehiculo.value ? Number(campoVehiculo.value) : null,
        idConductor: campoConductor.value ? Number(campoConductor.value) : null,
        fechaProgramada: campoFecha.value,
        estadoServicio: campoEstado.value
    };

    const id = campoId.value;
    const esEdicion = Boolean(id);

    try {

        const respuesta = await fetch(
            esEdicion ? API_ORDENES + "/" + id : API_ORDENES,
            {
                method: esEdicion ? "PUT" : "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dto)
            }
        );

        if (!respuesta.ok) {
            const cuerpo = await respuesta.json().catch(function () { return null; });
            throw new Error((cuerpo && cuerpo.error) || "No se pudo guardar la orden de servicio");
        }

        cerrarModal();
        cargarOrdenes();

    } catch (error) {
        mostrarError(error.message);
    }

});

function eliminarOrden(id) {
    mostrarConfirmarEliminar(id);
}


function mostrarConfirmarEliminar(id) {

    ordenAEliminar = id;

    confirmarEliminarOverlay.classList.add("show");
}


function cerrarConfirmarEliminar() {

    ordenAEliminar = null;

    confirmarEliminarOverlay.classList.remove("show");
}


async function ejecutarEliminacionOrden(id) {

    try {

        const respuesta = await fetch(API_ORDENES + "/" + id, {
            method: "DELETE"
        });

        if (!respuesta.ok) {

            const cuerpo = await respuesta.json().catch(function () {
                return null;
            });

            throw new Error(
                (cuerpo && cuerpo.error) ||
                "No se puede eliminar la orden de servicio porque está asociada a otros registros."
            );
        }

        cargarOrdenes();

    } catch (error) {

        mostrarModalMensaje(error.message);

    }

}

cargarSelects().then(cargarOrdenes);