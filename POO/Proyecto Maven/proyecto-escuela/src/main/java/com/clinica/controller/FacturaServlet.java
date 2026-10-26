package com.clinica.controller;

import com.clinica.dao.IFacturaDao;
import com.clinica.modelo.Factura;
import com.clinica.factory.FacturaDaoFactory; 
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

@WebServlet("/facturas")
public class FacturaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private IFacturaDao facturaDao = FacturaDaoFactory.getFacturaDao();
    
  
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

        // 🌟 CONFIGURAMOS UN GSON QUE SEPA CONVERTIR LOCALDATETIME A TEXTO JSON
        com.google.gson.Gson gsonPostman = new com.google.gson.GsonBuilder()
            .registerTypeAdapter(java.time.LocalDateTime.class, (com.google.gson.JsonSerializer<java.time.LocalDateTime>) (src, typeOfSrc, context) -> {
                // Esto define cómo se va a ver la fecha en el JSON que recibe Postman
                return new com.google.gson.JsonPrimitive(src.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            })
            .create();

        String idStr = request.getParameter("id");

        if (idStr != null && !idStr.isEmpty()) {
            // --- CASO A: BUSCAR POR ID (?id=X) ---
            try {
                int id = Integer.parseInt(idStr);
                Factura factura = facturaDao.buscarPorId(id);

                if (factura != null) {
                    // 🔄 Usamos gsonPostman configurado
                    out.print(gsonPostman.toJson(factura));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"Factura no encontrada con el ID: " + id + "\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"El ID proporcionado debe ser un número entero válido.\"}");
            }
        } else {
            // --- CASO B: LISTAR TODO ---
            List<Factura> lista = facturaDao.listar();
            // 🔄 Usamos gsonPostman configurado
            out.print(gsonPostman.toJson(lista));
        }
        out.flush();
    }


    // INSERTAR (POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"error\": \"Acceso denegado. Debe iniciar sesión.\"}");
            return;
        }*/
        
        request.setCharacterEncoding("UTF-8");
        BufferedReader reader = request.getReader();

        // 🌟 ACÁ DEFINIMOS EL GSON CORRECTO CON EL ADAPTADOR PARA LOCALDATETIME
        com.google.gson.Gson gsonPostman = new com.google.gson.GsonBuilder()
            .registerTypeAdapter(java.time.LocalDateTime.class, (com.google.gson.JsonDeserializer<java.time.LocalDateTime>) (json, typeOfT, context) -> {
                String fechaStr = json.getAsString();
                // Soporta formatos con espacio ("2010-12-09 12:00:00") o con T ("2010-12-09T12:00:00")
                if (fechaStr.contains(" ")) {
                    return java.time.LocalDateTime.parse(fechaStr, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } else {
                    return java.time.LocalDateTime.parse(fechaStr, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
            })
            .create();

        // 🔄 Usamos "gsonPostman" en lugar del gson global viejo
        Factura nueva = gsonPostman.fromJson(reader, Factura.class);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (facturaDao.insertar(nueva)) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print("{\"mensaje\": \"Factura insertada correctamente\"}");
        } else {
            // Si entra acá, el JSON se parseó bien, pero falló la base de datos (por ejemplo, IDs inexistentes)
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"No se pudo insertar la factura en la base de datos\"}");
        }
        out.flush();
    }
    

    // ACTUALIZAR (PUT)
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"error\": \"Acceso denegado. Debe iniciar sesión.\"}");
            return;
        }*/

        request.setCharacterEncoding("UTF-8");
        BufferedReader reader = request.getReader();

        // 🌟 CONFIGURAMOS EL GSON COMPATIBLE CON LOCALDATETIME PARA EL PUT
        com.google.gson.Gson gsonPostman = new com.google.gson.GsonBuilder()
            .registerTypeAdapter(java.time.LocalDateTime.class, (com.google.gson.JsonDeserializer<java.time.LocalDateTime>) (json, typeOfT, context) -> {
                String fechaStr = json.getAsString();
                if (fechaStr.contains(" ")) {
                    return java.time.LocalDateTime.parse(fechaStr, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } else {
                    return java.time.LocalDateTime.parse(fechaStr, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
            })
            .create();

        // 🔄 Usamos la instancia configurada en lugar de la global vieja
        Factura modificada = gsonPostman.fromJson(reader, Factura.class);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (facturaDao.actualizar(modificada)) {
            // Podés agregar opcionalmente un status 200 OK explícito
            response.setStatus(HttpServletResponse.SC_OK); 
            out.print("{\"mensaje\": \"Factura actualizada correctamente\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"No se pudo actualizar la factura\"}");
        }
        out.flush();
    }


    // ELIMINAR (DELETE)
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"error\": \"Acceso denegado. Debe iniciar sesión.\"}");
            return;
        }*/
        
        String idStr = request.getParameter("id");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (idStr != null && !idStr.isEmpty()) {
            try {
                response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
                out.print("{\"error\": \"La funcionalidad de eliminación no está implementada en el DAO de facturas.\"}");
                
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