package com.clinica.service;

import com.clinica.factory.AlergiaPacienteDaoFactory;
import com.clinica.dao.IAlergiaPacienteDao;
import com.clinica.modelo.AlergiaPaciente;
import com.clinica.security.UsuarioActual;
import java.util.List;

/**
 * Regla de negocio: los datos clínicos no se eliminan.
 * Las asignaciones de alergias se pueden agregar y corregir (con auditoría),
 * pero no borrar físicamente.
 */
public class AlergiaPacienteService implements IAlergiaPacienteDao {

    private final IAlergiaPacienteDao alergiaPacienteDao;
    private final AutorizacionService autorizacion;
    private final AuditoriaService auditoria;

    public AlergiaPacienteService() {
        this(AlergiaPacienteDaoFactory.getAlergiaPacienteDao(),
                new AutorizacionService(), new AuditoriaService());
    }

    public AlergiaPacienteService(IAlergiaPacienteDao alergiaPacienteDao,
            AutorizacionService autorizacion, AuditoriaService auditoria) {
        this.alergiaPacienteDao = alergiaPacienteDao;
        this.autorizacion = autorizacion;
        this.auditoria = auditoria;
    }

    @Override
    public List<AlergiaPaciente> listar() {
        return alergiaPacienteDao.listar();
    }

    @Override
    public AlergiaPaciente buscarPorId(int id) {
        return alergiaPacienteDao.buscarPorId(id);
    }

    @Override
    public boolean insertar(AlergiaPaciente alergiaPaciente) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.ALERGIAS_GESTIONAR);

        if (alergiaPaciente == null) {
            throw new IllegalArgumentException("El registro de alergia no puede ser nulo.");
        }
        if (alergiaPaciente.getPacienteId() <= 0) {
            throw new IllegalArgumentException("El registro debe indicar un paciente.");
        }
        if (alergiaPaciente.getAlergiaId() <= 0) {
            throw new IllegalArgumentException("El registro debe indicar una alergia.");
        }

        return alergiaPacienteDao.insertar(alergiaPaciente);
    }

    @Override
    public boolean actualizar(AlergiaPaciente alergiaPaciente) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.ALERGIAS_GESTIONAR);

        if (alergiaPaciente == null || alergiaPaciente.getIdAlergiaPaciente() <= 0) {
            throw new IllegalArgumentException(
                    "El registro de alergia a corregir no es válido.");
        }

        AlergiaPaciente anterior =
                alergiaPacienteDao.buscarPorId(alergiaPaciente.getIdAlergiaPaciente());
        if (anterior == null) {
            throw new IllegalArgumentException("El registro de alergia no existe.");
        }

        auditoria.registrar("alergia_paciente", anterior.getIdAlergiaPaciente(),
                "CORRECCION", anterior);

        return alergiaPacienteDao.actualizar(alergiaPaciente);
    }

    @Override
    public boolean eliminar(int id) {
        // Regla: los datos clínicos nunca se eliminan.
        throw new UnsupportedOperationException(
                "Los datos clínicos no se eliminan. "
                + "Use una corrección (PUT) para dejar registro del cambio.");
    }
}
