// Base del contexto de la aplicación. Funciona tanto si el WAR se despliega
// como /Proyecto-escuela/... como si se despliega en la raíz del servidor.
const API_BASE = window.location.pathname.replace(/\/[^/]*$/, '');

const CLAVE_USUARIO = 'usuarioLogueado';
const CLAVE_ROL = 'rolUsuario';

// Página de inicio que corresponde a cada rol del backend.
// 1 = Administrador, 2 = Médico, 3 = Enfermero/a, 4 = Recepcionista, 5 = Paciente
const PAGINA_POR_ROL = {
    1: 'admin.html',
    2: 'medico.html',
    3: 'medico.html',
    4: 'admin.html',
    5: 'paciente.html'
};

const PAGINAS_PROTEGIDAS = ['admin.html', 'medico.html', 'paciente.html'];
const PAGINAS_PUBLICAS = ['login.html', 'register.html', 'index.html'];

const ROL_POR_NOMBRE = {
    admin: 1,
    administrador: 1,
    administradora: 1,
    medico: 2,
    'médico': 2,
    medica: 2,
    'médica': 2,
    doctor: 2,
    doctora: 2,
    enfermero: 3,
    enfermera: 3,
    recepcionista: 4,
    paciente: 5
};

function paginaActual() {
    return window.location.pathname.split('/').pop() || 'index.html';
}

function normalizarRol(valor) {
    if (valor === null || valor === undefined || valor === '') {
        return null;
    }

    const numero = Number(valor);
    if (!Number.isNaN(numero)) {
        return numero;
    }

    return ROL_POR_NOMBRE[String(valor).trim().toLowerCase()] || null;
}

/**
 * Autenticador de respaldo: cuando /sesion no puede responder (servidor
 * caído o sin desplegar), se usan los datos que guardó el login — el
 * usuario que devolvió el procedure y el rol calculado por el servidor —
 * para decidir si la cuenta logeada puede estar en esta página y a qué
 * vista hay que mandarla si no le corresponde.
 */
function autenticarConDatosDelLogin(redirigir, pagina) {
    const usuarioGuardado = sessionStorage.getItem(CLAVE_USUARIO);
    const rolGuardado = normalizarRol(sessionStorage.getItem(CLAVE_ROL));

    if (!usuarioGuardado || rolGuardado === null || !PAGINA_POR_ROL[rolGuardado]) {
        // Sin datos del login no hay forma de autenticar la cuenta.
        if (redirigir && PAGINAS_PROTEGIDAS.includes(pagina)) {
            window.location.replace('login.html');
        }
        return null;
    }

    const paginaDelRol = PAGINA_POR_ROL[rolGuardado];

    // Un usuario logueado que entra al login o al registro vuelve a su vista,
    // y si intenta entrar a una vista que no le corresponde, va a la suya.
    if (redirigir && (PAGINAS_PUBLICAS.includes(pagina) || pagina !== paginaDelRol)) {
        window.location.replace(paginaDelRol);
    }

    return {
        autenticado: true,
        usuario: JSON.parse(usuarioGuardado),
        rol: rolGuardado
    };
}

/**
 * Verifica contra el servidor si hay una sesión activa y obtiene el rol.
 *
 * - Si no hay sesión y la página es privada, redirige a login.html.
 * - Si hay sesión pero la página no corresponde al rol, redirige a la vista
 *   que le corresponde a ese rol.
 * - Si hay sesión y está en login/registro, lo manda a su vista.
 *
 * @param {{ redirigir?: boolean }} opciones
 * @returns {Promise<null|{autenticado: boolean, usuario: object, rol: number, roles?: number[]}>}
 */
