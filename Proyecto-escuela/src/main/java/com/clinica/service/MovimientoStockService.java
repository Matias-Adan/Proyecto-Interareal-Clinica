package com.clinica.service;

import com.clinica.factory.ArticuloDaoFactory;
import com.clinica.dao.DataAccessException;
import com.clinica.dao.IArticuloDao;
import com.clinica.dao.IMovimientoStockDao;
import com.clinica.factory.MovimientoStockDaoFactory;
import com.clinica.modelo.Articulo;
import com.clinica.modelo.MovimientoStock;
import com.clinica.security.UsuarioActual;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Registra movimientos de stock y mantiene sincronizado el stock del artículo.
 * La operación nunca permite que el stock quede negativo.
 */
public class MovimientoStockService implements IMovimientoStockDao {

    private final IMovimientoStockDao movimientoStockDao;
    private final IArticuloDao articuloDao;
    private final AutorizacionService autorizacion;

    public MovimientoStockService() {
        this(MovimientoStockDaoFactory.getMovimientoStockDao(),
                ArticuloDaoFactory.getArticuloDao(),
                new AutorizacionService());
    }

    public MovimientoStockService(IMovimientoStockDao movimientoStockDao,
            IArticuloDao articuloDao, AutorizacionService autorizacion) {
        this.movimientoStockDao = movimientoStockDao;
        this.articuloDao = articuloDao;
        this.autorizacion = autorizacion;
    }

    @Override
    public List<MovimientoStock> listar() {
        return movimientoStockDao.listar();
    }

    @Override
    public MovimientoStock buscarPorId(int id) {
        return movimientoStockDao.buscarPorId(id);
    }

    @Override
    public boolean insertar(MovimientoStock movimiento) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.STOCK_GESTIONAR);

        if (movimiento == null) {
            throw new IllegalArgumentException("El movimiento de stock no puede ser nulo.");
        }
        if (movimiento.getIdArticulo() <= 0) {
            throw new IllegalArgumentException("El movimiento debe indicar un artículo.");
        }
        if (movimiento.getCantidad() < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }
        if (movimiento.getTipoMovimiento() == null
                || movimiento.getTipoMovimiento().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio.");
        }

        Articulo articulo = articuloDao.buscarPorId(movimiento.getIdArticulo());
        if (articulo == null) {
            throw new IllegalArgumentException("El artículo no existe.");
        }

        String tipo = movimiento.getTipoMovimiento().trim().toLowerCase();
        int nuevoStock;

        switch (tipo) {
            case "ingreso":
            case "entrada":
            case "compra":
            case "devolucion":
            case "devolución":
                nuevoStock = articulo.getStockActual() + movimiento.getCantidad();
                break;
            case "egreso":
            case "salida":
            case "consumo":
            case "venta":
                nuevoStock = articulo.getStockActual() - movimiento.getCantidad();
                break;
            case "ajuste":
                // En un ajuste, la cantidad informada es el stock final deseado.
                nuevoStock = movimiento.getCantidad();
                break;
            default:
                throw new IllegalArgumentException(
                        "Tipo de movimiento inválido. Use Ingreso, Egreso o Ajuste.");
        }

        if (nuevoStock < 0) {
            throw new IllegalStateException(
                    "El stock del artículo " + articulo.getNombre()
                    + " no puede quedar negativo.");
        }

        articulo.setStockActual(nuevoStock);
        if (!articuloDao.actualizar(articulo)) {
            throw new DataAccessException(
                    "No se pudo actualizar el stock del artículo " + articulo.getNombre() + ".");
        }

        if (actor > 0) {
            movimiento.setUsuarioId(actor);
        }
        if (movimiento.getFechaHora() == null) {
            movimiento.setFechaHora(LocalDateTime.now());
        }

        return movimientoStockDao.insertar(movimiento);
    }
}
