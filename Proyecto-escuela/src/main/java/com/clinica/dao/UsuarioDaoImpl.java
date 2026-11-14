package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Usuario;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

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
        String sql = "{CALL sp_usuarios_insertar(?, ?, ?, ?, ?, ?, ?, ?)}";
        
        // Hash antes de enviar a la base de datos
        String passwordHash = BCrypt.hashpw(u.getPassword(), BCrypt.gensalt());
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setString(1, u.getNombre());
            cs.setString(2, u.getApellido());
            cs.setInt(3, u.getDni());
            cs.setDate(4, u.getFechaNacimiento() != null ? Date.valueOf(u.getFechaNacimiento()) : null);
            // El teléfono es opcional: si no se cargó, se guarda NULL (la BD lo permite).
            fijarTelefonoOpcional(cs, 5, u.getTelefono());
            cs.setString(6, u.getEmail());
            cs.setString(7, passwordHash);
            // obraSocialId 0 significa "sin obra social". Se envía NULL para no
            // violar la FK fk_usuarios_obrasocial (no existe la obra social 0).
            fijarObraSocialOpcional(cs, 8, u.getObraSocialId());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * La obra social es opcional: si el valor no es válido se envía NULL.
     * Enviar 0 rompería la clave foránea fk_usuarios_obrasocial.
     */
    private static void fijarObraSocialOpcional(CallableStatement cs, int indice, int obraSocialId)
            throws SQLException {
        if (obraSocialId > 0) {
            cs.setInt(indice, obraSocialId);
        } else {
            cs.setNull(indice, Types.INTEGER);
        }
    }

    /**
     * El teléfono es opcional: si no se cargó se envía NULL en vez de 0.
     */
    private static void fijarTelefonoOpcional(CallableStatement cs, int indice, int telefono)
            throws SQLException {
        if (telefono > 0) {
            cs.setInt(indice, telefono);
        } else {
            cs.setNull(indice, Types.VARCHAR);
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
            fijarTelefonoOpcional(cs, 6, u.getTelefono());
            cs.setString(7, u.getEmail());
            cs.setString(8, u.getPassword());
            fijarObraSocialOpcional(cs, 9, u.getObraSocialId());
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
    public Usuario login(String usuario, String password) {
        Usuario u = null;
        String sql = "{CALL sp_usuarios_login(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setString(1, usuario);
            
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    String passwordAlmacenada = rs.getString("password");
                    

                    System.out.println("=== DEBUG LOGIN ===");
                    System.out.println("Password escrita en el form: [" + password + "]");
                    System.out.println("Hash recuperado de la BD:   [" + passwordAlmacenada + "]");
                    System.out.println("Longitud del hash en BD:    " + (passwordAlmacenada != null ? passwordAlmacenada.length() : "NULL"));
                    
                    boolean passwordValida = false;
                    if (passwordAlmacenada != null) {
                        passwordValida = BCrypt.checkpw(password, passwordAlmacenada.trim());
                    }
                    System.out.println("Resultado de BCrypt.checkpw: " + passwordValida);
                    System.out.println("===================");
                    
                    if (passwordValida) {
                        Date sqlDate = rs.getDate("fechaNacimiento");
                        
                        u = new Usuario(
                                rs.getInt("idUsuario"),
                                rs.getString("nombre"),
                                rs.getString("apellido"),
                                rs.getInt("dni"),
                                sqlDate != null ? sqlDate.toLocalDate() : null,
                                rs.getInt("telefono"),
                                rs.getString("email"),
                                passwordAlmacenada, // o rs.getString("password")
                                rs.getInt("obraSocialId"),
                                rs.getBoolean("flagBorrado")
                        );
                    } else {
                        System.out.println("❌ Contraseña incorrecta para el usuario: " + usuario);
                    }
                } else {
                    System.out.println("❌ Usuario no encontrado: " + usuario);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al intentar iniciar sesión: " + e.getMessage());
        }
        
        // Retornará el objeto Usuario si todo es correcto, o null si falló algo
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