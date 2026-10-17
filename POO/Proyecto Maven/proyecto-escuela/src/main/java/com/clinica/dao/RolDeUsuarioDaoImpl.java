package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.RolDeUsuario;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RolDeUsuarioDaoImpl implements IRolDeUsuarioDao {

    @Override
    public List<RolDeUsuario> listar() {
        List<RolDeUsuario> lista = new ArrayList<>();
        // Ajusta el nombre de la tabla en el procedure si difiere (ej. "roles_usuario")
        String sql = "{CALL sp_seleccionar(\"roles_usuario\")}";
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                RolDeUsuario ru = new RolDeUsuario(
                        rs.getInt("idRolUsuario"),
                        rs.getInt("usuarioId"),
                        rs.getInt("rolId")
                );
                lista.add(ru);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar roles de usuario: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public RolDeUsuario buscarPorId(int id) {
        RolDeUsuario ru = null;
        String sql = "{CALL sp_roles_usuario_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    ru = new RolDeUsuario(
                            rs.getInt("idRolUsuario"),
                            rs.getInt("usuarioId"),
                            rs.getInt("rolId")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar rol de usuario: " + e.getMessage());
        }
        return ru;
    }

    @Override
    public boolean insertar(RolDeUsuario ru) {
        String sql = "{CALL sp_roles_usuario_insertar(?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, ru.getUsuarioId());
            cs.setInt(2, ru.getRolId());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar rol de usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(RolDeUsuario ru) {
        String sql = "{CALL sp_roles_usuario_actualizar(?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, ru.getIdRolUsuario());
            cs.setInt(2, ru.getUsuarioId());
            cs.setInt(3, ru.getRolId());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar rol de usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "{CALL sp_roles_usuario_eliminar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar rol de usuario: " + e.getMessage());
            return false;
        }
    }
}