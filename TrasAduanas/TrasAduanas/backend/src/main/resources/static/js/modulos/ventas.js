const API_COTIZACIONES = "/api/cotizaciones";
const API_CLIENTES = "/api/clientes";
const API_USUARIOS = "/api/usuarios";

const modalOverlay = document.getElementById("modalOverlay");
const btnNuevaCotizacion = document.getElementById("btnNuevaCotizacion");
const btnCerrarModal = document.getElementById("btnCerrarModal");
const btnCancelarModal = document.getElementById("btnCancelarModal");
const mensajeModalOverlay = document.getElementById("mensajeModalOverlay");
const mensajeModalTexto = document.getElementById("mensajeModalTexto");
const btnCerrarMensajeModal = document.getElementById("btnCerrarMensajeModal");
const btnAceptarMensajeModal = document.getElementById("btnAceptarMensajeModal");
const formCotizacion = document.getElementById("formCotizacion");
const formError = document.getElementById("formError");
const modalTitulo = document.getElementById("modalTitulo");
const confirmarEliminarOverlay = document.getElementById("confirmarEliminarOverlay");
const btnCerrarConfirmarEliminar = document.getElementById("btnCerrarConfirmarEliminar");
const btnCancelarEliminar = document.getElementById("btnCancelarEliminar");
const btnConfirmarEliminar = document.getElementById("btnConfirmarEliminar");

const campoId = document.getElementById("campoId");
const campoCliente = document.getElementById("campoCliente");
const campoUsuario = document.getElementById("campoUsuario");
const campoOrigen = document.getElementById("campoOrigen");
const campoDestino = document.getElementById("campoDestino");
const campoTipoCarga = document.getElementById("campoTipoCarga");
const campoPesoKg = document.getElementById("campoPesoKg");

const serviciosContenedor = document.getElementById("serviciosContenedor");
const btnAgregarServicio = document.getElementById("btnAgregarServicio");
const tplServicioFila = document.getElementById("tplServicioFila");
const servicioTotalPreview = document.getElementById("servicioTotalPreview");

const cotizacionesTbody = document.getElementById("cotizaciones-tbody");
const mensajeVacio = document.getElementById("mensaje-vacio");

const kpiTotal = document.getElementById("kpi-total");
const kpiPendientes = document.getElementById("kpi-pendientes");
const kpiMonto = document.getElementById("kpi-monto");

let clientesCache = [];
let usuariosCache = [];
let cotizacionAEliminar = null;

async function cargarSelects() {

    const [respClientes, respUsuarios] = await Promise.all([
        fetch(API_CLIENTES),
        fetch(API_USUARIOS)
    ]);

    clientesCache = respClientes.ok ? await respClientes.json() : [];
    usuariosCache = respUsuarios.ok ? await respUsuarios.json() : [];

    campoCliente.innerHTML = clientesCache.map(function (c) {
        return '<option value="' + c.idCliente + '">' + escaparHtml(c.razonSocial) + "</option>";
    }).join("");

    campoUsuario.innerHTML = usuariosCache.map(function (u) {
        return '<option value="' + u.idUsuario + '">' + escaparHtml(u.nombresUsuario + " " + u.apellidosUsuario) + "</option>";
    }).join("");

}

async function cargarCotizaciones() {

    try {

        const respuesta = await fetch(API_COTIZACIONES);

        if (!respuesta.ok) {
            throw new Error("No se pudo cargar la lista de cotizaciones");
        }

        const cotizaciones = await respuesta.json();

        pintarTabla(cotizaciones);
        pintarKpis(cotizaciones);

    } catch (error) {
        console.error(error);
        cotizacionesTbody.innerHTML =
            '<tr><td colspan="8">Error al cargar las cotizaciones.</td></tr>';
    }

}


