package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Usuario;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDaoImpl implements IUsuarioDao {

    @Override
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "{CALL sp_seleccionar(\"usuarios\")}";
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                // Manejo de fecha por si viene nula de la base de datos
                Date sqlDate = rs.getDate("fechaNacimiento");
                
                Usuario u = new Usuario(
                        rs.getInt("idUsuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getInt("dni"),
                        sqlDate != null ? sqlDate.toLocalDate() : null,
                        rs.getInt("telefono"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getInt("obraSocialId"),
                        rs.getBoolean("flagBorrado")
                );
                lista.add(u);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Usuario buscarPorId(int id) {
        Usuario u = null;
        String sql = "{CALL sp_usuarios_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    Date sqlDate = rs.getDate("fechaNacimiento");
                    
                    u = new Usuario(
                            rs.getInt("idUsuario"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getInt("dni"),
                            sqlDate != null ? sqlDate.toLocalDate() : null,
                            rs.getInt("telefono"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getInt("obraSocialId"),
                            rs.getBoolean("flagBorrado")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar usuario: " + e.getMessage());
        }
        return u;
    }

    @Override
    public boolean insertar(Usuario u) {
        // Ajusta la cantidad de interrogaciones según los parámetros que espere tu SP de insertar usuarios
        String sql = "{CALL sp_usuarios_insertar(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setString(1, u.getNombre());
            cs.setString(2, u.getApellido());
            cs.setInt(3, u.getDni());
            cs.setDate(4, u.getFechaNacimiento() != null ? Date.valueOf(u.getFechaNacimiento()) : null);
            cs.setInt(5, u.getTelefono());
            cs.setString(6, u.getEmail());
            cs.setString(7, u.getPassword());
            cs.setInt(8, u.getObraSocialId());
            cs.setBoolean(9, u.isFlagBorrado());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Usuario u) {
        // Ajusta la cantidad de interrogaciones según los parámetros de tu SP de actualizar usuarios
        String sql = "{CALL sp_usuarios_actualizar(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, u.getIdUsuario());
            cs.setString(2, u.getNombre());
            cs.setString(3, u.getApellido());
            cs.setInt(4, u.getDni());
            cs.setDate(5, u.getFechaNacimiento() != null ? Date.valueOf(u.getFechaNacimiento()) : null);
            cs.setInt(6, u.getTelefono());
            cs.setString(7, u.getEmail());
            cs.setString(8, u.getPassword());
            cs.setInt(9, u.getObraSocialId());
            cs.setBoolean(10, u.isFlagBorrado());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "{CALL sp_usuarios_eliminar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public Usuario login(String usuario) {
        Usuario u = null;
        String sql = "{CALL sp_usuarios_login(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setString(1, usuario);
            
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    Date sqlDate = rs.getDate("fechaNacimiento");
                    
                    u = new Usuario(
                            rs.getInt("idUsuario"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getInt("dni"),
                            sqlDate != null ? sqlDate.toLocalDate() : null,
                            rs.getInt("telefono"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getInt("obraSocialId"),
                            rs.getBoolean("flagBorrado")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al intentar iniciar sesión: " + e.getMessage());
        }
        return u;
    }
    
    @Override
    public boolean cambiarEstadoBorrado(int id, boolean estado) {
    	String sql = "{CALL sp_usuarios_cambiar_estado(?, ?)}";
		
    	try (Connection con = Conexion.getInstancia().getConexion();
    		 CallableStatement cs = con.prepareCall(sql)){
    		
    		cs.setInt(1, id);
    		cs.setBoolean(2, estado);
    		
    		return cs.executeUpdate() > 0;
    	} catch (SQLException e) {
    		System.err.println("❌ Error al cambiar el flag de borrado del usuario: " + e.getMessage());
            return false;
        }
		
    }
    
}