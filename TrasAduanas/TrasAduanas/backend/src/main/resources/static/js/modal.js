// =========================================================
// MODAL REUTILIZABLE
//
// Los 8 modulos tenian su propio abrirModal/cerrarModal con
// el mismo codigo. Aqui esta una sola vez.
//
// Uso:
//   const modal = crearModal({
//       overlay: "modalOverlay",
//       formulario: "formOportunidad",
//       primerCampo: "campoCliente",
//       abrirCon: ["btnNuevaOportunidad"],
//       cerrarCon: ["btnCerrarModal", "btnCancelarModal"]
//   });
//
//   modal.abrir();
//   modal.cerrar();
// =========================================================

function crearModal(opciones) {

    const overlay = document.getElementById(opciones.overlay);

    const formulario = opciones.formulario
        ? document.getElementById(opciones.formulario)
        : null;


    // Si la pantalla no tiene modal, devolvemos funciones
    // vacias para que el modulo no se rompa.
    if (!overlay) {

        return {
            abrir: function () {},
            cerrar: function () {},
            overlay: null,
            formulario: null
        };

    }


    function abrir() {

        overlay.classList.add("show");

        if (opciones.primerCampo) {

            const campo = document.getElementById(opciones.primerCampo);

            if (campo) {
                campo.focus();
            }

        }

    }


    function cerrar() {

        overlay.classList.remove("show");

        if (formulario) {
            formulario.reset();
        }

    }


    function conectar(ids, accion) {

        (ids || []).forEach(function (id) {

            const boton = document.getElementById(id);

            if (boton) {
                boton.addEventListener("click", accion);
            }

        });

    }


    conectar(opciones.abrirCon, abrir);
    conectar(opciones.cerrarCon, cerrar);


    // Clic en el fondo oscuro
    overlay.addEventListener("click", function (evento) {

        if (evento.target === overlay) {
            cerrar();
        }

    });


    // Tecla Escape
    document.addEventListener("keydown", function (evento) {

        if (evento.key === "Escape" && overlay.classList.contains("show")) {
            cerrar();
        }

    });


    return {
        abrir: abrir,
        cerrar: cerrar,
        overlay: overlay,
        formulario: formulario
    };

}
