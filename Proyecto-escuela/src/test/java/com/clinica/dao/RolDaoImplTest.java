package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class RolDaoImplTest {
	
	private RolDaoImpl rolDao;
	
	@Mock
	private Conexion conexionMock;
	
	@Mock
	private Connection connectionMock;
	
	@Mock
	private CallableStatement callableStatementMock;
	
	@Mock
	private ResultSet resultSetMock;
	
	@BeforeEach
	void setUp() throws SQLException {
		// Inicializa las anotaciones de Mockito antes de cada prueba 
		MockitoAnnotations.openMocks(this);
		rolDao = new RolDaoImpl();
		
		// Encadena comportamientos por defecto para simular JDBC
		when(conexionMock.getConexion()).thenReturn(connectionMock);
		when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
	}
	
	@Test
	@DisplayName("Test Listar Rol - Retorna lista con datos simulados")
	void testListar() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true, false);
			when(resultSetMock.getInt("idRol")).thenReturn(1);
			when(resultSetMock.getString("nombre")).thenReturn("ADMINISTRADOR");
			when(resultSetMock.getString("descripcion")).thenReturn("Acceso total al sistema");

			List<Rol> resultado = rolDao.listar();
			
			assertNotNull(resultado, "La lista no debe ser nula");
			assertFalse(resultado.isEmpty(), "La lista no puede estar vacia");
			assertEquals(1, resultado.size(), "El tamaño de la lista debe ser 1");
			
			Rol r = resultado.get(0);
			assertEquals(1, r.getIdRol());
			assertEquals("ADMINISTRADOR", r.getNombre());
			assertEquals("Acceso total al sistema", r.getDescripcion());
		}
	}
	
	@Test
	@DisplayName("Test Buscar Por ID - Retorna objeto cuando existe el ID")
	void testBuscarPorIdExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true);
			when(resultSetMock.getInt("idRol")).thenReturn(1);
			when(resultSetMock.getString("nombre")).thenReturn("ADMINISTRADOR");
			when(resultSetMock.getString("descripcion")).thenReturn("Acceso total al sistema");
			
			Rol r = rolDao.buscarPorId(1);
			
			assertNotNull(r, "El objeto devuelto no debe ser nulo");
			assertEquals(1, r.getIdRol());
			assertEquals("ADMINISTRADOR", r.getNombre());
			verify(callableStatementMock).setInt(1, 1);					
		}
	}
	
	@Test
	@DisplayName("Test Insertar Rol - Inserción exitosa retorna true")
	void testInsertarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			Rol nuevo = new Rol(0, "MEDICO", "Acceso a historias clínicas y recetas");
			boolean resultado = rolDao.insertar(nuevo);
			
			assertTrue(resultado, "El método debe responder true al insertar");
			verify(callableStatementMock).setString(1, "MEDICO");
			verify(callableStatementMock).setString(2, "Acceso a historias clínicas y recetas");
		}
	}
}