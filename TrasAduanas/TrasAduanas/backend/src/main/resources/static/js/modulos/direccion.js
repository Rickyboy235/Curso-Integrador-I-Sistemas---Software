const API_USUARIOS = "/api/usuarios";
const API_ROLES = "/api/roles";
const API_REPORTE_DIRECCION = "/api/reportes/direccion/pdf";

const mensajeModalOverlay = document.getElementById("mensajeModalOverlay");
const mensajeModalTexto = document.getElementById("mensajeModalTexto");
const btnCerrarMensajeModal = document.getElementById("btnCerrarMensajeModal");
const btnAceptarMensajeModal = document.getElementById("btnAceptarMensajeModal");
const btnReporteDireccion = document.getElementById("btnReporteDireccion");

function mostrarModalMensaje(mensaje) {

    mensajeModalTexto.textContent = mensaje;

    mensajeModalOverlay.classList.add("show");
}


function cerrarModalMensaje() {

    mensajeModalOverlay.classList.remove("show");

}

btnCerrarMensajeModal.addEventListener("click", cerrarModalMensaje);
btnAceptarMensajeModal.addEventListener("click", cerrarModalMensaje);

const confirmarEliminarUsuarioOverlay = document.getElementById("confirmarEliminarUsuarioOverlay");
const btnCerrarConfirmarEliminarUsuario = document.getElementById("btnCerrarConfirmarEliminarUsuario");
const btnCancelarEliminarUsuario = document.getElementById("btnCancelarEliminarUsuario");
const btnConfirmarEliminarUsuario = document.getElementById("btnConfirmarEliminarUsuario");

let usuarioAEliminar = null;

function mostrarConfirmarEliminarUsuario(id) {

    usuarioAEliminar = id;

    confirmarEliminarUsuarioOverlay.classList.add("show");
}


function cerrarConfirmarEliminarUsuario() {

    usuarioAEliminar = null;

    confirmarEliminarUsuarioOverlay.classList.remove("show");
}

btnCerrarConfirmarEliminarUsuario.addEventListener("click", cerrarConfirmarEliminarUsuario);
btnCancelarEliminarUsuario.addEventListener("click", cerrarConfirmarEliminarUsuario);
btnConfirmarEliminarUsuario.addEventListener("click", async function () {

    if (usuarioAEliminar === null) {
        return;
    }

    const id = usuarioAEliminar;

    cerrarConfirmarEliminarUsuario();

    await ejecutarEliminacionUsuario(id);

});

const confirmarEliminarRolOverlay = document.getElementById("confirmarEliminarRolOverlay");
const btnCerrarConfirmarEliminarRol = document.getElementById("btnCerrarConfirmarEliminarRol");
const btnCancelarEliminarRol = document.getElementById("btnCancelarEliminarRol");
const btnConfirmarEliminarRol = document.getElementById("btnConfirmarEliminarRol");

let rolAEliminar = null;

function mostrarConfirmarEliminarRol(id) {

    rolAEliminar = id;

    confirmarEliminarRolOverlay.classList.add("show");
}


function cerrarConfirmarEliminarRol() {

    rolAEliminar = null;

    confirmarEliminarRolOverlay.classList.remove("show");
}

btnCerrarConfirmarEliminarRol.addEventListener("click", cerrarConfirmarEliminarRol);
btnCancelarEliminarRol.addEventListener("click", cerrarConfirmarEliminarRol);
btnConfirmarEliminarRol.addEventListener("click", async function () {

    if (rolAEliminar === null) {
        return;
    }

    const id = rolAEliminar;

    cerrarConfirmarEliminarRol();

    await ejecutarEliminacionRol(id);

});

const modalOverlayUsuario = document.getElementById("modalOverlayUsuario");
const btnNuevoUsuario = document.getElementById("btnNuevoUsuario");
const btnCerrarModalUsuario = document.getElementById("btnCerrarModalUsuario");
const btnCancelarModalUsuario = document.getElementById("btnCancelarModalUsuario");
const formUsuario = document.getElementById("formUsuario");
const formErrorUsuario = document.getElementById("formErrorUsuario");
const modalTituloUsuario = document.getElementById("modalTituloUsuario");

