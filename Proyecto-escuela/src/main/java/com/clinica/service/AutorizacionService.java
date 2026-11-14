package com.clinica.service;

import com.clinica.dao.IPermisoDao;
import com.clinica.dao.IPermisoRolDao;
import com.clinica.dao.IRolDao;
import com.clinica.dao.IRolDeUsuarioDao;
import com.clinica.factory.PermisoDaoFactory;
import com.clinica.factory.PermisoRolDaoFactory;
import com.clinica.factory.RolDaoFactory;
import com.clinica.factory.RolDeUsuarioDaoFactory;
import com.clinica.modelo.Permiso;
import com.clinica.modelo.PermisoRol;
import com.clinica.modelo.Rol;
import com.clinica.modelo.RolDeUsuario;
import com.clinica.security.UsuarioActual;
import java.text.Normalizer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Servicio central de autorización.
 *
 * Regla de negocio: "Cada acción sólo puede ser realizada por quien tenga el
 * rol o permiso correspondiente."
 *
 * La comprobación principal se hace por rol (IDs 1..5, que son los usados por
 * el sistema original). Además, si la base tiene cargadas las tablas
 * permisos / permisosrol, también se acepta el permiso por nombre.
 */
public class AutorizacionService {

    public static final int ADMINISTRADOR = 1;
    public static final int MEDICO = 2;
    public static final int ENFERMERO = 3;
    public static final int RECEPCIONISTA = 4;
    public static final int PACIENTE = 5;

    public static final String USUARIOS_GESTIONAR = "USUARIOS_GESTIONAR";
    public static final String ROLES_GESTIONAR = "ROLES_GESTIONAR";
    public static final String TURNOS_CREAR = "TURNOS_CREAR";
    public static final String TURNOS_EDITAR = "TURNOS_EDITAR";
    public static final String CONSULTAS_CREAR = "CONSULTAS_CREAR";
    public static final String CONSULTAS_CORREGIR = "CONSULTAS_CORREGIR";
    public static final String CONSULTAS_VER = "CONSULTAS_VER";
    public static final String RECETAS_EMITIR = "RECETAS_EMITIR";
    public static final String HISTORIAS_GESTIONAR = "HISTORIAS_GESTIONAR";
    public static final String TRIAGES_GESTIONAR = "TRIAGES_GESTIONAR";
    public static final String STOCK_GESTIONAR = "STOCK_GESTIONAR";
    public static final String FACTURAS_GESTIONAR = "FACTURAS_GESTIONAR";
    public static final String CONSULTORIOS_GESTIONAR = "CONSULTORIOS_GESTIONAR";
    public static final String ALERGIAS_GESTIONAR = "ALERGIAS_GESTIONAR";
    public static final String OBRAS_SOCIALES_GESTIONAR = "OBRAS_SOCIALES_GESTIONAR";

    private static final Map<String, Set<Integer>> ROLES_POR_PERMISO = new HashMap<>();
    private static final Map<String, Set<String>> NOMBRES_POR_PERMISO = new HashMap<>();

