// Base de la app calculada desde la URL actual (mismo criterio que app.js).
// Se calcula acá mismo para que el registro no dependa de que el navegador
// tenga una versión actualizada de app.js en el caché.
const BASE_API = window.location.pathname.replace(/\/[^/]*$/, '');

/**
 * Envía el registro al servidor y recién redirige al login cuando el
 * servidor confirma el alta.
 */
async function registrarUsuario(datos) {
    let respuesta;
    try {
        respuesta = await fetch(`${BASE_API}/usuarios`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'same-origin',
            body: JSON.stringify(datos)
        });
    } catch (error) {
        console.error('Error de conexión al registrar:', error);
        alert('Error: no se pudo conectar con el servidor');
        return;
    }

    const cuerpo = await respuesta.json().catch(() => null);

    if (!respuesta.ok) {
        if (respuesta.status === 403) {
            // El servidor rechaza crear usuarios si hay una sesión iniciada
            // que no sea de administrador.
            alert('No se pudo registrar: tenés una sesión iniciada y solo un administrador puede crear usuarios.\nCerrá la sesión con el botón "Salir" y volvé a intentar.');
        } else {
            const mensaje = cuerpo && cuerpo.error
                ? cuerpo.error
                : 'no se pudo registrar el usuario';
            alert('Error: ' + mensaje);
        }
        return;
    }

    alert('El usuario se registró correctamente. Ahora iniciá sesión con tu email y contraseña.');
    document.getElementById('formRegister').reset();
    // Recién con el alta confirmada vamos al login.
    window.location.href = 'login.html';
}

document.getElementById('formRegister').addEventListener('submit', e => {
    e.preventDefault();
    const valor = id => document.getElementById(id).value.trim();
    const nombre = valor('nombre');
    const apellido = valor('apellido');
    const dni = valor('dni');
    const email = valor('email');
    const password = document.getElementById('password').value;
    const password2 = document.getElementById('password2').value;

    if (!nombre || !apellido || !dni || !email || !password) {
        alert('Completá los campos obligatorios');
        return;
    }
    if (!/^\d{7,8}$/.test(dni)) {
        alert('El DNI debe tener 7 u 8 números');
        return;
    }
    if (password !== password2) {
        alert('Las contraseñas no coinciden');
        return;
    }

    registrarUsuario({
        nombre: nombre,
        apellido: apellido,
        dni: dni,
        fechaNacimiento: valor('fechaNacimiento') || null,
        telefono: valor('telefono') || null,
        email: email,
        password: password,
        obraSocialId: valor('obraSocialId') ? Number(valor('obraSocialId')) : null
    });
});
