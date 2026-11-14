package com.clinica.service;

import com.clinica.dao.IRolDao;
import com.clinica.dao.IRolDeUsuarioDao;
import com.clinica.dao.IUsuarioDao;
import com.clinica.factory.RolDaoFactory;
import com.clinica.factory.RolDeUsuarioDaoFactory;
import com.clinica.factory.UsuarioDaoFactory;
import com.clinica.modelo.Rol;
import com.clinica.modelo.RolDeUsuario;
import com.clinica.modelo.Usuario;
import com.clinica.security.UsuarioActual;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Reglas de negocio de usuarios y seguridad:
 *  - DNI de 7 u 8 dígitos y email con formato válido.
 *  - DNI y email únicos.
 *  - Fecha de nacimiento estrictamente anterior a hoy.
 *  - Un usuario no puede tener el mismo rol dos veces.
 *  - Sólo un administrador puede gestionar usuarios y roles.
 *
 * El servlet sigue llamando a los mismos métodos del DAO (insertar, listar,
 * actualizar, eliminar, login, cambiarEstadoBorrado), pero ahora esos métodos
 * pasan por este servicio.
 */
public class UsuarioService implements IUsuarioDao {

    private static final Pattern EMAIL_VALIDO =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final IUsuarioDao usuarioDao;
    private final IRolDeUsuarioDao rolDeUsuarioDao;
    private final IRolDao rolDao;
    private final AutorizacionService autorizacion;

    public UsuarioService() {
        this(UsuarioDaoFactory.getUsuarioDao(),
                RolDeUsuarioDaoFactory.getRolDeUsuarioDao(),
                RolDaoFactory.getRolDao(),
                new AutorizacionService());
    }

    public UsuarioService(IUsuarioDao usuarioDao, IRolDeUsuarioDao rolDeUsuarioDao,
            IRolDao rolDao, AutorizacionService autorizacion) {
        this.usuarioDao = usuarioDao;
        this.rolDeUsuarioDao = rolDeUsuarioDao;
        this.rolDao = rolDao;
        this.autorizacion = autorizacion;
    }

    // ------------------------------------------------------------------
    // ALTA
    // ------------------------------------------------------------------

    @Override
    public boolean insertar(Usuario usuario) {
        // El registro público no requiere sesión. Si hay un usuario
        // identificado, debe ser administrador.
        Integer actor = UsuarioActual.get();
        if (actor != null && actor > 0) {
            autorizacion.exigirPermiso(actor, AutorizacionService.USUARIOS_GESTIONAR);
        }

        validarDatos(usuario, true);
        validarUnicidad(usuario);

        return usuarioDao.insertar(usuario);
    }

    /**
     * Método conservado para compatibilidad con el código original.
     */
    public boolean registrarUsuario(Usuario usuario) {
        return insertar(usuario);
    }

    // ------------------------------------------------------------------
    // LECTURA
    // ------------------------------------------------------------------

