// Base de la app calculada desde la URL actual (mismo criterio que app.js),
// para que el login no dependa del caché del navegador.
const BASE_API = window.location.pathname.replace(/\/[^/]*$/, '');

// Mismas claves que usa app.js, para que verificarSesion() encuentre los datos.
const CLAVE_USUARIO_LOGIN = 'usuarioLogueado';
const CLAVE_ROL_LOGIN = 'rolUsuario';

// Página de inicio por rol (mismo mapa que app.js). Se usa como respaldo si
// verificarSesion no está disponible (por ejemplo, un app.js viejo en caché).
const PAGINA_POR_ROL_LOGIN = {
    1: 'admin.html',
    2: 'medico.html',
    3: 'medico.html',
    4: 'admin.html',
    5: 'paciente.html'
};

/**
 * Inicia sesión y guarda los datos que devuelve el servidor: el usuario
 * que trajo el procedure de login más el rol que le corresponde. Esos
 * datos quedan guardados como autenticador de la cuenta logeada.
 */
async function loginUsuario(email, password) {
    let respuesta;
    try {
        respuesta = await fetch(`${BASE_API}/usuarios/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'same-origin',
            body: JSON.stringify({ email, password })
        });
    } catch (error) {
        console.error('Error en el login:', error);
        alert('Error: no se pudo conectar con el servidor');
        return;
    }

    const data = await respuesta.json().catch(() => null);

    if (!respuesta.ok || !data || !data.usuario) {
        const mensaje = data && data.error ? data.error : 'email o contraseña incorrectos';
        alert('Error: ' + mensaje);
        return;
    }

    // Guardamos los datos recibidos del procedure de login. verificarSesion
    // los usa para autenticar la cuenta en cada página (y como respaldo
    // cuando /sesion no puede responder). El hash de la contraseña no se
    // guarda: es un dato que no debe llegar al navegador.
    const usuarioGuardado = { ...data.usuario };
    delete usuarioGuardado.password;
    sessionStorage.setItem(CLAVE_USUARIO_LOGIN, JSON.stringify(usuarioGuardado));
    if (data.rol) {
        sessionStorage.setItem(CLAVE_ROL_LOGIN, String(data.rol));
    }

    // verificarSesion (de app.js) consulta el rol al servidor y redirige a
    // la vista que corresponde. Si no está disponible o no logra responder,
    // el rol que vino en el login decide a qué vista entrar.
    if (typeof verificarSesion === 'function') {
        const sesion = await verificarSesion();
        if (sesion) {
            return;
        }
    }

    window.location.href = PAGINA_POR_ROL_LOGIN[data.rol] || 'index.html';
}

document.getElementById('formLogin').addEventListener('submit', e => {
    e.preventDefault();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
	
    if (!email || !password) {
        alert('Completá todos los campos');
        return;
    }

    loginUsuario(email, password);
});
