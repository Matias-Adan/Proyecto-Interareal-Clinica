package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.TriageActual;
import com.clinica.modelo.VistaTurnos;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de la sección "Agenda y pacientes" del médico.
 *
 * Lee las vistas:
 *   - v_turnos_medico
 *   - v_triage_actual
 *
 * Igual que MiSaludDaoImpl, cada consulta usa su propia conexión porque la
 * vista del médico lanza dos lecturas y la conexión compartida del singleton
 * no soporta dos ResultSet abiertos al mismo tiempo.
 */
public class VistaMedicoDaoImpl implements IVistaMedicoDao {

    private Connection abrirConexion() {
        Connection con = Conexion.getInstancia().nuevaConexion();
        if (con == null) {
            throw new DataAccessException("No hay conexión con la base de datos.");
        }
        return con;
    }

    @Override
    public List<VistaTurnos> listarTurnos(int medicoId) {
        List<VistaTurnos> lista = new ArrayList<>();
        String sql = "SELECT idTurno, fecha, horaInicio, horaFin, estado_turno, motivo, "
                + "medico_nombre, paciente_nombre, paciente_dni, consultorio_numero "
                + "FROM v_turnos_medico "
                + "WHERE medico_id = ? "
                + "ORDER BY fecha ASC, horaInicio ASC";

        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, medicoId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearTurno(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "No se pudieron leer los turnos del médico: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<TriageActual> listarTriageActual() {
        List<TriageActual> lista = new ArrayList<>();
        String sql = "SELECT idTriage, fecha, prioridad, estado, observacion, "
                + "paciente_id, paciente_nombre, paciente_dni, consultorio_id, "
                + "consultorio_numero, enfermero_id, enfermero_nombre "
                + "FROM v_triage_actual "
                + "ORDER BY prioridad ASC, fecha ASC";

        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Timestamp fecha = rs.getTimestamp("fecha");

                lista.add(new TriageActual(
                        rs.getInt("idTriage"),
                        fecha != null ? fecha.toLocalDateTime() : null,
                        rs.getString("prioridad"),
                        rs.getString("estado"),
                        rs.getString("observacion"),
                        rs.getInt("paciente_id"),
                        rs.getString("paciente_nombre"),
                        rs.getString("paciente_dni"),
                        rs.getInt("consultorio_id"),
                        rs.getString("consultorio_numero"),
                        rs.getInt("enfermero_id"),
                        rs.getString("enfermero_nombre")
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "No se pudieron leer los pacientes del triage: " + e.getMessage(), e);
        }
        return lista;
    }

    private VistaTurnos mapearTurno(ResultSet rs) throws SQLException {
        Date fecha = rs.getDate("fecha");
        Time horaInicio = rs.getTime("horaInicio");
        Time horaFin = rs.getTime("horaFin");

        return new VistaTurnos(
                rs.getInt("idTurno"),
                fecha != null ? fecha.toLocalDate() : null,
                horaInicio != null ? horaInicio.toLocalTime() : null,
                horaFin != null ? horaFin.toLocalTime() : null,
                rs.getString("estado_turno"),
                rs.getString("motivo"),
                rs.getString("medico_nombre"),
                rs.getString("paciente_nombre"),
                rs.getString("paciente_dni"),
                rs.getString("consultorio_numero")
        );
    }
}
