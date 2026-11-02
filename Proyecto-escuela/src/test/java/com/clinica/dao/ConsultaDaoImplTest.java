package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Consulta;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ConsultaDaoImplTest {
	
	private ConsultaDaoImpl consultaDao;
	
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
		consultaDao = new ConsultaDaoImpl();
		
		// Encadena comportamientos por defecto para simular JDBC
		when(conexionMock.getConexion()).thenReturn(connectionMock);
		when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
	}
	
	@Test
	@DisplayName("Test Listar Consulta - Retorna lista con datos simulados")
	void testListar() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true, false);
			when(resultSetMock.getInt("idConsulta")).thenReturn(1);
			when(resultSetMock.getString("fechahora")).thenReturn("2026-06-06 10:00:00"); // O getTimestamp / getObject según tu DAO
			when(resultSetMock.getString("motivo")).thenReturn("Control general");
			when(resultSetMock.getString("diagnostico")).thenReturn("Paciente estable");
			when(resultSetMock.getString("obvservacion")).thenReturn("Sin observaciones relevantes");
			when(resultSetMock.getInt("historialId")).thenReturn(3);
			when(resultSetMock.getInt("medicoId")).thenReturn(2);

			List<Consulta> resultado = consultaDao.listar();
			
			assertNotNull(resultado, "La lista no debe ser nula");
			assertFalse(resultado.isEmpty(), "La lista no puede estar vacia");
			assertEquals(1, resultado.size(), "El tamaño de la lista debe ser 1");
			
			Consulta c = resultado.get(0);
			assertEquals(1, c.getIdConsulta());
			assertEquals("Control general", c.getMotivo());
			assertEquals("Paciente estable", c.getDiagnostico());
			assertEquals(3, c.getHistoriaId());
			assertEquals(2, c.getMedicoId());
		}
	}
	
	@Test
	@DisplayName("Test Buscar Por ID - Retorna objeto cuando existe el ID")
	void testBuscarPorIdExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true);
			when(resultSetMock.getInt("idConsulta")).thenReturn(1);
			when(resultSetMock.getString("motivo")).thenReturn("Dolor de cabeza");
			when(resultSetMock.getInt("medicoId")).thenReturn(2);
			
			Consulta c = consultaDao.buscarPorId(1);
			
			assertNotNull(c, "El objeto devuelto no debe ser nulo");
			assertEquals(1, c.getIdConsulta());
			assertEquals("Dolor de cabeza", c.getMotivo());
			assertEquals(2, c.getMedicoId());
			verify(callableStatementMock).setInt(1, 1);					
		}
	}
	
	@Test
	@DisplayName("Test Insertar Consulta - Inserción exitosa retorna true")
	void testInsertarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			LocalDateTime fechaHoraEjemplo = LocalDateTime.of(2026, 6, 6, 10, 0);
			Consulta nuevo = new Consulta(0, fechaHoraEjemplo, "Fiebre", "Gripe común", "Reposo", 3, 2);
			boolean resultado = consultaDao.insertar(nuevo);
			
			assertTrue(resultado, "El método debe responder true al insertar");
			verify(callableStatementMock).setTimestamp(eq(1), any(java.sql.Timestamp.class)); 
			verify(callableStatementMock).setString(2, "Fiebre");
			verify(callableStatementMock).setString(3, "Gripe común");
			verify(callableStatementMock).setString(4, "Reposo");
			verify(callableStatementMock).setInt(5, 3);
			verify(callableStatementMock).setInt(6, 2);
		}
	}
	
	@Test
	@DisplayName("Test Actualizar Consulta - Actualización exitosa retorna true")
	void testActualizarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			LocalDateTime fechaHoraEjemplo = LocalDateTime.of(2026, 6, 6, 10, 0);
			Consulta c = new Consulta(1, fechaHoraEjemplo, "Fiebre alta", "Faringitis", "Antibióticos", 3, 2);
			boolean resultado = consultaDao.actualizar(c);
			
			assertTrue(resultado, "El método debe responder true al actualizar");
			verify(callableStatementMock).setInt(1, 1);
			verify(callableStatementMock).setTimestamp(eq(2), any(java.sql.Timestamp.class));
			verify(callableStatementMock).setString(3, "Fiebre alta");
			verify(callableStatementMock).setString(4, "Faringitis");
			verify(callableStatementMock).setString(5, "Antibióticos");
			verify(callableStatementMock).setInt(6, 3);
			verify(callableStatementMock).setInt(7, 2);
		}
	}
}