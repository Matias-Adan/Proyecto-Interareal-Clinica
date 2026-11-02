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
        String sql = "{CALL sp_seleccionar(\"detallesreceta\")}";
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                DetalleReceta dr = new DetalleReceta(
                        rs.getInt("idDetalle"),
                        rs.getInt("cantidad"),
                        rs.getString("dosis"),
                        rs.getString("frecuencia"),
                        rs.getString("duracion"),
                        rs.getInt("recetaId"),
                        rs.getInt("articuloId")
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
        String sql = "{CALL sp_detallesreceta_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    dr = new DetalleReceta(
                            rs.getInt("idDetalle"),
                            rs.getInt("cantidad"),
                            rs.getString("dosis"),
                            rs.getString("frecuencia"),
                            rs.getString("duracion"),
                            rs.getInt("recetaId"),
                            rs.getInt("articuloId")
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
        String sql = "{CALL sp_detallesreceta_insertar(?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, dr.getCantidad());
            cs.setString(2, dr.getDosis());
            cs.setString(3, dr.getFrecuencia());
            cs.setString(4, dr.getDuracion());
            cs.setInt(5, dr.getRecetaId());
            cs.setInt(6, dr.getArticuloId());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar detalle de receta: " + e.getMessage());
            return false;
        }
    }

}