function pintarTabla(cotizaciones) {

    cotizacionesTbody.innerHTML = "";

    mensajeVacio.style.display = cotizaciones.length === 0 ? "block" : "none";

    cotizaciones.forEach(function (cot) {

        const fila = document.createElement("tr");

        fila.innerHTML =
            "<td>" + escaparHtml(cot.codigoCotizacion || "-") + "</td>" +
            "<td>" + escaparHtml(cot.clienteRazonSocial) + "</td>" +
            "<td>" + escaparHtml(cot.origen) + " → " + escaparHtml(cot.destino) + "</td>" +
            "<td>" + escaparHtml(cot.tipoCarga) + "</td>" +
            "<td>" + Number(cot.pesoKg) + "</td>" +
            "<td><span class=\"stage-badge\">" + escaparHtml(cot.estadoCotizacion || "-") + "</span></td>" +
            "<td>" + formatearMonto(cot.total) + "</td>" +
            "<td>" +
                "<button type=\"button\" class=\"btn-secundario btn-editar\" data-id=\"" + cot.idCotizacion + "\">Editar</button> " +
                "<button type=\"button\" class=\"btn-secundario btn-eliminar\" data-id=\"" + cot.idCotizacion + "\">Eliminar</button>" +
            "</td>";

        cotizacionesTbody.appendChild(fila);

    });

    document.querySelectorAll(".btn-editar").forEach(function (boton) {
        boton.addEventListener("click", function () {
            abrirModalEdicion(boton.dataset.id, cotizaciones);
        });
    });

    document.querySelectorAll(".btn-eliminar").forEach(function (boton) {
        boton.addEventListener("click", function () {
            eliminarCotizacion(boton.dataset.id);
        });
    });

}


function pintarKpis(cotizaciones) {

    kpiTotal.textContent = cotizaciones.length;

    const pendientes = cotizaciones.filter(function (c) {
        return (c.estadoCotizacion || "").toUpperCase() === "PENDIENTE";
    }).length;

    kpiPendientes.textContent = pendientes;

    const montoTotal = cotizaciones.reduce(function (acumulado, c) {
        return acumulado + Number(c.total || 0);
    }, 0);

    kpiMonto.textContent = formatearMonto(montoTotal);

}

function agregarFilaServicio(servicio) {

    const nodo = tplServicioFila.content.cloneNode(true);
    const fila = nodo.querySelector(".servicio-fila");

    const campoDescripcion = fila.querySelector(".servicio-descripcion");
    const campoCantidad = fila.querySelector(".servicio-cantidad");
    const campoPrecio = fila.querySelector(".servicio-precio");
    const btnQuitar = fila.querySelector(".btn-quitar-servicio");

    if (servicio) {
        campoDescripcion.value = servicio.descripcionServicio;
        campoCantidad.value = servicio.cantidad;
        campoPrecio.value = servicio.precioUnitario;
    }

    campoCantidad.addEventListener("input", actualizarTotalPreview);
    campoPrecio.addEventListener("input", actualizarTotalPreview);

    btnQuitar.addEventListener("click", function () {
        fila.remove();
        actualizarTotalPreview();
    });

    serviciosContenedor.appendChild(fila);
    actualizarTotalPreview();

}


function leerServiciosDelFormulario() {

    const filas = serviciosContenedor.querySelectorAll(".servicio-fila");

    return Array.from(filas).map(function (fila) {
        return {
            descripcionServicio: fila.querySelector(".servicio-descripcion").value.trim(),
            cantidad: Number(fila.querySelector(".servicio-cantidad").value),
            precioUnitario: Number(fila.querySelector(".servicio-precio").value)
        };
    });

}


function actualizarTotalPreview() {

    const servicios = leerServiciosDelFormulario();

    const total = servicios.reduce(function (acumulado, s) {
        return acumulado + (Number(s.cantidad) || 0) * (Number(s.precioUnitario) || 0);
    }, 0);

    servicioTotalPreview.textContent = formatearMonto(total);

}


btnAgregarServicio.addEventListener("click", function () {
    agregarFilaServicio(null);
});

async function abrirModalNuevo() {

    formCotizacion.reset();
    campoId.value = "";
    serviciosContenedor.innerHTML = "";
    modalTitulo.textContent = "Nueva cotización";
    ocultarError();

    await cargarSelects();
    agregarFilaServicio(null);
    actualizarTotalPreview();

    modalOverlay.classList.add("show");

}


