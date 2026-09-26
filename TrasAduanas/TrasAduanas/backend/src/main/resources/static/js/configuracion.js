const API_USUARIOS_CONFIG = "/api/usuarios";

function usuarioSesion() {
    try {
        const guardado = sessionStorage.getItem("usuario");
        return guardado ? JSON.parse(guardado) : null;
    } catch (error) {
        return null;
    }
}

const formPerfil = document.getElementById("formPerfil");
const campoNombres = document.getElementById("campoNombres");
const campoApellidos = document.getElementById("campoApellidos");
const campoEmail = document.getElementById("campoEmail");
const campoDni = document.getElementById("campoDni");
const campoRol = document.getElementById("campoRol");
const mensajeErrorPerfil = document.getElementById("mensajeErrorPerfil");
const mensajeExitoPerfil = document.getElementById("mensajeExitoPerfil");

const formPassword = document.getElementById("formPassword");
const campoPasswordNueva = document.getElementById("campoPasswordNueva");
const campoPasswordConfirmar = document.getElementById("campoPasswordConfirmar");
const mensajeErrorPassword = document.getElementById("mensajeErrorPassword");
const mensajeExitoPassword = document.getElementById("mensajeExitoPassword");

let usuarioActualDTO = null;

function mostrarMensaje(elemento, texto) {
    elemento.textContent = texto;
    elemento.style.display = "block";
}

function ocultarMensajes() {
    [mensajeErrorPerfil, mensajeExitoPerfil, mensajeErrorPassword, mensajeExitoPassword].forEach(function (el) {
        el.style.display = "none";
        el.textContent = "";
    });
}

async function cargarPerfil() {

    const sesion = usuarioSesion();

    if (!sesion || !sesion.idUsuario) {
        mostrarMensaje(mensajeErrorPerfil, "No se encontró una sesión activa. Vuelve a iniciar sesión.");
        formPerfil.querySelector("button[type=submit]").disabled = true;
        formPassword.querySelector("button[type=submit]").disabled = true;
        return;
    }

    try {

        const respuesta = await fetch(API_USUARIOS_CONFIG + "/" + sesion.idUsuario);

        if (!respuesta.ok) {
            throw new Error("No se pudo cargar tu perfil");
        }

        usuarioActualDTO = await respuesta.json();

        campoNombres.value = usuarioActualDTO.nombresUsuario;
        campoApellidos.value = usuarioActualDTO.apellidosUsuario;
        campoEmail.value = usuarioActualDTO.email;
        campoDni.value = usuarioActualDTO.dni;
        campoRol.value = usuarioActualDTO.rolNombre || "";

    } catch (error) {
        mostrarMensaje(mensajeErrorPerfil, error.message || "No se pudo cargar tu perfil");
    }

}

formPerfil.addEventListener("submit", async function (evento) {

    evento.preventDefault();
    ocultarMensajes();

    if (!usuarioActualDTO) {
        return;
    }

    const cuerpo = {
        idRol: usuarioActualDTO.idRol,
        email: campoEmail.value.trim(),
        dni: campoDni.value.trim(),
        nombresUsuario: campoNombres.value.trim(),
        apellidosUsuario: campoApellidos.value.trim(),
        estadoUsuario: usuarioActualDTO.estadoUsuario
    };

    try {

        const respuesta = await fetch(API_USUARIOS_CONFIG + "/" + usuarioActualDTO.idUsuario, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(cuerpo)
        });

        if (!respuesta.ok) {
            const texto = await respuesta.text();
            throw new Error(texto || "No se pudo guardar el perfil");
        }

        usuarioActualDTO = await respuesta.json();
        sessionStorage.setItem("usuario", JSON.stringify(usuarioActualDTO));

        mostrarMensaje(mensajeExitoPerfil, "Perfil actualizado correctamente.");

    } catch (error) {
        mostrarMensaje(mensajeErrorPerfil, error.message || "No se pudo guardar el perfil");
    }

});

formPassword.addEventListener("submit", async function (evento) {

    evento.preventDefault();
    ocultarMensajes();

    if (!usuarioActualDTO) {
        return;
    }

    const nueva = campoPasswordNueva.value;
    const confirmar = campoPasswordConfirmar.value;

    if (nueva.length === 0) {
        mostrarMensaje(mensajeErrorPassword, "Escribe la nueva contraseña.");
        return;
    }

    if (nueva.length < 8) {
        mostrarMensaje(mensajeErrorPassword, "La contraseña debe tener al menos 8 caracteres.");
        return;
    }

    if (nueva !== confirmar) {
        mostrarMensaje(mensajeErrorPassword, "Las contraseñas no coinciden.");
        return;
    }

    const cuerpo = {
        idRol: usuarioActualDTO.idRol,
        email: usuarioActualDTO.email,
        dni: usuarioActualDTO.dni,
        nombresUsuario: usuarioActualDTO.nombresUsuario,
        apellidosUsuario: usuarioActualDTO.apellidosUsuario,
        estadoUsuario: usuarioActualDTO.estadoUsuario,
        password: nueva
    };

    try {

        const respuesta = await fetch(API_USUARIOS_CONFIG + "/" + usuarioActualDTO.idUsuario, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(cuerpo)
        });

        if (!respuesta.ok) {
            const texto = await respuesta.text();
            throw new Error(texto || "No se pudo actualizar la contraseña");
        }

        formPassword.reset();
        mostrarMensaje(mensajeExitoPassword, "Contraseña actualizada correctamente.");

    } catch (error) {
        mostrarMensaje(mensajeErrorPassword, error.message || "No se pudo actualizar la contraseña");
    }

});

document.addEventListener("DOMContentLoaded", cargarPerfil);