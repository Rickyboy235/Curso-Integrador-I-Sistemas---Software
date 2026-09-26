# Transflow — Sistema de Gestión Aduanera

Aplicación web para la gestión de los procesos de una agencia de aduanas,
organizada en ocho áreas: Marketing, Ventas, Logística, Almacén, Producción,
Operaciones, Dirección General y Auditoría y control legal.

---

## Estructura

```
TrasAduanas/
├── frontend/
│   ├── html/
│   │   ├── login.html
│   │   ├── index.html          panel principal
│   │   └── <modulo>.html       una por área
│   ├── css/
│   │   ├── base.css            variables de diseño y reset
│   │   ├── layout.css          sidebar, topbar, menú de cuenta
│   │   ├── componentes.css     modales, tablas, formularios, pipeline
│   │   ├── login.css
│   │   └── modulos/            solo lo exclusivo de cada pantalla
│   └── js/
│       ├── layout.js           construye el sidebar y el topbar
│       ├── utils.js            funciones de formato compartidas
│       ├── modal.js            controlador de modales reutilizable
│       ├── login.js
│       └── modulos/            lógica propia de cada pantalla
├── backend/                    (pendiente — Spring Boot)
├── .gitignore
└── README.md
```

---

## Cómo ejecutarlo

Las páginas usan rutas relativas, así que funcionan abriendo
`frontend/html/login.html` directamente en el navegador.

Aun así conviene usar un servidor local, porque es el escenario real:

```bash
cd frontend
python3 -m http.server 5500
```

Luego abrir `http://localhost:5500/html/login.html`.

Con VS Code también sirve la extensión **Live Server**.

---

## Cómo está organizado el CSS

El orden de carga importa y es siempre el mismo:

1. `base.css` — variables de color, tipografía y reset.
2. `layout.css` — la estructura que comparten todas las pantallas.
3. `componentes.css` — piezas repetidas (modal, tabla, formulario, badges).
4. `modulos/<modulo>.css` — lo exclusivo de esa pantalla.

Como el archivo del módulo se carga al final, puede sobrescribir cualquier
componente sin tocar el archivo compartido. Marketing lo hace: tiene su
propia variante del modal y de los campos de formulario, declarada al final
de `css/modulos/marketing.css`.

**Regla:** si un estilo lo van a usar dos pantallas o más, va en
`componentes.css`. Nunca se copia y pega entre módulos.

---

## Cómo está organizado el JavaScript

Cada página carga, en este orden:

```html
<script src="../js/layout.js"></script>
<script src="../js/utils.js"></script>
<script src="../js/modal.js"></script>
<script src="../js/modulos/<modulo>.js"></script>
```

### layout.js

Genera el sidebar y el topbar. Las páginas solo declaran dos contenedores
vacíos:

```html
<div id="layout-sidebar"></div>
<div id="layout-topbar"></div>
```

Para agregar un módulo nuevo al menú basta con añadir una entrada al array
`MENU`. El item activo, el título y el breadcrumb se calculan solos a partir
de la URL.

El topbar admite variantes con atributos `data-`:

```html
<div id="layout-topbar"
     data-clase="dash-topbar"
     data-buscador="Buscar campañas..."
     data-buscador-id="buscadorCampanas"></div>
```

### modal.js

Un solo controlador para todos los modales:

```js
const modal = crearModal({
    overlay: "modalOverlay",
    formulario: "formOportunidad",
    primerCampo: "campoCliente",
    abrirCon: ["btnNuevaOportunidad"],
    cerrarCon: ["btnCerrarModal", "btnCancelarModal"]
});
```

Incluye cierre con clic en el fondo y con la tecla `Escape`.

### utils.js

`obtenerIniciales`, `formatearMonto`, `formatearFecha` y `escaparHtml`.

---

## Estado actual

**Hecho**

- Las nueve pantallas con su maquetación y comportamiento.
- Validación del formulario de acceso.
- Alta de registros y actualización de KPIs en cada módulo.
- Persistencia en `localStorage` en Marketing.

**Pendiente**

- Backend en Spring Boot y base de datos.
- Autenticación real: hoy `login.js` solo valida el formato de los campos y
  redirige. No hay sesión ni control de acceso por página.
- Roles y permisos por área.
- Los datos de las tablas todavía están escritos en el HTML.
- Pantallas de notificaciones y configuración.

---

## Siguientes pasos previstos

1. Crear la base de datos con las tablas `usuario`, `rol` y `area`.
2. Generar el proyecto Spring Boot en `backend/`.
3. Endpoint `POST /api/auth/login` que devuelva un JWT.
4. Conectar `login.js` a ese endpoint.
5. Migrar un módulo completo a la API (Ventas) y luego replicar el patrón.