const campoIdUsuario = document.getElementById("campoIdUsuario");
const campoNombres = document.getElementById("campoNombres");
const campoApellidos = document.getElementById("campoApellidos");
const campoEmail = document.getElementById("campoEmail");
const campoDni = document.getElementById("campoDni");
const campoRol = document.getElementById("campoRol");
const campoPassword = document.getElementById("campoPassword");
const campoEstadoUsuario = document.getElementById("campoEstadoUsuario");
const passwordHint = document.getElementById("passwordHint");

const usuariosTbody = document.getElementById("usuarios-tbody");
const usuariosMensajeVacio = document.getElementById("usuarios-mensaje-vacio");
const usuariosCount = document.getElementById("usuarios-count");
const kpiUsuariosTotal = document.getElementById("kpi-usuarios-total");
const kpiUsuariosActivos = document.getElementById("kpi-usuarios-activos");

const modalOverlayRol = document.getElementById("modalOverlayRol");
const btnNuevoRol = document.getElementById("btnNuevoRol");
const btnCerrarModalRol = document.getElementById("btnCerrarModalRol");
const btnCancelarModalRol = document.getElementById("btnCancelarModalRol");
const formRol = document.getElementById("formRol");
const formErrorRol = document.getElementById("formErrorRol");
const modalTituloRol = document.getElementById("modalTituloRol");

const campoIdRol = document.getElementById("campoIdRol");
const campoNombreRol = document.getElementById("campoNombreRol");
const campoDescripcionRol = document.getElementById("campoDescripcionRol");

const rolesTbody = document.getElementById("roles-tbody");
const rolesMensajeVacio = document.getElementById("roles-mensaje-vacio");
const rolesCount = document.getElementById("roles-count");
const kpiRolesTotal = document.getElementById("kpi-roles-total");

let rolesCache = [];

async function cargarRoles() {

    try {

        const respuesta = await fetch(API_ROLES);

        if (!respuesta.ok) {
            throw new Error("No se pudo cargar la lista de roles");
        }

        rolesCache = await respuesta.json();

        pintarRoles();
        llenarSelectRoles();

    } catch (error) {
        console.error(error);
        rolesTbody.innerHTML = '<tr><td colspan="3">Error al cargar los roles.</td></tr>';
    }

}


function pintarRoles() {

    rolesTbody.innerHTML = "";
    rolesMensajeVacio.style.display = rolesCache.length === 0 ? "block" : "none";
    rolesCount.textContent = rolesCache.length;
    kpiRolesTotal.textContent = rolesCache.length;

    rolesCache.forEach(function (rol) {

        const fila = document.createElement("tr");

        fila.innerHTML =
            "<td>" + escaparHtml(rol.nombreRol) + "</td>" +
            "<td>" + escaparHtml(rol.descripcion || "-") + "</td>" +
            "<td>" +
                '<button type="button" class="btn-secundario btn-editar-rol" data-id="' + rol.idRol + '">Editar</button> ' +
                '<button type="button" class="btn-secundario btn-eliminar-rol" data-id="' + rol.idRol + '">Eliminar</button>' +
            "</td>";

        rolesTbody.appendChild(fila);

    });

    document.querySelectorAll(".btn-editar-rol").forEach(function (boton) {
        boton.addEventListener("click", function () {
            abrirModalEdicionRol(boton.dataset.id);
        });
    });

    document.querySelectorAll(".btn-eliminar-rol").forEach(function (boton) {
        boton.addEventListener("click", function () {
            eliminarRol(boton.dataset.id);
        });
    });

}


function llenarSelectRoles() {
    campoRol.innerHTML = rolesCache.map(function (rol) {
        return '<option value="' + rol.idRol + '">' + escaparHtml(rol.nombreRol) + "</option>";
    }).join("");
}


function abrirModalNuevoRol() {
    formRol.reset();
    campoIdRol.value = "";
    modalTituloRol.textContent = "Nuevo rol";
    ocultarErrorRol();
    modalOverlayRol.classList.add("show");
}


function abrirModalEdicionRol(id) {

    const rol = rolesCache.find(function (r) { return String(r.idRol) === String(id); });
    if (!rol) return;

    campoIdRol.value = rol.idRol;
    campoNombreRol.value = rol.nombreRol;
    campoDescripcionRol.value = rol.descripcion || "";

    modalTituloRol.textContent = "Editar rol";
    ocultarErrorRol();
    modalOverlayRol.classList.add("show");

}


