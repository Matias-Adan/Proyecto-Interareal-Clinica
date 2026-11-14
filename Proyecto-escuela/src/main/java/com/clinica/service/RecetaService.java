package com.clinica.service;

import com.clinica.dao.DataAccessException;
import com.clinica.factory.DetalleRecetaDaoFactory;
import com.clinica.dao.IDetalleRecetaDao;
import com.clinica.dao.IRecetaDao;
import com.clinica.factory.RecetaDaoFactory;
import com.clinica.modelo.DetalleReceta;
import com.clinica.modelo.Receta;
import com.clinica.security.UsuarioActual;
import java.time.LocalDate;
import java.util.List;

/**
 * Reglas de negocio de recetas:
 *  - Una receta pertenece siempre a una consulta.
 *  - Para emitirla debe tener al menos un detalle.
 *  - No se puede emitir si algún artículo no tiene stock suficiente.
 *  - Al emitirla se descuenta el stock de todos los artículos involucrados.
 */
public class RecetaService implements IRecetaDao {

    private final IRecetaDao recetaDao;
    private final IDetalleRecetaDao detalleRecetaDao;
    private final StockService stockService;
    private final AutorizacionService autorizacion;

    public RecetaService() {
        this(RecetaDaoFactory.getRecetaDao(),
                DetalleRecetaDaoFactory.getDetalleRecetaDao(),
                new StockService(), new AutorizacionService());
    }

    public RecetaService(IRecetaDao recetaDao, IDetalleRecetaDao detalleRecetaDao,
            StockService stockService, AutorizacionService autorizacion) {
        this.recetaDao = recetaDao;
        this.detalleRecetaDao = detalleRecetaDao;
        this.stockService = stockService;
        this.autorizacion = autorizacion;
    }

    // ------------------------------------------------------------------
    // LECTURA
    // ------------------------------------------------------------------

    @Override
    public List<Receta> listar() {
        return recetaDao.listar();
    }

    @Override
    public Receta buscarPorId(int id) {
        return recetaDao.buscarPorId(id);
    }

    // ------------------------------------------------------------------
    // ALTA
    // ------------------------------------------------------------------

    @Override
    public boolean insertar(Receta receta) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.RECETAS_EMITIR);

        validarReceta(receta);

        return recetaDao.insertar(receta);
    }

    @Override
    public int insertarYRetornarId(Receta receta) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.RECETAS_EMITIR);

        validarReceta(receta);

        int idReceta = recetaDao.insertarYRetornarId(receta);
        if (idReceta <= 0) {
            throw new DataAccessException(
                    "No se pudo insertar la receta en la base de datos.");
        }
        receta.setIdReceta(idReceta);
        return idReceta;
    }

    /**
     * Alta completa: inserta la receta, sus detalles y descuenta el stock.
     * Es la vía recomendada para cumplir la regla de "al menos un detalle".
     *
     * @return id de la receta creada
     */
    public int insertar(Receta receta, List<DetalleReceta> detalles) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.RECETAS_EMITIR);

        validarReceta(receta);

        // Regla: la receta debe tener al menos un detalle.
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException(
                    "La receta debe tener al menos un detalle.");
        }

        // Se validan todas las cantidades juntas antes de descontar nada.
        stockService.validarStock(detalles);

        int idReceta = recetaDao.insertarYRetornarId(receta);
        if (idReceta <= 0) {
            throw new DataAccessException(
                    "No se pudo insertar la receta en la base de datos.");
        }
        receta.setIdReceta(idReceta);

        for (DetalleReceta detalle : detalles) {
            detalle.setRecetaId(idReceta);
            if (!detalleRecetaDao.insertar(detalle)) {
                throw new DataAccessException(
                        "No se pudo insertar uno de los detalles de la receta.");
            }
        }

        stockService.descontarStock(detalles, actor);
        return idReceta;
    }

    /**
     * Nombre alternativo conservado para llamadas explícitas desde otros servicios.
     */
    public int insertarConDetalles(Receta receta, List<DetalleReceta> detalles) {
        return insertar(receta, detalles);
    }

    // ------------------------------------------------------------------
    // AUXILIARES
    // ------------------------------------------------------------------

    private void validarReceta(Receta receta) {
        if (receta == null) {
            throw new IllegalArgumentException("La receta no puede ser nula.");
        }
        if (receta.getConsultaId() <= 0) {
            throw new IllegalArgumentException(
                    "La receta debe estar asociada a una consulta.");
        }
        if (receta.getFecha() == null) {
            receta.setFecha(LocalDate.now());
        }
    }
}