async function verificarSesion(opciones = {}) {
    const redirigir = opciones.redirigir !== false;
    const pagina = paginaActual();

    try {
        const respuesta = await fetch(`${API_BASE}/sesion`, {
            method: 'GET',
            credentials: 'same-origin',
            cache: 'no-store',
            headers: { 'Accept': 'application/json' }
        });

        if (!respuesta.ok) {
            // El endpoint de sesión no está disponible: los datos que guardó
            // el login quedan como autenticador de la cuenta logeada.
            return autenticarConDatosDelLogin(redirigir, pagina);
        }

        const data = await respuesta.json();

        if (!data || data.autenticado !== true) {
            if (redirigir && PAGINAS_PROTEGIDAS.includes(pagina)) {
                window.location.replace('login.html');
            }
            return null;
        }

        const rol = normalizarRol(
            data.rol ?? data.idRol ?? (data.usuario && data.usuario.idRol) ??
            (Array.isArray(data.roles) ? data.roles[0] : null)
        );
        const paginaDelRol = PAGINA_POR_ROL[rol];

        if (!paginaDelRol) {
            console.warn('El usuario está autenticado pero no tiene un rol válido.', data);
            if (redirigir && PAGINAS_PROTEGIDAS.includes(pagina)) {
                window.location.replace('login.html?error=sin-rol');
            }
            return null;
        }

        // Guardamos los datos para que el resto del front pueda consultarlos.
        if (data.usuario) {
            sessionStorage.setItem(CLAVE_USUARIO, JSON.stringify(data.usuario));
        }
        sessionStorage.setItem(CLAVE_ROL, String(rol));

        // Un usuario logueado que entra al login o al registro vuelve a su vista.
        if (redirigir && PAGINAS_PUBLICAS.includes(pagina)) {
            window.location.replace(paginaDelRol);
            return { ...data, rol };
        }

        // Si intenta entrar a una vista que no le corresponde, lo mandamos a la suya.
        if (redirigir && pagina !== paginaDelRol) {
            window.location.replace(paginaDelRol);
            return { ...data, rol };
        }

        // Controles especiales de administrador (si la vista los tiene).
        if (rol === 1) {
            const linkAdmin = document.getElementById('link-admin');
            if (linkAdmin) {
                linkAdmin.classList.remove('hidden');
            }
        }

        return { ...data, rol };
    } catch (error) {
        console.error('Error al verificar la sesión:', error);
        // Sin conexión con /sesion: autenticamos con los datos del login.
        return autenticarConDatosDelLogin(redirigir, pagina);
    }
}

async function cerrarSesion() {
    try {
        await fetch(`${API_BASE}/sesion/logout`, {
            method: 'POST',
            credentials: 'same-origin'
        });
    } finally {
        sessionStorage.removeItem(CLAVE_USUARIO);
        sessionStorage.removeItem(CLAVE_ROL);
        window.location.replace('login.html');
    }
}

async function borradoLogico(id, servlet) {
    if (!confirm('¿Desea realizar el borrado lógico (desactivar)?')) return;
    await fetch(`${API_BASE}/${servlet}?id=${id}&fisico=false`, { method: 'DELETE' });
    location.reload();
}

async function borradoFisico(id, servlet) {
    if (!confirm('¡ATENCIÓN! Esto eliminará el registro permanentemente de la base de datos.')) return;
    await fetch(`${API_BASE}/${servlet}?id=${id}&fisico=true`, { method: 'DELETE' });
    location.reload();
}

function iniciarPagina() {
    // Los enlaces "Salir" cierran la sesión en el servidor antes de ir al login.
    document.querySelectorAll('a.out').forEach(enlace => {
        enlace.addEventListener('click', evento => {
            evento.preventDefault();
            cerrarSesion();
        });
    });

    // Verificamos la sesión apenas carga la página.
    verificarSesion();

    // El navegador puede restaurar la página desde memoria (botón "atrás")
    // o volver a mostrar una pestaña vieja que quedó abierta. En esos casos
    // NO se vuelve a ejecutar el script, así que la vista podría seguir
    // usándose aunque el rol ya no corresponda. Re-chequeamos:
    window.addEventListener('pageshow', evento => {
        if (evento.persisted) {
            verificarSesion();
        }
    });
    document.addEventListener('visibilitychange', () => {
        if (document.visibilityState === 'visible') {
            verificarSesion();
        }
    });
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', iniciarPagina);
} else {
    iniciarPagina();
}
