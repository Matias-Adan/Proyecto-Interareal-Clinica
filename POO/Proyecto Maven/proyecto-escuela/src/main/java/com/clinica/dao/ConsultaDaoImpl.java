package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Consulta;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDaoImpl implements IConsultaDao {

    @Override
    public List<Consulta> listar() {
        List<Consulta> lista = new ArrayList<>();
        String sql = "{CALL sp_seleccionar(\"consultas\")}";
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Timestamp sqlTimestamp = rs.getTimestamp("fechaHora");
                
                Consulta c = new Consulta(
                        rs.getInt("idConsulta"),
                        sqlTimestamp != null ? sqlTimestamp.toLocalDateTime() : null,
                        rs.getString("motivo"),
                        rs.getString("diagnostico"),
                        rs.getString("obvservacion"),
                        rs.getInt("historialId"),
                        rs.getInt("medicoId")
                );
                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar consultas: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Consulta buscarPorId(int id) {
        Consulta c = null;
        String sql = "{CALL sp_consultas_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    Timestamp sqlTimestamp = rs.getTimestamp("fechaHora");
                    
                    c = new Consulta(
                            rs.getInt("idConsulta"),
                            sqlTimestamp != null ? sqlTimestamp.toLocalDateTime() : null,
                            rs.getString("motivo"),
                            rs.getString("diagnostico"),
                            rs.getString("obvservacion"),
                            rs.getInt("historialId"),
                            rs.getInt("medicoId")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar consulta: " + e.getMessage());
        }
        return c;
    }

    @Override
    public boolean insertar(Consulta c) {
        String sql = "{CALL sp_consultas_insertar(?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setTimestamp(1, c.getFecha() != null ? Timestamp.valueOf(c.getFecha()) : null);
            cs.setString(2, c.getMotivo());
            cs.setString(3, c.getDiagnostico());
            cs.setString(4, c.getObvservacion());
            cs.setInt(5, c.getHistorialId());
            cs.setInt(6, c.getMedicoId());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar consulta: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Consulta c) {
        String sql = "{CALL sp_consultas_actualizar(?, ?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, c.getIdConsulta());
            cs.setTimestamp(2, c.getFecha() != null ? Timestamp.valueOf(c.getFecha()) : null);
            cs.setString(3, c.getMotivo());
            cs.setString(4, c.getDiagnostico());
            cs.setString(5, c.getObvservacion());
            cs.setInt(6, c.getHistorialId());
            cs.setInt(7, c.getMedicoId());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar consulta: " + e.getMessage());
            return false;
        }
    }
}