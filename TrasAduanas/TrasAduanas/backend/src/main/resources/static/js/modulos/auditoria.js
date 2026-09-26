const API_EXPEDIENTES = "/api/expedientes-dam";
const API_INCIDENCIAS = "/api/incidencias";
const API_ORDENES = "/api/ordenes-servicio";
const API_COTIZACIONES = "/api/cotizaciones";
const API_REPORTE_EXPEDIENTES = "/api/reportes/auditoria/expedientes/pdf";
const API_REPORTE_INCIDENCIAS = "/api/reportes/auditoria/incidencias/pdf";

const CANALES = ["pendiente", "verde", "naranja", "rojo"];

const ETIQUETAS_TRAMITE = {
    en_proceso: "En proceso",
    levante_autorizado: "Levante autorizado",
    retenida: "Carga retenida"
};

// --- referencias EXPEDIENTE --- //
const modalOverlayExpediente = document.getElementById("modalOverlayExpediente");
const btnNuevoExpediente = document.getElementById("btnNuevoExpediente");
const btnReporteExpedientes = document.getElementById("btnReporteExpedientes");
const btnCerrarModalExpediente = document.getElementById("btnCerrarModalExpediente");
const btnCancelarModalExpediente = document.getElementById("btnCancelarModalExpediente");
const formExpediente = document.getElementById("formExpediente");
const formErrorExpediente = document.getElementById("formErrorExpediente");
const modalTituloExpediente = document.getElementById("modalTituloExpediente");

const campoIdExpediente = document.getElementById("campoIdExpediente");
const campoOrdenExpediente = document.getElementById("campoOrdenExpediente");
const campoNumeroDam = document.getElementById("campoNumeroDam");
const campoRegimen = document.getElementById("campoRegimen");
const campoCanal = document.getElementById("campoCanal");
const campoEstadoTramite = document.getElementById("campoEstadoTramite");

const expedientesTbody = document.getElementById("expedientes-tbody");
const mensajeVacioExpedientes = document.getElementById("mensaje-vacio-expedientes");
const expedientesCount = document.getElementById("expedientes-count");

const kpiTotal = document.getElementById("kpi-total");
const kpiVerde = document.getElementById("kpi-verde");
const kpiNaranja = document.getElementById("kpi-naranja");
const kpiRojo = document.getElementById("kpi-rojo");

// --- referencias INCIDENCIA --- //
const modalOverlayIncidencia = document.getElementById("modalOverlayIncidencia");
const btnNuevaIncidencia = document.getElementById("btnNuevaIncidencia");
const btnReporteIncidencias = document.getElementById("btnReporteIncidencias");
const btnCerrarModalIncidencia = document.getElementById("btnCerrarModalIncidencia");
const btnCancelarModalIncidencia = document.getElementById("btnCancelarModalIncidencia");
const formIncidencia = document.getElementById("formIncidencia");
const formErrorIncidencia = document.getElementById("formErrorIncidencia");

const campoIdIncidencia = document.getElementById("campoIdIncidencia");
const campoOrdenIncidencia = document.getElementById("campoOrdenIncidencia");
const campoTipoIncidencia = document.getElementById("campoTipoIncidencia");
const campoDescripcionIncidencia = document.getElementById("campoDescripcionIncidencia");

const incidenciasTbody = document.getElementById("incidencias-tbody");
const mensajeVacioIncidencias = document.getElementById("mensaje-vacio-incidencias");

// --- referencias MODAL DE MENSAJE / CONFIRMACIÓN (compartidos) --- //
const mensajeModalOverlay = document.getElementById("mensajeModalOverlay");
const mensajeModalTexto = document.getElementById("mensajeModalTexto");
const btnCerrarMensajeModal = document.getElementById("btnCerrarMensajeModal");
const btnAceptarMensajeModal = document.getElementById("btnAceptarMensajeModal");

const confirmarEliminarOverlay = document.getElementById("confirmarEliminarOverlay");
const confirmarEliminarTexto = document.getElementById("confirmarEliminarTexto");
const btnCerrarConfirmarEliminar = document.getElementById("btnCerrarConfirmarEliminar");
const btnCancelarEliminar = document.getElementById("btnCancelarEliminar");
const btnConfirmarEliminar = document.getElementById("btnConfirmarEliminar");