    static {
        permiso(USUARIOS_GESTIONAR, roles(ADMINISTRADOR), nombres("administrador"));
        permiso(ROLES_GESTIONAR, roles(ADMINISTRADOR), nombres("administrador"));
        permiso(TURNOS_CREAR,
                roles(ADMINISTRADOR, MEDICO, RECEPCIONISTA, PACIENTE),
                nombres("administrador", "medico", "recepcionista", "paciente"));
        permiso(TURNOS_EDITAR,
                roles(ADMINISTRADOR, MEDICO, RECEPCIONISTA),
                nombres("administrador", "medico", "recepcionista"));
        permiso(CONSULTAS_CREAR, roles(MEDICO), nombres("medico"));
        permiso(CONSULTAS_CORREGIR, roles(MEDICO), nombres("medico"));
        permiso(CONSULTAS_VER,
                roles(ADMINISTRADOR, MEDICO, ENFERMERO),
                nombres("administrador", "medico", "enfermero", "enfermera"));
        permiso(RECETAS_EMITIR, roles(MEDICO), nombres("medico"));
        permiso(HISTORIAS_GESTIONAR,
                roles(MEDICO, ENFERMERO),
                nombres("medico", "enfermero", "enfermera"));
        permiso(TRIAGES_GESTIONAR,
                roles(MEDICO, ENFERMERO),
                nombres("medico", "enfermero", "enfermera"));
        permiso(STOCK_GESTIONAR,
                roles(ADMINISTRADOR, ENFERMERO),
                nombres("administrador", "enfermero", "enfermera"));
        permiso(FACTURAS_GESTIONAR,
                roles(ADMINISTRADOR, RECEPCIONISTA),
                nombres("administrador", "recepcionista"));
        permiso(CONSULTORIOS_GESTIONAR,
                roles(ADMINISTRADOR, ENFERMERO, RECEPCIONISTA),
                nombres("administrador", "enfermero", "enfermera", "recepcionista"));
        permiso(ALERGIAS_GESTIONAR,
                roles(ADMINISTRADOR, MEDICO, ENFERMERO),
                nombres("administrador", "medico", "enfermero", "enfermera"));
        permiso(OBRAS_SOCIALES_GESTIONAR,
                roles(ADMINISTRADOR, RECEPCIONISTA),
                nombres("administrador", "recepcionista"));
    }

    private final IRolDeUsuarioDao rolDeUsuarioDao;
    private final IRolDao rolDao;
    private final IPermisoRolDao permisoRolDao;
    private final IPermisoDao permisoDao;

    public AutorizacionService() {
        this(RolDeUsuarioDaoFactory.getRolDeUsuarioDao(),
                RolDaoFactory.getRolDao(),
                PermisoRolDaoFactory.getPermisoRolDao(),
                PermisoDaoFactory.getPermisoDao());
    }

    public AutorizacionService(IRolDeUsuarioDao rolDeUsuarioDao, IRolDao rolDao,
            IPermisoRolDao permisoRolDao, IPermisoDao permisoDao) {
        this.rolDeUsuarioDao = rolDeUsuarioDao;
        this.rolDao = rolDao;
        this.permisoRolDao = permisoRolDao;
        this.permisoDao = permisoDao;
    }

    /**
     * Rol principal de un usuario con varios roles: se toma el de mayor
     * prioridad (admin > médico > enfermero > recepcionista > paciente).
     * Devuelve null si no tiene roles.
     */
    public static Integer rolPrincipal(Set<Integer> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }

