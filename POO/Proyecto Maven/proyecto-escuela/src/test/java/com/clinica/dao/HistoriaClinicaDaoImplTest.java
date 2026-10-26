package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.HistoriaClinica;
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

public class HistoriaClinicaDaoImplTest {
	
	private HistoriaClinicaDaoImpl historiaClinicaDao;
	
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
		historiaClinicaDao = new HistoriaClinicaDaoImpl();
		
		// Encadena comportamientos por defecto para simular JDBC
		when(conexionMock.getConexion()).thenReturn(connectionMock);
		when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
	}
	
	@Test
	@DisplayName("Test Listar HistoriaClinica - Retorna lista con datos simulados")
	void testListar() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true, false);
			when(resultSetMock.getInt("idHistoria")).thenReturn(1);
			when(resultSetMock.getString("fechaApertura")).thenReturn("2026-06-06");
			when(resultSetMock.getString("antecedentes")).thenReturn("Ninguno de importancia");
			when(resultSetMock.getString("obvservacion")).thenReturn("Paciente estable");
			when(resultSetMock.getInt("pacienteId")).thenReturn(5);
			when(resultSetMock.getBoolean("flagBorrado")).thenReturn(false);

			List<HistoriaClinica> resultado = historiaClinicaDao.listar();
			
			assertNotNull(resultado, "La lista no debe ser nula");
			assertFalse(resultado.isEmpty(), "La lista no puede estar vacia");
			assertEquals(1, resultado.size(), "El tamaño de la lista debe ser 1");
			
			HistoriaClinica hc = resultado.get(0);
			assertEquals(1, hc.getIdHistoria());
			assertEquals("Ninguno de importancia", hc.getAntecedentes());
			assertEquals("Paciente estable", hc.getObvservacion());
			assertEquals(5, hc.getPacienteId());
			assertFalse(hc.getFlagBorrado());
		}
	}
	
	@Test
	@DisplayName("Test Buscar Por ID - Retorna objeto cuando existe el ID")
	void testBuscarPorIdExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true);
			when(resultSetMock.getInt("idHistoria")).thenReturn(1);
			when(resultSetMock.getString("antecedentes")).thenReturn("Alergia a la penicilina");
			when(resultSetMock.getInt("pacienteId")).thenReturn(5);
			
			HistoriaClinica hc = historiaClinicaDao.buscarPorId(1);
			
			assertNotNull(hc, "El objeto devuelto no debe ser nulo");
			assertEquals(1, hc.getIdHistoria());
			assertEquals("Alergia a la penicilina", hc.getAntecedentes());
			assertEquals(5, hc.getPacienteId());
			verify(callableStatementMock).setInt(1, 1);					
		}
	}
	
	@Test
	@DisplayName("Test Insertar HistoriaClinica - Inserción exitosa retorna true")
	void testInsertarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			LocalDate fechaAperturaEjemplo = LocalDate.of(2026, 6, 6);
			HistoriaClinica nuevo = new HistoriaClinica(0, fechaAperturaEjemplo, "Sin antecedentes", "Ninguna", 5, false);
			boolean resultado = historiaClinicaDao.insertar(nuevo);
			
			assertTrue(resultado, "El método debe responder true al insertar");
			// Si tu DAO usa setDate para LocalDate, o setObject en su defecto:
			verify(callableStatementMock).setDate(eq(1), any(java.sql.Date.class)); 
			verify(callableStatementMock).setString(2, "Sin antecedentes");
			verify(callableStatementMock).setString(3, "Ninguna");
			verify(callableStatementMock).setInt(4, 5);
			verify(callableStatementMock).setBoolean(5, false);
		}
	}
	
	@Test
	@DisplayName("Test Actualizar HistoriaClinica - Actualización exitosa retorna true")
	void testActualizarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			LocalDate fechaAperturaEjemplo = LocalDate.of(2026, 6, 6);
			HistoriaClinica hc = new HistoriaClinica(1, fechaAperturaEjemplo, "Asma leve", "Control periódico", 5, false);
			boolean resultado = historiaClinicaDao.actualizar(hc);
			
			assertTrue(resultado, "El método debe responder true al actualizar");
			verify(callableStatementMock).setInt(1, 1);
			verify(callableStatementMock).setDate(eq(2), any(java.sql.Date.class));
			verify(callableStatementMock).setString(3, "Asma leve");
			verify(callableStatementMock).setString(4, "Control periódico");
			verify(callableStatementMock).setInt(5, 5);
			verify(callableStatementMock).setBoolean(6, false);
		}
	}
}