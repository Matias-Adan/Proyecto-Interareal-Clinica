// Vista del profesional.
// Carga por id de usuario médico sus turnos asignados y los pacientes que
// siguen en triage desde GET /vista-medico?medico_id=...
// y los dibuja en las tablas de la sección "Agenda y pacientes".
(function () {
    'use strict';

    const API_BASE = window.location.pathname.replace(/\/[^/]*$/, '');
    const CLAVE_USUARIO = 'usuarioLogueado';

    const formFiltro = document.getElementById('formFiltroTurnos');
    const filtroFecha = document.getElementById('filtroFecha');
    const filtroEstado = document.getElementById('filtroEstado');
    const cuerpoTurnos = document.getElementById('tablaTurnosMedico');
    const mensajeTurnos = document.getElementById('turnosMedicoMensaje');
    const cuerpoTriage = document.getElementById('tablaTriageMedico');
    const mensajeTriage = document.getElementById('triageMedicoMensaje');

    let usuarioActual = null;
    let turnosCargados = [];

    // ------------------------------------------------------------------
    // Utilidades
    // ------------------------------------------------------------------

    function mostrarMensaje(elemento, texto, tipo) {
        if (!elemento) return;
        elemento.textContent = texto || '';
        elemento.classList.remove('error', 'ok');
        if (tipo) elemento.classList.add(tipo);
    }

    function fechaLegible(fecha) {
        if (!fecha) return '—';
        const soloFecha = String(fecha).substring(0, 10);
        const partes = soloFecha.split('-');
        if (partes.length !== 3) return soloFecha;
        return partes[2] + '/' + partes[1] + '/' + partes[0];
    }

    function fechaHoraLegible(valor) {
        if (!valor) return '—';
        const texto = String(valor);
        const fecha = fechaLegible(texto);
        const hora = texto.length >= 16 ? texto.substring(11, 16) : '';
        return hora ? fecha + ' ' + hora : fecha;
    }

    function horaLegible(hora) {
        if (!hora) return '—';
        return String(hora).substring(0, 5);
    }

    function rangoHorario(inicio, fin) {
        const desde = horaLegible(inicio);
        const hasta = horaLegible(fin);
        return hasta === '—' ? desde : desde + ' – ' + hasta;
    }

    function textoODefecto(valor) {
        return valor == null || String(valor).trim() === '' ? '—' : String(valor);
    }

    function claseEstado(estado) {
        const valor = String(estado || '').toLowerCase();
        if (valor === 'confirmado' || valor === 'atendido') return 'tag ok';
        if (valor === 'pendiente') return 'tag warn';
        if (valor === 'cancelado' || valor === 'anulado' || valor === 'ausente') return 'tag alert';
        return 'tag';
    }

    function claseEstadoTriage(estado) {
        const valor = String(estado || '').toLowerCase();
        if (valor.indexOf('atend') !== -1 || valor.indexOf('alta') !== -1
                || valor.indexOf('final') !== -1 || valor.indexOf('egres') !== -1
                || valor.indexOf('resuel') !== -1 || valor.indexOf('deriv') !== -1) {
            return 'tag ok';
        }
        return 'tag warn';
    }

    function prioridadLegible(prioridad) {
        if (prioridad == null || String(prioridad).trim() === '') return '—';
        const numero = Number(prioridad);
        if (Number.isNaN(numero)) return textoODefecto(prioridad);

        switch (numero) {
            case 1: return '1 · Crítica';
            case 2: return '2 · Alta';
            case 3: return '3 · Media';
            case 4: return '4 · Baja';
            case 5: return '5 · No urgente';
            default: return String(prioridad);
        }
    }

    function clasePrioridad(prioridad) {
        if (prioridad == null || String(prioridad).trim() === '') return 'tag';
        const numero = Number(prioridad);
        if (numero <= 2) return 'tag alert';
        if (numero === 3) return 'tag warn';
        if (numero >= 4) return 'tag ok';
        return 'tag';
    }

    function fechaHoyISO() {
        const hoy = new Date();
        const mes = String(hoy.getMonth() + 1).padStart(2, '0');
        const dia = String(hoy.getDate()).padStart(2, '0');
        return hoy.getFullYear() + '-' + mes + '-' + dia;
    }

    function crearCelda(texto) {
        const celda = document.createElement('td');
        celda.textContent = textoODefecto(texto);
        return celda;
    }

    function crearCeldaEtiqueta(texto, clase) {
        const celda = document.createElement('td');
        const etiqueta = document.createElement('span');
        etiqueta.className = clase;
        etiqueta.textContent = textoODefecto(texto);
        celda.appendChild(etiqueta);
        return celda;
    }

    function mensajeVacio(cuerpo, columnas, texto) {
        cuerpo.innerHTML = '';
        const fila = document.createElement('tr');
        const celda = document.createElement('td');
        celda.colSpan = columnas;
        celda.textContent = texto;
        fila.appendChild(celda);
        cuerpo.appendChild(fila);
    }

    async function leerJson(respuesta) {
        try {
            return await respuesta.json();
        } catch (error) {
            return null;
        }
    }

    // ------------------------------------------------------------------
    // Sesión del profesional
    // ------------------------------------------------------------------

    async function obtenerUsuario() {
        try {
            const respuesta = await fetch(API_BASE + '/sesion', {
                method: 'GET',
                credentials: 'same-origin',
                cache: 'no-store',
                headers: { 'Accept': 'application/json' }
            });

            if (respuesta.ok) {
                const datos = await leerJson(respuesta);
                if (datos && datos.autenticado && datos.usuario) {
                    return datos.usuario;
                }
            }
        } catch (error) {
            console.warn('No se pudo consultar /sesion:', error);
        }

        // Respaldo: los datos que guardó el login.
        try {
            const guardado = sessionStorage.getItem(CLAVE_USUARIO);
            return guardado ? JSON.parse(guardado) : null;
        } catch (error) {
            return null;
        }
    }

    // ------------------------------------------------------------------
    // Turnos del médico
    // ------------------------------------------------------------------

    function ordenarTurnos(turnos) {
        const hoy = fechaHoyISO();

        return turnos.slice().sort((a, b) => {
            const fechaA = String(a.fecha || '').substring(0, 10);
            const fechaB = String(b.fecha || '').substring(0, 10);
            const claveA = fechaA + ' ' + String(a.horaInicio || '');
            const claveB = fechaB + ' ' + String(b.horaInicio || '');
            const futuroA = fechaA >= hoy;
            const futuroB = fechaB >= hoy;

            // Primero los turnos de hoy en adelante (más próximo arriba)
            // y después los pasados (más reciente arriba).
            if (futuroA !== futuroB) {
                return futuroA ? -1 : 1;
            }
            return futuroA
                ? claveA.localeCompare(claveB)
                : claveB.localeCompare(claveA);
        });
    }

    function pintarTurnos(turnos, filtrado) {
        cuerpoTurnos.innerHTML = '';

        if (!Array.isArray(turnos) || !turnos.length) {
            mensajeVacio(cuerpoTurnos, 7, filtrado
                ? 'No hay turnos que coincidan con el filtro.'
                : 'No tenés turnos asignados.');
            return;
        }

        ordenarTurnos(turnos).forEach(turno => {
            const fila = document.createElement('tr');

            fila.appendChild(crearCelda(fechaLegible(turno.fecha)));
            fila.appendChild(crearCelda(rangoHorario(turno.horaInicio, turno.horaFin)));
            fila.appendChild(crearCelda(turno.pacienteNombre));
            fila.appendChild(crearCelda(turno.pacienteDni));
            fila.appendChild(crearCelda(turno.consultorioNumero));
            fila.appendChild(crearCelda(turno.motivo));
            fila.appendChild(crearCeldaEtiqueta(turno.estadoTurno,
                claseEstado(turno.estadoTurno)));

            cuerpoTurnos.appendChild(fila);
        });
    }

    function aplicarFiltro() {
        const fecha = filtroFecha ? filtroFecha.value : '';
        const estado = filtroEstado ? filtroEstado.value : '';

        let filtrados = turnosCargados;

        if (fecha) {
            filtrados = filtrados.filter(turno =>
                String(turno.fecha || '').substring(0, 10) === fecha);
        }
        if (estado) {
            filtrados = filtrados.filter(turno =>
                String(turno.estadoTurno || '') === estado);
        }

        pintarTurnos(filtrados, Boolean(fecha) || Boolean(estado));
    }

    // ------------------------------------------------------------------
    // Pacientes en triage
    // ------------------------------------------------------------------

    function pintarTriage(triage) {
        cuerpoTriage.innerHTML = '';

        if (!Array.isArray(triage) || !triage.length) {
            mensajeVacio(cuerpoTriage, 8,
                'No hay pacientes pendientes en el triage.');
            return;
        }

        triage.forEach(paciente => {
            const fila = document.createElement('tr');

            fila.appendChild(crearCelda(fechaHoraLegible(paciente.fecha)));
            fila.appendChild(crearCelda(paciente.pacienteNombre));
            fila.appendChild(crearCelda(paciente.pacienteDni));
            fila.appendChild(crearCeldaEtiqueta(prioridadLegible(paciente.prioridad),
                clasePrioridad(paciente.prioridad)));
            fila.appendChild(crearCelda(paciente.consultorioNumero));
            fila.appendChild(crearCelda(paciente.enfermeroNombre));
            fila.appendChild(crearCeldaEtiqueta(paciente.estado,
                claseEstadoTriage(paciente.estado)));
            fila.appendChild(crearCelda(paciente.observacion));

            cuerpoTriage.appendChild(fila);
        });
    }

    // ------------------------------------------------------------------
    // Carga
    // ------------------------------------------------------------------

    async function cargarVista() {
        const medicoId = usuarioActual ? usuarioActual.idUsuario : null;

        if (!medicoId) {
            mostrarMensaje(mensajeTurnos,
                'No se pudo obtener el id del profesional logeado.', 'error');
            mostrarMensaje(mensajeTriage,
                'No se pudo obtener el id del profesional logeado.', 'error');
            mensajeVacio(cuerpoTurnos, 7, 'Sin datos.');
            mensajeVacio(cuerpoTriage, 8, 'Sin datos.');
            return;
        }

        mostrarMensaje(mensajeTurnos, 'Cargando turnos...', null);
        mostrarMensaje(mensajeTriage, 'Cargando pacientes...', null);

        try {
            const respuesta = await fetch(
                API_BASE + '/vista-medico?medico_id=' + encodeURIComponent(medicoId),
                {
                    credentials: 'same-origin',
                    cache: 'no-store',
                    headers: { 'Accept': 'application/json' }
                }
            );

            const datos = await leerJson(respuesta);

            if (!respuesta.ok) {
                const detalle = datos && datos.error
                    ? datos.error
                    : 'No se pudo cargar la agenda (HTTP ' + respuesta.status + ').';
                throw new Error(detalle);
            }

            turnosCargados = Array.isArray(datos && datos.turnos) ? datos.turnos : [];
            aplicarFiltro();
            pintarTriage(datos && datos.triage);

            mostrarMensaje(mensajeTurnos, '', null);
            mostrarMensaje(mensajeTriage, '', null);
        } catch (error) {
            console.error('Error al cargar la agenda del médico:', error);
            const texto = error.message || 'No se pudo cargar la agenda.';
            mostrarMensaje(mensajeTurnos, texto, 'error');
            mostrarMensaje(mensajeTriage, texto, 'error');
            mensajeVacio(cuerpoTurnos, 7, 'No se pudieron cargar los turnos.');
            mensajeVacio(cuerpoTriage, 8, 'No se pudieron cargar los pacientes.');
        }
    }

    // ------------------------------------------------------------------
    // Inicio
    // ------------------------------------------------------------------

    async function iniciar() {
        if (!cuerpoTurnos || !cuerpoTriage) return;

        if (formFiltro) {
            formFiltro.addEventListener('submit', evento => {
                evento.preventDefault();
                aplicarFiltro();
            });
        }

        usuarioActual = await obtenerUsuario();

        if (!usuarioActual) {
            mostrarMensaje(mensajeTurnos,
                'Iniciá sesión para ver tu agenda.', 'error');
            mostrarMensaje(mensajeTriage,
                'Iniciá sesión para ver los pacientes del triage.', 'error');
            mensajeVacio(cuerpoTurnos, 7, 'Sesión no iniciada.');
            mensajeVacio(cuerpoTriage, 8, 'Sesión no iniciada.');
            return;
        }

        await cargarVista();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', iniciar);
    } else {
        iniciar();
    }
})();
