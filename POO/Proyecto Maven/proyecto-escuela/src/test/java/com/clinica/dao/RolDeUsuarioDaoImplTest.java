package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.RolDeUsuario;
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

public class RolDeUsuarioDaoImplTest {
	
	private RolDeUsuarioDaoImpl rolDeUsuarioDao;
	
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
		rolDeUsuarioDao = new RolDeUsuarioDaoImpl();
		
		// Encadena comportamientos por defecto para simular JDBC
		when(conexionMock.getConexion()).thenReturn(connectionMock);
		when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
	}
	
	@Test
	@DisplayName("Test Listar RolDeUsuario - Retorna lista con datos simulados")
	void testListar() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true, false);
			when(resultSetMock.getInt("idRolUsuario")).thenReturn(1);
			when(resultSetMock.getInt("usuarioId")).thenReturn(10);
			when(resultSetMock.getInt("rolId")).thenReturn(2);

			List<RolDeUsuario> resultado = rolDeUsuarioDao.listar();
			
			assertNotNull(resultado, "La lista no debe ser nula");
			assertFalse(resultado.isEmpty(), "La lista no puede estar vacia");
			assertEquals(1, resultado.size(), "El tamaño de la lista debe ser 1");
			
			RolDeUsuario ru = resultado.get(0);
			assertEquals(1, ru.getIdRolUsuario());
			assertEquals(10, ru.getUsuarioId());
			assertEquals(2, ru.getRolId());
		}
	}
	
	@Test
	@DisplayName("Test Buscar Por ID - Retorna objeto cuando existe el ID")
	void testBuscarPorIdExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true);
			when(resultSetMock.getInt("idRolUsuario")).thenReturn(1);
			when(resultSetMock.getInt("usuarioId")).thenReturn(10);
			when(resultSetMock.getInt("rolId")).thenReturn(2);
			
			RolDeUsuario ru = rolDeUsuarioDao.buscarPorId(1);
			
			assertNotNull(ru, "El objeto devuelto no debe ser nulo");
			assertEquals(1, ru.getIdRolUsuario());
			assertEquals(10, ru.getUsuarioId());
			assertEquals(2, ru.getRolId());
			verify(callableStatementMock).setInt(1, 1);					
		}
	}
	
	@Test
	@DisplayName("Test Insertar RolDeUsuario - Inserción exitosa retorna true")
	void testInsertarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			RolDeUsuario nuevo = new RolDeUsuario(0, 10, 2);
			boolean resultado = rolDeUsuarioDao.insertar(nuevo);
			
			assertTrue(resultado, "El método debe responder true al insertar");
			verify(callableStatementMock).setInt(1, 10);
			verify(callableStatementMock).setInt(2, 2);
		}
	}
	
	@Test
	@DisplayName("Test Actualizar RolDeUsuario - Actualización exitosa retorna true")
	void testActualizarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			RolDeUsuario ru = new RolDeUsuario(1, 10, 3);
			boolean resultado = rolDeUsuarioDao.actualizar(ru);
			
			assertTrue(resultado, "El método debe responder true al actualizar");
			verify(callableStatementMock).setInt(1, 1);
			verify(callableStatementMock).setInt(2, 10);
			verify(callableStatementMock).setInt(3, 3);
		}
	}
}