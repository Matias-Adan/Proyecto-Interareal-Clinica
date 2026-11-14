package com.clinica.service;

import com.clinica.factory.ConsultorioDaoFactory;
import com.clinica.dao.IConsultorioDao;
import com.clinica.dao.IUbicacionPacienteDao;
import com.clinica.factory.UbicacionPacienteDaoFactory;
import com.clinica.modelo.Consultorio;
import com.clinica.modelo.UbicacionPaciente;
import com.clinica.security.UsuarioActual;
import java.util.List;

/**
 * Reglas de negocio de consultorios:
 *  - Un consultorio ocupado no puede asignarse a otro paciente.
 *  - Un consultorio no puede marcarse disponible mientras tenga pacientes.
 */
public class ConsultorioService implements IConsultorioDao {

    private static final String OCUPADO = "Ocupado";
    private static final String DISPONIBLE = "Disponible";

    private final IConsultorioDao consultorioDao;
    private final IUbicacionPacienteDao ubicacionDao;
    private final AutorizacionService autorizacion;

    public ConsultorioService() {
        this(ConsultorioDaoFactory.getConsultorioDao(),
                UbicacionPacienteDaoFactory.getUbicacionPacienteDao(),
                new AutorizacionService());
    }

    public ConsultorioService(IConsultorioDao consultorioDao,
            IUbicacionPacienteDao ubicacionDao, AutorizacionService autorizacion) {
        this.consultorioDao = consultorioDao;
        this.ubicacionDao = ubicacionDao;
        this.autorizacion = autorizacion;
    }

    @Override
    public List<Consultorio> listar() {
        return consultorioDao.listar();
    }

    @Override
    public Consultorio buscarPorId(int id) {
        return consultorioDao.buscarPorId(id);
    }

    @Override
    public boolean insertar(Consultorio consultorio) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.CONSULTORIOS_GESTIONAR);

        if (consultorio == null) {
            throw new IllegalArgumentException("El consultorio no puede ser nulo.");
        }
        if (consultorio.getNumero() == null) {
            throw new IllegalArgumentException("El número de consultorio es obligatorio.");
        }
        if (consultorio.getEstado() == null || consultorio.getEstado().trim().isEmpty()) {
            consultorio.setEstado(DISPONIBLE);
        }

        return consultorioDao.insertar(consultorio);
    }

    @Override
    public boolean actualizar(Consultorio consultorio) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.CONSULTORIOS_GESTIONAR);

        if (consultorio == null || consultorio.getIdConsultorio() <= 0) {
            throw new IllegalArgumentException("El consultorio a actualizar no es válido.");
        }

        Consultorio existente = consultorioDao.buscarPorId(consultorio.getIdConsultorio());
        if (existente == null) {
            throw new IllegalArgumentException("El consultorio no existe.");
        }

        // No se puede liberar un consultorio que todavía tiene un paciente.
        if (consultorio.getEstado() != null
                && !OCUPADO.equalsIgnoreCase(consultorio.getEstado())
                && tienePacientesActivos(consultorio.getIdConsultorio())) {
            throw new IllegalStateException(
                    "El consultorio todavía tiene un paciente. Debe liberarlo primero.");
        }

        return consultorioDao.actualizar(consultorio);
    }

    // ------------------------------------------------------------------
    // Compatibilidad con el servicio original
    // ------------------------------------------------------------------

    public boolean asignarConsultorio(UbicacionPaciente nuevaUbicacion) {
        return new UbicacionPacienteService().insertar(nuevaUbicacion);
    }

    public boolean actualizarUbicacion(UbicacionPaciente ubicacionActualizada) {
        return new UbicacionPacienteService().actualizar(ubicacionActualizada);
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------

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
}
