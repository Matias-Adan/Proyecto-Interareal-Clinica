package com.clinica.service;

import com.clinica.dao.ITurnoDao;
import com.clinica.factory.TurnoDaoFactory;
import com.clinica.modelo.Turno;
import com.clinica.security.UsuarioActual;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Reglas de negocio de turnos:
 *  - Sólo se piden para fecha y hora futuras.
 *  - No puede haber dos turnos activos en el mismo horario con el mismo médico.
 *  - Un paciente no puede tener más de un turno activo a la misma hora.
 *
 * Si quien crea el turno es un paciente, se fuerza pacienteId al usuario
 * logueado para que no pueda pedir turnos a nombre de otro.
 */
public class TurnosService implements ITurnoDao {

    private static final List<String> ESTADOS_VALIDOS = Arrays.asList(
            "Pendiente", "Confirmado", "Cancelado", "Atendido", "Ausente");

    private final ITurnoDao turnoDao;
    private final AutorizacionService autorizacion;

    public TurnosService() {
        this(TurnoDaoFactory.getTurnoDao(), new AutorizacionService());
    }

    public TurnosService(ITurnoDao turnoDao, AutorizacionService autorizacion) {
        this.turnoDao = turnoDao;
        this.autorizacion = autorizacion;
    }

    // ------------------------------------------------------------------
    // LECTURA
    // ------------------------------------------------------------------

    @Override
    public List<Turno> listar() {
        return turnoDao.listar();
    }

    @Override
    public Turno buscarPorId(int id) {
        return turnoDao.buscarPorId(id);
    }

    // ------------------------------------------------------------------
    // ALTA
    // ------------------------------------------------------------------

