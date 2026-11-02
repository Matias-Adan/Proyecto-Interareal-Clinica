package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.DetalleReceta;
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

public class DetalleRecetaDaoImplTest {
	
	private DetalleRecetaDaoImpl detalleRecetaDao;
	
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
		detalleRecetaDao = new DetalleRecetaDaoImpl();
		
		// Encadena comportamientos por defecto para simular JDBC
		when(conexionMock.getConexion()).thenReturn(connectionMock);
		when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
	}
	
	@Test
	@DisplayName("Test Listar DetalleReceta - Retorna lista con datos simulados")
	void testListar() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true, false);
			when(resultSetMock.getInt("idDetalle")).thenReturn(1);
			when(resultSetMock.getInt("cantidad")).thenReturn(2);
			when(resultSetMock.getInt("dosis")).thenReturn(1); 
			when(resultSetMock.getString("frecuencia")).thenReturn("Cada 8 horas");
			when(resultSetMock.getString("duracion")).thenReturn("5 días");
			when(resultSetMock.getInt("recetaId")).thenReturn(10);
			when(resultSetMock.getInt("medicamentoId")).thenReturn(4);

			List<DetalleReceta> resultado = detalleRecetaDao.listar();
			
			assertNotNull(resultado, "La lista no debe ser nula");
			assertFalse(resultado.isEmpty(), "La lista no puede estar vacia");
			assertEquals(1, resultado.size(), "El tamaño de la lista debe ser 1");
			
			DetalleReceta dr = resultado.get(0);
			assertEquals(1, dr.getIdReceta());
			assertEquals(2, dr.getCantidad());
			assertEquals(1, dr.getDosis());
			assertEquals("Cada 8 horas", dr.getFrecuencia());
			assertEquals("5 días", dr.getDuracion());
			assertEquals(10, dr.getRecetaId());
			assertEquals(4, dr.getArticuloId());
		}
	}
	
	@Test
	@DisplayName("Test Buscar Por ID - Retorna objeto cuando existe el ID")
	void testBuscarPorIdExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true);
			when(resultSetMock.getInt("idDetalle")).thenReturn(1);
			when(resultSetMock.getInt("cantidad")).thenReturn(2);
			when(resultSetMock.getInt("dosis")).thenReturn(1);
			when(resultSetMock.getString("frecuencia")).thenReturn("Cada 8 horas");
			when(resultSetMock.getString("duracion")).thenReturn("5 días");
			when(resultSetMock.getInt("recetaId")).thenReturn(10);
			when(resultSetMock.getInt("medicamentoId")).thenReturn(4);
			
			DetalleReceta dr = detalleRecetaDao.buscarPorId(1);
			
			assertNotNull(dr, "El objeto devuelto no debe ser nulo");
			assertEquals(1, dr.getIdReceta());
			assertEquals(1, dr.getDosis());
			assertEquals(4, dr.getArticuloId());
			verify(callableStatementMock).setInt(1, 1);					
		}
	}
	
	@Test
	@DisplayName("Test Insertar DetalleReceta - Inserción exitosa retorna true")
	void testInsertarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			DetalleReceta nuevo = new DetalleReceta(0, 2, "1", "Cada 8 horas", "5 días", 10, 4);
			boolean resultado = detalleRecetaDao.insertar(nuevo);
			
			assertTrue(resultado, "El método debe responder true al insertar");
			verify(callableStatementMock).setInt(1, 2);
			verify(callableStatementMock).setInt(2, 1);
			verify(callableStatementMock).setString(3, "Cada 8 horas");
			verify(callableStatementMock).setString(4, "5 días");
			verify(callableStatementMock).setInt(5, 10);
			verify(callableStatementMock).setInt(6, 4);
		}
	}
}