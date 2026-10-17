package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Triage;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TriageDaoImpl implements ITriageDao {

    @Override
    public List<Triage> listar() {
        List<Triage> lista = new ArrayList<>();
        String sql = "{CALL sp_seleccionar(\"triages\")}";
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Date sqlDate = rs.getDate("fecha");
                
                Triage t = new Triage(
                        rs.getInt("idTriage"),
                        sqlDate != null ? sqlDate.toLocalDate() : null,
                        rs.getString("prioridad"),
                        rs.getString("estado"),
                        rs.getString("observacion"),
                        rs.getInt("pacienteId"),
                        rs.getInt("consultorioId"),
                        rs.getInt("enfermeroId")
                );
                lista.add(t);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar triages: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Triage buscarPorId(int id) {
        Triage t = null;
        String sql = "{CALL sp_triages_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    Date sqlDate = rs.getDate("fecha");
                    
                    t = new Triage(
                            rs.getInt("idTriage"),
                            sqlDate != null ? sqlDate.toLocalDate() : null,
                            rs.getString("prioridad"),
                            rs.getString("estado"),
                            rs.getString("observacion"),
                            rs.getInt("pacienteId"),
                            rs.getInt("consultorioId"),
                            rs.getInt("enfermeroId")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar triage: " + e.getMessage());
        }
        return t;
    }

    @Override
    public boolean insertar(Triage t) {
        String sql = "{CALL sp_triages_insertar(?, ?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setDate(1, t.getFecha() != null ? Date.valueOf(t.getFecha()) : null);
            cs.setString(2, t.getPrioridad());
            cs.setString(3, t.getEstado());
            cs.setString(4, t.getObservacion());
            cs.setInt(5, t.getPacienteId());
            cs.setInt(6, t.getConsultorioId());
            cs.setInt(7, t.getEnfermeroId());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar triage: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Triage t) {
        String sql = "{CALL sp_triages_actualizar(?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, t.getIdTriage());
            cs.setDate(2, t.getFecha() != null ? Date.valueOf(t.getFecha()) : null);
            cs.setString(3, t.getPrioridad());
            cs.setString(4, t.getEstado());
            cs.setString(5, t.getObservacion());
            cs.setInt(6, t.getPacienteId());
            cs.setInt(7, t.getConsultorioId());
            cs.setInt(8, t.getEnfermeroId());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar triage: " + e.getMessage());
            return false;
        }
    }

}