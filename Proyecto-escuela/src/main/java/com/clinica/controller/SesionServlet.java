package com.clinica.controller;

import com.clinica.modelo.Usuario;
import com.clinica.service.AutorizacionService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

/**
 * Devuelve el estado de la sesión y el rol del usuario autenticado.
 *
 * GET  /sesion          -> { autenticado: true/false, usuario, roles, rol }
 * POST /sesion/logout   -> cierra la sesión
 */
@WebServlet(urlPatterns = {"/sesion", "/sesion/logout"})
public class SesionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AutorizacionService autorizacion = new AutorizacionService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");

        PrintWriter out = response.getWriter();
        HttpSession sesion = request.getSession(false);
        Object atributo = sesion != null ? sesion.getAttribute("usuarioLogueado") : null;

        if (!(atributo instanceof Usuario)) {
            out.print("{\"autenticado\":false}");
            out.flush();
            return;
        }

        Usuario usuario = (Usuario) atributo;
        Set<Integer> roles = autorizacion.rolesDe(usuario.getIdUsuario());
        Integer rol = AutorizacionService.rolPrincipal(roles);

        JsonObject respuesta = new JsonObject();
        respuesta.addProperty("autenticado", true);
        respuesta.add("usuario", usuarioJson(usuario));

        JsonArray rolesJson = new JsonArray();
        for (Integer idRol : roles) {
            rolesJson.add(idRol);
        }
        respuesta.add("roles", rolesJson);

        if (rol != null) {
            respuesta.addProperty("rol", rol);
            respuesta.addProperty("idRol", rol);
        }

        out.print(gson.toJson(respuesta));
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print("{\"mensaje\":\"Sesión cerrada\"}");
    }

    private JsonObject usuarioJson(Usuario usuario) {
        JsonObject json = new JsonObject();
        json.addProperty("idUsuario", usuario.getIdUsuario());
        json.addProperty("nombre", usuario.getNombre());
        json.addProperty("apellido", usuario.getApellido());
        json.addProperty("dni", usuario.getDni());
        json.addProperty("telefono", usuario.getTelefono());
        json.addProperty("email", usuario.getEmail());
        json.addProperty("obraSocialId", usuario.getObraSocialId());
        json.addProperty("flagBorrado", usuario.isFlagBorrado());

        if (usuario.getFechaNacimiento() != null) {
            json.addProperty("fechaNacimiento", usuario.getFechaNacimiento().toString());
        }

        return json;
    }
}
