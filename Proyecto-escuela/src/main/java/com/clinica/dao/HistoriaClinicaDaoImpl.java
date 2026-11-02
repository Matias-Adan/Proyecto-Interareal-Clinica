package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.HistoriaClinica;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HistoriaClinicaDaoImpl implements IHistoriaClinicaDao {

    @Override
    public List<HistoriaClinica> listar() {
        List<HistoriaClinica> lista = new ArrayList<>();
        String sql = "{CALL sp_seleccionar(\"historiasclinicas\")}";
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Date sqlDate = rs.getDate("fechaApertura");
                
                HistoriaClinica hc = new HistoriaClinica(
                        rs.getInt("idHistoria"),
                        sqlDate != null ? sqlDate.toLocalDate() : null,
                        rs.getString("antecedentes"),
                        rs.getString("observacion"),
                        rs.getInt("pacienteId"),
                        rs.getBoolean("flagBorrado")
                );
                lista.add(hc);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar historias clínicas: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public HistoriaClinica buscarPorId(int id) {
        HistoriaClinica hc = null;
        String sql = "{CALL sp_historiasclinicas_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    Date sqlDate = rs.getDate("fechaApertura");
                    
                    hc = new HistoriaClinica(
                            rs.getInt("idHistoria"),
                            sqlDate != null ? sqlDate.toLocalDate() : null,
                            rs.getString("antecedentes"),
                            rs.getString("observacion"),
                            rs.getInt("pacienteId"),
                            rs.getBoolean("flagBorrado")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar historia clínica: " + e.getMessage());
        }
        return hc;
    }

    @Override
    public boolean insertar(HistoriaClinica hc) {
        String sql = "{CALL sp_historiasclinicas_insertar(?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setDate(1, hc.getFechaApertura() != null ? Date.valueOf(hc.getFechaApertura()) : null);
            cs.setString(2, hc.getAntecedentes());
            cs.setString(3, hc.getObservacion());
            cs.setInt(4, hc.getPacienteId()); 

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar historia clínica: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(HistoriaClinica hc) {
        String sql = "{CALL sp_historiasclinicas_actualizar(?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, hc.getIdHistoria());
            cs.setString(2, hc.getAntecedentes());
            cs.setString(3, hc.getObservacion());
            cs.setBoolean(4, hc.getFlagBorrado());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar historia clínica: " + e.getMessage());
            return false;
        }
    }


}