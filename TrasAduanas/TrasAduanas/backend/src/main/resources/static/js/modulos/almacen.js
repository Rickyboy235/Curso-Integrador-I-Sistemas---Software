const API_INSUMOS = "/api/insumos";
const API_REPORTE_ALMACEN = "/api/reportes/almacen/pdf";

const modalOverlay = document.getElementById("modalOverlay");
const btnNuevoInsumo = document.getElementById("btnNuevoInsumo");
const btnReporteAlmacen = document.getElementById("btnReporteAlmacen");
const btnCerrarModal = document.getElementById("btnCerrarModal");
const btnCancelarModal = document.getElementById("btnCancelarModal");
const formInsumo = document.getElementById("formInsumo");
const formError = document.getElementById("formError");
const modalTitulo = document.getElementById("modalTitulo");

const campoId = document.getElementById("campoId");
const campoCodigo = document.getElementById("campoCodigo");
const campoDescripcion = document.getElementById("campoDescripcion");
const campoStockActual = document.getElementById("campoStockActual");
const campoStockMinimo = document.getElementById("campoStockMinimo");

const insumosTbody = document.getElementById("insumos-tbody");
const mensajeVacio = document.getElementById("mensaje-vacio");

const kpiTotal = document.getElementById("kpi-total");
const kpiDisponible = document.getElementById("kpi-disponible");
const kpiAlerta = document.getElementById("kpi-alerta");

const mensajeModalOverlay = document.getElementById("mensajeModalOverlay");
const mensajeModalTexto = document.getElementById("mensajeModalTexto");
const btnCerrarMensajeModal = document.getElementById("btnCerrarMensajeModal");
const btnAceptarMensajeModal = document.getElementById("btnAceptarMensajeModal");

function mostrarModalMensaje(mensaje) {
    mensajeModalTexto.textContent = mensaje;
    mensajeModalOverlay.classList.add("show");
}

function cerrarModalMensaje() {
    mensajeModalOverlay.classList.remove("show");
}

btnCerrarMensajeModal.addEventListener("click", cerrarModalMensaje);
btnAceptarMensajeModal.addEventListener("click", cerrarModalMensaje);

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

async function cargarInsumos() {

    try {

        const respuesta = await fetch(API_INSUMOS);

        if (!respuesta.ok) {
            throw new Error("No se pudo cargar la lista de insumos");
        }

        const insumos = await respuesta.json();

        insumosTbody.innerHTML = "";

        if (!insumos || insumos.length === 0) {
            mensajeVacio.style.display = "block";
        } else {
            mensajeVacio.style.display = "none";
        }

        let total = insumos.length;
        let disponibles = 0;
        let enAlerta = 0;

        insumos.forEach(function (insumo) {

            if (insumo.stockActual > insumo.stockMinimo) {
                disponibles++;
            }

            if (insumo.stockActual <= insumo.stockMinimo) {
                enAlerta++;
            }

            const fila = document.createElement("tr");

            fila.innerHTML =
                "<td>" + insumo.codigo + "</td>" +
                "<td>" + insumo.descripcion + "</td>" +
                "<td>" + insumo.stockActual + "</td>" +
                "<td>" + insumo.stockMinimo + "</td>" +
                "<td>" +
                "<button type=\"button\" class=\"btn-secundario btn-editar\" data-id=\"" + insumo.idInsumo + "\">Editar</button> " +
                "<button type=\"button\" class=\"btn-secundario btn-eliminar\" data-id=\"" + insumo.idInsumo + "\">Eliminar</button>" +
                "</td>";

            insumosTbody.appendChild(fila);

        });

        kpiTotal.textContent = total;
        kpiDisponible.textContent = disponibles;
        kpiAlerta.textContent = enAlerta;

    } catch (error) {
        mostrarError(error.message);
    }

}

insumosTbody.addEventListener("click", function (evento) {

    const boton = evento.target.closest("button");

    if (!boton) {
        return;
    }

    if (boton.classList.contains("btn-editar")) {
        abrirModalEditar(boton.dataset.id);
    }

    if (boton.classList.contains("btn-eliminar")) {
        eliminarInsumo(boton.dataset.id);
    }

});

function abrirModalNuevo() {
    modalTitulo.textContent = "Nuevo Insumo";
    formInsumo.reset();
    campoId.value = "";
    formError.textContent = "";
    modalOverlay.classList.add("show");
}

async function abrirModalEditar(id) {

    const respuesta = await fetch(API_INSUMOS);
    const insumos = await respuesta.json();

    const insumo = insumos.find(function (item) {
        return String(item.idInsumo) === String(id);
    });

    if (!insumo) {
        return;
    }

    modalTitulo.textContent = "Editar Insumo";
    formError.textContent = "";

    campoId.value = insumo.idInsumo;
    campoCodigo.value = insumo.codigo;
    campoDescripcion.value = insumo.descripcion;
    campoStockActual.value = insumo.stockActual;
    campoStockMinimo.value = insumo.stockMinimo;

    modalOverlay.classList.add("show");

}

function cerrarModal() {
    modalOverlay.classList.remove("show");
}

function mostrarError(mensaje) {
    formError.textContent = mensaje;
}

btnNuevoInsumo.addEventListener("click", abrirModalNuevo);
btnReporteAlmacen.addEventListener("click", function () {
    window.open(API_REPORTE_ALMACEN, "_blank");
});
btnCerrarModal.addEventListener("click", cerrarModal);
btnCancelarModal.addEventListener("click", cerrarModal);

formInsumo.addEventListener("submit", async function (evento) {

    evento.preventDefault();
    formError.textContent = "";

    const payload = {
        codigo: campoCodigo.value,
        descripcion: campoDescripcion.value,
        stockActual: Number(campoStockActual.value),
        stockMinimo: Number(campoStockMinimo.value)
    };

    const id = campoId.value;
    const esEdicion = id !== "";

    try {

        const respuesta = await fetch(
            esEdicion ? API_INSUMOS + "/" + id : API_INSUMOS,
            {
                method: esEdicion ? "PUT" : "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            }
        );

        if (!respuesta.ok) {
            const cuerpo = await respuesta.json().catch(function () { return null; });
            throw new Error((cuerpo && cuerpo.error) || "No se pudo guardar el insumo");
        }

        cerrarModal();
        cargarInsumos();

    } catch (error) {
        mostrarError(error.message);
    }

});

async function eliminarInsumo(id) {

    const confirmado = await mostrarModalConfirmacion("¿Eliminar este insumo?");

    if (!confirmado) {
        return;
    }

    try {

        const respuesta = await fetch(API_INSUMOS + "/" + id, {
            method: "DELETE"
        });

        if (!respuesta.ok) {
            const cuerpo = await respuesta.json().catch(function () { return null; });
            throw new Error((cuerpo && cuerpo.error) || "No se pudo eliminar el insumo");
        }

        cargarInsumos();

        } catch (error) {
        mostrarModalMensaje(error.message);
    }

}

cargarInsumos();