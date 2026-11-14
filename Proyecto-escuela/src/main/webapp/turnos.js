const API_URL = 'http://localhost:8080/Proyecto-escuela/vista-turnos';
const CLAVE_USUARIO = 'usuarioLogueado';

function loginUsuario(email, password) {
    fetch(API_URL + '/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: email, password: password })
    })
    .then(response => {
        // Si el servidor responde con 401 u otro error, podemos manejarlo
        if (!response.ok) {
            throw new Error('Credenciales inválidas');
        }
        return response.json();
    })
    .then(data => {
        // Tu servlet devuelve un objeto que contiene la propiedad "usuario"
        if (data && data.usuario) {
            // Guardamos los datos del usuario en el sessionStorage
            sessionStorage.setItem(CLAVE_USUARIO, JSON.stringify(data.usuario));
            
            alert('El usuario se logueó correctamente');
            window.location.href = "paciente.html";
        } else {
            alert('Error: email o contraseña incorrectos');
        }
    })
    .catch(error => {
        console.error("Error en el login:", error);
        alert('Error: email o contraseña incorrectos o no se pudo conectar con el servidor');
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