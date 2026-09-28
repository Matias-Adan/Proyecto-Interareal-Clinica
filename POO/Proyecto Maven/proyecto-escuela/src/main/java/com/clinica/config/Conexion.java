package com.clinica.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
	private static final String URL = "jdbc:mysql://localhost:3306/proyecto-clinica";
    private static final String USER = "root";
    private static final String PASS = ""; 

    public static Connection conectar() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            System.out.println("Error: Driver de MySQL no encontrado -> " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos -> " + e.getMessage());
        }
        return con;
    }

    public static Connection getConnection() {
        return conectar();
    }
}