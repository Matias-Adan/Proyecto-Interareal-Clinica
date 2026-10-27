const API_URL = 'http://localhost:8080/Proyecto-escuela/api/usuarios';
const CLAVE_USUARIO = 'usuarioLogueado';

function registrarUsuario(nombre, apellido, dni, fechaNacimiento, telefono, email, password, obraSocialId) {
    fetch(API_URL + '/registrar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            nombre: nombre,
            apellido: apellido,
            dni: dni,
            fechaNacimiento: fechaNacimiento,
            telefono: telefono,
            email: email,
            password: password,
            obraSocialId: obraSocialId
        })
    })
        .then(rs => rs.json())
        .then(rs => {
            if (rs) {
                const lista = [];
                lista.push({
                    idUsuario: rs.idUsuario,
                    nombre: rs.nombre,
                    apellido: rs.apellido,
                    dni: rs.dni,
                    fechaNacimiento: rs.fechaNacimiento,
                    telefono: rs.telefono,
                    email: rs.email,
                    obraSocialId: rs.obraSocialId
                });
                sessionStorage.getItem(CLAVE_USUARIO, JSON.stringify(lista));
                alert('El usuario se registró correctamente');
                document.getElementById('formRegister').reset();
            } else {
                alert('Error: no se pudo registrar el usuario (el email o el DNI ya existen)');
            }
        })
        .catch(() => {
            alert('Error: no se pudo conectar con el servidor');
        });
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
    registrarUsuario(
        nombre,
        apellido,
        dni,
        valor('fechaNacimiento') || null,
        valor('telefono') || null,
        email,
        password,
        valor('obraSocialId') || null
    );
});
