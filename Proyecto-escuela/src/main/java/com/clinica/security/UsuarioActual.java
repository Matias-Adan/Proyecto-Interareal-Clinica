package com.clinica.security;

/**
 * Contexto de seguridad de la petición actual.
 *
 * El filtro ContextoUsuarioFilter carga en este ThreadLocal el usuario y/o el
 * rol que identifican la petición, y lo limpia al terminar.
 *
 * La identidad puede venir de dos lugares:
 *   - La sesión (login normal, como antes).
 *   - Las cabeceras X-Usuario-Id / X-Rol-Id (o X-Rol) para poder probar con
 *     Postman sin iniciar sesión.
 *
 * La autorización se resuelve siempre por rol/permiso. El id de usuario sólo
 * se usa para las reglas de negocio que necesitan saber quién actúa (por
 * ejemplo, el médico que firma una consulta o el paciente que pide un turno).
 */
public final class UsuarioActual {

    private static final ThreadLocal<Integer> ID_USUARIO = new ThreadLocal<>();
    private static final ThreadLocal<Integer> ROL = new ThreadLocal<>();

    private UsuarioActual() {
    }

    public static void set(Integer idUsuario) {
        set(idUsuario, null);
    }

    public static void set(Integer idUsuario, Integer rolId) {
        if (idUsuario == null) {
            ID_USUARIO.remove();
        } else {
            ID_USUARIO.set(idUsuario);
        }

        if (rolId == null) {
            ROL.remove();
        } else {
            ROL.set(rolId);
        }
    }

    public static Integer get() {
        return ID_USUARIO.get();
    }

    /**
     * Rol explícito indicado por la petición (cabecera X-Rol-Id / X-Rol).
     * Puede ser null cuando el rol se resuelve desde la base a partir del
     * usuario.
     */
    public static Integer getRol() {
        return ROL.get();
    }

    public static boolean hayIdentidad() {
        return ID_USUARIO.get() != null || ROL.get() != null;
    }

    /**
     * Devuelve el id del usuario que ejecuta la acción.
     *
     * Si la petición sólo indica un rol (sin usuario), devuelve 0 como actor
     * anónimo para que la autorización por rol pueda continuar. Las reglas de
     * negocio que necesitan un usuario real usan {@link #get()} y conservan el
     * valor enviado en el cuerpo de la petición.
     */
    public static int requerido() {
        Integer id = ID_USUARIO.get();
        if (id != null) {
            return id;
        }

        if (ROL.get() != null) {
            return 0;
        }

        throw new NoAutenticadoException(
                "Debe indicar un rol en la cabecera X-Rol-Id (o iniciar sesión).");
    }

    public static void limpiar() {
        ID_USUARIO.remove();
        ROL.remove();
    }
}