function cerrarModalRol() {
    modalOverlayRol.classList.remove("show");
}


function mostrarErrorRol(mensaje) {
    formErrorRol.textContent = mensaje;
    formErrorRol.style.display = "block";
}


function ocultarErrorRol() {
    formErrorRol.style.display = "none";
    formErrorRol.textContent = "";
}


btnNuevoRol.addEventListener("click", abrirModalNuevoRol);
btnCerrarModalRol.addEventListener("click", cerrarModalRol);
btnCancelarModalRol.addEventListener("click", cerrarModalRol);


formRol.addEventListener("submit", async function (evento) {

    evento.preventDefault();
    ocultarErrorRol();

    const dto = {
        nombreRol: campoNombreRol.value.trim(),
        descripcion: campoDescripcionRol.value.trim()
    };

    const id = campoIdRol.value;
    const esEdicion = Boolean(id);

    try {

        const respuesta = await fetch(
            esEdicion ? API_ROLES + "/" + id : API_ROLES,
            {
                method: esEdicion ? "PUT" : "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dto)
            }
        );

        if (!respuesta.ok) {
            const cuerpo = await respuesta.json().catch(function () { return null; });
            throw new Error((cuerpo && cuerpo.error) || "No se pudo guardar el rol");
        }

        cerrarModalRol();
        await cargarRoles();

    } catch (error) {
        mostrarErrorRol(error.message);
    }

});


function eliminarRol(id) {
    mostrarConfirmarEliminarRol(id);
}

async function ejecutarEliminacionRol(id) {

    try {

        const respuesta = await fetch(API_ROLES + "/" + id, { method: "DELETE" });

        if (!respuesta.ok) {
            const cuerpo = await respuesta.json().catch(function () { return null; });
            throw new Error((cuerpo && cuerpo.error) || "No se pudo eliminar el rol");
        }

        await cargarRoles();

    } catch (error) {
        mostrarModalMensaje(error.message);
    }

}


async function cargarUsuarios() {

    try {

        const respuesta = await fetch(API_USUARIOS);

        if (!respuesta.ok) {
            throw new Error("No se pudo cargar la lista de usuarios");
        }

        const usuarios = await respuesta.json();

        pintarUsuarios(usuarios);
        pintarKpisUsuarios(usuarios);

    } catch (error) {
        console.error(error);
        usuariosTbody.innerHTML = '<tr><td colspan="6">Error al cargar los usuarios.</td></tr>';
    }

}


function pintarUsuarios(usuarios) {

    usuariosTbody.innerHTML = "";
    usuariosMensajeVacio.style.display = usuarios.length === 0 ? "block" : "none";
    usuariosCount.textContent = usuarios.length;

    usuarios.forEach(function (u) {

        const fila = document.createElement("tr");
        const estadoTexto = u.estadoUsuario ? "Activo" : "Inactivo";
        const estadoClase = u.estadoUsuario ? "stage-badge is-ganada" : "stage-badge";

        fila.innerHTML =
            "<td>" + escaparHtml(u.nombresUsuario + " " + u.apellidosUsuario) + "</td>" +
            "<td>" + escaparHtml(u.email) + "</td>" +
            "<td>" + escaparHtml(u.dni) + "</td>" +
            "<td>" + escaparHtml(u.rolNombre || "-") + "</td>" +
            "<td><span class=\"" + estadoClase + "\">" + estadoTexto + "</span></td>" +
            "<td>" +
                '<button type="button" class="btn-secundario btn-editar-usuario" data-id="' + u.idUsuario + '">Editar</button> ' +
                '<button type="button" class="btn-secundario btn-eliminar-usuario" data-id="' + u.idUsuario + '">Eliminar</button>' +
            "</td>";

        usuariosTbody.appendChild(fila);

    });

    document.querySelectorAll(".btn-editar-usuario").forEach(function (boton) {
        boton.addEventListener("click", function () {
            abrirModalEdicionUsuario(boton.dataset.id, usuarios);
        });
    });

    document.querySelectorAll(".btn-eliminar-usuario").forEach(function (boton) {
        boton.addEventListener("click", function () {
            eliminarUsuario(boton.dataset.id);
        });
    });

}


