package com.clinica.controller;

import com.clinica.dao.IVistaTurnosDao;
import com.clinica.dao.VistaTurnosDaoImpl;
import com.clinica.modelo.VistaTurnos;

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
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/vista-turnos")
public class VistaTurnosServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private IVistaTurnosDao vistaTurnosDao = new VistaTurnosDaoImpl();
  
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> 
                src == null ? null : new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> 
                json == null ? null : LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
            .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (src, typeOfSrc, context) -> 
                src == null ? null : new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_TIME)))
            .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, typeOfT, context) -> 
                json == null ? null : LocalTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_TIME))
            .create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String dniStr = request.getParameter("paciente_dni");

        if (dniStr != null && !dniStr.isEmpty()) {
            try {
                // Como tu método DAO recibe String, pasamos directamente dniStr (o Integer.parseInt si prefieres)
                VistaTurnos vt = vistaTurnosDao.buscarPorDni(dniStr);

                if (vt != null) {
                    out.print(gson.toJson(vt));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"VistaTurno no encontrado con el DNI: " + dniStr + "\"}");
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Error al procesar la búsqueda por DNI: " + e.getMessage() + "\"}");
            }
        } else {
            List<VistaTurnos> lista = vistaTurnosDao.listar();
            out.print(gson.toJson(lista));
        }
        out.flush();
    }
}












