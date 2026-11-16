// Vista del paciente.
// Lista sus turnos desde v_agenda_turnos_medicos (GET /vista-turnos?paciente_dni=...),
// permite pedir turnos nuevos con POST /turnos y carga la sección "Mi salud"
// desde GET /mi-salud (alergias y recetas).
(function () {
    'use strict';

    const API_BASE = window.location.pathname.replace(/\/[^/]*$/, '');
    const CLAVE_USUARIO = 'usuarioLogueado';

    const form = document.getElementById('formTurno');
    const panelNuevo = document.getElementById('panelNuevoTurno');
    const selectMedico = document.getElementById('turnoMedico');
    const inputFecha = document.getElementById('turnoFecha');
    const inputHora = document.getElementById('turnoHora');
    const selectDuracion = document.getElementById('turnoDuracion');
    const inputMotivo = document.getElementById('turnoMotivo');
    const botonPedir = document.getElementById('btnPedirTurno');
    const mensajeForm = document.getElementById('turnoMensaje');
    const mensajeTabla = document.getElementById('turnosMensaje');
    const cuerpoTabla = document.getElementById('tablaTurnos');

    const cuerpoAlergias = document.getElementById('tablaAlergias');
    const mensajeAlergias = document.getElementById('alergiasMensaje');
    const cuerpoRecetas = document.getElementById('tablaRecetas');
    const mensajeRecetas = document.getElementById('recetasMensaje');

    let usuarioActual = null;

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

    function horaLegible(hora) {
        if (!hora) return '—';
        return String(hora).substring(0, 5);
    }

    function claseEstado(estado) {
        const valor = String(estado || '').toLowerCase();
        if (valor === 'confirmado' || valor === 'atendido') return 'tag ok';
        if (valor === 'pendiente') return 'tag warn';
        if (valor === 'cancelado' || valor === 'anulado' || valor === 'ausente') return 'tag alert';
        return 'tag';
    }

    function sumarMinutos(hora, minutos) {
        const partes = String(hora).split(':');
        const horas = Number(partes[0]);
        const mins = Number(partes[1]);
        if (Number.isNaN(horas) || Number.isNaN(mins)) return null;

        const total = horas * 60 + mins + minutos;
        if (total >= 24 * 60) return null;

        const hh = String(Math.floor(total / 60)).padStart(2, '0');
        const mm = String(total % 60).padStart(2, '0');
        return hh + ':' + mm;
    }

    function fechaHoyISO() {
        const hoy = new Date();
        const mes = String(hoy.getMonth() + 1).padStart(2, '0');
        const dia = String(hoy.getDate()).padStart(2, '0');
        return hoy.getFullYear() + '-' + mes + '-' + dia;
    }

    async function leerJson(respuesta) {
        try {
            return await respuesta.json();
        } catch (error) {
            return null;
        }
    }

    // ------------------------------------------------------------------
    // Sesión del paciente
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
    // Profesionales (usuarios con rol Médico = 2)
    // ------------------------------------------------------------------

    async function cargarMedicos() {
        selectMedico.disabled = true;
        botonPedir.disabled = true;
        selectMedico.innerHTML = '<option value="">Cargando profesionales...</option>';

        try {
            const respuesta = await fetch(API_BASE + '/medicos', {
                credentials: 'same-origin',
                cache: 'no-store',
                headers: { 'Accept': 'application/json' }
            });

            const datos = await leerJson(respuesta);

            if (!respuesta.ok) {
                const detalle = datos && datos.error
                    ? datos.error
                    : 'No se pudo cargar la lista de profesionales (HTTP ' + respuesta.status + ').';
                throw new Error(detalle);
            }

            const medicos = (Array.isArray(datos) ? datos : [])
                .sort((a, b) => {
                    const apellidoA = String(a.apellido || '').toLowerCase();
                    const apellidoB = String(b.apellido || '').toLowerCase();
                    return apellidoA.localeCompare(apellidoB);
                });

            selectMedico.innerHTML = '';

            if (!medicos.length) {
                const opcion = document.createElement('option');
                opcion.value = '';
                opcion.textContent = 'No hay profesionales disponibles';
                selectMedico.appendChild(opcion);
                selectMedico.disabled = true;
                botonPedir.disabled = true;
                mostrarMensaje(mensajeForm,
                    'No hay médicos cargados en el sistema. Contactá a recepción.', 'error');
                return;
            }

            const inicial = document.createElement('option');
            inicial.value = '';
            inicial.textContent = 'Seleccioná un profesional';
            selectMedico.appendChild(inicial);

            medicos.forEach(medico => {
                const opcion = document.createElement('option');
                opcion.value = medico.idUsuario;
                opcion.textContent = (medico.apellido || '') + ', ' + (medico.nombre || '');
                selectMedico.appendChild(opcion);
            });

            selectMedico.disabled = false;
            botonPedir.disabled = false;
        } catch (error) {
            console.error('Error al cargar profesionales:', error);
            selectMedico.innerHTML = '';
            const opcion = document.createElement('option');
            opcion.value = '';
            opcion.textContent = 'Error al cargar los profesionales';
            selectMedico.appendChild(opcion);
            selectMedico.disabled = true;
            botonPedir.disabled = true;
            mostrarMensaje(mensajeForm,
                (error.message || 'No se pudieron cargar los profesionales.')
                + ' Recargá la página para reintentar.', 'error');
        }
    }

    // ------------------------------------------------------------------
    // Listado de turnos del paciente
    // ------------------------------------------------------------------

    function crearCelda(texto) {
        const celda = document.createElement('td');
        celda.textContent = texto == null || texto === '' ? '—' : String(texto);
        return celda;
    }

    function pintarTurnos(turnos) {
        cuerpoTabla.innerHTML = '';

        if (!Array.isArray(turnos) || !turnos.length) {
            const fila = document.createElement('tr');
            const celda = document.createElement('td');
            celda.colSpan = 6;
            celda.textContent = 'Todavía no tenés turnos programados.';
            fila.appendChild(celda);
            cuerpoTabla.appendChild(fila);
            return;
        }

        const hoy = fechaHoyISO();

        turnos
            .slice()
            .sort((a, b) => {
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
            })
            .forEach(turno => {
                const fila = document.createElement('tr');

                fila.appendChild(crearCelda(fechaLegible(turno.fecha)));
                fila.appendChild(crearCelda(horaLegible(turno.horaInicio)));
                fila.appendChild(crearCelda(turno.medicoNombre));
                fila.appendChild(crearCelda(turno.consultorioNumero));
                fila.appendChild(crearCelda(turno.motivo));

                const celdaEstado = document.createElement('td');
                const etiqueta = document.createElement('span');
                etiqueta.className = claseEstado(turno.estadoTurno);
                etiqueta.textContent = turno.estadoTurno || 'Sin estado';
                celdaEstado.appendChild(etiqueta);
                fila.appendChild(celdaEstado);

                cuerpoTabla.appendChild(fila);
            });
    }

    async function cargarTurnos() {
        const dni = usuarioActual ? String(usuarioActual.dni || '').trim() : '';

        if (!dni) {
            mostrarMensaje(mensajeTabla, 'No se pudo obtener el DNI del paciente logeado.', 'error');
            cuerpoTabla.innerHTML = '<tr><td colspan="6">Sin datos.</td></tr>';
            return;
        }

        mostrarMensaje(mensajeTabla, 'Cargando turnos...', null);

        try {
            const respuesta = await fetch(
                API_BASE + '/vista-turnos?paciente_dni=' + encodeURIComponent(dni),
                {
                    credentials: 'same-origin',
                    cache: 'no-store',
                    headers: { 'Accept': 'application/json' }
                }
            );

            const datos = await leerJson(respuesta);

            if (!respuesta.ok) {
                const detalle = datos && datos.error ? datos.error : 'No se pudieron cargar los turnos.';
                throw new Error(detalle);
            }

            pintarTurnos(datos);
            mostrarMensaje(mensajeTabla, '', null);
        } catch (error) {
            console.error('Error al cargar turnos:', error);
            mostrarMensaje(mensajeTabla, error.message || 'No se pudieron cargar los turnos.', 'error');
            cuerpoTabla.innerHTML = '<tr><td colspan="6">No se pudieron cargar los turnos.</td></tr>';
        }
    }

    // ------------------------------------------------------------------
    // Mi salud: alergias y recetas
    // ------------------------------------------------------------------

    function pintarAlergias(alergias) {
        cuerpoAlergias.innerHTML = '';

        if (!Array.isArray(alergias) || !alergias.length) {
            const fila = document.createElement('tr');
            const celda = document.createElement('td');
            celda.colSpan = 2;
            celda.textContent = 'No tenés alergias registradas.';
            fila.appendChild(celda);
            cuerpoAlergias.appendChild(fila);
            return;
        }

        alergias.forEach(alergia => {
            const fila = document.createElement('tr');
            fila.appendChild(crearCelda(alergia.alergiaNombre));
            fila.appendChild(crearCelda(alergia.alergiaDescripcion));
            cuerpoAlergias.appendChild(fila);
        });
    }

    function pintarRecetas(recetas) {
        cuerpoRecetas.innerHTML = '';

        if (!Array.isArray(recetas) || !recetas.length) {
            const fila = document.createElement('tr');
            const celda = document.createElement('td');
            celda.colSpan = 7;
            celda.textContent = 'No tenés recetas registradas.';
            fila.appendChild(celda);
            cuerpoRecetas.appendChild(fila);
            return;
        }

        recetas.forEach(receta => {
            const fila = document.createElement('tr');

            const medicamento = receta.presentacion
                ? receta.articuloNombre + ' (' + receta.presentacion + ')'
                : receta.articuloNombre;

            fila.appendChild(crearCelda(fechaLegible(receta.fecha)));
            fila.appendChild(crearCelda(medicamento));
            fila.appendChild(crearCelda(receta.cantidad));
            fila.appendChild(crearCelda(receta.dosis));
            fila.appendChild(crearCelda(receta.frecuencia));
            fila.appendChild(crearCelda(receta.duracion));
            fila.appendChild(crearCelda(receta.indicacion));
            cuerpoRecetas.appendChild(fila);
        });
    }

    async function cargarMiSalud() {
        const dni = usuarioActual ? String(usuarioActual.dni || '').trim() : '';

        if (!dni) {
            mostrarMensaje(mensajeAlergias, 'No se pudo obtener el DNI del paciente.', 'error');
            mostrarMensaje(mensajeRecetas, 'No se pudo obtener el DNI del paciente.', 'error');
            cuerpoAlergias.innerHTML = '<tr><td colspan="2">Sin datos.</td></tr>';
            cuerpoRecetas.innerHTML = '<tr><td colspan="7">Sin datos.</td></tr>';
            return;
        }

        mostrarMensaje(mensajeAlergias, 'Cargando alergias...', null);
        mostrarMensaje(mensajeRecetas, 'Cargando recetas...', null);

        try {
            const respuesta = await fetch(
                API_BASE + '/mi-salud?paciente_dni=' + encodeURIComponent(dni),
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
                    : 'No se pudo cargar la información de salud (HTTP ' + respuesta.status + ').';
                throw new Error(detalle);
            }

            pintarAlergias(datos && datos.alergias);
            pintarRecetas(datos && datos.recetas);

            mostrarMensaje(mensajeAlergias, '', null);
            mostrarMensaje(mensajeRecetas, '', null);
        } catch (error) {
            console.error('Error al cargar Mi salud:', error);
            const texto = error.message || 'No se pudo cargar la información de salud.';
            mostrarMensaje(mensajeAlergias, texto, 'error');
            mostrarMensaje(mensajeRecetas, texto, 'error');
            cuerpoAlergias.innerHTML = '<tr><td colspan="2">No se pudieron cargar las alergias.</td></tr>';
            cuerpoRecetas.innerHTML = '<tr><td colspan="7">No se pudieron cargar las recetas.</td></tr>';
        }
    }

    // ------------------------------------------------------------------
    // Alta de un turno
    // ------------------------------------------------------------------

    async function pedirTurno(evento) {
        evento.preventDefault();
        mostrarMensaje(mensajeForm, '', null);

        const medicoId = Number(selectMedico.value);
        const fecha = inputFecha.value;
        const horaInicio = inputHora.value;
        const duracion = Number(selectDuracion.value) || 30;
        const motivo = inputMotivo.value.trim();

        if (!medicoId) {
            mostrarMensaje(mensajeForm, 'Seleccioná un profesional.', 'error');
            selectMedico.focus();
            return;
        }
        if (!fecha) {
            mostrarMensaje(mensajeForm, 'Seleccioná una fecha.', 'error');
            inputFecha.focus();
            return;
        }
        if (!horaInicio) {
            mostrarMensaje(mensajeForm, 'Seleccioná una hora.', 'error');
            inputHora.focus();
            return;
        }

        const inicio = new Date(fecha + 'T' + horaInicio + ':00');
        if (Number.isNaN(inicio.getTime()) || inicio.getTime() <= Date.now()) {
            mostrarMensaje(mensajeForm, 'El turno debe ser para una fecha y hora futuras.', 'error');
            return;
        }

        const horaFin = sumarMinutos(horaInicio, duracion);
        if (!horaFin) {
            mostrarMensaje(mensajeForm, 'La hora de fin no puede pasar de la medianoche.', 'error');
            return;
        }

        const turno = {
            fecha: fecha,
            horaInicio: horaInicio,
            horaFin: horaFin,
            motivo: motivo,
            medicoId: medicoId
        };

        // Un paciente sólo puede pedir turnos para sí mismo; el backend
        // también lo fuerza, pero enviamos el id para mayor claridad.
        if (usuarioActual && usuarioActual.idUsuario) {
            turno.pacienteId = Number(usuarioActual.idUsuario);
        }

        botonPedir.disabled = true;
        const textoBoton = botonPedir.textContent;
        botonPedir.textContent = 'Enviando...';

        try {
            const respuesta = await fetch(API_BASE + '/turnos', {
                method: 'POST',
                credentials: 'same-origin',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(turno)
            });

            const datos = await leerJson(respuesta);

            if (respuesta.status === 401) {
                throw new Error('Tu sesión expiró. Volvé a iniciar sesión.');
            }
            if (!respuesta.ok) {
                const detalle = datos && datos.error
                    ? datos.error
                    : 'No se pudo pedir el turno (HTTP ' + respuesta.status + ').';
                throw new Error(detalle);
            }

            mostrarMensaje(mensajeForm,
                'Turno solicitado correctamente. Se le asignó un consultorio.', 'ok');
            form.reset();
            inputFecha.min = fechaHoyISO();
            if (panelNuevo) panelNuevo.open = false;

            await cargarTurnos();
            mostrarMensaje(mensajeTabla,
                'Turno solicitado correctamente. Se le asignó un consultorio.', 'ok');
        } catch (error) {
            console.error('Error al pedir turno:', error);
            mostrarMensaje(mensajeForm, error.message || 'No se pudo pedir el turno.', 'error');
        } finally {
            botonPedir.disabled = false;
            botonPedir.textContent = textoBoton;
        }
    }

    // ------------------------------------------------------------------
    // Inicio
    // ------------------------------------------------------------------

    async function iniciar() {
        if (!form || !cuerpoTabla) return;

        inputFecha.min = fechaHoyISO();
        form.addEventListener('submit', pedirTurno);

        usuarioActual = await obtenerUsuario();

        if (!usuarioActual) {
            mostrarMensaje(mensajeTabla, 'Iniciá sesión para ver tus turnos.', 'error');
            cuerpoTabla.innerHTML = '<tr><td colspan="6">Sesión no iniciada.</td></tr>';
            mostrarMensaje(mensajeAlergias, 'Iniciá sesión para ver tu información de salud.', 'error');
            mostrarMensaje(mensajeRecetas, 'Iniciá sesión para ver tu información de salud.', 'error');
            cuerpoAlergias.innerHTML = '<tr><td colspan="2">Sesión no iniciada.</td></tr>';
            cuerpoRecetas.innerHTML = '<tr><td colspan="7">Sesión no iniciada.</td></tr>';
            return;
        }

        await cargarMedicos();
        await cargarTurnos();
        await cargarMiSalud();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', iniciar);
    } else {
        iniciar();
    }
})();
