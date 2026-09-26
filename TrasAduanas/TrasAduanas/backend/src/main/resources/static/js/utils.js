// =========================================================
// UTILIDADES COMPARTIDAS
// Funciones de formato que antes estaban repetidas en varios
// modulos. Se cargan en todas las pantallas.
// =========================================================


// ---------------------------------------------------------
// INICIALES DE UN NOMBRE
// "Michael Kevin Torres" -> "MK"
// ---------------------------------------------------------

function obtenerIniciales(nombre) {

    const partes = String(nombre || "").trim().split(/\s+/);

    const iniciales = partes
        .slice(0, 2)
        .map(function (parte) {
            return parte.charAt(0).toUpperCase();
        })
        .join("");

    return iniciales || "?";

}


// ---------------------------------------------------------
// MONTO EN DOLARES
// 15000 -> "US$ 15,000"
// ---------------------------------------------------------

function formatearMonto(monto) {

    const numero = Number(monto) || 0;

    return "US$ " + numero.toLocaleString("en-US");

}


// ---------------------------------------------------------
// FECHA DE INPUT date A FORMATO LOCAL
// "2026-03-14" -> "14/03/2026"
// ---------------------------------------------------------

function formatearFecha(valorInput) {

    if (!valorInput) {
        return "-";
    }

    const partes = String(valorInput).split("-");

    if (partes.length !== 3) {
        return valorInput;
    }

    return partes[2] + "/" + partes[1] + "/" + partes[0];

}


// ---------------------------------------------------------
// ESCAPAR TEXTO ANTES DE INSERTARLO EN EL DOM
// Evita que lo que escribe el usuario en un formulario se
// interprete como HTML.
// ---------------------------------------------------------

function escaparHtml(texto) {

    const div = document.createElement("div");

    div.textContent = String(texto == null ? "" : texto);

    return div.innerHTML;

}
