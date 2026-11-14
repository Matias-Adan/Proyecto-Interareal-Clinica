package com.clinica.service;

import com.clinica.factory.ConsultorioDaoFactory;
import com.clinica.dao.IConsultorioDao;
import com.clinica.dao.IUbicacionPacienteDao;
import com.clinica.factory.UbicacionPacienteDaoFactory;
import com.clinica.modelo.Consultorio;
import com.clinica.modelo.UbicacionPaciente;
import com.clinica.security.UsuarioActual;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Reglas de negocio de guardia/consultorios:
 *  - Un paciente sólo puede estar en un consultorio a la vez.
 *  - Un consultorio ocupado no puede asignarse a otro paciente hasta liberarse.
 *  - Al corregir una ubicación también se valida el consultorio destino.
 */
public class UbicacionPacienteService implements IUbicacionPacienteDao {

    private static final String OCUPADO = "Ocupado";
    private static final String DISPONIBLE = "Disponible";

    private final IUbicacionPacienteDao ubicacionDao;
    private final IConsultorioDao consultorioDao;
    private final AutorizacionService autorizacion;

    public UbicacionPacienteService() {
        this(UbicacionPacienteDaoFactory.getUbicacionPacienteDao(),
                ConsultorioDaoFactory.getConsultorioDao(),
                new AutorizacionService());
    }

    public UbicacionPacienteService(IUbicacionPacienteDao ubicacionDao,
            IConsultorioDao consultorioDao, AutorizacionService autorizacion) {
        this.ubicacionDao = ubicacionDao;
        this.consultorioDao = consultorioDao;
        this.autorizacion = autorizacion;
    }

    // ------------------------------------------------------------------
    // LECTURA
    // ------------------------------------------------------------------

    @Override
    public List<UbicacionPaciente> listar() {
        return ubicacionDao.listar();
    }

    @Override
    public UbicacionPaciente buscarPorId(int id) {
        return ubicacionDao.buscarPorId(id);
    }

    // ------------------------------------------------------------------
    // ASIGNACIÓN
    // ------------------------------------------------------------------

