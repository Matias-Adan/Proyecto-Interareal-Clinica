package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.VistaTurnos;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class VistaTurnosDaoImpl implements IVistaTurnosDao {

    @Override
    public List<VistaTurnos> listar() {
        List<VistaTurnos> lista = new ArrayList<>();
        String sql = "{CALL sp_seleccionar(\"v_agenda_turnos_medicos\")}";
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Date sqlDate = rs.getDate("fecha");
                Time sqlHoraInicio = rs.getTime("horaInicio");
                Time sqlHoraFin = rs.getTime("horaFin");
                
                VistaTurnos vt = new VistaTurnos(
                        rs.getInt("idTurno"),
                        sqlDate != null ? sqlDate.toLocalDate() : null,
                        sqlHoraInicio != null ? sqlHoraInicio.toLocalTime() : null,
                        sqlHoraFin != null ? sqlHoraFin.toLocalTime() : null,
                        rs.getString("estado_turno"),
                        rs.getString("motivo"),
                        rs.getString("medico_nombre"),
                        rs.getString("paciente_nombre"),
                        rs.getString("paciente_dni"),
                        rs.getString("consultorio_numero")
                );
                lista.add(vt);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar vista de turnos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public VistaTurnos buscarPorDni(String dni) {
        VistaTurnos vt = null;
        String sql = "{CALL sp_mis_turnos(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setString(1, dni);
            
            // Usamos execute() en lugar de executeQuery() para procedimientos almacenados
            boolean tieneResultados = cs.execute();
            
            if (tieneResultados) {
                try (ResultSet rs = cs.getResultSet()) {
                    if (rs.next()) {
                        Date sqlDate = rs.getDate("fecha");
                        Time sqlHoraInicio = rs.getTime("horaInicio");
                        Time sqlHoraFin = rs.getTime("horaFin");
                        
                        vt = new VistaTurnos(); // Constructor vacío
                        vt.setFecha(sqlDate != null ? sqlDate.toLocalDate() : null);
                        vt.setHoraInicio(sqlHoraInicio != null ? sqlHoraInicio.toLocalTime() : null);
                        vt.setHoraFin(sqlHoraFin != null ? sqlHoraFin.toLocalTime() : null);
                        vt.setEstadoTurno(rs.getString("estado_turno"));
                        vt.setMotivo(rs.getString("motivo"));
                        vt.setMedicoNombre(rs.getString("medico_nombre"));
                        vt.setConsultorioNumero(rs.getString("consultorio_numero"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar en la vista de turnos: " + e.getMessage());
        }
        return vt;
    }
}