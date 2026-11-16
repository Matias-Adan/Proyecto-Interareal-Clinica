package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.MiAlergia;
import com.clinica.modelo.MiRecetaDetalle;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de la sección "Mi salud".
 *
 * Lee las vistas:
 *   - v_mis_alergias
 *   - v_mis_recetas_detalle
 *
 * Todas las consultas se filtran por el DNI del paciente.
 */
public class MiSaludDaoImpl implements IMiSaludDao {

    /**
     * Cada consulta de "Mi salud" usa su propia conexión: la vista del
     * paciente lanza varias lecturas y la conexión compartida del singleton
     * no soporta dos ResultSet abiertos al mismo tiempo.
     */
    private Connection abrirConexion() {
        Connection con = Conexion.getInstancia().nuevaConexion();
        if (con == null) {
            throw new DataAccessException("No hay conexión con la base de datos.");
        }
        return con;
    }

    @Override
    public List<MiAlergia> listarAlergias(String dni) {
        List<MiAlergia> lista = new ArrayList<>();
        String sql = "SELECT id_alergia_paciente, id_alergia, alergia_nombre, "
                + "alergia_descripcion FROM v_mis_alergias "
                + "WHERE paciente_dni = ? ORDER BY alergia_nombre";

        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new MiAlergia(
                            rs.getInt("id_alergia_paciente"),
                            rs.getInt("id_alergia"),
                            rs.getString("alergia_nombre"),
                            rs.getString("alergia_descripcion")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "No se pudieron leer las alergias del paciente: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<MiRecetaDetalle> listarRecetas(String dni) {
        List<MiRecetaDetalle> lista = new ArrayList<>();
        String sql = "SELECT id_receta, fecha, indicacion, id_detalle, cantidad, dosis, "
                + "frecuencia, duracion, id_articulo, articulo_nombre, presentacion, "
                + "id_consulta, fecha_consulta, diagnostico, medico_nombre "
                + "FROM v_mis_recetas_detalle "
                + "WHERE paciente_dni = ? "
                + "ORDER BY fecha DESC, id_receta DESC, id_detalle ASC";

        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date fecha = rs.getDate("fecha");
                    Timestamp fechaConsulta = rs.getTimestamp("fecha_consulta");

                    lista.add(new MiRecetaDetalle(
                            rs.getInt("id_receta"),
                            fecha != null ? fecha.toLocalDate() : null,
                            rs.getString("indicacion"),
                            rs.getInt("id_detalle"),
                            rs.getInt("cantidad"),
                            rs.getString("dosis"),
                            rs.getString("frecuencia"),
                            rs.getString("duracion"),
                            rs.getInt("id_articulo"),
                            rs.getString("articulo_nombre"),
                            rs.getString("presentacion"),
                            rs.getInt("id_consulta"),
                            fechaConsulta != null ? fechaConsulta.toLocalDateTime() : null,
                            rs.getString("diagnostico"),
                            rs.getString("medico_nombre")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "No se pudieron leer las recetas del paciente: " + e.getMessage(), e);
        }
        return lista;
    }
}
