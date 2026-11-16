package com.clinica.controller;

import com.clinica.dao.DataAccessException;
import com.clinica.dao.IVistaMedicoDao;
import com.clinica.factory.VistaMedicoDaoFactory;
import com.clinica.modelo.TriageActual;
import com.clinica.modelo.Usuario;
import com.clinica.modelo.VistaTurnos;

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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Devuelve la información de la sección "Agenda y pacientes" del médico:
 *   - sus turnos asignados,
 *   - los pacientes que continúan en el triage.
 *
 * GET /vista-medico?medico_id=...
 *
 * Si hay una sesión iniciada, se usa el id del usuario logueado y se ignora
 * el parámetro. Si no hay sesión, se usa `medico_id`. Sin ningún id, se
 * responde 400.
 */
@WebServlet("/vista-medico")
public class VistaMedicoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IVistaMedicoDao vistaMedicoDao = VistaMedicoDaoFactory.getVistaMedicoDao();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, tipo, ctx) ->
                    src == null ? null : new JsonPrimitive(
                            src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (src, tipo, ctx) ->
                    src == null ? null : new JsonPrimitive(
                            src.format(DateTimeFormatter.ISO_LOCAL_TIME)))
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

        // La sesión manda: un médico no puede leer la agenda de otro
        // cambiando el id de la URL.
        Integer medicoId = null;
        HttpSession sesion = request.getSession(false);
        Object atributo = sesion != null ? sesion.getAttribute("usuarioLogueado") : null;
        if (atributo instanceof Usuario) {
            medicoId = ((Usuario) atributo).getIdUsuario();
        }

        if (medicoId == null) {
            String idParam = request.getParameter("medico_id");
            if (idParam != null && !idParam.trim().isEmpty()) {
                try {
                    medicoId = Integer.valueOf(idParam.trim());
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"El id del médico debe ser un número entero.\"}");
                    out.flush();
                    return;
                }
            }
        }

        if (medicoId == null || medicoId <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Falta el id del médico.\"}");
            out.flush();
            return;
        }

        try {
            List<VistaTurnos> turnos = vistaMedicoDao.listarTurnos(medicoId);
            List<TriageActual> triage = vistaMedicoDao.listarTriageActual();

            JsonObject respuesta = new JsonObject();
            respuesta.addProperty("medicoId", medicoId);
            respuesta.add("turnos", gson.toJsonTree(turnos));
            respuesta.add("triage", gson.toJsonTree(triage));

            out.print(gson.toJson(respuesta));
            out.flush();
        } catch (DataAccessException e) {
            System.err.println("❌ Error al cargar la agenda del médico: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"No se pudo cargar la agenda del médico.\"}");
            out.flush();
        }
    }
}
