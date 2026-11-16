package com.clinica.controller;

import com.clinica.dao.DataAccessException;
import com.clinica.dao.IMiSaludDao;
import com.clinica.factory.MiSaludDaoFactory;
import com.clinica.modelo.MiAlergia;
import com.clinica.modelo.MiRecetaDetalle;
import com.clinica.modelo.Usuario;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Devuelve la información de la sección "Mi salud" del paciente:
 *   - alergias actuales,
 *   - recetas con el detalle de cada medicamento.
 *
 * GET /mi-salud?paciente_dni=...
 *
 * Si hay una sesión iniciada, se usa el DNI del usuario logueado y se ignora
 * el parámetro. Si no hay sesión, se usa `paciente_dni`. Sin ningún DNI, se
 * responde 400.
 */
@WebServlet("/mi-salud")
public class MiSaludServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IMiSaludDao miSaludDao = MiSaludDaoFactory.getMiSaludDao();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, tipo, ctx) ->
                    src == null ? null : new JsonPrimitive(
                            src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonSerializer<LocalDateTime>) (src, tipo, ctx) ->
                            src == null ? null : new JsonPrimitive(
                                    src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        PrintWriter out = response.getWriter();

        String dni = request.getParameter("paciente_dni");

        // Si hay una sesión iniciada, el DNI sale siempre del usuario logueado:
        // un paciente no puede leer la salud de otro cambiando la URL.
        HttpSession sesion = request.getSession(false);
        Object atributo = sesion != null ? sesion.getAttribute("usuarioLogueado") : null;
        if (atributo instanceof Usuario) {
            int dniSesion = ((Usuario) atributo).getDni();
            if (dniSesion > 0) {
                dni = String.valueOf(dniSesion);
            }
        }

        if (dni == null || dni.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Falta el DNI del paciente.\"}");
            out.flush();
            return;
        }

        dni = dni.trim();

        try {
            List<MiAlergia> alergias = miSaludDao.listarAlergias(dni);
            List<MiRecetaDetalle> recetas = miSaludDao.listarRecetas(dni);

            JsonObject respuesta = new JsonObject();
            respuesta.add("alergias", gson.toJsonTree(alergias));
            respuesta.add("recetas", gson.toJsonTree(recetas));

            out.print(gson.toJson(respuesta));
            out.flush();
        } catch (DataAccessException e) {
            System.err.println("❌ Error al cargar Mi salud: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"No se pudo cargar la información de salud.\"}");
            out.flush();
        }
    }
}
