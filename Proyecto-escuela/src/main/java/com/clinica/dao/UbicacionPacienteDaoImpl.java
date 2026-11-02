package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.UbicacionPaciente;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UbicacionPacienteDaoImpl implements IUbicacionPacienteDao {

    @Override
    public List<UbicacionPaciente> listar() {
        List<UbicacionPaciente> lista = new ArrayList<>();
        String sql = "{CALL sp_seleccionar(\"ubicaciones_pacientes\")}";
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Date sqlFechaIngreso = rs.getDate("fechaIngreso");
                Date sqlFechaSalida = rs.getDate("fechaSalida");
                
                UbicacionPaciente up = new UbicacionPaciente(
                        rs.getInt("idUbicacion"),
                        rs.getString("tipoAtencion"),
                        sqlFechaIngreso != null ? sqlFechaIngreso.toLocalDate() : null,
                        sqlFechaSalida != null ? sqlFechaSalida.toLocalDate() : null,
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
        String sql = "{CALL sp_ubicaciones_pacientes_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    Date sqlFechaIngreso = rs.getDate("fechaIngreso");
                    Date sqlFechaSalida = rs.getDate("fechaSalida");
                    
                    up = new UbicacionPaciente(
                            rs.getInt("idUbicacion"),
                            rs.getString("tipoAtencion"),
                            sqlFechaIngreso != null ? sqlFechaIngreso.toLocalDate() : null,
                            sqlFechaSalida != null ? sqlFechaSalida.toLocalDate() : null,
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
        String sql = "{CALL sp_ubicaciones_pacientes_insertar(?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setString(1, up.getTipoAtencion());
            cs.setDate(2, up.getFechaIngreso() != null ? Date.valueOf(up.getFechaIngreso()) : null);
            cs.setDate(3, up.getFechaSalida() != null ? Date.valueOf(up.getFechaSalida()) : null);
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
        String sql = "{CALL sp_ubicaciones_pacientes_actualizar(?, ?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, up.getIdUbicacion());
            cs.setString(2, up.getTipoAtencion());
            cs.setDate(3, up.getFechaIngreso() != null ? Date.valueOf(up.getFechaIngreso()) : null);
            cs.setDate(4, up.getFechaSalida() != null ? Date.valueOf(up.getFechaSalida()) : null);
            cs.setInt(5, up.getPacienteId());
            cs.setInt(6, up.getMedicoId());
            cs.setInt(7, up.getConsultorioId());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar ubicación de paciente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "{CALL sp_ubicaciones_pacientes_eliminar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar ubicación de paciente: " + e.getMessage());
            return false;
        }
    }
}