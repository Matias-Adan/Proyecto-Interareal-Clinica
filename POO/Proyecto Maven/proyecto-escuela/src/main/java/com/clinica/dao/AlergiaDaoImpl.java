package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Alergia;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlergiaDaoImpl implements IAlergiaDao {

    @Override
    public List<Alergia> listar() {
        List<Alergia> lista = new ArrayList<>();
        String sql = "{CALL sp_seleccionar(\"alergias\")}";
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Alergia a = new Alergia(
                        rs.getInt("idAlergia"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                );
                lista.add(a);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar alergias: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Alergia buscarPorId(int id) {
        Alergia a = null;
        String sql = "{CALL sp_alergias_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    a = new Alergia(
                            rs.getInt("idAlergia"),
                            rs.getString("nombre"),
                            rs.getString("descripcion")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar alergia: " + e.getMessage());
        }
        return a;
    }

    @Override
    public boolean insertar(Alergia a) {
        String sql = "{CALL sp_alergias_insertar(?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setString(1, a.getNombre());
            cs.setString(2, a.getDescripcion());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar alergia: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Alergia a) {
        String sql = "{CALL sp_alergias_actualizar(?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, a.getIdAlergia());
            cs.setString(2, a.getNombre());
            cs.setString(3, a.getDescripcion());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar alergia: " + e.getMessage());
            return false;
        }
    }
}