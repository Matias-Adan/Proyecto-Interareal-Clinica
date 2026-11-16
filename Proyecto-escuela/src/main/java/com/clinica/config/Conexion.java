package com.clinica.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
// Configuración de la base de datos
	private static final String URL = "jdbc:mysql://localhost:3306/clinica";
	private static final String USER = "root";
	private static final String PASS = "";
// Instancia única del Singleton
	private static Conexion instancia;
	private Connection conexion;

// Constructor privado para evitar instanciación directa
	private Conexion() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			this.conexion = DriverManager.getConnection(URL, USER, PASS);
		} catch (ClassNotFoundException e) {
			System.err.println("❌ Error: Driver MySQL no encontrado -> " + e.getMessage());
		} catch (SQLException e) {
			System.err.println("❌ Error al conectar a la BD -> " + e.getMessage());
		}
	}

// Método de acceso global sincronizado
	public static synchronized Conexion getInstancia() {
		try {
			if (instancia == null || instancia.getConexion() == null || instancia.getConexion().isClosed()) {
				instancia = new Conexion();
			}
		} catch (SQLException e) {
			System.err.println("❌ Error comprobando estado de conexión -> " + e.getMessage());
		}
		return instancia;
	}

// Retorna la conexión activa a MySQL
	public Connection getConexion() {
		return conexion;
	}

	/**
	 * Abre una conexión nueva e independiente.
	 *
	 * La conexión compartida del singleton no soporta bien dos consultas
	 * simultáneas: un DAO puede cerrarla mientras otro está leyendo su
	 * ResultSet. Las lecturas que pueden ejecutarse en paralelo con otras
	 * (por ejemplo, las de "Mi salud" o la lista de médicos) usan esta
	 * conexión propia y la cierran al terminar.
	 */
	public Connection nuevaConexion() {
		try {
			return DriverManager.getConnection(URL, USER, PASS);
		} catch (SQLException e) {
			System.err.println("❌ Error al abrir una conexión a la BD -> " + e.getMessage());
			return null;
		}
	}
}