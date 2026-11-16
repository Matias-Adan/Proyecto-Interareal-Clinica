package com.clinica.controller;

import com.clinica.dao.DataAccessException;
import com.clinica.dao.IMiCuentaDao;
import com.clinica.factory.MiCuentaDaoFactory;
import com.clinica.modelo.MiCuenta;
import com.clinica.modelo.Usuario;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

/**
 * Devuelve los datos de la cuenta del usuario logueado.
 *
 * GET /mi-cuenta            -> usa el usuario de la sesión.
 * GET /mi-cuenta?dni=...    -> respaldo sin sesión (por ejemplo, pruebas).
 *
 * La respuesta es la misma para todos los roles; el campo `rol` indica si la
 * cuenta es de un paciente, un médico, un administrador, etc. Por eso la
 * sección "Mi cuenta" se puede reutilizar en las tres vistas sin cambios.
 */
@WebServlet("/mi-cuenta")
public class MiCuentaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IMiCuentaDao cuentaDao = MiCuentaDaoFactory.getMiCuentaDao();

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

        // La sesión manda: nadie puede leer la cuenta de otro cambiando la URL.
        HttpSession sesion = request.getSession(false);
        Object atributo = sesion != null ? sesion.getAttribute("usuarioLogueado") : null;

        try {
            MiCuenta cuenta;

            if (atributo instanceof Usuario) {
                cuenta = cuentaDao.buscarPorId(((Usuario) atributo).getIdUsuario());
            } else {
                String dni = request.getParameter("dni");
                if (dni == null || dni.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.print("{\"error\": \"Iniciá sesión para ver los datos de tu cuenta.\"}");
                    out.flush();
                    return;
                }
                cuenta = cuentaDao.buscarPorDni(dni);
            }

            if (cuenta == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"No se encontraron los datos de la cuenta.\"}");
                out.flush();
                return;
            }

            out.print(gson.toJson(cuenta));
            out.flush();
        } catch (DataAccessException e) {
            System.err.println("❌ Error al cargar Mi cuenta: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"No se pudieron cargar los datos de la cuenta.\"}");
            out.flush();
        }
    }
}
