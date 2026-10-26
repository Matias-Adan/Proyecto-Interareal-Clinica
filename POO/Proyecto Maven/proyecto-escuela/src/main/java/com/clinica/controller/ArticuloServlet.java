package com.clinica.controller;

import com.clinica.dao.IArticuloDao;
import com.clinica.modelo.Articulo;
import com.clinica.factory.ArticuloDaoFactory;

import com.google.gson.Gson; // O la librería que usen para JSON (ej: Jackson o Gson)

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/articulos")
public class ArticuloServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private IArticuloDao articuloDao = ArticuloDaoFactory.getArticuloDao();
    private Gson gson = new Gson(); // Asegurate de tener Gson o Jackson en el pom.xml / libs

    // LISTAR (GET)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        PrintWriter out = response.getWriter();
        
        // 1. Intentamos leer el parámetro "id" desde la URL
        String idStr = request.getParameter("id");

        if (idStr != null && !idStr.isEmpty()) {
            // --- CASO A: SI EL USUARIO ENVÍO UN ID (?id=X) ---
            try {
                int id = Integer.parseInt(idStr);
                Articulo articulo = articuloDao.buscarPorId(id);

                if (articulo != null) {
                    // Si el artículo existe, lo devolvemos en formato JSON
                    out.print(gson.toJson(articulo));
                } else {
                    // Si no existe en la BD, devolvemos un estado 404 (No encontrado)
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"Artículo no encontrado con el ID: " + id + "\"}");
                }
            } catch (NumberFormatException e) {
                // Por si el usuario escribe texto en lugar de un número (ej: ?id=hola)
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"El ID proporcionado debe ser un número entero válido.\"}");
            }
        } else {
            // --- CASO B: SI NO SE ENVIÓ NINGÚN ID ---
            // Ejecuta tu código original que trae la lista completa
            List<Articulo> lista = articuloDao.listar();
            out.print(gson.toJson(lista));
        }
        
        out.flush();
    }

    // INSERTAR (POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    	jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"error\": \"Acceso denegado. Debe iniciar sesión.\"}");
            return; // Detiene la petición aquí
        }
        
    	request.setCharacterEncoding("UTF-8");
        BufferedReader reader = request.getReader();
        Articulo nuevo = gson.fromJson(reader, Articulo.class);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (articuloDao.insertar(nuevo)) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print("{\"mensaje\": \"Artículo insertado correctamente\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"No se pudo insertar el artículo\"}");
        }
        out.flush();
    }

    // ACTUALIZAR (PUT)
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    	jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"error\": \"Acceso denegado. Debe iniciar sesión.\"}");
            return; // Detiene la petición aquí
        }
    	
    	request.setCharacterEncoding("UTF-8");
        BufferedReader reader = request.getReader();
        Articulo modificado = gson.fromJson(reader, Articulo.class);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (articuloDao.actualizar(modificado)) {
            out.print("{\"mensaje\": \"Artículo actualizado correctamente\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"No se pudo actualizar el artículo\"}");
        }
        out.flush();
    }

    // ELIMINAR (DELETE) - Usando parámetro por URL (ej: /articulos?id=5)
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    	jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"error\": \"Acceso denegado. Debe iniciar sesión.\"}");
            return; // Detiene la petición aquí
        }
    	
    	String idStr = request.getParameter("id");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (idStr != null) {
            int id = Integer.parseInt(idStr);
            if (articuloDao.eliminar(id)) {
                out.print("{\"mensaje\": \"Artículo eliminado correctamente\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"No se pudo eliminar el artículo\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Falta el parámetro id\"}");
        }
        out.flush();
    }
}