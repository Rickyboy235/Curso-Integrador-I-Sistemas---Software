// MOSTRAR U OCULTAR CONTRASEÑA //

const botonVerContrasena =
  document.getElementById('boton-ver-contrasena');

const campoContrasena =
  document.getElementById('contrasena');


botonVerContrasena.addEventListener('click', () => {

  const esVisible =
    campoContrasena.type === 'text';


  campoContrasena.type =
    esVisible ? 'password' : 'text';


  botonVerContrasena.classList.toggle(
    'activo',
    !esVisible
  );


  botonVerContrasena.setAttribute(
    'aria-pressed',
    String(!esVisible)
  );


  botonVerContrasena.setAttribute(
    'aria-label',
    esVisible
      ? 'Mostrar contraseña'
      : 'Ocultar contraseña'
  );

});

// ELEMENTOS DEL FORMULARIO //

const formulario =
  document.getElementById('formulario-login');

const correo =
  document.getElementById('correo');

const dni =
  document.getElementById('dni');

const contrasena =
  document.getElementById('contrasena');

const mensajeEstado =
  document.getElementById('mensaje-estado');

// SOLO PERMITIR NÚMEROS EN EL DNI //

dni.addEventListener('input', () => {

  dni.value = dni.value
    .replace(/\D/g, '')
    .slice(0, 8);

});

// MARCAR CAMPO //

function marcarCampo(campo, esValido) {

  const contenedor =
    campo.closest('.campo');


  contenedor.classList.toggle(
    'campo-invalido',
    !esValido
  );

}

// VALIDAR FORMULARIO Y AUTENTICAR CONTRA EL BACKEND //

formulario.addEventListener('submit', async (evento) => {

  evento.preventDefault();

  const correoValido =
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(correo.value.trim());

  const dniValido =
    /^\d{8}$/.test(dni.value.trim());

  const contrasenaValida =
    contrasena.value.length >= 8;

  marcarCampo(correo, correoValido);
  marcarCampo(dni, dniValido);
  marcarCampo(contrasena, contrasenaValida);

  if (!(correoValido && dniValido && contrasenaValida)) {
    mensajeEstado.textContent =
      'Revisa los campos marcados antes de continuar.';
    mensajeEstado.classList.remove('mensaje-exito');
    mensajeEstado.classList.add('mensaje-error');
    return;
  }

  mensajeEstado.textContent = 'Verificando tus credenciales…';
  mensajeEstado.classList.remove('mensaje-error');
  mensajeEstado.classList.add('mensaje-exito');

  try {

    const respuesta = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email: correo.value.trim(),
        dni: dni.value.trim(),
        password: contrasena.value
      })
    });

    if (!respuesta.ok) {
      const mensaje = await respuesta.text();
      throw new Error(mensaje || 'Credenciales inválidas');
    }

    const usuario = await respuesta.json();

    // Guardamos datos básicos de sesión en el navegador //
    sessionStorage.setItem('usuario', JSON.stringify(usuario));

    setTimeout(() => {
      window.location.href = 'index.html';
    }, 700);

  } catch (error) {

    mensajeEstado.textContent = error.message || 'No se pudo iniciar sesión.';
    mensajeEstado.classList.remove('mensaje-exito');
    mensajeEstado.classList.add('mensaje-error');

  }

});