    @Override
    public boolean insertar(UbicacionPaciente ubicacion) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.CONSULTORIOS_GESTIONAR);

        if (ubicacion == null) {
            throw new IllegalArgumentException("La ubicación no puede ser nula.");
        }
        if (ubicacion.getPacienteId() <= 0) {
            throw new IllegalArgumentException("La ubicación debe tener un paciente.");
        }
        if (ubicacion.getConsultorioId() <= 0) {
            throw new IllegalArgumentException("La ubicación debe tener un consultorio.");
        }

        Consultorio consultorio = consultorioDao.buscarPorId(ubicacion.getConsultorioId());
        if (consultorio == null) {
            throw new IllegalArgumentException("El consultorio no existe.");
        }
        if (OCUPADO.equalsIgnoreCase(consultorio.getEstado())) {
            throw new IllegalStateException(
                    "El consultorio está ocupado por otro paciente.");
        }

        List<UbicacionPaciente> ubicaciones = ubicacionDao.listar();
        if (ubicaciones != null) {
            for (UbicacionPaciente actual : ubicaciones) {
                if (actual.getFechaSalida() != null) {
                    continue;
                }
                if (actual.getPacienteId() == ubicacion.getPacienteId()) {
                    throw new IllegalStateException(
                            "El paciente ya se encuentra en otro consultorio.");
                }
                if (actual.getConsultorioId() == ubicacion.getConsultorioId()) {
                    throw new IllegalStateException(
                            "El consultorio ya está ocupado por otro paciente.");
                }
            }
        }

        if (ubicacion.getFechaIngreso() == null) {
            ubicacion.setFechaIngreso(LocalDateTime.now());
        }
        ubicacion.setFechaSalida(null);

        boolean asignado = ubicacionDao.insertar(ubicacion);
        if (asignado) {
            marcarConsultorio(ubicacion.getConsultorioId(), OCUPADO);
        }
        return asignado;
    }

    // ------------------------------------------------------------------
    // CORRECCIÓN DE UBICACIÓN
    // ------------------------------------------------------------------

    @Override
    public boolean actualizar(UbicacionPaciente ubicacion) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.CONSULTORIOS_GESTIONAR);

        if (ubicacion == null || ubicacion.getIdUbicacion() <= 0) {
            throw new IllegalArgumentException("La ubicación a actualizar no es válida.");
        }

        UbicacionPaciente anterior = ubicacionDao.buscarPorId(ubicacion.getIdUbicacion());
        if (anterior == null) {
            throw new IllegalArgumentException("La ubicación no existe.");
        }

        if (ubicacion.getPacienteId() <= 0) {
            ubicacion.setPacienteId(anterior.getPacienteId());
        }
        if (ubicacion.getConsultorioId() <= 0) {
            ubicacion.setConsultorioId(anterior.getConsultorioId());
        }

        boolean cambiaDeConsultorio =
                ubicacion.getConsultorioId() != anterior.getConsultorioId();
        boolean seReactiva = anterior.getFechaSalida() != null
                && ubicacion.getFechaSalida() == null;

        if (cambiaDeConsultorio || seReactiva) {
            validarDestino(ubicacion, anterior);
        }

        boolean actualizado = ubicacionDao.actualizar(ubicacion);

        if (actualizado) {
            if (ubicacion.getFechaSalida() != null) {
                liberarConsultorioSiCorresponde(ubicacion.getConsultorioId());
            } else if (cambiaDeConsultorio) {
                liberarConsultorioSiCorresponde(anterior.getConsultorioId());
                marcarConsultorio(ubicacion.getConsultorioId(), OCUPADO);
            }
        }

        return actualizado;
    }

    @Override
    public boolean darAlta(int id, LocalDateTime fechaSalida) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.CONSULTORIOS_GESTIONAR);

        if (id <= 0) {
            throw new IllegalArgumentException("La ubicación no es válida.");
        }

        UbicacionPaciente ubicacion = ubicacionDao.buscarPorId(id);
        if (ubicacion == null) {
            throw new IllegalArgumentException("La ubicación no existe.");
        }

        LocalDateTime salida = fechaSalida != null ? fechaSalida : LocalDateTime.now();
        boolean dadoDeAlta = ubicacionDao.darAlta(id, salida);

        if (dadoDeAlta) {
            liberarConsultorioSiCorresponde(ubicacion.getConsultorioId());
        }
        return dadoDeAlta;
    }

    // ------------------------------------------------------------------
    // AUXILIARES
    // ------------------------------------------------------------------

    private void validarDestino(UbicacionPaciente ubicacion, UbicacionPaciente anterior) {
        Consultorio destino = consultorioDao.buscarPorId(ubicacion.getConsultorioId());
        if (destino == null) {
            throw new IllegalArgumentException("El consultorio destino no existe.");
        }

        if (OCUPADO.equalsIgnoreCase(destino.getEstado())
                && destino.getIdConsultorio() != anterior.getConsultorioId()) {
            throw new IllegalStateException("El consultorio destino está ocupado.");
        }

        List<UbicacionPaciente> ubicaciones = ubicacionDao.listar();
        if (ubicaciones == null) {
            return;
        }
        for (UbicacionPaciente otra : ubicaciones) {
            if (otra.getIdUbicacion() == ubicacion.getIdUbicacion()) {
                continue;
            }
            if (otra.getFechaSalida() == null
                    && otra.getPacienteId() == ubicacion.getPacienteId()) {
                throw new IllegalStateException(
                        "El paciente ya se encuentra en otro consultorio.");
            }
        }
    }

    private void liberarConsultorioSiCorresponde(int consultorioId) {
        if (!tienePacientesActivos(consultorioId)) {
            marcarConsultorio(consultorioId, DISPONIBLE);
        }
    }

    private boolean tienePacientesActivos(int consultorioId) {
        List<UbicacionPaciente> ubicaciones = ubicacionDao.listar();
        if (ubicaciones == null) {
            return false;
        }
        for (UbicacionPaciente ubicacion : ubicaciones) {
            if (ubicacion.getConsultorioId() == consultorioId
                    && ubicacion.getFechaSalida() == null) {
                return true;
            }
        }
        return false;
    }

    private void marcarConsultorio(int consultorioId, String estado) {
        Consultorio consultorio = consultorioDao.buscarPorId(consultorioId);
        if (consultorio != null) {
            consultorio.setEstado(estado);
            consultorioDao.actualizar(consultorio);
        }
    }
}
