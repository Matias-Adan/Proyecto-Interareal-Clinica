package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Permiso;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PermisoDaoImpl implements IPermisoDao {

    @Override
    public List<Permiso> listar() {
        List<Permiso> lista = new ArrayList<>();
        String sql = "{CALL sp_seleccionar(\"permisos\")}";
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Permiso p = new Permiso(
                        rs.getInt("idPermiso"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                );
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar permisos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Permiso buscarPorId(int id) {
        Permiso p = null;
        String sql = "{CALL sp_permisos_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    p = new Permiso(
                            rs.getInt("idPermiso"),
                            rs.getString("nombre"),
                            rs.getString("descripcion")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar permiso: " + e.getMessage());
        }
        return p;
    }

    @Override
    public boolean insertar(Permiso p) {
        String sql = "{CALL sp_permisos_insertar(?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setString(1, p.getNombre());
            cs.setString(2, p.getDescripcion());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar permiso: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Permiso p) {
        String sql = "{CALL sp_permisos_actualizar(?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, p.getIdPermiso());
            cs.setString(2, p.getNombre());
            cs.setString(3, p.getDescripcion());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar permiso: " + e.getMessage());
            return false;
        }
    }


}