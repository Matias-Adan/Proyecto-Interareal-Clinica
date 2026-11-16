package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.MiCuenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * DAO de la sección "Mi cuenta".
 *
 * Lee la vista v_mi_cuenta, que ya resuelve la obra social y el rol
 * principal del usuario (admin > médico > enfermero > recepcionista >
 * paciente). Funciona igual para las vistas de paciente, médico y admin.
 */
public class MiCuentaDaoImpl implements IMiCuentaDao {

    private static final String SELECT_BASE =
            "SELECT usuario_id, dni, nombre, apellido, nombre_completo, email, "
            + "telefono, obra_social_id, obra_social, numero_convenio, cobertura, "
            + "rol_id, rol, fecha_alta FROM v_mi_cuenta ";

    /**
     * Cada consulta usa su propia conexión: la conexión compartida del
     * singleton no soporta dos ResultSet abiertos al mismo tiempo y esta
     * vista puede consultarse junto con las demás lecturas de la página.
     */
    private Connection abrirConexion() {
        Connection con = Conexion.getInstancia().nuevaConexion();
        if (con == null) {
            throw new DataAccessException("No hay conexión con la base de datos.");
        }
        return con;
    }

    @Override
    public MiCuenta buscarPorDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            return null;
        }
        return buscar(SELECT_BASE + "WHERE dni = ?", dni.trim());
    }

    @Override
    public MiCuenta buscarPorId(int usuarioId) {
        if (usuarioId <= 0) {
            return null;
        }
        return buscar(SELECT_BASE + "WHERE usuario_id = ?", Integer.valueOf(usuarioId));
    }

    private MiCuenta buscar(String sql, Object criterio) {
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (criterio instanceof Integer) {
                ps.setInt(1, (Integer) criterio);
            } else {
                ps.setString(1, (String) criterio);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "No se pudieron leer los datos de la cuenta: " + e.getMessage(), e);
        }
    }

    private MiCuenta mapear(ResultSet rs) throws SQLException {
        Timestamp fechaAlta = rs.getTimestamp("fecha_alta");

        return new MiCuenta(
                rs.getInt("usuario_id"),
                rs.getString("dni"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("nombre_completo"),
                rs.getString("email"),
                rs.getString("telefono"),
                rs.getInt("obra_social_id"),
                rs.getString("obra_social"),
                rs.getString("numero_convenio"),
                rs.getString("cobertura"),
                rs.getInt("rol_id"),
                rs.getString("rol"),
                fechaAlta != null ? fechaAlta.toLocalDateTime() : null
        );
    }
}
