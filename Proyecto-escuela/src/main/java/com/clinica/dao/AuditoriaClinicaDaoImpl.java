package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.AuditoriaClinica;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de auditoría clínica.
 * A diferencia de los DAO existentes, usa SQL directo (INSERT/SELECT) porque
 * la tabla de auditoría no depende de los procedimientos almacenados del
 * sistema original.
 */
public class AuditoriaClinicaDaoImpl implements IAuditoriaClinicaDao {

    private static volatile boolean tablaAsegurada = false;

    /**
     * Crea la tabla de auditoría si todavía no existe.
     * Así la aplicación funciona sin necesidad de ejecutar ningún script aparte.
     */
    private void asegurarTabla(Connection con) throws SQLException {
        if (tablaAsegurada) {
            return;
        }

        String ddl = "CREATE TABLE IF NOT EXISTS auditoria_clinica ("
                + "idAuditoria INT AUTO_INCREMENT PRIMARY KEY, "
                + "tabla VARCHAR(60) NOT NULL, "
                + "registroId INT NOT NULL, "
                + "accion VARCHAR(30) NOT NULL, "
                + "datosAnteriores LONGTEXT NULL, "
                + "usuarioId INT NOT NULL, "
                + "fechaCambio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                + "INDEX idx_auditoria_tabla (tabla, registroId), "
                + "INDEX idx_auditoria_usuario (usuarioId)"
                + ") ENGINE = InnoDB DEFAULT CHARSET = utf8mb4";

        try (Statement st = con.createStatement()) {
            st.executeUpdate(ddl);
        }
        tablaAsegurada = true;
    }

    @Override
    public boolean registrar(AuditoriaClinica auditoria) {
        String sql = "INSERT INTO auditoria_clinica "
                + "(tabla, registroId, accion, datosAnteriores, usuarioId) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = Conexion.getInstancia().getConexion()) {

            asegurarTabla(con);

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, auditoria.getTabla());
                ps.setInt(2, auditoria.getRegistroId());
                ps.setString(3, auditoria.getAccion());
                ps.setString(4, auditoria.getDatosAnteriores());
                ps.setInt(5, auditoria.getUsuarioId());

                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            // La trazabilidad es obligatoria: si no se puede auditar, la
            // corrección clínica no debe continuar.
            throw new DataAccessException(
                    "No se pudo registrar la auditoría clínica: " + e.getMessage(), e);
        }
    }

    @Override
    public List<AuditoriaClinica> listar() {
        List<AuditoriaClinica> lista = new ArrayList<>();
        String sql = "SELECT idAuditoria, tabla, registroId, accion, datosAnteriores, "
                + "usuarioId, fechaCambio FROM auditoria_clinica "
                + "ORDER BY idAuditoria DESC";

        try (Connection con = Conexion.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Timestamp fecha = rs.getTimestamp("fechaCambio");
                lista.add(new AuditoriaClinica(
                        rs.getInt("idAuditoria"),
                        rs.getString("tabla"),
                        rs.getInt("registroId"),
                        rs.getString("accion"),
                        rs.getString("datosAnteriores"),
                        rs.getInt("usuarioId"),
                        fecha != null ? fecha.toLocalDateTime() : null
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo leer la auditoría clínica.", e);
        }
        return lista;
    }
}
