package com.clinica.controller;

import com.clinica.dao.IPermisoDao;
import com.clinica.modelo.Permiso;
import com.clinica.factory.PermisoDaoFactory; 
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/permisos")
public class PermisoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private IPermisoDao permisoDao = PermisoDaoFactory.getPermisoDao();
    private Gson gson = new Gson();

    // LISTAR O BUSCAR POR ID (GET)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String idStr = request.getParameter("id");

        if (idStr != null && !idStr.isEmpty()) {
            // --- CASO A: BUSCAR POR ID (?id=X) ---
            try {
                int id = Integer.parseInt(idStr);
                Permiso permiso = permisoDao.buscarPorId(id);

                if (permiso != null) {
                    out.print(gson.toJson(permiso));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"Permiso no encontrado con el ID: " + id + "\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"El ID proporcionado debe ser un número entero válido.\"}");
            }
        } else {
            // --- CASO B: LISTAR TODO ---
            List<Permiso> lista = permisoDao.listar();
            out.print(gson.toJson(lista));
        }
        out.flush();
    }

    // INSERTAR (POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"error\": \"Acceso denegado. Debe iniciar sesión.\"}");
            return;
        }
        
        request.setCharacterEncoding("UTF-8");
        BufferedReader reader = request.getReader();
        Permiso nuevo = gson.fromJson(reader, Permiso.class);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (permisoDao.insertar(nuevo)) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print("{\"mensaje\": \"Permiso insertado correctamente\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"No se pudo insertar el permiso\"}");
        }
        out.flush();
    }

    // ACTUALIZAR (PUT)
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"error\": \"Acceso denegado. Debe iniciar sesión.\"}");
            return;
        }
        
        request.setCharacterEncoding("UTF-8");
        BufferedReader reader = request.getReader();
        Permiso modificado = gson.fromJson(reader, Permiso.class);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (permisoDao.actualizar(modificado)) {
            out.print("{\"mensaje\": \"Permiso actualizado correctamente\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"No se pudo actualizar el permiso\"}");
        }
        out.flush();
    }

    // ELIMINAR (DELETE)
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"error\": \"Acceso denegado. Debe iniciar sesión.\"}");
            return;
        }
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
        out.print("{\"error\": \"La funcionalidad de eliminación no está implementada en el DAO de permisos.\"}");
        out.flush();
    }
}