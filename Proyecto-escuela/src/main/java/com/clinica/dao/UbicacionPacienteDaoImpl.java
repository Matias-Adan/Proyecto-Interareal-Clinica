package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.UbicacionPaciente;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UbicacionPacienteDaoImpl implements IUbicacionPacienteDao {

    @Override
    public List<UbicacionPaciente> listar() {
        List<UbicacionPaciente> lista = new ArrayList<>();
        String sql = "{CALL sp_seleccionar(\"ubicacionpaciente\")}";
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
            	Timestamp sqlFechaIngreso = rs.getTimestamp("fechaIngreso");
            	Timestamp sqlFechaSalida = rs.getTimestamp("fechaSalida");
                
                UbicacionPaciente up = new UbicacionPaciente(
                        rs.getInt("idUbicacion"),
                        rs.getString("tipoAtencion"),
                        sqlFechaIngreso != null ? sqlFechaIngreso.toLocalDateTime() : null,
                        sqlFechaSalida != null ? sqlFechaSalida.toLocalDateTime() : null,
                        rs.getInt("pacienteId"),
                        rs.getInt("consultorioId")
                );
                up.setMedicoId(rs.getInt("medicoId"));
                lista.add(up);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar ubicaciones de pacientes: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public UbicacionPaciente buscarPorId(int id) {
        UbicacionPaciente up = null;
        String sql = "{CALL sp_ubicacionpaciente_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                	Timestamp sqlFechaIngreso = rs.getTimestamp("fechaIngreso");
                	Timestamp sqlFechaSalida = rs.getTimestamp("fechaSalida");
                    
                    up = new UbicacionPaciente(
                            rs.getInt("idUbicacion"),
                            rs.getString("tipoAtencion"),
                            sqlFechaIngreso != null ? sqlFechaIngreso.toLocalDateTime() : null,
                            sqlFechaSalida != null ? sqlFechaSalida.toLocalDateTime() : null,
                            rs.getInt("pacienteId"),
                            rs.getInt("consultorioId")
                    );
                    up.setMedicoId(rs.getInt("medicoId"));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar ubicación de paciente: " + e.getMessage());
        }
        return up;
    }

    @Override
    public boolean insertar(UbicacionPaciente up) {
        String sql = "{CALL sp_ubicacionpaciente_insertar(?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setString(1, up.getTipoAtencion());
            cs.setTimestamp(2, up.getFechaIngreso() != null ? Timestamp.valueOf(up.getFechaIngreso()) : null);
            cs.setTimestamp(3, up.getFechaSalida() != null ? Timestamp.valueOf(up.getFechaSalida()) : null);
            cs.setInt(4, up.getPacienteId());
            cs.setInt(5, up.getMedicoId());
            cs.setInt(6, up.getConsultorioId());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar ubicación de paciente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(UbicacionPaciente up) {
        String sql = "{CALL sp_ubicacionpaciente_actualizar(?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, up.getIdUbicacion());
            cs.setString(2, up.getTipoAtencion());
            cs.setTimestamp(3, up.getFechaSalida() != null ? Timestamp.valueOf(up.getFechaSalida()) : null);
            cs.setInt(4, up.getMedicoId());
            cs.setInt(5, up.getConsultorioId());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar ubicación de paciente: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean darAlta(int id, java.time.LocalDateTime fechaSalida) {
        String sql = "{CALL sp_ubicacionpaciente_dar_alta(?, ?)}";
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            cs.setTimestamp(2, fechaSalida != null ? Timestamp.valueOf(fechaSalida) : null);
            
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al dar de alta (finalizar ubicación): " + e.getMessage());
            return false;
        }
    }

    
}