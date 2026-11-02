package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.UbicacionPaciente;
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

public class UbicacionPacienteDaoImplTest {
	
	private UbicacionPacienteDaoImpl ubicacionPacienteDao;
	
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
		ubicacionPacienteDao = new UbicacionPacienteDaoImpl();
		
		// Encadena comportamientos por defecto para simular JDBC
		when(conexionMock.getConexion()).thenReturn(connectionMock);
		when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
	}
	
	@Test
	@DisplayName("Test Listar UbicacionPaciente - Retorna lista con datos simulados")
	void testListar() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true, false);
			when(resultSetMock.getInt("idUbicacion")).thenReturn(1);
			when(resultSetMock.getString("tipoAtencion")).thenReturn("Internación");
			when(resultSetMock.getString("fechaIngreso")).thenReturn("2026-06-06");
			when(resultSetMock.getString("fechaSalida")).thenReturn("2026-06-10");
			when(resultSetMock.getInt("pacienteId")).thenReturn(10);
			when(resultSetMock.getInt("medicoId")).thenReturn(3);
			when(resultSetMock.getInt("consultorioId")).thenReturn(2);

			List<UbicacionPaciente> resultado = ubicacionPacienteDao.listar();
			
			assertNotNull(resultado, "La lista no debe ser nula");
			assertFalse(resultado.isEmpty(), "La lista no puede estar vacia");
			assertEquals(1, resultado.size(), "El tamaño de la lista debe ser 1");
			
			UbicacionPaciente up = resultado.get(0);
			assertEquals(1, up.getIdUbicacion());
			assertEquals("Internación", up.getTipoAtencion());
			assertEquals(10, up.getPacienteId());
			assertEquals(3, up.getMedicoId());
			assertEquals(2, up.getConsultorioId());
		}
	}
	
	@Test
	@DisplayName("Test Buscar Por ID - Retorna objeto cuando existe el ID")
	void testBuscarPorIdExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true);
			when(resultSetMock.getInt("idUbicacion")).thenReturn(1);
			when(resultSetMock.getString("tipoAtencion")).thenReturn("Internación");
			when(resultSetMock.getInt("pacienteId")).thenReturn(10);
			
			UbicacionPaciente up = ubicacionPacienteDao.buscarPorId(1);
			
			assertNotNull(up, "El objeto devuelto no debe ser nulo");
			assertEquals(1, up.getIdUbicacion());
			assertEquals("Internación", up.getTipoAtencion());
			assertEquals(10, up.getPacienteId());
			verify(callableStatementMock).setInt(1, 1);					
		}
	}
	
	@Test
	@DisplayName("Test Insertar UbicacionPaciente - Inserción exitosa retorna true")
	void testInsertarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			LocalDate fechaIngresoEjemplo = LocalDate.of(2026, 6, 6);
			LocalDate fechaSalidaEjemplo = LocalDate.of(2026, 6, 10);
			
			UbicacionPaciente nuevo = new UbicacionPaciente(0, "Guardia", fechaIngresoEjemplo, fechaSalidaEjemplo, 10, 2);
			nuevo.setMedicoId(3);
			
			boolean resultado = ubicacionPacienteDao.insertar(nuevo);
			
			assertTrue(resultado, "El método debe responder true al insertar");
			verify(callableStatementMock).setString(1, "Guardia");
			verify(callableStatementMock).setDate(eq(2), any(java.sql.Date.class));
			verify(callableStatementMock).setDate(eq(3), any(java.sql.Date.class));
			verify(callableStatementMock).setInt(4, 10);
			verify(callableStatementMock).setInt(5, 3);
			verify(callableStatementMock).setInt(6, 2);
		}
	}
	
	@Test
	@DisplayName("Test Actualizar UbicacionPaciente - Actualización exitosa retorna true")
	void testActualizarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			LocalDate fechaIngresoEjemplo = LocalDate.of(2026, 6, 6);
			LocalDate fechaSalidaEjemplo = LocalDate.of(2026, 6, 12);
			
			UbicacionPaciente up = new UbicacionPaciente(1, "Internación", fechaIngresoEjemplo, fechaSalidaEjemplo, 10, 2);
			up.setMedicoId(3);
			
			boolean resultado = ubicacionPacienteDao.actualizar(up);
			
			assertTrue(resultado, "El método debe responder true al actualizar");
			verify(callableStatementMock).setInt(1, 1);
			verify(callableStatementMock).setString(2, "Internación");
			verify(callableStatementMock).setDate(eq(3), any(java.sql.Date.class));
			verify(callableStatementMock).setDate(eq(4), any(java.sql.Date.class));
			verify(callableStatementMock).setInt(5, 10);
			verify(callableStatementMock).setInt(6, 3);
			verify(callableStatementMock).setInt(7, 2);
		}
	}
}