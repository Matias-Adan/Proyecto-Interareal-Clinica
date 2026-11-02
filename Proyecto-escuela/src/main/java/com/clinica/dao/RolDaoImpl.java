package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Rol;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RolDaoImpl implements IRolDao {

    @Override
    public List<Rol> listar() {
        List<Rol> lista = new ArrayList<>();
        String sql = "{CALL sp_seleccionar(\"roles\")}";
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Rol r = new Rol(
                        rs.getInt("idRol"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                );
                lista.add(r);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar roles: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Rol buscarPorId(int id) {
        Rol r = null;
        String sql = "{CALL sp_roles_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    r = new Rol(
                            rs.getInt("idRol"),
                            rs.getString("nombre"),
                            rs.getString("descripcion")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar rol: " + e.getMessage());
        }
        return r;
    }

    @Override
    public boolean insertar(Rol r) {
        String sql = "{CALL sp_roles_insertar(?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setString(1, r.getNombre());
            cs.setString(2, r.getDescripcion());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar rol: " + e.getMessage());
            return false;
        }
    }


}