package com.clinica.security;

import com.clinica.service.AutorizacionService;

/**
 * Política central de acceso por recurso y método HTTP.
 *
 * Las lecturas (GET) se mantienen abiertas para no romper el front-end
 * existente. Toda operación de escritura exige un rol identificado (por la
 * sesión o por las cabeceras X-Usuario-Id / X-Rol-Id) y el permiso
 * correspondiente.
 *
 * Esta clase complementa (no reemplaza) las validaciones de los servicios.
 */
public final class PoliticaAcceso {

    private static final AutorizacionService AUTORIZACION = new AutorizacionService();

    private PoliticaAcceso() {
    }

    public static void exigir(String ruta, String metodoHttp) {
        String rutaNormalizada = ruta == null ? "" : ruta;
        String metodo = metodoHttp == null ? "GET" : metodoHttp.toUpperCase();

        // Lecturas abiertas (compatibilidad con el front-end actual).
        if ("GET".equals(metodo) || "HEAD".equals(metodo) || "OPTIONS".equals(metodo)) {
            return;
        }

        // Registro y login son públicos.
        if ("POST".equals(metodo) && "/usuarios".equals(rutaNormalizada)) {
            return;
        }
        if ("POST".equals(metodo) && "/usuarios/login".equals(rutaNormalizada)) {
            return;
        }

        // El cierre de sesión debe funcionar incluso si la sesión ya expiró.
        if ("POST".equals(metodo) && ("/sesion".equals(rutaNormalizada)
                || "/sesion/logout".equals(rutaNormalizada))) {
            return;
        }

        int usuarioId = UsuarioActual.requerido();

        if (rutaNormalizada.startsWith("/usuarios")) {
            if ("PUT".equals(metodo)) {
                // El servicio valida que sea el propio usuario o un administrador.
                return;
            }
            AUTORIZACION.exigirPermiso(usuarioId, AutorizacionService.USUARIOS_GESTIONAR);

        } else if (rutaNormalizada.startsWith("/roles-usuario")
                || rutaNormalizada.startsWith("/roles")
                || rutaNormalizada.startsWith("/permisos")
                || rutaNormalizada.startsWith("/permiso-roles")) {
            AUTORIZACION.exigirPermiso(usuarioId, AutorizacionService.ROLES_GESTIONAR);

        } else if (rutaNormalizada.startsWith("/turnos")) {
            if ("POST".equals(metodo)) {
                AUTORIZACION.exigirPermiso(usuarioId, AutorizacionService.TURNOS_CREAR);
            } else {
                AUTORIZACION.exigirPermiso(usuarioId, AutorizacionService.TURNOS_EDITAR);
            }

        } else if (rutaNormalizada.startsWith("/consultas")) {
            AUTORIZACION.exigirPermiso(usuarioId, AutorizacionService.CONSULTAS_CREAR);

        } else if (rutaNormalizada.startsWith("/recetas")
                || rutaNormalizada.startsWith("/detalle-recetas")) {
            AUTORIZACION.exigirPermiso(usuarioId, AutorizacionService.RECETAS_EMITIR);

        } else if (rutaNormalizada.startsWith("/historias-clinicas")) {
            AUTORIZACION.exigirPermiso(usuarioId, AutorizacionService.HISTORIAS_GESTIONAR);

        } else if (rutaNormalizada.startsWith("/triages")) {
            AUTORIZACION.exigirPermiso(usuarioId, AutorizacionService.TRIAGES_GESTIONAR);

        } else if (rutaNormalizada.startsWith("/articulos")
                || rutaNormalizada.startsWith("/movimientos-stock")) {
            AUTORIZACION.exigirPermiso(usuarioId, AutorizacionService.STOCK_GESTIONAR);

        } else if (rutaNormalizada.startsWith("/facturas")) {
            AUTORIZACION.exigirPermiso(usuarioId, AutorizacionService.FACTURAS_GESTIONAR);

        } else if (rutaNormalizada.startsWith("/consultorios")
                || rutaNormalizada.startsWith("/ubicacionpaciente")) {
            AUTORIZACION.exigirPermiso(usuarioId, AutorizacionService.CONSULTORIOS_GESTIONAR);

        } else if (rutaNormalizada.startsWith("/alergias-pacientes")
                || rutaNormalizada.startsWith("/alergias")) {
            AUTORIZACION.exigirPermiso(usuarioId, AutorizacionService.ALERGIAS_GESTIONAR);

        } else if (rutaNormalizada.startsWith("/obras-sociales")) {
            AUTORIZACION.exigirPermiso(usuarioId, AutorizacionService.OBRAS_SOCIALES_GESTIONAR);
        }
    }
}
