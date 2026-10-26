package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Turno;
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
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TurnoDaoImplTest {
	
	private TurnoDaoImpl turnoDao;
	
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
		turnoDao = new TurnoDaoImpl();
		
		// Encadena comportamientos por defecto para simular JDBC
		when(conexionMock.getConexion()).thenReturn(connectionMock);
		when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
	}
	
	@Test
	@DisplayName("Test Listar Turno - Retorna lista con datos simulados")
	void testListar() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true, false);
			when(resultSetMock.getInt("idTurno")).thenReturn(1);
			when(resultSetMock.getString("fecha")).thenReturn("2026-06-06"); // O getDate / getObject según tu DAO
			when(resultSetMock.getString("horaInicio")).thenReturn("09:00:00"); // O getTime
			when(resultSetMock.getString("horaFin")).thenReturn("09:30:00");
			when(resultSetMock.getString("estado")).thenReturn("Confirmado");
			when(resultSetMock.getString("motivo")).thenReturn("Control general");
			when(resultSetMock.getInt("pacienteId")).thenReturn(10);
			when(resultSetMock.getInt("personalId")).thenReturn(3);
			when(resultSetMock.getInt("especialidadId")).thenReturn(1);
			when(resultSetMock.getInt("consultorioId")).thenReturn(2);

			List<Turno> resultado = turnoDao.listar();
			
			assertNotNull(resultado, "La lista no debe ser nula");
			assertFalse(resultado.isEmpty(), "La lista no puede estar vacia");
			assertEquals(1, resultado.size(), "El tamaño de la lista debe ser 1");
			
			Turno t = resultado.get(0);
			assertEquals(1, t.getIdTurno());
			assertEquals("Confirmado", t.getEstado());
			assertEquals("Control general", t.getMotivo());
			assertEquals(10, t.getPacienteId());
			assertEquals(3, t.getPersonalId());
			assertEquals(1, t.getEspecialidadId());
			assertEquals(2, t.getConsultorioId());
		}
	}
	
	@Test
	@DisplayName("Test Buscar Por ID - Retorna objeto cuando existe el ID")
	void testBuscarPorIdExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true);
			when(resultSetMock.getInt("idTurno")).thenReturn(1);
			when(resultSetMock.getString("estado")).thenReturn("Confirmado");
			when(resultSetMock.getInt("pacienteId")).thenReturn(10);
			
			Turno t = turnoDao.buscarPorId(1);
			
			assertNotNull(t, "El objeto devuelto no debe ser nulo");
			assertEquals(1, t.getIdTurno());
			assertEquals("Confirmado", t.getEstado());
			assertEquals(10, t.getPacienteId());
			verify(callableStatementMock).setInt(1, 1);					
		}
	}
	
	@Test
	@DisplayName("Test Insertar Turno - Inserción exitosa retorna true")
	void testInsertarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			LocalDate fechaEjemplo = LocalDate.of(2026, 6, 6);
			LocalTime horaInicioEjemplo = LocalTime.of(9, 0);
			LocalTime horaFinEjemplo = LocalTime.of(9, 30);
			
			Turno nuevo = new Turno(0, fechaEjemplo, horaInicioEjemplo, horaFinEjemplo, "Pendiente", "Dolor de cabeza", 10, 3, 1, 2);
			boolean resultado = turnoDao.insertar(nuevo);
			
			assertTrue(resultado, "El método debe responder true al insertar");
			// Ajusta a setDate, setTime, setObject o setString según los tipos de parámetros que reciba tu Stored Procedure
			verify(callableStatementMock).setDate(eq(1), any(java.sql.Date.class));
			verify(callableStatementMock).setTime(eq(2), any(Time.class));
			verify(callableStatementMock).setTime(eq(3), any(Time.class));
			verify(callableStatementMock).setString(4, "Pendiente");
			verify(callableStatementMock).setString(5, "Dolor de cabeza");
			verify(callableStatementMock).setInt(6, 10);
			verify(callableStatementMock).setInt(7, 3);
			verify(callableStatementMock).setInt(8, 1);
			verify(callableStatementMock).setInt(9, 2);
		}
	}
	
	@Test
	@DisplayName("Test Actualizar Turno - Actualización exitosa retorna true")
	void testActualizarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			LocalDate fechaEjemplo = LocalDate.of(2026, 6, 6);
			LocalTime horaInicioEjemplo = LocalTime.of(9, 0);
			LocalTime horaFinEjemplo = LocalTime.of(9, 30);
			
			Turno t = new Turno(1, fechaEjemplo, horaInicioEjemplo, horaFinEjemplo, "Completado", "Control", 10, 3, 1, 2);
			boolean resultado = turnoDao.actualizar(t);
			
			assertTrue(resultado, "El método debe responder true al actualizar");
			verify(callableStatementMock).setInt(1, 1);
			verify(callableStatementMock).setDate(eq(2), any(java.sql.Date.class));
			verify(callableStatementMock).setTime(eq(3), any(Time.class));
			verify(callableStatementMock).setTime(eq(4), any(Time.class));
			verify(callableStatementMock).setString(5, "Completado");
			verify(callableStatementMock).setString(6, "Control");
			verify(callableStatementMock).setInt(7, 10);
			verify(callableStatementMock).setInt(8, 3);
			verify(callableStatementMock).setInt(9, 1);
			verify(callableStatementMock).setInt(10, 2);
		}
	}
}