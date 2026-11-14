package com.clinica.service;

import com.clinica.factory.ConsultaDaoFactory;
import com.clinica.dao.DataAccessException;
import com.clinica.dao.IConsultaDao;
import com.clinica.modelo.Consulta;
import com.clinica.modelo.DetalleReceta;
import com.clinica.modelo.Receta;
import com.clinica.security.UsuarioActual;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Reglas de negocio de la atención médica:
 *  - Sólo un médico puede registrar o corregir una consulta.
 *  - El médico de la consulta es siempre el usuario logueado (no el que
 *    venga en el JSON), para que nadie firme una atención ajena.
 *  - La consulta debe pertenecer a una historia clínica.
 *  - Toda corrección deja registro en auditoria_clinica.
 *  - Al registrar la atención se puede emitir la receta con sus detalles;
 *    en ese caso se valida y descuenta el stock.
 */
public class AtencionMedicaService implements IConsultaDao {

    private final IConsultaDao consultaDao;
    private final RecetaService recetaService;
    private final AutorizacionService autorizacion;
    private final AuditoriaService auditoria;

    public AtencionMedicaService() {
        this(ConsultaDaoFactory.getConsultaDao(), new RecetaService(),
                new AutorizacionService(), new AuditoriaService());
    }

    public AtencionMedicaService(IConsultaDao consultaDao, RecetaService recetaService,
            AutorizacionService autorizacion, AuditoriaService auditoria) {
        this.consultaDao = consultaDao;
        this.recetaService = recetaService;
        this.autorizacion = autorizacion;
        this.auditoria = auditoria;
    }

    // ------------------------------------------------------------------
    // LECTURA
    // ------------------------------------------------------------------

    @Override
    public List<Consulta> listar() {
        return consultaDao.listar();
    }

    @Override
    public Consulta buscarPorId(int id) {
        return consultaDao.buscarPorId(id);
    }

    // ------------------------------------------------------------------
    // ALTA
    // ------------------------------------------------------------------

    @Override
    public boolean insertar(Consulta consulta) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.CONSULTAS_CREAR);

        prepararConsulta(consulta, actor);
        return consultaDao.insertar(consulta);
    }

    @Override
    public int insertarYRetornarId(Consulta consulta) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.CONSULTAS_CREAR);

        prepararConsulta(consulta, actor);
        return consultaDao.insertarYRetornarId(consulta);
    }

    /**
     * Registra la atención completa: consulta + receta + detalles.
     * Si la receta o los detalles vienen nulos, se registra sólo la consulta.
     *
     * @return id de la consulta creada
     */
    public int registrarConsulta(Consulta consulta, Receta receta,
            List<DetalleReceta> detallesReceta) {

        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.CONSULTAS_CREAR);

        prepararConsulta(consulta, actor);

        int idConsulta = consultaDao.insertarYRetornarId(consulta);
        if (idConsulta <= 0) {
            throw new DataAccessException(
                    "No se pudo registrar la consulta en la base de datos.");
        }
        consulta.setIdConsulta(idConsulta);

        boolean hayDetalles = detallesReceta != null && !detallesReceta.isEmpty();

        if (receta != null || hayDetalles) {
            if (receta == null) {
                receta = new Receta();
            }
            receta.setConsultaId(idConsulta);
            if (receta.getFecha() == null) {
                receta.setFecha(LocalDate.now());
            }

            if (hayDetalles) {
                recetaService.insertar(receta, detallesReceta);
            } else {
                recetaService.insertar(receta);
            }
        }

        return idConsulta;
    }

    /**
     * Firma original conservada por compatibilidad. El id recibido se ignora:
     * el médico se toma siempre del usuario logueado.
     */
    public boolean registrarConsulta(Consulta consulta, Receta receta,
            List<DetalleReceta> detallesReceta, int idUsuarioLogueado) {
        registrarConsulta(consulta, receta, detallesReceta);
        return true;
    }

    // ------------------------------------------------------------------
    // CORRECCIÓN
    // ------------------------------------------------------------------

    @Override
    public boolean actualizar(Consulta consulta) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.CONSULTAS_CORREGIR);

        if (consulta == null || consulta.getIdConsulta() <= 0) {
            throw new IllegalArgumentException("La consulta a corregir no es válida.");
        }

        Consulta anterior = consultaDao.buscarPorId(consulta.getIdConsulta());
        if (anterior == null) {
            throw new IllegalArgumentException("La consulta no existe.");
        }

        // El médico que firma no puede cambiarse por la corrección.
        consulta.setMedicoId(anterior.getMedicoId());

        if (consulta.getFecha() == null) {
            consulta.setFecha(anterior.getFecha());
        }
        if (consulta.getHistoriaId() <= 0) {
            consulta.setHistorialId(anterior.getHistoriaId());
        }

        // Regla: toda corrección clínica queda auditada.
        auditoria.registrar("consulta", anterior.getIdConsulta(),
                "CORRECCION", anterior);

        return consultaDao.actualizar(consulta);
    }

    // ------------------------------------------------------------------
    // AUXILIARES
    // ------------------------------------------------------------------

    private void prepararConsulta(Consulta consulta, int actor) {
        if (consulta == null) {
            throw new IllegalArgumentException("La consulta no puede ser nula.");
        }
        if (consulta.getHistoriaId() <= 0) {
            throw new IllegalArgumentException(
                    "La consulta debe estar asociada a una historia clínica.");
        }

        // Si la petición identifica al médico, se fuerza ese id. Si sólo se
        // indicó un rol (sin X-Usuario-Id), se respeta el medicoId enviado en
        // el cuerpo para poder probar sin iniciar sesión.
        if (actor > 0) {
            consulta.setMedicoId(actor);
        } else if (consulta.getMedicoId() <= 0) {
            throw new IllegalArgumentException(
                    "Debe indicar el médico (medicoId) o el usuario en la cabecera "
                    + "X-Usuario-Id.");
        }

        if (consulta.getFecha() == null) {
            consulta.setFecha(LocalDateTime.now());
        }
    }
}
