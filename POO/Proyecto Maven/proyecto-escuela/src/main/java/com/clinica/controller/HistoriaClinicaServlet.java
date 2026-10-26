package com.clinica.controller;

import com.clinica.dao.IHistoriaClinicaDao;
import com.clinica.modelo.HistoriaClinica;
import com.clinica.factory.HistoriaClinicaDaoFactory; 

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/historias-clinicas")
public class HistoriaClinicaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private IHistoriaClinicaDao historiaClinicaDao = HistoriaClinicaDaoFactory.getHistoriaClinicaDao();
    
   
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> 
                src == null ? null : new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> 
                json == null ? null : LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
            .create();

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
                HistoriaClinica hc = historiaClinicaDao.buscarPorId(id);

                if (hc != null) {
                    out.print(gson.toJson(hc));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"Historia clínica no encontrada con el ID: " + id + "\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"El ID proporcionado debe ser un número entero válido.\"}");
            }
        } else {
            // --- CASO B: LISTAR TODO ---
            List<HistoriaClinica> lista = historiaClinicaDao.listar();
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
        HistoriaClinica nueva = gson.fromJson(reader, HistoriaClinica.class);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (historiaClinicaDao.insertar(nueva)) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print("{\"mensaje\": \"Historia clínica insertada correctamente\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"No se pudo insertar la historia clínica\"}");
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
        HistoriaClinica modificada = gson.fromJson(reader, HistoriaClinica.class);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (historiaClinicaDao.actualizar(modificada)) {
            out.print("{\"mensaje\": \"Historia clínica actualizada correctamente\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"No se pudo actualizar la historia clínica\"}");
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
                
                
                if (historiaClinicaDao.eliminar(id)) {
                    out.print("{\"mensaje\": \"Historia clínica eliminada correctamente\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"error\": \"No se pudo eliminar la historia clínica\"}");
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