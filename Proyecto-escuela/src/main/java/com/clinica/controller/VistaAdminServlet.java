package com.clinica.controller;

import com.clinica.dao.DataAccessException;
import com.clinica.dao.IVistaAdminDao;
import com.clinica.factory.VistaAdminDaoFactory;
import com.clinica.modelo.Usuario;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Devuelve los datos de la vista del administrador:
 *   - usuarios activos (flagBorrado = 0) con sus roles,
 *   - catálogo de roles para asignar,
 *   - artículos con la marca de stock bajo.
 *
 * GET /vista-admin
 *
 * El id del usuario logueado se envía como usuarioActualId para que el front
 * pueda impedir que el administrador se dé de baja a sí mismo o se quite su
 * propio rol. Si no hay sesión, se acepta el parámetro usuario_id (útil para
 * pruebas con Postman).
 */
@WebServlet("/vista-admin")
public class VistaAdminServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IVistaAdminDao vistaAdminDao = VistaAdminDaoFactory.getVistaAdminDao();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        PrintWriter out = response.getWriter();

        Integer usuarioActualId = null;
        HttpSession sesion = request.getSession(false);
        Object atributo = sesion != null ? sesion.getAttribute("usuarioLogueado") : null;
        if (atributo instanceof Usuario) {
            usuarioActualId = ((Usuario) atributo).getIdUsuario();
        }
        if (usuarioActualId == null) {
            String idParam = request.getParameter("usuario_id");
            if (idParam != null && !idParam.trim().isEmpty()) {
                try {
                    usuarioActualId = Integer.valueOf(idParam.trim());
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"El id del usuario debe ser un número entero.\"}");
                    out.flush();
                    return;
                }
            }
        }

        try {
            JsonObject respuesta = new JsonObject();
            if (usuarioActualId != null) {
                respuesta.addProperty("usuarioActualId", usuarioActualId);
            }
            respuesta.add("usuarios", gson.toJsonTree(vistaAdminDao.listarUsuarios()));
            respuesta.add("roles", gson.toJsonTree(vistaAdminDao.listarRoles()));
            respuesta.add("articulos", gson.toJsonTree(vistaAdminDao.listarArticulos()));

            out.print(gson.toJson(respuesta));
            out.flush();
        } catch (DataAccessException e) {
            System.err.println("❌ Error al cargar la vista de administrador: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"No se pudo cargar la vista del administrador.\"}");
            out.flush();
        }
    }
}