let elementoAEliminar = { tipo: null, id: null };

let ordenesCache = [];
let cotizacionesCache = [];


function mostrarModalMensaje(mensaje) {
    mensajeModalTexto.textContent = mensaje;
    mensajeModalOverlay.classList.add("show");
}


function cerrarModalMensaje() {
    mensajeModalOverlay.classList.remove("show");
}


function mostrarConfirmarEliminar(tipo, id, texto) {
    elementoAEliminar = { tipo: tipo, id: id };
    confirmarEliminarTexto.textContent = texto;
    confirmarEliminarOverlay.classList.add("show");
}


function cerrarConfirmarEliminar() {
    elementoAEliminar = { tipo: null, id: null };
    confirmarEliminarOverlay.classList.remove("show");
}


btnCerrarMensajeModal.addEventListener("click", cerrarModalMensaje);
btnAceptarMensajeModal.addEventListener("click", cerrarModalMensaje);
btnCerrarConfirmarEliminar.addEventListener("click", cerrarConfirmarEliminar);
btnCancelarEliminar.addEventListener("click", cerrarConfirmarEliminar);

btnConfirmarEliminar.addEventListener("click", async function () {

    if (elementoAEliminar.id === null) {
        return;
    }

    const tipo = elementoAEliminar.tipo;
    const id = elementoAEliminar.id;

    cerrarConfirmarEliminar();

    if (tipo === "expediente") {
        await ejecutarEliminacionExpediente(id);
    } else if (tipo === "incidencia") {
        await ejecutarEliminacionIncidencia(id);
    }

});

async function cargarOrdenesYCotizaciones() {

    const [respOrdenes, respCotizaciones] = await Promise.all([
        fetch(API_ORDENES),
        fetch(API_COTIZACIONES)
    ]);

    ordenesCache = respOrdenes.ok ? await respOrdenes.json() : [];
    cotizacionesCache = respCotizaciones.ok ? await respCotizaciones.json() : [];

    const opciones = ordenesCache.map(function (orden) {
        const cliente = obtenerClienteDeOrden(orden);
        const etiqueta = orden.numeroOrden + " · " + cliente;
        return '<option value="' + orden.idOrden + '">' + escaparHtml(etiqueta) + "</option>";
    }).join("");

    campoOrdenExpediente.innerHTML = opciones;
    campoOrdenIncidencia.innerHTML = opciones;

}


function obtenerOrden(idOrden) {
    return ordenesCache.find(function (o) {
        return String(o.idOrden) === String(idOrden);
    });
}


function obtenerClienteDeOrden(orden) {
    if (!orden) {
        return "-";
    }
    const cotizacion = cotizacionesCache.find(function (c) {
        return String(c.idCotizacion) === String(orden.idCotizacion);
    });
    return cotizacion ? cotizacion.clienteRazonSocial : "-";
}

async function cargarExpedientes() {

    try {

        const respuesta = await fetch(API_EXPEDIENTES);

        if (!respuesta.ok) {
            throw new Error("No se pudo cargar la lista de expedientes DAM");
        }

        const expedientes = await respuesta.json();

        pintarTablaExpedientes(expedientes);
        pintarPipelineExpedientes(expedientes);
        pintarKpisExpedientes(expedientes);

    } catch (error) {
        console.error(error);
        expedientesTbody.innerHTML =
            '<tr><td colspan="8">Error al cargar los expedientes DAM.</td></tr>';
    }

}


function claveCanal(canal) {
    const normalizado = String(canal || "").trim().toLowerCase();
    return CANALES.includes(normalizado) ? normalizado : "pendiente";
}


