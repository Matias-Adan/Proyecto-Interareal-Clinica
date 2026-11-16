package com.clinica.dao;

import com.clinica.modelo.ArticuloAdmin;
import com.clinica.modelo.Rol;
import com.clinica.modelo.UsuarioAdmin;

import java.util.List;

/**
 * Acceso a los datos de la vista del administrador:
 *   - usuarios activos (flagBorrado = 0) con sus roles,
 *   - catálogo de roles para asignar,
 *   - artículos con la marca de stock bajo.
 *
 * Las lecturas usan las vistas v_admin_usuarios y v_admin_articulos.
 */
public interface IVistaAdminDao {

    /** Usuarios activos, con los roles que tienen asignados. */
    List<UsuarioAdmin> listarUsuarios();

    /** Todos los roles disponibles para asignar. */
    List<Rol> listarRoles();

    /** Todos los artículos, con la marca de stock bajo. */
    List<ArticuloAdmin> listarArticulos();
}
