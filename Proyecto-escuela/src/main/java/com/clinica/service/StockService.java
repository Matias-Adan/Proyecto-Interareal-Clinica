package com.clinica.service;

import com.clinica.factory.ArticuloDaoFactory;
import com.clinica.dao.DataAccessException;
import com.clinica.dao.IArticuloDao;
import com.clinica.dao.IMovimientoStockDao;
import com.clinica.factory.MovimientoStockDaoFactory;
import com.clinica.modelo.Articulo;
import com.clinica.modelo.DetalleReceta;
import com.clinica.modelo.MovimientoStock;
import com.clinica.security.UsuarioActual;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reglas de negocio de stock:
 *  - No se puede emitir una receta si algún artículo no tiene stock suficiente.
 *  - Al emitir una receta, se descuenta el stock de cada artículo.
 *  - El stock nunca puede quedar negativo.
 *
 * Si el mismo artículo aparece en varios detalles, las cantidades se suman
 * antes de validar y descontar.
 */
public class StockService {

    private final IArticuloDao articuloDao;
    private final IMovimientoStockDao movimientoStockDao;

    public StockService() {
        this(ArticuloDaoFactory.getArticuloDao(),
                MovimientoStockDaoFactory.getMovimientoStockDao());
    }

    public StockService(IArticuloDao articuloDao, IMovimientoStockDao movimientoStockDao) {
        this.articuloDao = articuloDao;
        this.movimientoStockDao = movimientoStockDao;
    }

    /**
     * Agrupa las cantidades por artículo y valida que haya al menos un detalle
     * con cantidad mayor a cero.
     */
    public Map<Integer, Integer> agruparCantidades(List<DetalleReceta> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("La receta debe tener al menos un detalle.");
        }

        Map<Integer, Integer> cantidades = new LinkedHashMap<>();
        for (DetalleReceta detalle : detalles) {
            if (detalle == null) {
                throw new IllegalArgumentException("La receta contiene un detalle vacío.");
            }
            if (detalle.getArticuloId() <= 0) {
                throw new IllegalArgumentException(
                        "Cada detalle de la receta debe indicar un artículo.");
            }
            if (detalle.getCantidad() <= 0) {
                throw new IllegalArgumentException(
                        "La cantidad en cada detalle debe ser mayor a cero.");
            }
            cantidades.merge(detalle.getArticuloId(), detalle.getCantidad(), Integer::sum);
        }
        return cantidades;
    }

    public void validarStock(List<DetalleReceta> detalles) {
        Map<Integer, Integer> cantidades = agruparCantidades(detalles);

        for (Map.Entry<Integer, Integer> entrada : cantidades.entrySet()) {
            Articulo articulo = articuloDao.buscarPorId(entrada.getKey());
            if (articulo == null) {
                throw new IllegalStateException(
                        "El artículo con id " + entrada.getKey() + " no existe.");
            }
            if (articulo.getStockActual() < entrada.getValue()) {
                throw new IllegalStateException(
                        "Stock insuficiente para el artículo: " + articulo.getNombre()
                        + " (disponible: " + articulo.getStockActual()
                        + ", requerido: " + entrada.getValue() + ").");
            }
        }
    }

    public void descontarStock(List<DetalleReceta> detalles) {
        descontarStock(detalles, UsuarioActual.get());
    }

    public void descontarStock(List<DetalleReceta> detalles, Integer usuarioId) {
        Map<Integer, Integer> cantidades = agruparCantidades(detalles);

        for (Map.Entry<Integer, Integer> entrada : cantidades.entrySet()) {
            Articulo articulo = articuloDao.buscarPorId(entrada.getKey());
            if (articulo == null) {
                throw new IllegalStateException(
                        "El artículo con id " + entrada.getKey() + " no existe.");
            }

            int nuevoStock = articulo.getStockActual() - entrada.getValue();
            if (nuevoStock < 0) {
                throw new IllegalStateException(
                        "El stock del artículo " + articulo.getNombre()
                        + " no puede quedar negativo.");
            }

            articulo.setStockActual(nuevoStock);
            if (!articuloDao.actualizar(articulo)) {
                throw new DataAccessException(
                        "No se pudo actualizar el stock del artículo "
                        + articulo.getNombre() + ".");
            }

            MovimientoStock movimiento = new MovimientoStock(
                    0,
                    articulo.getIdArticulo(),
                    "Egreso",
                    entrada.getValue(),
                    "Consumo por receta",
                    LocalDateTime.now(),
                    usuarioId != null ? usuarioId : 0);
            movimientoStockDao.insertar(movimiento);
        }
    }
}
