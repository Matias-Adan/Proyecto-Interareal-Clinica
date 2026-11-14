package com.clinica.service;

import com.clinica.factory.HistoriaClinicaDaoFactory;
import com.clinica.dao.IHistoriaClinicaDao;
import com.clinica.modelo.HistoriaClinica;
import com.clinica.security.UsuarioActual;
import java.time.LocalDate;
import java.util.List;

/**
 * Regla de negocio: los datos clínicos no se eliminan; sólo se agregan o se
 * corrigen dejando registro en la auditoría.
 */
public class HistoriaClinicaService implements IHistoriaClinicaDao {

    private final IHistoriaClinicaDao historiaClinicaDao;
    private final AutorizacionService autorizacion;
    private final AuditoriaService auditoria;

    public HistoriaClinicaService() {
        this(HistoriaClinicaDaoFactory.getHistoriaClinicaDao(),
                new AutorizacionService(), new AuditoriaService());
    }

    public HistoriaClinicaService(IHistoriaClinicaDao historiaClinicaDao,
            AutorizacionService autorizacion, AuditoriaService auditoria) {
        this.historiaClinicaDao = historiaClinicaDao;
        this.autorizacion = autorizacion;
        this.auditoria = auditoria;
    }

    @Override
    public List<HistoriaClinica> listar() {
        return historiaClinicaDao.listar();
    }

    @Override
    public HistoriaClinica buscarPorId(int id) {
        return historiaClinicaDao.buscarPorId(id);
    }

    @Override
    public boolean insertar(HistoriaClinica historia) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.HISTORIAS_GESTIONAR);

        if (historia == null) {
            throw new IllegalArgumentException("La historia clínica no puede ser nula.");
        }
        if (historia.getPacienteId() <= 0) {
            throw new IllegalArgumentException("La historia clínica debe tener un paciente.");
        }

        if (historia.getFechaApertura() == null) {
            historia.setFechaApertura(LocalDate.now());
        }

        return historiaClinicaDao.insertar(historia);
    }

    @Override
    public boolean actualizar(HistoriaClinica historia) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.HISTORIAS_GESTIONAR);

        if (historia == null || historia.getIdHistoria() <= 0) {
            throw new IllegalArgumentException(
                    "La historia clínica a corregir no es válida.");
        }

        HistoriaClinica anterior = historiaClinicaDao.buscarPorId(historia.getIdHistoria());
        if (anterior == null) {
            throw new IllegalArgumentException("La historia clínica no existe.");
        }

        // Deja registro del estado anterior antes de pisarlo.
        auditoria.registrar("historia_clinica", anterior.getIdHistoria(),
                "CORRECCION", anterior);

        return historiaClinicaDao.actualizar(historia);
    }
}
