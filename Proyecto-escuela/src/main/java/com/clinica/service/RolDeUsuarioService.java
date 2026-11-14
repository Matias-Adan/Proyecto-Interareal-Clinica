package com.clinica.service;

import com.clinica.dao.IRolDao;
import com.clinica.dao.IRolDeUsuarioDao;
import com.clinica.factory.RolDaoFactory;
import com.clinica.factory.RolDeUsuarioDaoFactory;
import com.clinica.modelo.Rol;
import com.clinica.modelo.RolDeUsuario;
import com.clinica.security.UsuarioActual;
import java.util.List;

/**
 * Reglas de negocio de asignación de roles:
 *  - Sólo un administrador puede asignar o quitar roles.
 *  - Un usuario no puede tener el mismo rol dos veces.
 *  - El rol asignado debe existir.
 */
public class RolDeUsuarioService implements IRolDeUsuarioDao {

    private final IRolDeUsuarioDao rolDeUsuarioDao;
    private final IRolDao rolDao;
    private final AutorizacionService autorizacion;

    public RolDeUsuarioService() {
        this(RolDeUsuarioDaoFactory.getRolDeUsuarioDao(),
                RolDaoFactory.getRolDao(), new AutorizacionService());
    }

    public RolDeUsuarioService(IRolDeUsuarioDao rolDeUsuarioDao, IRolDao rolDao,
            AutorizacionService autorizacion) {
        this.rolDeUsuarioDao = rolDeUsuarioDao;
        this.rolDao = rolDao;
        this.autorizacion = autorizacion;
    }

    // ------------------------------------------------------------------
    // LECTURA
    // ------------------------------------------------------------------

    @Override
    public List<RolDeUsuario> listar() {
        return rolDeUsuarioDao.listar();
    }

    @Override
    public RolDeUsuario buscarPorId(int id) {
        return rolDeUsuarioDao.buscarPorId(id);
    }

    // ------------------------------------------------------------------
    // ALTA
    // ------------------------------------------------------------------

    @Override
    public boolean insertar(RolDeUsuario asignacion) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.ROLES_GESTIONAR);

        if (asignacion == null) {
            throw new IllegalArgumentException("La asignación de rol no puede ser nula.");
        }
        if (asignacion.getUsuarioId() <= 0) {
            throw new IllegalArgumentException("La asignación debe indicar un usuario.");
        }
        if (asignacion.getRolId() <= 0) {
            throw new IllegalArgumentException("La asignación debe indicar un rol.");
        }

        Rol rol = rolDao.buscarPorId(asignacion.getRolId());
        if (rol == null) {
            throw new IllegalArgumentException("El rol que intenta asignar no existe.");
        }

        // Regla: un usuario no puede tener el mismo rol dos veces.
        List<RolDeUsuario> existentes = rolDeUsuarioDao.listar();
        if (existentes != null) {
            for (RolDeUsuario existente : existentes) {
                if (existente.getUsuarioId() == asignacion.getUsuarioId()
                        && existente.getRolId() == asignacion.getRolId()) {
                    throw new IllegalStateException(
                            "El usuario ya tiene asignado ese rol.");
                }
            }
        }

        return rolDeUsuarioDao.insertar(asignacion);
    }

    // ------------------------------------------------------------------
    // BAJA
    // ------------------------------------------------------------------

    @Override
    public boolean eliminar(int id) {
        int actor = UsuarioActual.requerido();
        autorizacion.exigirPermiso(actor, AutorizacionService.ROLES_GESTIONAR);

        if (id <= 0) {
            throw new IllegalArgumentException("La asignación de rol no es válida.");
        }

        return rolDeUsuarioDao.eliminar(id);
    }
}