function pintarKpisUsuarios(usuarios) {
    kpiUsuariosTotal.textContent = usuarios.length;
    kpiUsuariosActivos.textContent = usuarios.filter(function (u) { return u.estadoUsuario; }).length;
}


async function abrirModalNuevoUsuario() {

    formUsuario.reset();
    campoIdUsuario.value = "";
    modalTituloUsuario.textContent = "Nuevo usuario";
    passwordHint.textContent = "";
    campoPassword.required = true;
    ocultarErrorUsuario();

    if (rolesCache.length === 0) {
        await cargarRoles();
    }

    modalOverlayUsuario.classList.add("show");

}


function abrirModalEdicionUsuario(id, usuarios) {

    const usuario = usuarios.find(function (u) { return String(u.idUsuario) === String(id); });
    if (!usuario) return;

    campoIdUsuario.value = usuario.idUsuario;
    campoNombres.value = usuario.nombresUsuario;
    campoApellidos.value = usuario.apellidosUsuario;
    campoEmail.value = usuario.email;
    campoDni.value = usuario.dni;
    campoRol.value = usuario.idRol;
    campoEstadoUsuario.value = String(usuario.estadoUsuario);

    campoPassword.value = "";
    campoPassword.required = false;
    passwordHint.textContent = "(déjalo vacío para no cambiarla)";

    modalTituloUsuario.textContent = "Editar usuario";
    ocultarErrorUsuario();
    modalOverlayUsuario.classList.add("show");

}


function cerrarModalUsuario() {
    modalOverlayUsuario.classList.remove("show");
}


function mostrarErrorUsuario(mensaje) {
    formErrorUsuario.textContent = mensaje;
    formErrorUsuario.style.display = "block";
}


function ocultarErrorUsuario() {
    formErrorUsuario.style.display = "none";
    formErrorUsuario.textContent = "";
}

function abrirReporteDireccion() {
    window.open(API_REPORTE_DIRECCION, "_blank");
}

btnReporteDireccion.addEventListener("click", abrirReporteDireccion);


btnNuevoUsuario.addEventListener("click", abrirModalNuevoUsuario);
btnCerrarModalUsuario.addEventListener("click", cerrarModalUsuario);
btnCancelarModalUsuario.addEventListener("click", cerrarModalUsuario);


formUsuario.addEventListener("submit", async function (evento) {

    evento.preventDefault();
    ocultarErrorUsuario();

    const dto = {
        idRol: Number(campoRol.value),
        email: campoEmail.value.trim(),
        dni: campoDni.value.trim(),
        nombresUsuario: campoNombres.value.trim(),
        apellidosUsuario: campoApellidos.value.trim(),
        estadoUsuario: campoEstadoUsuario.value === "true"
    };

    if (campoPassword.value) {
        dto.password = campoPassword.value;
    }

    const id = campoIdUsuario.value;
    const esEdicion = Boolean(id);

    try {

        const respuesta = await fetch(
            esEdicion ? API_USUARIOS + "/" + id : API_USUARIOS,
            {
                method: esEdicion ? "PUT" : "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dto)
            }
        );

        if (!respuesta.ok) {
            const cuerpo = await respuesta.json().catch(function () { return null; });
            throw new Error((cuerpo && cuerpo.error) || "No se pudo guardar el usuario");
        }

        cerrarModalUsuario();
        await cargarUsuarios();

    } catch (error) {
        mostrarErrorUsuario(error.message);
    }

});


function eliminarUsuario(id) {
    mostrarConfirmarEliminarUsuario(id);
}


async function ejecutarEliminacionUsuario(id) {

    try {

        const respuesta = await fetch(API_USUARIOS + "/" + id, { method: "DELETE" });

        if (!respuesta.ok) {
            const cuerpo = await respuesta.json().catch(function () { return null; });
            throw new Error((cuerpo && cuerpo.error) || "No se pudo eliminar el usuario");
        }

        await cargarUsuarios();

    } catch (error) {
        mostrarModalMensaje(error.message);
    }

}


(async function iniciar() {
    await cargarRoles();
    await cargarUsuarios();
})();