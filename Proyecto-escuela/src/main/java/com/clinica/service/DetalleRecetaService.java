package com.clinica.service;

import com.clinica.factory.DetalleRecetaDaoFactory;
import com.clinica.dao.IDetalleRecetaDao;
import com.clinica.modelo.DetalleReceta;
import com.clinica.security.UsuarioActual;
import java.util.Collections;
import java.util.List;

/**
 * Reglas de negocio de los detalles de receta:
 *  - Cada detalle debe indicar un artículo y una cantidad mayor a cero.
 *  - Antes de insertar se valida que exista stock suficiente.
 *  - Al insertar el detalle se descuenta el stock (con su movimiento).
 */
public class DetalleRecetaService implements IDetalleRecetaDao {

    private final IDetalleRecetaDao detalleRecetaDao;
    private final StockService stockService;
    private final AutorizacionService autorizacion;

    public DetalleRecetaService() {
        this(DetalleRecetaDaoFactory.getDetalleRecetaDao(),
                new StockService(), new AutorizacionService());
    }

    public DetalleRecetaService(IDetalleRecetaDao detalleRecetaDao,
            StockService stockService, AutorizacionService autorizacion) {
        this.detalleRecetaDao = detalleRecetaDao;
        this.stockService = stockService;
        this.autorizacion = autorizacion;
    }

    @Override
    public List<DetalleReceta> listar() {
        return detalleRecetaDao.listar();
    }

    @Override
    public DetalleReceta buscarPorId(int id) {
        return detalleRecetaDao.buscarPorId(id);
    }

    @Override
    public boolean insertar(DetalleReceta detalle) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.RECETAS_EMITIR);

        if (detalle == null) {
            throw new IllegalArgumentException("El detalle de receta no puede ser nulo.");
        }
        if (detalle.getRecetaId() <= 0) {
            throw new IllegalArgumentException("El detalle debe pertenecer a una receta.");
        }
        if (detalle.getArticuloId() <= 0) {
            throw new IllegalArgumentException("El detalle debe indicar un artículo.");
        }
        if (detalle.getCantidad() <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad en cada detalle debe ser mayor a cero.");
        }

        List<DetalleReceta> unoSolo = Collections.singletonList(detalle);

        // Regla: no se puede emitir un detalle si no hay stock.
        stockService.validarStock(unoSolo);

        if (!detalleRecetaDao.insertar(detalle)) {
            return false;
        }

        // Regla: al emitir, se descuenta el stock del artículo.
        stockService.descontarStock(unoSolo, actor);
        return true;
    }
}
