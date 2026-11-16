package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.VistaTurnos;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
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
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar vista de turnos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Turnos de un paciente, tomados de la vista v_agenda_turnos_medicos.
     * Se consulta la vista directamente (en lugar de sp_mis_turnos) para
     * traer también el idTurno y los datos del paciente.
     */
    @Override
    public List<VistaTurnos> listarPorDni(String dni) {
        List<VistaTurnos> lista = new ArrayList<>();
        String sql = "SELECT idTurno, fecha, horaInicio, horaFin, estado_turno, motivo, "
                + "medico_nombre, paciente_nombre, paciente_dni, consultorio_numero "
                + "FROM v_agenda_turnos_medicos "
                + "WHERE paciente_dni = ? "
                + "ORDER BY fecha ASC, horaInicio ASC";

        try (Connection con = Conexion.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar turnos del paciente: " + e.getMessage());
        }
        return lista;
    }

    private VistaTurnos mapear(ResultSet rs) throws SQLException {
        Date sqlDate = rs.getDate("fecha");
        Time sqlHoraInicio = rs.getTime("horaInicio");
        Time sqlHoraFin = rs.getTime("horaFin");

        return new VistaTurnos(
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
    }
}