function pintarTablaExpedientes(expedientes) {

    expedientesTbody.innerHTML = "";

    mensajeVacioExpedientes.style.display = expedientes.length === 0 ? "block" : "none";
    expedientesCount.textContent = expedientes.length;

    expedientes.forEach(function (exp) {

        const orden = obtenerOrden(exp.idOrdenServicio);
        const cliente = obtenerClienteDeOrden(orden);
        const numeroOrden = orden ? orden.numeroOrden : ("Orden #" + exp.idOrdenServicio);
        const etiquetaTramite = ETIQUETAS_TRAMITE[exp.estadoTramite] || exp.estadoTramite;

        const fila = document.createElement("tr");

        fila.innerHTML =
            "<td>" + exp.idExpediente + "</td>" +
            "<td>" + escaparHtml(exp.numeroDam || "-") + "</td>" +
            "<td>" + escaparHtml(exp.regimen) + "</td>" +
            "<td>" + escaparHtml(numeroOrden) + "</td>" +
            "<td>" + escaparHtml(cliente) + "</td>" +
            "<td><span class=\"stage-badge\">" + escaparHtml(exp.canalControl || "Sin asignar") + "</span></td>" +
            "<td>" + escaparHtml(etiquetaTramite) + "</td>" +
            "<td>" +
                "<button type=\"button\" class=\"btn-secundario btn-editar-exp\" data-id=\"" + exp.idExpediente + "\">Editar</button> " +
                "<button type=\"button\" class=\"btn-secundario btn-eliminar-exp\" data-id=\"" + exp.idExpediente + "\">Eliminar</button>" +
            "</td>";

        expedientesTbody.appendChild(fila);

    });

    document.querySelectorAll(".btn-editar-exp").forEach(function (boton) {
        boton.addEventListener("click", function () {
            abrirModalEdicionExpediente(boton.dataset.id, expedientes);
        });
    });

    document.querySelectorAll(".btn-eliminar-exp").forEach(function (boton) {
        boton.addEventListener("click", function () {
            eliminarExpediente(boton.dataset.id);
        });
    });

}


function pintarPipelineExpedientes(expedientes) {

    CANALES.forEach(function (canal) {

        const contenedor = document.getElementById("pipeline-" + canal);
        const contador = document.getElementById("count-" + canal);

        const delCanal = expedientes.filter(function (exp) {
            return claveCanal(exp.canalControl) === canal;
        });

        contador.textContent = delCanal.length;

        contenedor.innerHTML = delCanal.map(function (exp) {

            const orden = obtenerOrden(exp.idOrdenServicio);
            const cliente = obtenerClienteDeOrden(orden);

            return (
                '<div class="pipeline-card" data-id="' + exp.idExpediente + '">' +
                    "<strong>" + escaparHtml(exp.numeroDam || "Sin DAM aún") + "</strong>" +
                    "<span>" + escaparHtml(cliente) + " · " + escaparHtml(exp.regimen) + "</span>" +
                    '<div class="pipeline-card-foot">' +
                        '<span class="pipeline-amount">' + escaparHtml(ETIQUETAS_TRAMITE[exp.estadoTramite] || exp.estadoTramite) + "</span>" +
                        '<div class="pipeline-owner">TF</div>' +
                    "</div>" +
                "</div>"
            );

        }).join("");

    });

    document.querySelectorAll(".pipeline-card").forEach(function (tarjeta) {
        tarjeta.addEventListener("click", function () {
            fetch(API_EXPEDIENTES).then(function (r) { return r.json(); }).then(function (expedientes) {
                abrirModalEdicionExpediente(tarjeta.dataset.id, expedientes);
            });
        });
    });

}


function pintarKpisExpedientes(expedientes) {

    kpiTotal.textContent = expedientes.length;

    kpiVerde.textContent = expedientes.filter(function (e) { return claveCanal(e.canalControl) === "verde"; }).length;
    kpiNaranja.textContent = expedientes.filter(function (e) { return claveCanal(e.canalControl) === "naranja"; }).length;
    kpiRojo.textContent = expedientes.filter(function (e) { return claveCanal(e.canalControl) === "rojo"; }).length;

}


async function abrirModalNuevoExpediente() {

    formExpediente.reset();
    campoIdExpediente.value = "";
    modalTituloExpediente.textContent = "Nuevo expediente";
    ocultarErrorExpediente();

    await cargarOrdenesYCotizaciones();
    campoEstadoTramite.value = "en_proceso";

    modalOverlayExpediente.classList.add("show");

}


