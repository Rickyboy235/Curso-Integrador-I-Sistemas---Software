const API_ORDENES_RESUMEN = "/api/ordenes-servicio";
const API_EXPEDIENTES_RESUMEN = "/api/expedientes-dam";

async function cargarResumenInicio() {

    const elActivos = document.getElementById("statProcesosActivos");
    const elPendientes = document.getElementById("statDocumentosPendientes");
    const elRevision = document.getElementById("statEnRevision");
    const elCompletados = document.getElementById("statProcesosCompletados");

    if (!elActivos || !elPendientes || !elRevision || !elCompletados) {
        return;
    }

    try {

        const [respOrdenes, respExpedientes] = await Promise.all([
            fetch(API_ORDENES_RESUMEN),
            fetch(API_EXPEDIENTES_RESUMEN)
        ]);

        const ordenes = respOrdenes.ok ? await respOrdenes.json() : [];
        const expedientes = respExpedientes.ok ? await respExpedientes.json() : [];

        const activos = ordenes.filter(function (o) {
            return o.estadoServicio !== "finalizada";
        }).length;

        const completados = ordenes.filter(function (o) {
            return o.estadoServicio === "finalizada";
        }).length;

        const pendientes = expedientes.filter(function (e) {
            return e.estadoTramite === "en_proceso";
        }).length;

        const enRevision = expedientes.filter(function (e) {
            return e.estadoTramite === "retenida";
        }).length;

        elActivos.textContent = activos;
        elPendientes.textContent = pendientes;
        elRevision.textContent = enRevision;
        elCompletados.textContent = completados;

    } catch (error) {
        console.warn("No se pudo cargar el resumen del panel principal:", error);
    }

}

document.addEventListener("DOMContentLoaded", cargarResumenInicio);