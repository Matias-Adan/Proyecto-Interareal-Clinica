package com.clinica.service;

import com.clinica.factory.FacturaDaoFactory;
import com.clinica.dao.IFacturaDao;
import com.clinica.modelo.Factura;
import com.clinica.security.UsuarioActual;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Reglas de negocio de facturación:
 *  - Toda factura nace en estado "Pendiente".
 *  - Una factura pagada no puede anularse ni modificarse.
 *  - Ninguna factura se elimina (el DAO no expone eliminar y el servlet
 *    responde 501).
 */
public class FacturacionService implements IFacturaDao {

    private static final List<String> ESTADOS_VALIDOS =
            Arrays.asList("Pendiente", "Pagada", "Anulada");

    private final IFacturaDao facturaDao;
    private final AutorizacionService autorizacion;

    public FacturacionService() {
        this(FacturaDaoFactory.getFacturaDao(), new AutorizacionService());
    }

    public FacturacionService(IFacturaDao facturaDao, AutorizacionService autorizacion) {
        this.facturaDao = facturaDao;
        this.autorizacion = autorizacion;
    }

    // ------------------------------------------------------------------
    // LECTURA
    // ------------------------------------------------------------------

    @Override
    public List<Factura> listar() {
        return facturaDao.listar();
    }

    @Override
    public Factura buscarPorId(int id) {
        return facturaDao.buscarPorId(id);
    }

    // ------------------------------------------------------------------
    // ALTA
    // ------------------------------------------------------------------

    @Override
    public boolean insertar(Factura factura) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.FACTURAS_GESTIONAR);

        if (factura == null) {
            throw new IllegalArgumentException("La factura no puede ser nula.");
        }
        if (factura.getPacienteId() <= 0) {
            throw new IllegalArgumentException("La factura debe tener un paciente.");
        }
        if (factura.getImporteTotal() < 0) {
            throw new IllegalArgumentException("El importe total no puede ser negativo.");
        }

        // Regla: toda factura nace pendiente, sin importar lo que envíe el cliente.
        factura.setEstado("Pendiente");
        if (factura.getFecha() == null) {
            factura.setFecha(LocalDateTime.now());
        }

        return facturaDao.insertar(factura);
    }

    /**
     * Método conservado del servicio original.
     */
    public boolean crearFactura(Factura factura) {
        return insertar(factura);
    }

    // ------------------------------------------------------------------
    // MODIFICACIÓN
    // ------------------------------------------------------------------

    @Override
    public boolean actualizar(Factura factura) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.FACTURAS_GESTIONAR);

        if (factura == null || factura.getIdFactura() <= 0) {
            throw new IllegalArgumentException("La factura a actualizar no es válida.");
        }

        Factura actual = facturaDao.buscarPorId(factura.getIdFactura());
        if (actual == null) {
            throw new IllegalArgumentException("La factura no existe.");
        }

        String estadoActual = normalizarEstado(actual.getEstado());
        String estadoNuevo = factura.getEstado() == null || factura.getEstado().trim().isEmpty()
                ? estadoActual
                : normalizarEstado(factura.getEstado());

        // Regla: una factura pagada no puede anularse ni modificarse.
        if ("Pagada".equals(estadoActual) && !"Pagada".equals(estadoNuevo)) {
            throw new IllegalStateException(
                    "Una factura pagada no puede anularse ni modificarse.");
        }

        // Una factura anulada queda inmutable.
        if ("Anulada".equals(estadoActual) && !"Anulada".equals(estadoNuevo)) {
            throw new IllegalStateException("Una factura anulada no puede modificarse.");
        }

        if ("Anulada".equals(estadoNuevo) && "Pagada".equals(estadoActual)) {
            throw new IllegalStateException("Una factura pagada no puede anularse.");
        }

        factura.setEstado(estadoNuevo);
        return facturaDao.actualizar(factura);
    }

    public boolean anularFactura(int facturaId) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.FACTURAS_GESTIONAR);

        if (facturaId <= 0) {
            throw new IllegalArgumentException("La factura no es válida.");
        }

        Factura factura = facturaDao.buscarPorId(facturaId);
        if (factura == null) {
            throw new IllegalArgumentException("La factura no existe.");
        }
        if ("Pagada".equalsIgnoreCase(factura.getEstado())) {
            throw new IllegalStateException("Una factura pagada no puede anularse.");
        }
        if ("Anulada".equalsIgnoreCase(factura.getEstado())) {
            throw new IllegalStateException("La factura ya está anulada.");
        }

        factura.setEstado("Anulada");
        return facturaDao.actualizar(factura);
    }

    public boolean registrarPago(int facturaId) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.FACTURAS_GESTIONAR);

        if (facturaId <= 0) {
            throw new IllegalArgumentException("La factura no es válida.");
        }

        Factura factura = facturaDao.buscarPorId(facturaId);
        if (factura == null) {
            throw new IllegalArgumentException("La factura no existe.");
        }
        if ("Pagada".equalsIgnoreCase(factura.getEstado())) {
            throw new IllegalStateException("La factura ya está pagada.");
        }
        if ("Anulada".equalsIgnoreCase(factura.getEstado())) {
            throw new IllegalStateException("Una factura anulada no puede pagarse.");
        }

        factura.setEstado("Pagada");
        return facturaDao.actualizar(factura);
    }

    // ------------------------------------------------------------------
    // AUXILIARES
    // ------------------------------------------------------------------

    private String normalizarEstado(String estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado de la factura es obligatorio.");
        }
        for (String valido : ESTADOS_VALIDOS) {
            if (valido.equalsIgnoreCase(estado.trim())) {
                return valido;
            }
        }
        throw new IllegalArgumentException(
                "Estado de factura inválido. Use: Pendiente, Pagada o Anulada.");
    }
}
