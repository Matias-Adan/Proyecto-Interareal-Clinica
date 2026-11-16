package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.ArticuloAdmin;
import com.clinica.modelo.Rol;
import com.clinica.modelo.RolUsuarioAdmin;
import com.clinica.modelo.UsuarioAdmin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO de la vista del administrador.
 *
 * Lee las vistas:
 *   - v_admin_usuarios (usuarios activos + obra social),
 *   - v_admin_articulos (artículos + stock bajo).
 *
 * Los roles de cada usuario se resuelven con una sola consulta a
 * roldeusuario/roles y se agrupan en memoria para no hacer una consulta por
 * usuario.
 */
public class VistaAdminDaoImpl implements IVistaAdminDao {

    private Connection abrirConexion() {
        Connection con = Conexion.getInstancia().nuevaConexion();
        if (con == null) {
            throw new DataAccessException("No hay conexión con la base de datos.");
        }
        return con;
    }

    @Override
    public List<UsuarioAdmin> listarUsuarios() {
        List<UsuarioAdmin> usuarios = new ArrayList<>();
        String sql = "SELECT usuario_id, nombre, apellido, nombre_completo, dni, email, "
                + "telefono, obra_social "
                + "FROM v_admin_usuarios "
                + "ORDER BY apellido ASC, nombre ASC";

        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(new UsuarioAdmin(
                        rs.getInt("usuario_id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("nombre_completo"),
                        rs.getString("dni"),
                        rs.getString("email"),
                        rs.getString("telefono"),
                        rs.getString("obra_social")
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "No se pudieron leer los usuarios: " + e.getMessage(), e);
        }

        Map<Integer, List<RolUsuarioAdmin>> rolesPorUsuario = listarRolesPorUsuario();
        for (UsuarioAdmin usuario : usuarios) {
            List<RolUsuarioAdmin> roles = rolesPorUsuario.get(usuario.getIdUsuario());
            usuario.setRoles(roles == null ? new ArrayList<>() : roles);
        }
        return usuarios;
    }

    /**
     * Asignaciones de rol agrupadas por usuario. Incluye los datos del rol y
     * el id de la asignación (necesario para poder quitarla).
     */
    private Map<Integer, List<RolUsuarioAdmin>> listarRolesPorUsuario() {
        Map<Integer, List<RolUsuarioAdmin>> rolesPorUsuario = new HashMap<>();
        String sql = "SELECT ru.idRolUsuario, ru.usuarioId, ru.rolId, "
                + "r.nombre, r.descripcion "
                + "FROM roldeusuario ru "
                + "JOIN roles r ON r.idRol = ru.rolId "
                + "ORDER BY ru.usuarioId ASC, r.nombre ASC";

        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int usuarioId = rs.getInt("usuarioId");
                rolesPorUsuario
                        .computeIfAbsent(usuarioId, clave -> new ArrayList<>())
                        .add(new RolUsuarioAdmin(
                                rs.getInt("idRolUsuario"),
                                rs.getInt("rolId"),
                                rs.getString("nombre"),
                                rs.getString("descripcion")
                        ));
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "No se pudieron leer los roles de los usuarios: " + e.getMessage(), e);
        }
        return rolesPorUsuario;
    }

    @Override
    public List<Rol> listarRoles() {
        List<Rol> roles = new ArrayList<>();
        String sql = "SELECT idRol, nombre, descripcion FROM roles ORDER BY nombre ASC";

        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                roles.add(new Rol(
                        rs.getInt("idRol"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "No se pudieron leer los roles: " + e.getMessage(), e);
        }
        return roles;
    }

    @Override
    public List<ArticuloAdmin> listarArticulos() {
        List<ArticuloAdmin> articulos = new ArrayList<>();
        String sql = "SELECT idArticulo, nombre, descripcion, presentacion, tipoArticulo, "
                + "stockActual, stockMinimo, stock_bajo, cantidad_a_reponer "
                + "FROM v_admin_articulos "
                + "ORDER BY stock_bajo DESC, nombre ASC";

        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                articulos.add(new ArticuloAdmin(
                        rs.getInt("idArticulo"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getString("presentacion"),
                        rs.getString("tipoArticulo"),
                        rs.getInt("stockActual"),
                        rs.getInt("stockMinimo"),
                        rs.getInt("stock_bajo") == 1,
                        rs.getInt("cantidad_a_reponer")
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "No se pudieron leer los artículos: " + e.getMessage(), e);
        }
        return articulos;
    }
}
