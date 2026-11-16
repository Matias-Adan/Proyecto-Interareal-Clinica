// Sección "Mi cuenta" reutilizable.
//
// Funciona igual en la vista de paciente, de médico y de administrador:
// pide los datos a GET /mi-cuenta (el servlet usa el usuario de la sesión)
// y los dibuja dentro de cualquier elemento marcado con data-cuenta.
//
// Uso en una vista:
//   <link rel="stylesheet" href="cuenta.css?v=1">
//   ...
//   <div data-cuenta></div>
//   ...
//   <script src="cuenta.js?v=1"></script>
(function () {
    'use strict';

    const API_BASE = window.location.pathname.replace(/\/[^/]*$/, '');
    const contenedores = document.querySelectorAll('[data-cuenta]');

    if (!contenedores.length) {
        return;
    }

    // ------------------------------------------------------------------
    // Utilidades
    // ------------------------------------------------------------------

    function crear(etiqueta, clase) {
        const elemento = document.createElement(etiqueta);
        if (clase) elemento.className = clase;
        return elemento;
    }

    function fechaHoraLegible(valor) {
        if (!valor) return '—';
        const texto = String(valor);
        const partes = texto.substring(0, 10).split('-');
        if (partes.length !== 3) return texto;
        const fecha = partes[2] + '/' + partes[1] + '/' + partes[0];
        const hora = texto.length >= 16 ? texto.substring(11, 16) : '';
        return hora ? fecha + ' ' + hora : fecha;
    }

    function textoODefecto(valor) {
        return valor == null || String(valor).trim() === '' ? '—' : String(valor);
    }

    function claseRol(rol) {
        const valor = String(rol || '').toLowerCase();
        if (valor.indexOf('administrador') !== -1) return 'tag warn';
        if (valor.indexOf('médico') !== -1 || valor.indexOf('medico') !== -1) return 'tag ok';
        if (valor.indexOf('paciente') !== -1) return 'tag';
        return 'tag';
    }

    // ------------------------------------------------------------------
    // Dibujo de la tarjeta
    // ------------------------------------------------------------------

    function crearDato(etiqueta, valor, nota) {
        const bloque = crear('div', 'cuenta-dato');
        const termino = crear('dt');
        termino.textContent = etiqueta;

        const definicion = crear('dd');
        definicion.textContent = textoODefecto(valor);
        if (nota) {
            const detalle = crear('span', 'cuenta-nota');
            detalle.textContent = nota;
            definicion.appendChild(detalle);
        }

        bloque.appendChild(termino);
        bloque.appendChild(definicion);
        return bloque;
    }

    function crearDatoRol(rol) {
        const bloque = crear('div', 'cuenta-dato');
        const termino = crear('dt');
        termino.textContent = 'Tipo de cuenta';

        const definicion = crear('dd');
        const etiqueta = crear('span', claseRol(rol));
        etiqueta.textContent = textoODefecto(rol);
        definicion.appendChild(etiqueta);

        bloque.appendChild(termino);
        bloque.appendChild(definicion);
        return bloque;
    }

    function pintar(contenedor, cuenta) {
        contenedor.innerHTML = '';

        const panel = crear('section', 'panel');
        const titulo = crear('h2');
        titulo.textContent = 'Datos de la cuenta';
        panel.appendChild(titulo);

        const lista = crear('dl', 'cuenta-datos');

        lista.appendChild(crearDato('Nombre y apellido',
            cuenta.nombreCompleto || ((cuenta.nombre || '') + ' ' + (cuenta.apellido || ''))));
        lista.appendChild(crearDato('Email', cuenta.email));
        lista.appendChild(crearDato('DNI', cuenta.dni));
        lista.appendChild(crearDato('Obra social', cuenta.obraSocial,
            cuenta.numeroConvenio ? 'Convenio: ' + cuenta.numeroConvenio : ''));
        lista.appendChild(crearDato('Teléfono', cuenta.telefono));
        lista.appendChild(crearDatoRol(cuenta.rol));
        lista.appendChild(crearDato('Cuenta creada', fechaHoraLegible(cuenta.fechaAlta)));

        panel.appendChild(lista);
        contenedor.appendChild(panel);
    }

    function mostrarError(contenedor, texto) {
        contenedor.innerHTML = '';

        const panel = crear('section', 'panel');
        const titulo = crear('h2');
        titulo.textContent = 'Datos de la cuenta';

        const mensaje = crear('p', 'mensaje error');
        mensaje.setAttribute('role', 'status');
        mensaje.setAttribute('aria-live', 'polite');
        mensaje.textContent = texto;

        panel.appendChild(titulo);
        panel.appendChild(mensaje);
        contenedor.appendChild(panel);
    }

    // ------------------------------------------------------------------
    // Carga
    // ------------------------------------------------------------------

    async function cargar(contenedor) {
        contenedor.innerHTML = '<p class="cuenta-cargando">Cargando datos de la cuenta...</p>';

        try {
            const respuesta = await fetch(API_BASE + '/mi-cuenta', {
                method: 'GET',
                credentials: 'same-origin',
                cache: 'no-store',
                headers: { 'Accept': 'application/json' }
            });

            let datos = null;
            try {
                datos = await respuesta.json();
            } catch (error) {
                datos = null;
            }

            if (!respuesta.ok) {
                const detalle = datos && datos.error
                    ? datos.error
                    : 'No se pudieron cargar los datos de la cuenta (HTTP '
                        + respuesta.status + ').';
                throw new Error(detalle);
            }

            pintar(contenedor, datos || {});
        } catch (error) {
            console.error('Error al cargar Mi cuenta:', error);
            mostrarError(contenedor,
                error.message || 'No se pudieron cargar los datos de la cuenta.');
        }
    }

    contenedores.forEach(cargar);
})();
