package com.clinica.controller;

import com.clinica.config.Conexion;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Lista los profesionales (usuarios con rol Médico = 2) para que el paciente
 * pueda elegir a quién pedirle un turno.
 *
 * Es una lectura pública, igual que el resto de los GET de la aplicación, y
 * no expone la contraseña ni datos sensibles del usuario.
 *
 * GET /medicos
 */
@WebServlet("/medicos")
public class MedicosServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String SQL =
            "SELECT u.idUsuario, u.nombre, u.apellido, u.dni, u.email "
            + "FROM usuarios u "
            + "INNER JOIN roldeusuario ru ON ru.usuarioId = u.idUsuario "
            + "WHERE ru.rolId = 2 AND u.flagBorrado = 0 "
            + "ORDER BY u.apellido, u.nombre";

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        JsonArray medicos = new JsonArray();

        try (Connection con = Conexion.getInstancia().nuevaConexion()) {
            if (con == null) {
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                out.print("{\"error\": \"No hay conexión con la base de datos.\"}");
                out.flush();
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(SQL);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    JsonObject medico = new JsonObject();
                    medico.addProperty("idUsuario", rs.getInt("idUsuario"));
                    medico.addProperty("nombre", rs.getString("nombre"));
                    medico.addProperty("apellido", rs.getString("apellido"));
                    medico.addProperty("dni", rs.getString("dni"));
                    medico.addProperty("email", rs.getString("email"));
                    medicos.add(medico);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar médicos: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"No se pudo cargar la lista de profesionales.\"}");
            out.flush();
            return;
        }

        out.print(gson.toJson(medicos));
        out.flush();
    }
}