        int[] prioridad = { ADMINISTRADOR, MEDICO, ENFERMERO, RECEPCIONISTA, PACIENTE };
        for (int rol : prioridad) {
            if (roles.contains(rol)) {
                return rol;
            }
        }
        return roles.iterator().next();
    }

    /**
     * Roles asignados al usuario. Devuelve un conjunto vacío si no tiene.
     */
    public Set<Integer> rolesDe(int usuarioId) {
        Set<Integer> roles = new HashSet<>();
        List<RolDeUsuario> asignaciones = rolDeUsuarioDao.listar();
        if (asignaciones == null) {
            return roles;
        }
        for (RolDeUsuario asignacion : asignaciones) {
            if (asignacion.getUsuarioId() == usuarioId) {
                roles.add(asignacion.getRolId());
            }
        }
        return roles;
    }

    public boolean tieneRol(int usuarioId, int... rolesPermitidos) {
        // Si la petición indicó un rol explícito, ese rol manda.
        Integer rolExplicito = UsuarioActual.getRol();
        if (rolExplicito != null) {
            for (int rol : rolesPermitidos) {
                if (rolExplicito == rol) {
                    return true;
                }
            }
            return false;
        }

        Set<Integer> roles = rolesDe(usuarioId);
        for (int rol : rolesPermitidos) {
            if (roles.contains(rol)) {
                return true;
            }
        }
        return false;
    }

    public void exigirRol(int usuarioId, int... rolesPermitidos) {
        if (!tieneRol(usuarioId, rolesPermitidos)) {
            throw new SecurityException("Acceso denegado: no tiene el rol requerido.");
        }
    }

    /**
     * Exige el permiso indicado. Primero compara por ID de rol, luego por el
     * nombre del rol (por si los IDs difieren en la base) y por último por las
     * tablas permisos/permisosrol.
     */
    public void exigirPermiso(int usuarioId, String permiso) {
        // Si la petición indicó un rol explícito (cabecera X-Rol-Id / X-Rol),
        // la autorización se resuelve con ese rol y no depende de una sesión.
        Integer rolExplicito = UsuarioActual.getRol();
        if (rolExplicito != null) {
            Set<Integer> permitidos =
                    ROLES_POR_PERMISO.getOrDefault(permiso, Collections.emptySet());

            if (permitidos.contains(rolExplicito)
                    || tienePermisoEnBase(Collections.singleton(rolExplicito), permiso)) {
                return;
            }

            throw new SecurityException("Acceso denegado: el rol " + rolExplicito
                    + " no tiene el permiso " + permiso + ".");
        }

        Set<Integer> roles = rolesDe(usuarioId);
        Set<Integer> rolesPermitidos = ROLES_POR_PERMISO.getOrDefault(permiso, Collections.emptySet());

        for (Integer rol : roles) {
            if (rolesPermitidos.contains(rol)) {
                return;
            }
        }

        if (tienePermisoEnBase(roles, permiso)) {
            return;
        }

        Set<String> nombresPermitidos = NOMBRES_POR_PERMISO.getOrDefault(permiso, Collections.emptySet());
        for (Integer rolId : roles) {
            Rol rol = rolDao.buscarPorId(rolId);
            if (rol != null && rol.getNombre() != null
                    && nombresPermitidos.contains(normalizar(rol.getNombre()))) {
                return;
            }
        }

        throw new SecurityException("Acceso denegado: falta el permiso " + permiso + ".");
    }

    public boolean tienePermiso(int usuarioId, String permiso) {
        try {
            exigirPermiso(usuarioId, permiso);
            return true;
        } catch (SecurityException e) {
            return false;
        }
    }

    private boolean tienePermisoEnBase(Set<Integer> roles, String permiso) {
        try {
            if (roles == null || roles.isEmpty()) {
                return false;
            }

            Map<Integer, String> nombres = new HashMap<>();
            List<Permiso> permisos = permisoDao.listar();
            if (permisos != null) {
                for (Permiso p : permisos) {
                    if (p.getNombre() != null) {
                        nombres.put(p.getIdPermiso(), p.getNombre());
                    }
                }
            }

            List<PermisoRol> relaciones = permisoRolDao.listar();
            if (relaciones != null) {
                for (PermisoRol relacion : relaciones) {
                    if (!roles.contains(relacion.getRolId())) {
                        continue;
                    }
                    String nombre = nombres.get(relacion.getPermisoId());
                    if (nombre != null && nombre.equalsIgnoreCase(permiso)) {
                        return true;
                    }
                }
            }
        } catch (RuntimeException e) {
            // Si las tablas de permisos no existen todavía, seguimos usando roles.
        }
        return false;
    }

    private static void permiso(String permiso, Set<Integer> roles, Set<String> nombres) {
        ROLES_POR_PERMISO.put(permiso, roles);
        NOMBRES_POR_PERMISO.put(permiso, nombres);
    }

    private static Set<Integer> roles(Integer... ids) {
        Set<Integer> set = new HashSet<>();
        Collections.addAll(set, ids);
        return Collections.unmodifiableSet(set);
    }

    private static Set<String> nombres(String... nombres) {
        Set<String> set = new HashSet<>();
        for (String nombre : nombres) {
            set.add(normalizar(nombre));
        }
        return Collections.unmodifiableSet(set);
    }

    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return sinAcentos.trim().toLowerCase();
    }
}