function abrirModalEdicionExpediente(id, expedientes) {

    const expediente = expedientes.find(function (item) {
        return String(item.idExpediente) === String(id);
    });

    if (!expediente) {
        return;
    }

    campoIdExpediente.value = expediente.idExpediente;
    campoOrdenExpediente.value = expediente.idOrdenServicio;
    campoNumeroDam.value = expediente.numeroDam || "";
    campoRegimen.value = expediente.regimen;
    campoCanal.value = expediente.canalControl || "";
    campoEstadoTramite.value = expediente.estadoTramite;

    modalTituloExpediente.textContent = "Editar expediente";
    ocultarErrorExpediente();
    modalOverlayExpediente.classList.add("show");

}


function cerrarModalExpediente() {
    modalOverlayExpediente.classList.remove("show");
}


function mostrarErrorExpediente(mensaje) {
    formErrorExpediente.textContent = mensaje;
    formErrorExpediente.style.display = "block";
}


function ocultarErrorExpediente() {
    formErrorExpediente.style.display = "none";
    formErrorExpediente.textContent = "";
}


function abrirReporteExpedientes() {
    window.open(API_REPORTE_EXPEDIENTES, "_blank");
}

btnReporteExpedientes.addEventListener("click", abrirReporteExpedientes);

btnNuevoExpediente.addEventListener("click", abrirModalNuevoExpediente);
btnCerrarModalExpediente.addEventListener("click", cerrarModalExpediente);
btnCancelarModalExpediente.addEventListener("click", cerrarModalExpediente);


formExpediente.addEventListener("submit", async function (evento) {

    evento.preventDefault();
    ocultarErrorExpediente();

    const dto = {
        idOrdenServicio: Number(campoOrdenExpediente.value),
        numeroDam: campoNumeroDam.value.trim() || null,
        regimen: campoRegimen.value.trim(),
        canalControl: campoCanal.value || null,
        estadoTramite: campoEstadoTramite.value
    };

    const id = campoIdExpediente.value;
    const esEdicion = Boolean(id);

    try {

        const respuesta = await fetch(
            esEdicion ? API_EXPEDIENTES + "/" + id : API_EXPEDIENTES,
            {
                method: esEdicion ? "PUT" : "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dto)
            }
        );

        if (!respuesta.ok) {
            const cuerpo = await respuesta.json().catch(function () { return null; });
            throw new Error((cuerpo && cuerpo.error) || "No se pudo guardar el expediente");
        }

        cerrarModalExpediente();
        cargarExpedientes();

    } catch (error) {
        mostrarErrorExpediente(error.message);
    }

});


function eliminarExpediente(id) {
    mostrarConfirmarEliminar(
        "expediente",
        id,
        "¿Está seguro de que desea eliminar este expediente DAM?"
    );
}


async function ejecutarEliminacionExpediente(id) {

    try {

        const respuesta = await fetch(API_EXPEDIENTES + "/" + id, { method: "DELETE" });

        if (!respuesta.ok) {
            const cuerpo = await respuesta.json().catch(function () { return null; });
            throw new Error(
                (cuerpo && cuerpo.error) ||
                "No se puede eliminar el expediente porque está asociado a otros registros."
            );
        }

        cargarExpedientes();

    } catch (error) {
        mostrarModalMensaje(error.message);
    }

}

async function cargarIncidencias() {

    try {

        const respuesta = await fetch(API_INCIDENCIAS);

        if (!respuesta.ok) {
            throw new Error("No se pudo cargar la lista de incidencias");
        }

        const incidencias = await respuesta.json();

        pintarTablaIncidencias(incidencias);

    } catch (error) {
        console.error(error);
        incidenciasTbody.innerHTML =
            '<tr><td colspan="6">Error al cargar las incidencias.</td></tr>';
    }

}