    @Override
    public boolean insertar(Turno turno) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.TURNOS_CREAR);

        if (turno == null) {
            throw new IllegalArgumentException("El turno no puede ser nulo.");
        }

        if (actor > 0 && autorizacion.tieneRol(actor, AutorizacionService.PACIENTE)) {
            // Un paciente sólo puede solicitar turnos para sí mismo.
            turno.setPacienteId(actor);
        }

        if (turno.getEstado() == null || turno.getEstado().trim().isEmpty()) {
            turno.setEstado("Pendiente");
        } else {
            turno.setEstado(normalizarEstado(turno.getEstado()));
        }

        validarTurno(turno, null);
        validarDisponibilidad(turno, 0);

        return turnoDao.insertar(turno);
    }

    /**
     * Método conservado del servicio original.
     */
    public boolean solicitarTurno(Turno turno) {
        return insertar(turno);
    }

    // ------------------------------------------------------------------
    // MODIFICACIÓN
    // ------------------------------------------------------------------

    @Override
    public boolean actualizar(Turno turno) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.TURNOS_EDITAR);

        if (turno == null || turno.getIdTurno() <= 0) {
            throw new IllegalArgumentException("El turno a actualizar no es válido.");
        }

        Turno existente = turnoDao.buscarPorId(turno.getIdTurno());
        if (existente == null) {
            throw new IllegalArgumentException("El turno no existe.");
        }

        completarDatosFaltantes(turno, existente);
        validarTurno(turno, existente);
        validarDisponibilidad(turno, turno.getIdTurno());

        return turnoDao.actualizar(turno);
    }

    @Override
    public boolean cambiarEstado(int id, String nuevoEstado) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.TURNOS_EDITAR);

        if (id <= 0) {
            throw new IllegalArgumentException("El turno no es válido.");
        }
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado del turno es obligatorio.");
        }

        String estado = normalizarEstado(nuevoEstado);
        Turno existente = turnoDao.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("El turno no existe.");
        }

        // Al reactivar o confirmar un turno se vuelven a validar los choques.
        if (!"Cancelado".equalsIgnoreCase(estado)) {
            existente.setEstado(estado);
            validarDisponibilidad(existente, id);
        }

        return turnoDao.cambiarEstado(id, estado);
    }

    // ------------------------------------------------------------------
    // VALIDACIONES
    // ------------------------------------------------------------------

    private void validarTurno(Turno turno, Turno existente) {
        if (turno.getFecha() == null) {
            throw new IllegalArgumentException("La fecha del turno no puede ser nula.");
        }
        if (turno.getHoraInicio() == null) {
            throw new IllegalArgumentException("La hora de inicio del turno no puede ser nula.");
        }
        if (turno.getMedicoId() <= 0) {
            throw new IllegalArgumentException("El turno debe tener un médico.");
        }
        if (turno.getPacienteId() <= 0) {
            throw new IllegalArgumentException("El turno debe tener un paciente.");
        }

        if (turno.getHoraFin() == null) {
            turno.setHoraFin(turno.getHoraInicio().plusMinutes(30));
        }

        if (!turno.getHoraFin().isAfter(turno.getHoraInicio())) {
            throw new IllegalArgumentException(
                    "La hora de fin debe ser posterior a la hora de inicio.");
        }

        boolean horarioModificado = existente == null
                || !Objects.equals(turno.getFecha(), existente.getFecha())
                || !Objects.equals(turno.getHoraInicio(), existente.getHoraInicio())
                || !Objects.equals(turno.getHoraFin(), existente.getHoraFin());

        if (horarioModificado) {
            LocalDateTime inicio = LocalDateTime.of(turno.getFecha(), turno.getHoraInicio());
            if (!inicio.isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException(
                        "El turno debe ser para una fecha y hora futuras.");
            }
        }
    }

    private void validarDisponibilidad(Turno turno, int idExcluir) {
        List<Turno> existentes = turnoDao.listar();
        if (existentes == null) {
            return;
        }

        LocalTime inicioNuevo = turno.getHoraInicio();
        LocalTime finNuevo = turno.getHoraFin() != null
                ? turno.getHoraFin()
                : turno.getHoraInicio().plusMinutes(30);

        for (Turno existente : existentes) {
            if (existente.getIdTurno() == idExcluir) {
                continue;
            }
            if (existente.getFecha() == null || existente.getHoraInicio() == null) {
                continue;
            }
            if ("Cancelado".equalsIgnoreCase(existente.getEstado())) {
                continue;
            }
            if (!existente.getFecha().isEqual(turno.getFecha())) {
                continue;
            }

            LocalTime inicioExistente = existente.getHoraInicio();
            LocalTime finExistente = existente.getHoraFin() != null
                    ? existente.getHoraFin()
                    : existente.getHoraInicio().plusMinutes(30);

            boolean seSolapan = inicioExistente.isBefore(finNuevo)
                    && inicioNuevo.isBefore(finExistente);

            if (!seSolapan) {
                continue;
            }

            // Regla: mismo médico, mismo día y horario, con un turno activo.
            if (existente.getMedicoId() == turno.getMedicoId()
                    && esEstadoActivo(existente.getEstado())) {
                throw new IllegalStateException(
                        "El médico ya tiene un turno activo en ese horario.");
            }

            // Regla: un paciente no puede tener más de un turno activo a la misma hora.
            if (existente.getPacienteId() == turno.getPacienteId()
                    && esEstadoActivo(existente.getEstado())) {
                throw new IllegalStateException(
                        "El paciente ya tiene un turno activo en ese horario.");
            }
        }
    }

    private boolean esEstadoActivo(String estado) {
        return "Pendiente".equalsIgnoreCase(estado)
                || "Confirmado".equalsIgnoreCase(estado);
    }

    private void completarDatosFaltantes(Turno turno, Turno existente) {
        if (turno.getFecha() == null) {
            turno.setFecha(existente.getFecha());
        }
        if (turno.getHoraInicio() == null) {
            turno.setHoraInicio(existente.getHoraInicio());
        }
        if (turno.getHoraFin() == null) {
            turno.setHoraFin(existente.getHoraFin());
        }
        if (turno.getMedicoId() <= 0) {
            turno.setMedicoId(existente.getMedicoId());
        }
        if (turno.getPacienteId() <= 0) {
            turno.setPacienteId(existente.getPacienteId());
        }
        if (turno.getEstado() == null || turno.getEstado().trim().isEmpty()) {
            turno.setEstado(existente.getEstado());
        }
        if (turno.getConsultorioId() <= 0) {
            turno.setConsultorioId(existente.getConsultorioId());
        }
    }

    private String normalizarEstado(String estado) {
        for (String valido : ESTADOS_VALIDOS) {
            if (valido.equalsIgnoreCase(estado.trim())) {
                return valido;
            }
        }
        throw new IllegalArgumentException(
                "Estado de turno inválido. Use: " + String.join(", ", ESTADOS_VALIDOS) + ".");
    }
}
