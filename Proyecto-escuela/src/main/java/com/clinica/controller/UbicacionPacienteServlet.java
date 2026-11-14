package com.clinica.controller;

import com.clinica.dao.IUbicacionPacienteDao;
import com.clinica.modelo.UbicacionPaciente;
import com.clinica.service.UbicacionPacienteService;

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
import java.time.LocalDateTime; 
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/ubicacionpaciente")
public class UbicacionPacienteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private IUbicacionPacienteDao ubicacionPacienteDao = new UbicacionPacienteService();
    
   
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

  
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }

    
    
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
                UbicacionPaciente ubicacion = ubicacionPacienteDao.buscarPorId(id);

                if (ubicacion != null) {
                    out.print(gson.toJson(ubicacion));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"Ubicación de paciente no encontrada con el ID: " + id + "\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"El ID proporcionado debe ser un número entero válido.\"}");
            }
        } else {
            // --- CASO B: LISTAR TODO ---
            List<UbicacionPaciente> lista = ubicacionPacienteDao.listar();
            out.print(gson.toJson(lista));
        }
        out.flush();
    }

    // INSERTAR (POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
        
        request.setCharacterEncoding("UTF-8");
        BufferedReader reader = request.getReader();
        UbicacionPaciente nueva = gson.fromJson(reader, UbicacionPaciente.class);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (ubicacionPacienteDao.insertar(nueva)) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print("{\"mensaje\": \"Ubicación de paciente insertada correctamente\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"No se pudo insertar la ubicación de paciente\"}");
        }
        out.flush();
    }

    // ACTUALIZAR (PUT)
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
        
        request.setCharacterEncoding("UTF-8");
        BufferedReader reader = request.getReader();
        UbicacionPaciente modificada = gson.fromJson(reader, UbicacionPaciente.class);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (ubicacionPacienteDao.actualizar(modificada)) {
            out.print("{\"mensaje\": \"Ubicación de paciente actualizada correctamente\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"No se pudo actualizar la ubicación de paciente\"}");
        }
        out.flush();
    }
    
    // DAR DE ALTA / FINALIZAR UBICACIÓN (PATCH)
    
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Validar la sesión del usuario
        /*HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"error\": \"Acceso denegado. Debe iniciar sesión.\"}");
            return;
        }*/
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // 2. Leer los parámetros enviados en el cuerpo JSON (ej: {"idUbicacion": 5, "fechaSalida": "2026-11-02T11:15:00"})
            BufferedReader reader = request.getReader();
            UbicacionPaciente datosAlta = gson.fromJson(reader, UbicacionPaciente.class);

            if (datosAlta == null || datosAlta.getIdUbicacion() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"El campo 'idUbicacion' es obligatorio en el cuerpo de la solicitud.\"}");
                out.flush();
                return;
            }

            int id = datosAlta.getIdUbicacion();
            LocalDateTime fechaSalida = datosAlta.getFechaSalida(); // Puede ser null, el SP usará NOW() por el COALESCE

            // 3. Ejecutar el método en el DAO
            if (ubicacionPacienteDao.darAlta(id, fechaSalida)) {
                out.print("{\"mensaje\": \"Paciente dado de alta de su ubicación correctamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"No se pudo procesar el alta. Verifique que el ID de ubicación exista.\"}");
            }

        } catch (com.google.gson.JsonSyntaxException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Error en el formato JSON enviado o formato de fecha inválido.\"}");
        } catch (SecurityException e) {
            throw e;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Ocurrió un error inesperado: " + e.getMessage() + "\"}");
        }
        out.flush();
    }


 // ELIMINAR (DELETE)
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
        out.print("{\"error\": \"La funcionalidad de eliminación no está implementada en el DAO de ubicacion del paciente.\"}");
        out.flush();
    }
}
