package com.clinica.service;

import com.clinica.dao.ITriageDao;
import com.clinica.factory.TriageDaoFactory;
import com.clinica.modelo.Triage;
import com.clinica.security.UsuarioActual;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Regla de negocio: los datos clínicos no se eliminan; sólo se agregan o se
 * corrigen dejando registro en la auditoría.
 */
public class TriageService implements ITriageDao {

    private final ITriageDao triageDao;
    private final AutorizacionService autorizacion;
    private final AuditoriaService auditoria;

    public TriageService() {
        this(TriageDaoFactory.getTriageDao(),
                new AutorizacionService(), new AuditoriaService());
    }

    public TriageService(ITriageDao triageDao, AutorizacionService autorizacion,
            AuditoriaService auditoria) {
        this.triageDao = triageDao;
        this.autorizacion = autorizacion;
        this.auditoria = auditoria;
    }

    @Override
    public List<Triage> listar() {
        return triageDao.listar();
    }

    @Override
    public Triage buscarPorId(int id) {
        return triageDao.buscarPorId(id);
    }

    @Override
    public boolean insertar(Triage triage) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.TRIAGES_GESTIONAR);

        if (triage == null) {
            throw new IllegalArgumentException("El triage no puede ser nulo.");
        }
        if (triage.getPacienteId() <= 0) {
            throw new IllegalArgumentException("El triage debe tener un paciente.");
        }
        if (triage.getPrioridad() == null || triage.getPrioridad().trim().isEmpty()) {
            throw new IllegalArgumentException("La prioridad del triage es obligatoria.");
        }

        if (triage.getFecha() == null) {
            triage.setFecha(LocalDateTime.now());
        }
        // Si la petición identifica al usuario, se fuerza ese id; si sólo se
        // indicó un rol, se respeta el enfermeroId enviado en el cuerpo.
        if (actor > 0) {
            triage.setEnfermeroId(actor);
        }

        return triageDao.insertar(triage);
    }

    @Override
    public boolean actualizar(Triage triage) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.TRIAGES_GESTIONAR);

        if (triage == null || triage.getIdTriage() <= 0) {
            throw new IllegalArgumentException("El triage a corregir no es válido.");
        }

        Triage anterior = triageDao.buscarPorId(triage.getIdTriage());
        if (anterior == null) {
            throw new IllegalArgumentException("El triage no existe.");
        }

        auditoria.registrar("triage", anterior.getIdTriage(), "CORRECCION", anterior);

        return triageDao.actualizar(triage);
    }
}
