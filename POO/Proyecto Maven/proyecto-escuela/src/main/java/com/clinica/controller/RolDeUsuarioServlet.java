package com.clinica.controller;

import com.clinica.dao.IRolDeUsuarioDao;
import com.clinica.modelo.RolDeUsuario;
import com.clinica.factory.RolDeUsuarioDaoFactory; 

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

@WebServlet("/roles-usuario")
public class RolDeUsuarioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private IRolDeUsuarioDao rolDeUsuarioDao = RolDeUsuarioDaoFactory.getRolDeUsuarioDao();
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
                RolDeUsuario rolDeUsuario = rolDeUsuarioDao.buscarPorId(id);

                if (rolDeUsuario != null) {
                    out.print(gson.toJson(rolDeUsuario));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"Rol de usuario no encontrado con el ID: " + id + "\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"El ID proporcionado debe ser un número entero válido.\"}");
            }
        } else {
            // --- CASO B: LISTAR TODO ---
            List<RolDeUsuario> lista = rolDeUsuarioDao.listar();
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
        RolDeUsuario nuevo = gson.fromJson(reader, RolDeUsuario.class);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (rolDeUsuarioDao.insertar(nuevo)) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print("{\"mensaje\": \"Rol de usuario insertado correctamente\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"No se pudo insertar el rol de usuario\"}");
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
        RolDeUsuario modificado = gson.fromJson(reader, RolDeUsuario.class);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (rolDeUsuarioDao.actualizar(modificado)) {
            out.print("{\"mensaje\": \"Rol de usuario actualizado correctamente\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"No se pudo actualizar el rol de usuario\"}");
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
        
        String idStr = request.getParameter("id");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                
                if (rolDeUsuarioDao.eliminar(id)) {
                    out.print("{\"mensaje\": \"Rol de usuario eliminado correctamente\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"error\": \"No se pudo eliminar el rol de usuario\"}");
                }
                
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"El ID proporcionado debe ser un número entero válido.\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Falta el parámetro id\"}");
        }
        out.flush();
    }
}