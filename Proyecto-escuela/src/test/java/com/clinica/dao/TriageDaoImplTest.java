package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Triage;
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
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TriageDaoImplTest {
	
	private TriageDaoImpl triageDao;
	
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
		triageDao = new TriageDaoImpl();
		
		// Encadena comportamientos por defecto para simular JDBC
		when(conexionMock.getConexion()).thenReturn(connectionMock);
		when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
	}
	
	@Test
	@DisplayName("Test Listar Triage - Retorna lista con datos simulados")
	void testListar() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true, false);
			when(resultSetMock.getInt("idTriage")).thenReturn(1);
			when(resultSetMock.getString("fecha")).thenReturn("2026-06-06"); // O getDate / getObject según tu DAO
			when(resultSetMock.getString("prioridad")).thenReturn("Rojo");
			when(resultSetMock.getString("estado")).thenReturn("Pendiente");
			when(resultSetMock.getString("observacion")).thenReturn("Urgencia vital");
			when(resultSetMock.getInt("pacienteId")).thenReturn(10);
			when(resultSetMock.getInt("consultorioId")).thenReturn(2);
			when(resultSetMock.getInt("enfermeroId")).thenReturn(5);

			List<Triage> resultado = triageDao.listar();
			
			assertNotNull(resultado, "La lista no debe ser nula");
			assertFalse(resultado.isEmpty(), "La lista no puede estar vacia");
			assertEquals(1, resultado.size(), "El tamaño de la lista debe ser 1");
			
			Triage t = resultado.get(0);
			assertEquals(1, t.getIdTriage());
			assertEquals("Rojo", t.getPrioridad());
			assertEquals("Pendiente", t.getEstado());
			assertEquals("Urgencia vital", t.getObservacion());
			assertEquals(10, t.getPacienteId());
			assertEquals(2, t.getConsultorioId());
			assertEquals(5, t.getEnfermeroId());
		}
	}
	
	@Test
	@DisplayName("Test Buscar Por ID - Retorna objeto cuando existe el ID")
	void testBuscarPorIdExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true);
			when(resultSetMock.getInt("idTriage")).thenReturn(1);
			when(resultSetMock.getString("prioridad")).thenReturn("Rojo");
			when(resultSetMock.getInt("pacienteId")).thenReturn(10);
			
			Triage t = triageDao.buscarPorId(1);
			
			assertNotNull(t, "El objeto devuelto no debe ser nulo");
			assertEquals(1, t.getIdTriage());
			assertEquals("Rojo", t.getPrioridad());
			assertEquals(10, t.getPacienteId());
			verify(callableStatementMock).setInt(1, 1);					
		}
	}
	
	@Test
	@DisplayName("Test Insertar Triage - Inserción exitosa retorna true")
	void testInsertarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			LocalDate fechaEjemplo = LocalDate.of(2026, 6, 6);
			Triage nuevo = new Triage(0, fechaEjemplo, "Amarillo", "Atendido", "Dolor moderado", 10, 2, 5);
			boolean resultado = triageDao.insertar(nuevo);
			
			assertTrue(resultado, "El método debe responder true al insertar");
			// Ajusta a setDate o setObject según lo que utilice el Stored Procedure de inserción
			verify(callableStatementMock).setDate(eq(1), any(java.sql.Date.class));
			verify(callableStatementMock).setString(2, "Amarillo");
			verify(callableStatementMock).setString(3, "Atendido");
			verify(callableStatementMock).setString(4, "Dolor moderado");
			verify(callableStatementMock).setInt(5, 10);
			verify(callableStatementMock).setInt(6, 2);
			verify(callableStatementMock).setInt(7, 5);
		}
	}
	
	@Test
	@DisplayName("Test Actualizar Triage - Actualización exitosa retorna true")
	void testActualizarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			LocalDate fechaEjemplo = LocalDate.of(2026, 6, 6);
			Triage t = new Triage(1, fechaEjemplo, "Verde", "Finalizado", "Controlado", 10, 2, 5);
			boolean resultado = triageDao.actualizar(t);
			
			assertTrue(resultado, "El método debe responder true al actualizar");
			verify(callableStatementMock).setInt(1, 1);
			verify(callableStatementMock).setDate(eq(2), any(java.sql.Date.class));
			verify(callableStatementMock).setString(3, "Verde");
			verify(callableStatementMock).setString(4, "Finalizado");
			verify(callableStatementMock).setString(5, "Controlado");
			verify(callableStatementMock).setInt(6, 10);
			verify(callableStatementMock).setInt(7, 2);
			verify(callableStatementMock).setInt(8, 5);
		}
	}
}