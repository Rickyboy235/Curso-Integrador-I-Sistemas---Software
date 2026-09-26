const MENU = [
    { archivo: "index.html", icono: "⌂", texto: "Inicio", titulo: "Panel principal" },
    { archivo: "marketing.html", icono: "01", texto: "Marketing" },
    { archivo: "ventas.html", icono: "02", texto: "Ventas" },
    { archivo: "logistica.html", icono: "03", texto: "Logística" },
    { archivo: "almacen.html", icono: "04", texto: "Almacén" },
    { archivo: "produccion.html", icono: "05", texto: "Producción" },
    { archivo: "operaciones.html", icono: "06", texto: "Operaciones" },
    { archivo: "direccion.html", icono: "07", texto: "Dirección General" },
    { archivo: "auditoria.html", icono: "08", texto: "Auditoría y control legal" }
];

const PAGINAS_PUBLICAS = ["login.html"];

function paginaActual() {

    const ruta = window.location.pathname.split("/").pop();

    return ruta === "" ? "index.html" : ruta;

}

const TITULOS_EXTRA = {
    "configuracion.html": { texto: "Configuración", titulo: "Configuración de la cuenta" }
};

function itemActual() {

    const actual = paginaActual();

    return MENU.find(function (item) {
        return item.archivo === actual;
    }) || TITULOS_EXTRA[actual];

}

// SESION REAL (sin usuario inventado de respaldo) //

function usuarioActual() {

    try {

        const guardado = sessionStorage.getItem("usuario");

        if (!guardado) {
            return null;
        }

        const usuario = JSON.parse(guardado);

        if (!usuario || !usuario.idUsuario) {
            return null;
        }

        return usuario;

    } catch (error) {
        console.warn("No se pudo leer el usuario en sesión:", error);
        return null;
    }

}


function iniciales(usuario) {

    const inicialNombre = (usuario.nombresUsuario || "").trim().charAt(0);
    const inicialApellido = (usuario.apellidosUsuario || "").trim().charAt(0);

    const resultado = (inicialNombre + inicialApellido).toUpperCase();

    return resultado || "??";

}

function protegerPagina() {

    const actual = paginaActual();

    if (PAGINAS_PUBLICAS.includes(actual)) {
        return true;
    }

    if (!usuarioActual()) {
        window.location.href = "login.html";
        return false;
    }

    return true;

}

function construirSidebarConfiguracion() {

    return ''
        + '<aside class="sidebar">'

        + '<div class="logo-container">'
        + '<div class="logo-icon">TF</div>'
        + '<div class="logo-text">'
        + '<h1>Transflow</h1>'
        + '<span>Gestión Aduanera</span>'
        + '</div>'
        + '</div>'

        + '<button class="menu-item" id="btnVolverConfiguracion" type="button" style="width:100%; border:none; cursor:pointer;">'
        + '<span class="menu-icon">←</span>'
        + '<span>Volver</span>'
        + '</button>'

        + '</aside>';

}

function construirSidebar() {

    const actual = paginaActual();

        if (actual === "configuracion.html") {
        return construirSidebarConfiguracion();
    }

    const items = MENU.map(function (item) {

        const activo = item.archivo === actual ? " active" : "";

        return ''
            + '<a href="' + item.archivo + '" class="menu-item' + activo + '">'
            + '<span class="menu-icon">' + item.icono + '</span>'
            + '<span>' + item.texto + '</span>'
            + '</a>';

    }).join("");

    return ''
        + '<aside class="sidebar">'

        + '<div class="logo-container">'
        + '<div class="logo-icon">TF</div>'
        + '<div class="logo-text">'
        + '<h1>Transflow</h1>'
        + '<span>Gestión Aduanera</span>'
        + '</div>'
        + '</div>'

        + '<nav class="menu">'
        + '<p class="menu-title">PROCESOS</p>'
        + items
        + '</nav>'

        + '</aside>';

}