    @Override
    public List<Usuario> listar() {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.USUARIOS_GESTIONAR);
        return usuarioDao.listar();
    }

    @Override
    public Usuario buscarPorId(int id) {
        int actor = UsuarioActual.requerido();
        if (actor != id) {
            autorizacion.exigirPermiso(actor, AutorizacionService.USUARIOS_GESTIONAR);
        }
        return usuarioDao.buscarPorId(id);
    }

    // ------------------------------------------------------------------
    // MODIFICACIÓN
    // ------------------------------------------------------------------

    @Override
    public boolean actualizar(Usuario usuario) {
        int actor = UsuarioActual.requerido();
        if (actor != usuario.getIdUsuario()) {
            autorizacion.exigirPermiso(actor, AutorizacionService.USUARIOS_GESTIONAR);
        } else {
            // Un usuario puede editar sus propios datos, pero no darse de baja
            // ni cambiar su estado de borrado por esta vía.
            Usuario existente = usuarioDao.buscarPorId(usuario.getIdUsuario());
            if (existente != null) {
                usuario.setFlagBorrado(existente.isFlagBorrado());
            }
        }

        validarDatos(usuario, false);
        validarUnicidad(usuario);

        return usuarioDao.actualizar(usuario);
    }

    @Override
    public boolean eliminar(int id) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.USUARIOS_GESTIONAR);

        // Baja lógica: nunca se borra físicamente un usuario.
        return usuarioDao.cambiarEstadoBorrado(id, true);
    }

    @Override
    public boolean cambiarEstadoBorrado(int id, boolean flagBorrado) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.USUARIOS_GESTIONAR);
        return usuarioDao.cambiarEstadoBorrado(id, flagBorrado);
    }

    // ------------------------------------------------------------------
    // LOGIN
    // ------------------------------------------------------------------

    @Override
    public Usuario login(String email, String password) {
        if (email == null || email.trim().isEmpty()
                || password == null || password.isEmpty()) {
            return null;
        }

        Usuario usuario = usuarioDao.login(email.trim(), password);
        if (usuario == null || usuario.isFlagBorrado()) {
            return null;
        }
        return usuario;
    }

    // ------------------------------------------------------------------
    // ROLES
    // ------------------------------------------------------------------

    public boolean asignarRolAUsuario(int idUsuario, int idRol) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.ROLES_GESTIONAR);

        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El usuario no es válido.");
        }

        Rol rol = rolDao.buscarPorId(idRol);
        if (rol == null) {
            throw new IllegalArgumentException("El rol que intenta asignar no existe.");
        }

        List<RolDeUsuario> asignaciones = rolDeUsuarioDao.listar();
        if (asignaciones != null) {
            for (RolDeUsuario asignacion : asignaciones) {
                if (asignacion.getUsuarioId() == idUsuario
                        && asignacion.getRolId() == idRol) {
                    throw new IllegalStateException(
                            "El usuario ya tiene asignado ese rol.");
                }
            }
        }

        RolDeUsuario nuevo = new RolDeUsuario();
        nuevo.setUsuarioId(idUsuario);
        nuevo.setRolId(idRol);

        return rolDeUsuarioDao.insertar(nuevo);
    }

    // ------------------------------------------------------------------
    // VALIDACIONES
    // ------------------------------------------------------------------

    private void validarDatos(Usuario usuario, boolean esAlta) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }

        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del usuario es obligatorio.");
        }
        if (usuario.getApellido() == null || usuario.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido del usuario es obligatorio.");
        }

        // Regla: el DNI debe tener 7 u 8 dígitos.
        if (!String.valueOf(usuario.getDni()).matches("\\d{7,8}")) {
            throw new IllegalArgumentException("El DNI debe tener 7 u 8 dígitos.");
        }

        // Regla: email con formato válido.
        if (usuario.getEmail() == null || !EMAIL_VALIDO.matcher(usuario.getEmail().trim()).matches()) {
            throw new IllegalArgumentException("El formato del email no es válido.");
        }

        // Regla: fecha de nacimiento estrictamente anterior a la fecha actual.
        if (usuario.getFechaNacimiento() == null
                || !usuario.getFechaNacimiento().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La fecha de nacimiento debe ser anterior a la fecha actual.");
        }

        if (esAlta && (usuario.getPassword() == null || usuario.getPassword().isEmpty())) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
    }

    private void validarUnicidad(Usuario usuario) {
        List<Usuario> existentes = usuarioDao.listar();
        if (existentes == null) {
            return;
        }

        for (Usuario existente : existentes) {
            if (usuario.getIdUsuario() > 0
                    && existente.getIdUsuario() == usuario.getIdUsuario()) {
                continue;
            }
            if (existente.getDni() == usuario.getDni()) {
                throw new IllegalArgumentException("El DNI ya se encuentra registrado.");
            }
            if (existente.getEmail() != null
                    && existente.getEmail().equalsIgnoreCase(usuario.getEmail().trim())) {
                throw new IllegalArgumentException("El email ya se encuentra registrado.");
            }
        }
    }
}