async function abrirModalEdicion(id, cotizaciones) {

    const cotizacion = cotizaciones.find(function (item) {
        return String(item.idCotizacion) === String(id);
    });

    if (!cotizacion) {
        return;
    }

    await cargarSelects();

    campoId.value = cotizacion.idCotizacion;
    campoCliente.value = cotizacion.idCliente;
    campoUsuario.value = cotizacion.idUsuario;
    campoOrigen.value = cotizacion.origen;
    campoDestino.value = cotizacion.destino;
    campoTipoCarga.value = cotizacion.tipoCarga;
    campoPesoKg.value = cotizacion.pesoKg;

    serviciosContenedor.innerHTML = "";

    if (cotizacion.servicios && cotizacion.servicios.length > 0) {
        cotizacion.servicios.forEach(function (s) {
            agregarFilaServicio(s);
        });
    } else {
        agregarFilaServicio(null);
    }

    modalTitulo.textContent = "Editar cotización";
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

function mostrarConfirmarEliminar(id) {
    cotizacionAEliminar = id;
    confirmarEliminarOverlay.classList.add("show");
}

function cerrarConfirmarEliminar() {
    cotizacionAEliminar = null;
    confirmarEliminarOverlay.classList.remove("show");
}

btnNuevaCotizacion.addEventListener("click", abrirModalNuevo);
btnCerrarModal.addEventListener("click", cerrarModal);
btnCancelarModal.addEventListener("click", cerrarModal);
btnCerrarMensajeModal.addEventListener("click", cerrarModalMensaje);
btnAceptarMensajeModal.addEventListener("click", cerrarModalMensaje);
btnCerrarConfirmarEliminar.addEventListener("click", cerrarConfirmarEliminar);
btnCancelarEliminar.addEventListener("click", cerrarConfirmarEliminar);
btnConfirmarEliminar.addEventListener("click", async () => {
    if (cotizacionAEliminar === null) {
        return;
    }

    const id = cotizacionAEliminar;

    cerrarConfirmarEliminar();

    await ejecutarEliminacionCotizacion(id);
});

formCotizacion.addEventListener("submit", async function (evento) {

    evento.preventDefault();
    ocultarError();

    const servicios = leerServiciosDelFormulario();

    if (servicios.length === 0) {
        mostrarError("Agrega al menos un servicio a la cotización.");
        return;
    }

    const dto = {
        idCliente: Number(campoCliente.value),
        idUsuario: Number(campoUsuario.value),
        origen: campoOrigen.value.trim(),
        destino: campoDestino.value.trim(),
        tipoCarga: campoTipoCarga.value.trim(),
        pesoKg: Number(campoPesoKg.value),
        servicios: servicios
    };

    const id = campoId.value;
    const esEdicion = Boolean(id);

    try {

        const respuesta = await fetch(
            esEdicion ? API_COTIZACIONES + "/" + id : API_COTIZACIONES,
            {
                method: esEdicion ? "PUT" : "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dto)
            }
        );

        if (!respuesta.ok) {
            const mensaje = await respuesta.text();
            throw new Error(mensaje || "No se pudo guardar la cotización");
        }

        cerrarModal();
        cargarCotizaciones();

    } catch (error) {
        mostrarError(error.message);
    }

});

function eliminarCotizacion(id) {
    mostrarConfirmarEliminar(id);
}

async function ejecutarEliminacionCotizacion(id) {
    try {
        const respuesta = await fetch(API_COTIZACIONES + "/" + id, {
            method: "DELETE"
        });

        if (!respuesta.ok) {
            const datos = await respuesta.json();

            throw new Error(
                datos.error || "No se pudo eliminar la cotización"
            );
        }

        cargarCotizaciones();

    } catch (error) {
        mostrarModalMensaje(error.message);
    }
}

cargarCotizaciones();


const btnReporteCotizaciones =
    document.getElementById("btnReporteCotizaciones");

if (btnReporteCotizaciones) {
    btnReporteCotizaciones.addEventListener("click", () => {
        window.open("/api/reportes/cotizaciones/pdf", "_blank");
    });
}