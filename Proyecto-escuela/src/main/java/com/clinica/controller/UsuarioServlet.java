package com.clinica.controller;

import com.clinica.dao.IUsuarioDao;
import com.clinica.factory.UsuarioDaoFactory;
import com.clinica.modelo.Usuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
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

// Mapeamos para capturar tanto la raíz como sub-rutas (ej: /usuarios/login)
@WebServlet(urlPatterns = {"/usuarios", "/usuarios/*"})

public class UsuarioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private IUsuarioDao usuarioDao = UsuarioDaoFactory.getUsuarioDao();
    
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> 
                new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> 
                LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
            .create();
    
    
    // INTERCEPTOR OBLIGATORIO PARA SOPORTAR PATCH
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        
        // Si viene por POST, revisamos si trae la cabecera X-HTTP-Method-Override
        if (method.equalsIgnoreCase("POST")) {
            String override = req.getHeader("X-HTTP-Method-Override");
            if (override != null && override.equalsIgnoreCase("PATCH")) {
                method = "PATCH";
            }
        }

        // Direccionamos al método correspondiente
        if (method.equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp); // Sigue hacia GET, POST, PUT, DELETE normales
        }
    }

    
    // GET - Mantiene la lógica de Listar y Buscar por ID
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String idStr = request.getParameter("id");
        
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                Usuario usuario = usuarioDao.buscarPorId(id);
                
                if (usuario != null) {
                    out.print(gson.toJson(usuario));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"Usuario no encontrado con el ID: " + id + "\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"El ID proporcionado debe ser un número entero válido.\"}");
            }
        } else {
            List<Usuario> lista = usuarioDao.listar();
            out.print(gson.toJson(lista));
        }
        out.flush();
    }

    // POST - Maneja tanto la creación de usuarios como el Login
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String pathInfo = request.getPathInfo(); // Captura lo que va después de /usuarios
        
     // --- CASO 1: PROCESO DE LOGIN (/usuarios/login) ---
        if (pathInfo != null && pathInfo.equals("/login")) {
            BufferedReader reader = request.getReader();
            try {
                JsonObject jsonLogin = gson.fromJson(reader, JsonObject.class);
                
                // Extraemos únicamente el email del JSON
                String emailInput = jsonLogin.has("email") ? jsonLogin.get("email").getAsString() : null;
                
                if (emailInput == null || emailInput.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Falta el parámetro requerido: email\"}");
                    return;
                }
                
                // Se ejecuta tu Stored Procedure enviando solo el email
                Usuario usuarioAutenticado = usuarioDao.login(emailInput);
                
                // El chequeo ahora solo valida si el procedimiento encontró al usuario
                if (usuarioAutenticado != null) {
                    HttpSession session = request.getSession();
                    session.setAttribute("usuarioLogueado", usuarioAutenticado);
                    
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print("{\"mensaje\": \"Login exitoso (Usuario encontrado)\", \"usuario\": " + gson.toJson(usuarioAutenticado) + "}");
                } else {
                    // Si el procedimiento sp_usuarios_login no devolvió ningún registro
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND); 
                    out.print("{\"error\": \"El email proporcionado no corresponde a ningún usuario registrado.\"}");
                }
                
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Error al procesar la solicitud de login.\"}");
            }
            out.flush();
            return; 
        }
        
        // --- CASO 2: CREACIÓN DE USUARIO NORMAL (/usuarios) ---
        BufferedReader reader = request.getReader();
        try {
            Usuario nuevo = gson.fromJson(reader, Usuario.class);
            if (usuarioDao.insertar(nuevo)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"mensaje\": \"Usuario insertado correctamente\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"No se pudo insertar el usuario\"}");
            }
            out.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Error en el formato de los datos de registro.\"}");
            out.flush();
        }
    }

    // PUT - Actualizar usuario
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTP 401
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"error\": \"No autorizado. Debe iniciar sesión.\"}");
            return; // Corta la ejecución de inmediato
        }

        request.setCharacterEncoding("UTF-8");
        BufferedReader reader = request.getReader();
        
        try {
            Usuario modificado = gson.fromJson(reader, Usuario.class);
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            
            if (usuarioDao.actualizar(modificado)) {
                out.print("{\"mensaje\": \"Usuario actualizado correctamente\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"No se pudo actualizar el usuario\"}");
            }
            out.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"error\": \"Error en el formato de los datos.\"}");
        }
    }

    // DELETE - Eliminar usuario por ?id=X
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTP 401
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"error\": \"No autorizado. Debe iniciar sesión.\"}");
            return; // Corta la ejecución de inmediato
        }
    	
        String idStr = request.getParameter("id");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                if (usuarioDao.eliminar(id)) {
                    out.print("{\"mensaje\": \"Usuario eliminado correctamente\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"error\": \"No se pudo eliminar el usuario\"}");
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
            if (!jsonBody.has("idUsuario") || !jsonBody.has("flagBorrado")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Faltan parámetros requeridos en el JSON: idUsuario y flagBorrado\"}");
                out.flush();
                return;
            }
            
            int id = jsonBody.get("idUsuario").getAsInt();
            boolean flagBorrado = jsonBody.get("flagBorrado").getAsBoolean();
            
            // Invocamos el método creado en el paso anterior en tu DAO
            if (usuarioDao.cambiarEstadoBorrado(id, flagBorrado)) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"mensaje\": \"Estado de borrado actualizado correctamente\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"No se pudo actualizar el estado de borrado del usuario\"}");
            }
            
        } catch (Exception e) {
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        	out.println("{\"error\": \"Error en el formato de los datos JSON enviados en el PATCH.\"}");
        			
    	}
    	out.flush();
    }
}

