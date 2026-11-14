package com.clinica.controller;

import com.clinica.dao.IConsultaDao;
import com.clinica.modelo.Consulta;
import com.clinica.modelo.DetalleReceta;
import com.clinica.modelo.Receta;
import com.clinica.service.AtencionMedicaService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/consultas")
public class ConsultaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private AtencionMedicaService consultaDao = new AtencionMedicaService();
    
   
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> 
                src == null ? null : new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> 
                json == null ? null : LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> 
                src == null ? null : new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> 
                json == null ? null : LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
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
                Consulta consulta = consultaDao.buscarPorId(id);

                if (consulta != null) {
                    out.print(gson.toJson(consulta));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"Consulta no encontrada con el ID: " + id + "\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"El ID proporcionado debe ser un número entero válido.\"}");
            }
        } else {
            // --- CASO B: LISTAR TODO ---
            List<Consulta> lista = consultaDao.listar();
            out.print(gson.toJson(lista));
        }
        out.flush();
    }

    // INSERTAR (POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        BufferedReader reader = request.getReader();

        // Se aceptan dos formatos:
        //   1) El formato plano original: { "motivo": ..., "historiaId": ... }
        //   2) El formato completo: { "consulta": {...}, "receta": {...}, "detalles": [...] }
        JsonObject json = gson.fromJson(reader, JsonObject.class);

        Consulta nueva;
        Receta receta = null;
        List<DetalleReceta> detalles = null;

        if (json != null && json.has("consulta") && json.get("consulta").isJsonObject()) {
            nueva = gson.fromJson(json.get("consulta"), Consulta.class);

            if (json.has("receta") && json.get("receta").isJsonObject()) {
                receta = gson.fromJson(json.get("receta"), Receta.class);
            }

            if (json.has("detalles") && json.get("detalles").isJsonArray()) {
                detalles = new ArrayList<>();
                for (JsonElement elemento : json.getAsJsonArray("detalles")) {
                    detalles.add(gson.fromJson(elemento, DetalleReceta.class));
                }
            }
        } else {
            nueva = gson.fromJson(json, Consulta.class);
        }

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        int idConsulta = consultaDao.registrarConsulta(nueva, receta, detalles);

        response.setStatus(HttpServletResponse.SC_CREATED);
        out.print("{\"mensaje\": \"Consulta insertada correctamente\", "
                + "\"idConsulta\": " + idConsulta + "}");
        out.flush();
    }

    // ACTUALIZAR (PUT)
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        BufferedReader reader = request.getReader();
        Consulta modificada = gson.fromJson(reader, Consulta.class);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (consultaDao.actualizar(modificada)) {
            out.print("{\"mensaje\": \"Consulta actualizada correctamente\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"No se pudo actualizar la consulta\"}");
        }
        out.flush();
    }

    // ELIMINAR (DELETE)
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (idStr != null && !idStr.isEmpty()) {
            try {
                response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
                out.print("{\"error\": \"La funcionalidad de eliminación no está implementada en el DAO de consultas.\"}");
                
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