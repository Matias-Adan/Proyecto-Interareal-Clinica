package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.DetalleReceta;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetalleRecetaDaoImpl implements IDetalleRecetaDao {

    @Override
    public List<DetalleReceta> listar() {
        List<DetalleReceta> lista = new ArrayList<>();
        String sql = "{CALL sp_seleccionar(\"detalle_recetas\")}";
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                DetalleReceta dr = new DetalleReceta(
                        rs.getInt("idDetalle"),
                        rs.getInt("cantidad"),
                        rs.getInt("dosis"),
                        rs.getString("frecuencia"),
                        rs.getString("duracion"),
                        rs.getInt("recetaId"),
                        rs.getInt("medicamentoId")
                );
                lista.add(dr);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar detalles de recetas: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public DetalleReceta buscarPorId(int id) {
        DetalleReceta dr = null;
        String sql = "{CALL sp_detalles_receta_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    dr = new DetalleReceta(
                            rs.getInt("idDetalle"),
                            rs.getInt("cantidad"),
                            rs.getInt("dosis"),
                            rs.getString("frecuencia"),
                            rs.getString("duracion"),
                            rs.getInt("recetaId"),
                            rs.getInt("medicamentoId")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar detalle de receta: " + e.getMessage());
        }
        return dr;
    }

    @Override
    public boolean insertar(DetalleReceta dr) {
        String sql = "{CALL sp_detalles_receta_insertar(?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, dr.getCantidad());
            cs.setInt(2, dr.getDosis());
            cs.setString(3, dr.getFrecuencia());
            cs.setString(4, dr.getDuracion());
            cs.setInt(5, dr.getRecetaId());
            cs.setInt(6, dr.getMedicamentoId());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar detalle de receta: " + e.getMessage());
            return false;
        }
    }

}