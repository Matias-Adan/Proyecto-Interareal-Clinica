package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Factura;
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

public class FacturaDaoImplTest {
	
	private FacturaDaoImpl facturaDao;
	
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
		facturaDao = new FacturaDaoImpl();
		
		// Encadena comportamientos por defecto para simular JDBC
		when(conexionMock.getConexion()).thenReturn(connectionMock);
		when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
	}
	
	@Test
	@DisplayName("Test Listar Factura - Retorna lista con datos simulados")
	void testListar() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true, false);
			when(resultSetMock.getInt("idFactura")).thenReturn(1);
			when(resultSetMock.getString("fecha")).thenReturn("2026-06-06"); // O getObject / getDate según tu DAO
			when(resultSetMock.getDouble("importeTotal")).thenReturn(1500.50);
			when(resultSetMock.getString("estado")).thenReturn("Pagado");
			when(resultSetMock.getInt("pacienteId")).thenReturn(10);
			when(resultSetMock.getInt("obraSocialId")).thenReturn(2);

			List<Factura> resultado = facturaDao.listar();
			
			assertNotNull(resultado, "La lista no debe ser nula");
			assertFalse(resultado.isEmpty(), "La lista no puede estar vacia");
			assertEquals(1, resultado.size(), "El tamaño de la lista debe ser 1");
			
			Factura f = resultado.get(0);
			assertEquals(1, f.getIdFactura());
			assertEquals(1500.50, f.getImporteTotal());
			assertEquals("Pagado", f.getEstado());
			assertEquals(10, f.getPacienteId());
			assertEquals(2, f.getObraSocialId());
		}
	}
	
	@Test
	@DisplayName("Test Buscar Por ID - Retorna objeto cuando existe el ID")
	void testBuscarPorIdExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true);
			when(resultSetMock.getInt("idFactura")).thenReturn(1);
			when(resultSetMock.getDouble("importeTotal")).thenReturn(1500.50);
			when(resultSetMock.getString("estado")).thenReturn("Pagado");
			
			Factura f = facturaDao.buscarPorId(1);
			
			assertNotNull(f, "El objeto devuelto no debe ser nulo");
			assertEquals(1, f.getIdFactura());
			assertEquals(1500.50, f.getImporteTotal());
			assertEquals("Pagado", f.getEstado());
			verify(callableStatementMock).setInt(1, 1);					
		}
	}
	
	@Test
	@DisplayName("Test Insertar Factura - Inserción exitosa retorna true")
	void testInsertarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			LocalDateTime fechaEjemplo = LocalDateTime.of(2026, 6, 6, 12, 0);
			Factura nuevo = new Factura(0, fechaEjemplo, 1500.50, "Emitida", 10, 2);
			boolean resultado = facturaDao.insertar(nuevo);
			
			assertTrue(resultado, "El método debe responder true al insertar");
			verify(callableStatementMock).setDate(eq(1), any(java.sql.Date.class));
			verify(callableStatementMock).setDouble(2, 1500.50);
			verify(callableStatementMock).setString(3, "Emitida");
			verify(callableStatementMock).setInt(4, 10);
			verify(callableStatementMock).setInt(5, 2);
		}
	}
	
	@Test
	@DisplayName("Test Actualizar Factura - Actualización exitosa retorna true")
	void testActualizarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			LocalDateTime fechaEjemplo = LocalDateTime.of(2026, 6, 6, 12, 0);
			Factura f = new Factura(1, fechaEjemplo, 1500.50, "Pagado", 10, 2);
			boolean resultado = facturaDao.actualizar(f);
			
			assertTrue(resultado, "El método debe responder true al actualizar");
			verify(callableStatementMock).setInt(1, 1);
			verify(callableStatementMock).setDate(eq(2), any(java.sql.Date.class)); // Cambiado a setDate
			verify(callableStatementMock).setDouble(3, 1500.50);
			verify(callableStatementMock).setString(4, "Pagado");
			verify(callableStatementMock).setInt(5, 10);
			verify(callableStatementMock).setInt(6, 2);
		}
	}
}