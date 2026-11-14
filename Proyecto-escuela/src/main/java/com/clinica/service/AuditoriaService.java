package com.clinica.service;

import com.clinica.dao.IAuditoriaClinicaDao;
import com.clinica.factory.AuditoriaClinicaDaoFactory;
import com.clinica.modelo.AuditoriaClinica;
import com.clinica.security.UsuarioActual;
import com.clinica.util.JsonUtil;

/**
 * Centraliza el registro de correcciones sobre datos clínicos.
 */
public class AuditoriaService {

    private final IAuditoriaClinicaDao auditoriaDao;

    public AuditoriaService() {
        this(AuditoriaClinicaDaoFactory.getAuditoriaClinicaDao());
    }

    public AuditoriaService(IAuditoriaClinicaDao auditoriaDao) {
        this.auditoriaDao = auditoriaDao;
    }

    /**
     * Guarda el estado anterior del registro clínico antes de corregirlo.
     *
     * @param tabla       nombre lógico de la tabla (consulta, historia_clinica, triage, alergia_paciente)
     * @param registroId  id del registro corregido
     * @param accion      CORRECCION / BAJA_LOGICA / ALTA
     * @param estadoAnterior objeto con los datos previos
     */
    public void registrar(String tabla, int registroId, String accion, Object estadoAnterior) {
        AuditoriaClinica auditoria = new AuditoriaClinica();
        auditoria.setTabla(tabla);
        auditoria.setRegistroId(registroId);
        auditoria.setAccion(accion);
        auditoria.setDatosAnteriores(JsonUtil.toJson(estadoAnterior));
        auditoria.setUsuarioId(UsuarioActual.requerido());

        if (!auditoriaDao.registrar(auditoria)) {
            throw new com.clinica.dao.DataAccessException(
                    "No se pudo registrar la auditoría clínica.");
        }
    }
}
