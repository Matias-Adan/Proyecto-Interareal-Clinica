package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.ObraSocial;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ObraSocialDaoImpl implements IObraSocialDao {

    @Override
    public List<ObraSocial> listar() {
        List<ObraSocial> lista = new ArrayList<>();
        // Ajusta el nombre de la tabla según tu base de datos (ej. "obras_sociales" o "obra_social")
        String sql = "{CALL sp_seleccionar(\"obras_sociales\")}";
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                ObraSocial os = new ObraSocial(
                        rs.getInt("idObraSocial"),
                        rs.getString("nombre"),
                        rs.getInt("numeroConvenio"),
                        rs.getString("beneficios"),
                        rs.getString("cobertura"),
                        rs.getBoolean("flagBorrado")
                );
                lista.add(os);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar obras sociales: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public ObraSocial buscarPorId(int id) {
        ObraSocial os = null;
        String sql = "{CALL sp_obras_sociales_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    os = new ObraSocial(
                            rs.getInt("idObraSocial"),
                            rs.getString("nombre"),
                            rs.getInt("numeroConvenio"),
                            rs.getString("beneficios"),
                            rs.getString("cobertura"),
                            rs.getBoolean("flagBorrado")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar obra social: " + e.getMessage());
        }
        return os;
    }

    @Override
    public boolean insertar(ObraSocial os) {
        String sql = "{CALL sp_obras_sociales_insertar(?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setString(1, os.getNombre());
            cs.setInt(2, os.getNumeroConvenio());
            cs.setString(3, os.getBeneficios());
            cs.setString(4, os.getCobertura());
            cs.setBoolean(5, os.isFlagBorrado());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar obra social: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(ObraSocial os) {
        String sql = "{CALL sp_obras_sociales_actualizar(?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, os.getIdObraSocial());
            cs.setString(2, os.getNombre());
            cs.setInt(3, os.getNumeroConvenio());
            cs.setString(4, os.getBeneficios());
            cs.setString(5, os.getCobertura());
            cs.setBoolean(6, os.isFlagBorrado());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar obra social: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "{CALL sp_obras_sociales_eliminar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar obra social: " + e.getMessage());
            return false;
        }
    }
}