package com.clinica.controller;

import com.clinica.dao.IObraSocialDao;
import com.clinica.modelo.ObraSocial;
import com.clinica.factory.ObraSocialDaoFactory; 

import com.google.gson.Gson;
import com.google.gson.JsonObject;


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

@WebServlet("/obras-sociales")
public class ObraSocialServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private IObraSocialDao obraSocialDao = ObraSocialDaoFactory.getObraSocialDao();
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
                ObraSocial obraSocial = obraSocialDao.buscarPorId(id);

                if (obraSocial != null) {
                    out.print(gson.toJson(obraSocial));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"Obra social no encontrada con el ID: " + id + "\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"El ID proporcionado debe ser un número entero válido.\"}");
            }
        } else {
            // --- CASO B: LISTAR TODO ---
            List<ObraSocial> lista = obraSocialDao.listar();
            out.print(gson.toJson(lista));
        }
        out.flush();
    }

    // INSERTAR (POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        BufferedReader reader = request.getReader();
        ObraSocial nueva = gson.fromJson(reader, ObraSocial.class);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (obraSocialDao.insertar(nueva)) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print("{\"mensaje\": \"Obra social insertada correctamente\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"No se pudo insertar la obra social\"}");
        }
        out.flush();
    }

    // ACTUALIZAR (PUT)
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        BufferedReader reader = request.getReader();
        ObraSocial modificada = gson.fromJson(reader, ObraSocial.class);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (obraSocialDao.actualizar(modificada)) {
            out.print("{\"mensaje\": \"Obra social actualizada correctamente\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"No se pudo actualizar la obra social\"}");
        }
        out.flush();
    }

 // Patch - Actualizar flagBorrado
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTP 401
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"error\": \"No autorizado. Debe iniciar sesión.\"}");
            return; // Corta la ejecución de inmediato
        }
    	
    	request.setCharacterEncoding("UTF-8");
    	response.setContentType("application/json;charset=UTF-8");
    	PrintWriter out = response.getWriter();
    	
    	BufferedReader reader = request.getReader();
    	try {
    		// Leemos la petición como un JSON genérico
            JsonObject jsonBody = gson.fromJson(reader, JsonObject.class);
            
            // Validamos que vengan los datos mínimos obligatorios
            if (!jsonBody.has("idObraSocial") || !jsonBody.has("flagBorrado")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Faltan parámetros requeridos en el JSON: idObraSocial y flagBorrado\"}");
                out.flush();
                return;
            }
            
            int id = jsonBody.get("idUsuario").getAsInt();
            boolean flagBorrado = jsonBody.get("flagBorrado").getAsBoolean();
            
            // Invocamos el método creado en el paso anterior en tu DAO
            if (obraSocialDao.cambiarEstadoBorrado(id, flagBorrado)) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"mensaje\": \"Estado de borrado actualizado correctamente\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"No se pudo actualizar el estado de borrado de la obra socaial\"}");
            }
            
        } catch (Exception e) {
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        	out.println("{\"error\": \"Error en el formato de los datos JSON enviados en el PATCH.\"}");
        			
    	}
    	out.flush();
    }
}