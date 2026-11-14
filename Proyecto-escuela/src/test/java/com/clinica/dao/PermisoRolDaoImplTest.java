package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.PermisoRol;
import com.mysql.cj.jdbc.CallableStatement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PermisoRolDaoImplTest {
	
	private PermisoRolDaoImpl permisoRolDao;
	
	@Mock
	private Conexion conexionMock;
	
	@Mock
	private Connection connectionMock;
	
	@Mock
	private CallableStatement callableStatementMock; // Cambiado a PreparedStatement
	
	@Mock
	private ResultSet resultSetMock;
	
	@BeforeEach
	void setUp() throws SQLException {
		// Inicializa las anotaciones de Mockito antes de cada prueba 
		MockitoAnnotations.openMocks(this);
		permisoRolDao = new PermisoRolDaoImpl();
		
		// Encadena comportamientos por defecto para simular JDBC con PreparedStatement
		when(conexionMock.getConexion()).thenReturn(connectionMock);
		when(connectionMock.prepareStatement(anyString())).thenReturn(callableStatementMock);
	}
	
	@Test
	@DisplayName("Test Listar PermisoRol - Retorna lista con datos simulados")
	void testListar() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true, false);
			when(resultSetMock.getInt("idPermisoRol")).thenReturn(1);
			when(resultSetMock.getInt("rolId")).thenReturn(2);
			when(resultSetMock.getInt("permisoId")).thenReturn(5);

			List<PermisoRol> resultado = permisoRolDao.listar();
			
			assertNotNull(resultado, "La lista no debe ser nula");
			assertFalse(resultado.isEmpty(), "La lista no puede estar vacia");
			assertEquals(1, resultado.size(), "El tamaño de la lista debe ser 1");
			
			PermisoRol pr = resultado.get(0);
			assertEquals(1, pr.getIdPermisoRol());
			assertEquals(2, pr.getRolId());
			assertEquals(5, pr.getPermisoId());
		}
	}
	
	@Test
	@DisplayName("Test Insertar PermisoRol - Inserción exitosa retorna true")
	void testInsertarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(conexionMock.getConexion()).thenReturn(connectionMock);
			
			when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			PermisoRol nuevo = new PermisoRol(1, 2, 5);
			boolean resultado = permisoRolDao.insertar(nuevo);
			
			assertTrue(resultado, "El método debe responder true al insertar");
			verify(callableStatementMock).setInt(1, 2);
			verify(callableStatementMock).setInt(2, 5);
			
		}
	}
	
	
	@Test
	@DisplayName("Test Eliminar PermisoRol - Eliminación exitosa retorna true")
	void testEliminarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(conexionMock.getConexion()).thenReturn(connectionMock);
			
			when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			boolean resultado = permisoRolDao.eliminar(1);
			
			assertTrue(resultado, "El método debe responder true al eliminar");
			verify(callableStatementMock).setInt(1, 1);
		}
	}
}