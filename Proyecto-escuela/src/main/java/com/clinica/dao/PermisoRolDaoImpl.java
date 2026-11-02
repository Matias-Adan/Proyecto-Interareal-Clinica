package com.clinica.dao;

import com.clinica.modelo.PermisoRol;
import com.clinica.config.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermisoRolDaoImpl implements IPermisoRolDao {

	@Override
    public List<PermisoRol> listar() {
        List<PermisoRol> lista = new ArrayList<>();
        String sql = "{CALL sp_seleccionar(\"permisorol\")}";
        try (Connection con = Conexion.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PermisoRol pr = new PermisoRol(
                    rs.getInt("idPermisoRol"),
                    rs.getInt("rolId"),
                    rs.getInt("permisoId")
                );
                lista.add(pr);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
	
	@Override
    public PermisoRol buscarPorId(int id) {
        PermisoRol p = null;
        String sql = "{CALL sp_alergias_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    p = new PermisoRol(
                            rs.getInt("idPermisoRol"),
                            rs.getInt("rolId"),
                            rs.getInt("permisoId")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar el permiso al rol: " + e.getMessage());
        }
        return p;
    }
	
	@Override
    public boolean insertar(PermisoRol p) {
        String sql = "{CALL sp_PermisoRol_insertar(?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, p.getRolId());
            cs.setInt(2, p.getPermisoId());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar alergia: " + e.getMessage());
            return false;
        }
    }

	

	@Override
    public boolean eliminar(int id) {
        String sql = "{CALL sp_permisorol_eliminar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar artículo: " + e.getMessage());
            return false;
        }
    }
}