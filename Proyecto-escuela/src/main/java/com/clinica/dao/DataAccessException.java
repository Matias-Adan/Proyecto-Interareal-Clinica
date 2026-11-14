package com.clinica.dao;

/**
 * Excepción de acceso a datos no controlada.
 * Permite que las capas superiores (servicios/servlets) distingan un fallo
 * de base de datos de un error de validación de negocio.
 */
public class DataAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataAccessException(String mensaje) {
        super(mensaje);
    }

    public DataAccessException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
