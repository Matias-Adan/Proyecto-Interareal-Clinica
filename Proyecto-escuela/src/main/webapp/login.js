const API_URL = 'http://localhost:8080/Proyecto-escuela/api/usuarios';
const CLAVE_USUARIO = 'usuarioLogueado';

function loginUsuario(email, password) {
    fetch(API_URL + '/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: email, password: password })
    })
        .then(rs => rs.json())
        .then(rs => {
            if (rs) {
                if (rs.password !== password) {
                    alert('La contraseña es incorrecta');
                    return;
                }
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
                alert('El usuario se logueó correctamente');
            } else {
                alert('Error: email o contraseña incorrectos');
            }
        })
        .catch(() => {
            alert('Error: no se pudo conectar con el servidor');
        });
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
