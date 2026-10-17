package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.MovimientoStock;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MovimientoStockDaoImpl implements IMovimientoStockDao {

    @Override
    public List<MovimientoStock> listar() {
        List<MovimientoStock> lista = new ArrayList<>();
        String sql = "{CALL sp_seleccionar(\"movimientos_stock\")}";
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Timestamp sqlTimestamp = rs.getTimestamp("fechaHora");
                
                MovimientoStock ms = new MovimientoStock(
                        rs.getInt("movimientoStock"),
                        rs.getInt("idArticulo"),
                        rs.getString("tipoMovimiento"),
                        rs.getInt("cantidad"),
                        rs.getString("motivo"),
                        sqlTimestamp != null ? sqlTimestamp.toLocalDateTime() : null,
                        rs.getInt("usuarioId")
                );
                lista.add(ms);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar movimientos de stock: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public MovimientoStock buscarPorId(int id) {
        MovimientoStock ms = null;
        String sql = "{CALL sp_movimientos_stock_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    Timestamp sqlTimestamp = rs.getTimestamp("fechaHora");
                    
                    ms = new MovimientoStock(
                            rs.getInt("movimientoStock"),
                            rs.getInt("idArticulo"),
                            rs.getString("tipoMovimiento"),
                            rs.getInt("cantidad"),
                            rs.getString("motivo"),
                            sqlTimestamp != null ? sqlTimestamp.toLocalDateTime() : null,
                            rs.getInt("usuarioId")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar movimiento de stock: " + e.getMessage());
        }
        return ms;
    }

    @Override
    public boolean insertar(MovimientoStock ms) {
        String sql = "{CALL sp_movimientos_stock_insertar(?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, ms.getIdArticulo());
            cs.setString(2, ms.getTipoMovimiento());
            cs.setInt(3, ms.getCantidad());
            cs.setString(4, ms.getMotivo());
            cs.setTimestamp(5, ms.getFechaHora() != null ? Timestamp.valueOf(ms.getFechaHora()) : null);
            cs.setInt(6, ms.getUsuarioId());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar movimiento de stock: " + e.getMessage());
            return false;
        }
    }


}