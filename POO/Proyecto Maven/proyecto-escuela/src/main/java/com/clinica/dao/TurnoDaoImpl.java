package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Turno;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class TurnoDaoImpl implements ITurnoDao {

    @Override
    public List<Turno> listar() {
        List<Turno> lista = new ArrayList<>();
        String sql = "{CALL sp_seleccionar(\"turnos\")}";
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Date sqlDate = rs.getDate("fecha");
                Time sqlHoraInicio = rs.getTime("horaInicio");
                Time sqlHoraFin = rs.getTime("horaFin");
                
                Turno t = new Turno(
                        rs.getInt("idTurno"),
                        sqlDate != null ? sqlDate.toLocalDate() : null,
                        sqlHoraInicio != null ? sqlHoraInicio.toLocalTime() : null,
                        sqlHoraFin != null ? sqlHoraFin.toLocalTime() : null,
                        rs.getString("estado"),
                        rs.getString("motivo"),
                        rs.getInt("pacienteId"),
                        rs.getInt("personalId"),
                        rs.getInt("especialidadId"),
                        rs.getInt("consultorioId")
                );
                lista.add(t);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar turnos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Turno buscarPorId(int id) {
        Turno t = null;
        String sql = "{CALL sp_turnos_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    Date sqlDate = rs.getDate("fecha");
                    Time sqlHoraInicio = rs.getTime("horaInicio");
                    Time sqlHoraFin = rs.getTime("horaFin");
                    
                    t = new Turno(
                            rs.getInt("idTurno"),
                            sqlDate != null ? sqlDate.toLocalDate() : null,
                            sqlHoraInicio != null ? sqlHoraInicio.toLocalTime() : null,
                            sqlHoraFin != null ? sqlHoraFin.toLocalTime() : null,
                            rs.getString("estado"),
                            rs.getString("motivo"),
                            rs.getInt("pacienteId"),
                            rs.getInt("personalId"),
                            rs.getInt("especialidadId"),
                            rs.getInt("consultorioId")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar turno: " + e.getMessage());
        }
        return t;
    }

    @Override
    public boolean insertar(Turno t) {
        String sql = "{CALL sp_turnos_insertar(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setDate(1, t.getFecha() != null ? Date.valueOf(t.getFecha()) : null);
            cs.setTime(2, t.getHoraInicio() != null ? Time.valueOf(t.getHoraInicio()) : null);
            cs.setTime(3, t.getHoraFin() != null ? Time.valueOf(t.getHoraFin()) : null);
            cs.setString(4, t.getEstado());
            cs.setString(5, t.getMotivo());
            cs.setInt(6, t.getPacienteId());
            cs.setInt(7, t.getPersonalId());
            cs.setInt(8, t.getEspecialidadId());
            cs.setInt(9, t.getConsultorioId());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar turno: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Turno t) {
        String sql = "{CALL sp_turnos_actualizar(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, t.getIdTurno());
            cs.setDate(2, t.getFecha() != null ? Date.valueOf(t.getFecha()) : null);
            cs.setTime(3, t.getHoraInicio() != null ? Time.valueOf(t.getHoraInicio()) : null);
            cs.setTime(4, t.getHoraFin() != null ? Time.valueOf(t.getHoraFin()) : null);
            cs.setString(5, t.getEstado());
            cs.setString(6, t.getMotivo());
            cs.setInt(7, t.getPacienteId());
            cs.setInt(8, t.getPersonalId());
            cs.setInt(9, t.getEspecialidadId());
            cs.setInt(10, t.getConsultorioId());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar turno: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "{CALL sp_turnos_eliminar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar turno: " + e.getMessage());
            return false;
        }
    }
}