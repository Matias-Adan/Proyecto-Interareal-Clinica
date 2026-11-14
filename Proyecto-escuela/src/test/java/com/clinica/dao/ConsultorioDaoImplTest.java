package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Consultorio;
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

public class ConsultorioDaoImplTest {
	
	private ConsultorioDaoImpl consultorioDao;
	
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
		consultorioDao = new ConsultorioDaoImpl();
		
		// Encadena comportamientos por defecto para simular JDBC
		when(conexionMock.getConexion()).thenReturn(connectionMock);
		when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
	}
	
	@Test
	@DisplayName("Test Listar Consultorio - Retorna lista con datos simulados")
	void testListar() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true, false);
			when(resultSetMock.getInt("idConsultorio")).thenReturn(1);
			when(resultSetMock.getString("numero")).thenReturn("101");
			when(resultSetMock.getString("tipo")).thenReturn("General");
			when(resultSetMock.getString("estado")).thenReturn("Disponible");
			when(resultSetMock.getInt("piso")).thenReturn(1);

			List<Consultorio> resultado = consultorioDao.listar();
			
			assertNotNull(resultado, "La lista no debe ser nula");
			assertFalse(resultado.isEmpty(), "La lista no puede estar vacia");
			
			Consultorio c = resultado.get(0);
			assertEquals(1, c.getIdConsultorio());
			assertEquals("101", c.getNumero());
			assertEquals("General", c.getTipo());
			assertEquals("Disponible", c.getEstado());
			assertEquals(1, c.getPiso());
		}
	}
	
	@Test
	@DisplayName("Test Buscar Por ID - Retorna objeto cuando existe el ID")
	void testBuscarPorIdExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true);
			when(resultSetMock.getInt("idConsultorio")).thenReturn(1);
			when(resultSetMock.getString("numero")).thenReturn("101");
			when(resultSetMock.getInt("piso")).thenReturn(1);
			when(resultSetMock.getString("tipo")).thenReturn("General");
			when(resultSetMock.getString("estado")).thenReturn("Disponible");

			
			Consultorio c = consultorioDao.buscarPorId(1);
			
			assertNotNull(c, "El objeto devuelto no debe ser nulo");
			assertEquals(1, c.getIdConsultorio());
			assertEquals("101", c.getNumero());
			assertEquals("General", c.getTipo());
			assertEquals("Disponible", c.getEstado());
			verify(callableStatementMock).setInt(1, 1);					
		}
	}
	
	@Test
	@DisplayName("Test Insertar Consultorio - Inserción exitosa retorna true")
	void testInsertarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			Consultorio nuevo = new Consultorio(0, "101", "General", "Disponible", 1);
			boolean resultado = consultorioDao.insertar(nuevo);
			
			assertTrue(resultado, "El método debe responder true al insertar");
			verify(callableStatementMock).setString(1, "101");
			verify(callableStatementMock).setInt(2, 1);
			verify(callableStatementMock).setString(3, "General");
			verify(callableStatementMock).setString(4, "Disponible");
		}
	}
	
	@Test
	@DisplayName("Test Actualizar Consultorio - Actualización exitosa retorna true")
	void testActualizarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			Consultorio c = new Consultorio();
			c.setIdConsultorio(1);
			c.setEstado("Ocupado");
			boolean resultado = consultorioDao.actualizar(c);
			
			assertTrue(resultado, "El método debe responder true al actualizar");
			verify(callableStatementMock).setInt(1, 1);
			verify(callableStatementMock).setString(2, "Ocupado");	
		}
	}
}