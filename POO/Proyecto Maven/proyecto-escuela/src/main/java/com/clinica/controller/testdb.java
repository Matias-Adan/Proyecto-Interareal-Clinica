package com.clinica.controller;

import jakarta.servlet.ServletException; // 1. IMPORTACIÓN OBLIGATORIA
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException; // Para cerrar la conexión de forma segura

@WebServlet("/testdb")
public class testdb extends HttpServlet {

    @Override
    // 2. SE AGREGÓ 'throws ServletException' PARA CORREGIR LA FIRMA DEL MÉTODO
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        // 3. SE INICIALIZA EN NULL EN CASO DE QUE LANCE EXCEPCIÓN INTERNA
        Connection conn = null; 
        
        try {
            conn = com.clinica.config.Conexion.getConnection();
            
            if (conn != null) {
                resp.getWriter().println("¡Conexión con la DB exitosa!");
            } else {
                resp.getWriter().println("No se pudo conectar con la DB.");
            }
        } catch (Exception e) {
            // 4. SI HAY UN ERROR EN EL DRIVER O CREDENCIALES, TE LO MOSTRARÁ EN LA PÁGINA
            resp.getWriter().println("Error al intentar conectar: " + e.getMessage());
            e.printStackTrace(); 
        } finally {
            // 5. BUENA PRÁCTICA: Cerrar siempre la conexión para no saturar la BD
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
