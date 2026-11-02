package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Factura;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FacturaDaoImpl implements IFacturaDao {

    @Override
    public List<Factura> listar() {
        List<Factura> lista = new ArrayList<>();
        String sql = "{CALL sp_seleccionar(\"facturas\")}";
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
            	Timestamp sqlTimestamp = rs.getTimestamp("fecha");
                
                Factura f = new Factura(
                        rs.getInt("idFactura"),
                        sqlTimestamp != null ? sqlTimestamp.toLocalDateTime() : null,
                        rs.getDouble("importeTotal"),
                        rs.getString("estado"),
                        rs.getInt("pacienteId"),
                        rs.getInt("obraSocialId")
                );
                lista.add(f);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar facturas: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Factura buscarPorId(int id) {
        Factura f = null;
        String sql = "{CALL sp_facturas_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                	Timestamp sqlTimestamp = rs.getTimestamp("fecha");
                    
                    f = new Factura(
                            rs.getInt("idFactura"),
                            sqlTimestamp != null ? sqlTimestamp.toLocalDateTime() : null,
                            rs.getDouble("importeTotal"),
                            rs.getString("estado"),
                            rs.getInt("pacienteId"),
                            rs.getInt("obraSocialId")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar factura: " + e.getMessage());
        }
        return f;
    }

    @Override
    public boolean insertar(Factura f) {
        String sql = "{CALL sp_facturas_insertar(?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setTimestamp(1, f.getFecha() != null ? Timestamp.valueOf(f.getFecha()) : null);
            cs.setDouble(2, f.getImporteTotal());
            cs.setString(3, f.getEstado());
            cs.setInt(4, f.getPacienteId());
            cs.setInt(5, f.getObraSocialId());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar factura: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Factura f) {
        String sql = "{CALL sp_facturas_actualizar(?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, f.getIdFactura());
            cs.setString(2, f.getEstado());


            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar factura: " + e.getMessage());
            return false;
        }
    }

}