function bloqueCuenta() {

    const usuario = usuarioActual();

    if (!usuario) {
        return '<div class="topbar-right"></div>';
    }

    const nombreCompleto = (usuario.nombresUsuario || "") + " " + (usuario.apellidosUsuario || "");
    const rol = usuario.rolNombre || "Sin rol";
    const sigla = iniciales(usuario);

    return ''
        + '<div class="topbar-right">'

        + '<div class="account" id="account">'

        + '<div class="account-avatar">' + sigla + '</div>'

        + '<div class="account-info">'
        + '<strong>' + nombreCompleto.trim() + '</strong>'
        + '<span>' + rol + '</span>'
        + '</div>'

        + '<span class="account-arrow">⌄</span>'

        + '<div class="account-menu" id="accountMenu">'

        + '<div class="account-menu-header">'
        + '<div class="account-avatar large">' + sigla + '</div>'
        + '<div>'
        + '<strong>' + nombreCompleto.trim() + '</strong>'
        + '<span>' + rol + '</span>'
        + '</div>'
        + '</div>'

        + '<hr>'

        + '<a href="configuracion.html">Mi perfil</a>'

        + '<hr>'

        + '<button id="cerrarSesionMenu">Cerrar sesión</button>'

        + '</div>'

        + '</div>'

        + '</div>';

}


function construirTopbar(contenedor) {

    const item = itemActual();

    const titulo = item
        ? (item.titulo || item.texto)
        : "Panel principal";

    const claseExtra = contenedor.dataset.clase
        ? " " + contenedor.dataset.clase
        : "";

    let izquierda;

    if (contenedor.dataset.buscador) {

        izquierda = ''
            + '<div class="topbar-search">'
            + '<span class="search-icon">🔍</span>'
            + '<input type="text" id="'
            + (contenedor.dataset.buscadorId || "buscador")
            + '" placeholder="' + contenedor.dataset.buscador + '">'
            + '</div>';

    } else {

        izquierda = ''
            + '<div class="page-title">'
            + '<span class="breadcrumb">Transflow / ' + titulo + '</span>'
            + '<h2>' + titulo + '</h2>'
            + '</div>';

    }

    return ''
        + '<header class="topbar' + claseExtra + '">'
        + izquierda
        + bloqueCuenta()
        + '</header>';

}

function activarMenuCuenta() {

    const account = document.getElementById("account");
    const accountMenu = document.getElementById("accountMenu");

    if (!account || !accountMenu) {
        return;
    }

    account.addEventListener("click", function () {
        accountMenu.classList.toggle("show");
    });

    document.addEventListener("click", function (evento) {

        if (!account.contains(evento.target)) {
            accountMenu.classList.remove("show");
        }

    });

}


function activarCerrarSesion() {

    function salir() {

        try {
            sessionStorage.removeItem("usuario");
            sessionStorage.removeItem("token");
        } catch (error) {
            console.warn("No se pudo limpiar la sesión:", error);
        }

        window.location.href = "login.html";

    }

        ["cerrarSesionMenu"].forEach(function (id) {

        const boton = document.getElementById(id);

        if (boton) {
            boton.addEventListener("click", salir);
        }

    });

}

function iniciarLayout() {

    if (!protegerPagina()) {
        return;
    }

    const contenedorSidebar = document.getElementById("layout-sidebar");
    const contenedorTopbar = document.getElementById("layout-topbar");

    if (contenedorSidebar) {
        contenedorSidebar.outerHTML = construirSidebar();
    }

    if (contenedorTopbar) {
        contenedorTopbar.outerHTML = construirTopbar(contenedorTopbar);
    }

    const item = itemActual();

    if (item) {
        document.title = "Transflow | " + (item.titulo || item.texto);
    }

    activarMenuCuenta();
    activarCerrarSesion();
    activarBotonVolver();

}


function activarBotonVolver() {

    const boton = document.getElementById("btnVolverConfiguracion");

    if (!boton) {
        return;
    }

    boton.addEventListener("click", function () {

        if (window.history.length > 1) {
            window.history.back();
        } else {
            window.location.href = "index.html";
        }

    });

}


if (document.getElementById("layout-sidebar")) {
    iniciarLayout();
} else {
    document.addEventListener("DOMContentLoaded", iniciarLayout);
}