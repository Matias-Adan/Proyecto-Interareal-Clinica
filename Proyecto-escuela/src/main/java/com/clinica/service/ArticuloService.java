package com.clinica.service;

import com.clinica.factory.ArticuloDaoFactory;
import com.clinica.dao.IArticuloDao;
import com.clinica.modelo.Articulo;
import com.clinica.security.UsuarioActual;
import java.util.List;

/**
 * Reglas de negocio de artículos:
 *  - El stock nunca puede quedar negativo.
 *  - Sólo administradores/enfermería pueden gestionar el catálogo y el stock.
 */
public class ArticuloService implements IArticuloDao {

    private final IArticuloDao articuloDao;
    private final AutorizacionService autorizacion;

    public ArticuloService() {
        this(ArticuloDaoFactory.getArticuloDao(), new AutorizacionService());
    }

    public ArticuloService(IArticuloDao articuloDao, AutorizacionService autorizacion) {
        this.articuloDao = articuloDao;
        this.autorizacion = autorizacion;
    }

    @Override
    public List<Articulo> listar() {
        return articuloDao.listar();
    }

    @Override
    public Articulo buscarPorId(int id) {
        return articuloDao.buscarPorId(id);
    }

    @Override
    public boolean insertar(Articulo articulo) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.STOCK_GESTIONAR);

        validarArticulo(articulo);
        return articuloDao.insertar(articulo);
    }

    @Override
    public boolean actualizar(Articulo articulo) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.STOCK_GESTIONAR);

        if (articulo == null || articulo.getIdArticulo() <= 0) {
            throw new IllegalArgumentException("El artículo a actualizar no es válido.");
        }

        Articulo existente = articuloDao.buscarPorId(articulo.getIdArticulo());
        if (existente == null) {
            throw new IllegalArgumentException("El artículo no existe.");
        }

        validarArticulo(articulo);
        return articuloDao.actualizar(articulo);
    }

    @Override
    public boolean eliminar(int id) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.STOCK_GESTIONAR);

        if (id <= 0) {
            throw new IllegalArgumentException("El artículo no es válido.");
        }
        return articuloDao.eliminar(id);
    }

    private void validarArticulo(Articulo articulo) {
        if (articulo == null) {
            throw new IllegalArgumentException("El artículo no puede ser nulo.");
        }
        if (articulo.getNombre() == null || articulo.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del artículo es obligatorio.");
        }
        if (articulo.getStockActual() < 0) {
            throw new IllegalArgumentException("El stock actual no puede ser negativo.");
        }
        if (articulo.getStockMinimo() < 0) {
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo.");
        }
    }
}