function pintarTablaIncidencias(incidencias) {

    incidenciasTbody.innerHTML = "";

    mensajeVacioIncidencias.style.display = incidencias.length === 0 ? "block" : "none";

    incidencias.forEach(function (inc) {

        const orden = obtenerOrden(inc.idOrdenServicio);
        const cliente = obtenerClienteDeOrden(orden);
        const numeroOrden = orden ? orden.numeroOrden : ("Orden #" + inc.idOrdenServicio);
        const fecha = inc.fechaReporte ? String(inc.fechaReporte).replace("T", " ").slice(0, 16) : "-";

        const fila = document.createElement("tr");

        fila.innerHTML =
            "<td>" + escaparHtml(numeroOrden) + "</td>" +
            "<td>" + escaparHtml(cliente) + "</td>" +
            "<td>" + escaparHtml(inc.tipoIncidencia) + "</td>" +
            "<td>" + escaparHtml(inc.descripcion || "-") + "</td>" +
            "<td>" + escaparHtml(fecha) + "</td>" +
            "<td>" +
                "<button type=\"button\" class=\"btn-secundario btn-eliminar-inc\" data-id=\"" + inc.idIncidencia + "\">Eliminar</button>" +
            "</td>";

        incidenciasTbody.appendChild(fila);

    });

    document.querySelectorAll(".btn-eliminar-inc").forEach(function (boton) {
        boton.addEventListener("click", function () {
            eliminarIncidencia(boton.dataset.id);
        });
    });

}


async function abrirModalNuevaIncidencia() {

    formIncidencia.reset();
    campoIdIncidencia.value = "";
    ocultarErrorIncidencia();

    await cargarOrdenesYCotizaciones();

    modalOverlayIncidencia.classList.add("show");

}


function cerrarModalIncidencia() {
    modalOverlayIncidencia.classList.remove("show");
}


function mostrarErrorIncidencia(mensaje) {
    formErrorIncidencia.textContent = mensaje;
    formErrorIncidencia.style.display = "block";
}


function ocultarErrorIncidencia() {
    formErrorIncidencia.style.display = "none";
    formErrorIncidencia.textContent = "";
}


function abrirReporteIncidencias() {
    window.open(API_REPORTE_INCIDENCIAS, "_blank");
}

btnReporteIncidencias.addEventListener("click", abrirReporteIncidencias);

btnNuevaIncidencia.addEventListener("click", abrirModalNuevaIncidencia);
btnCerrarModalIncidencia.addEventListener("click", cerrarModalIncidencia);
btnCancelarModalIncidencia.addEventListener("click", cerrarModalIncidencia);


formIncidencia.addEventListener("submit", async function (evento) {

    evento.preventDefault();
    ocultarErrorIncidencia();

    const dto = {
        idOrdenServicio: Number(campoOrdenIncidencia.value),
        tipoIncidencia: campoTipoIncidencia.value.trim(),
        descripcion: campoDescripcionIncidencia.value.trim() || null
    };

    try {

        const respuesta = await fetch(API_INCIDENCIAS, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(dto)
        });

        if (!respuesta.ok) {
            const cuerpo = await respuesta.json().catch(function () { return null; });
            throw new Error((cuerpo && cuerpo.error) || "No se pudo guardar la incidencia");
        }

        cerrarModalIncidencia();
        cargarIncidencias();

    } catch (error) {
        mostrarErrorIncidencia(error.message);
    }

});


function eliminarIncidencia(id) {
    mostrarConfirmarEliminar(
        "incidencia",
        id,
        "¿Está seguro de que desea eliminar esta incidencia?"
    );
}


async function ejecutarEliminacionIncidencia(id) {

    try {

        const respuesta = await fetch(API_INCIDENCIAS + "/" + id, { method: "DELETE" });

        if (!respuesta.ok) {
            const cuerpo = await respuesta.json().catch(function () { return null; });
            throw new Error(
                (cuerpo && cuerpo.error) ||
                "No se puede eliminar la incidencia porque está asociada a otros registros."
            );
        }

        cargarIncidencias();

    } catch (error) {
        mostrarModalMensaje(error.message);
    }

}

cargarOrdenesYCotizaciones().then(function () {
    cargarExpedientes();
    cargarIncidencias();
});