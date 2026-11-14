package com.clinica.security;

/**
 * Se lanza cuando una operación requiere identificar al usuario o su rol y la
 * petición no trae ni sesión ni cabeceras de identificación.
 * Extiende SecurityException para que el filtro la pueda distinguir
 * y responder HTTP 401 en lugar de 403.
 */
public class NoAutenticadoException extends SecurityException {

    private static final long serialVersionUID = 1L;

    public NoAutenticadoException(String mensaje) {
        super(mensaje);
    }
}
