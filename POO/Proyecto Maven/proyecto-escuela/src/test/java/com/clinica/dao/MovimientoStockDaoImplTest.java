package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.MovimientoStock;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MovimientoStockDaoImplTest {
	
	private MovimientoStockDaoImpl movimientoStockDao;
	
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
		movimientoStockDao = new MovimientoStockDaoImpl();
		
		// Encadena comportamientos por defecto para simular JDBC
		when(conexionMock.getConexion()).thenReturn(connectionMock);
		when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
	}
	
	@Test
	@DisplayName("Test Listar MovimientoStock - Retorna lista con datos simulados")
	void testListar() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true, false);
			when(resultSetMock.getInt("movimientoStock")).thenReturn(1);
			when(resultSetMock.getInt("idArticulo")).thenReturn(3);
			when(resultSetMock.getString("tipoMovimiento")).thenReturn("ENTRADA");
			when(resultSetMock.getInt("cantidad")).thenReturn(50);
			when(resultSetMock.getString("motivo")).thenReturn("Compra de proveedor");
			when(resultSetMock.getString("fechaHora")).thenReturn("2026-06-06 10:00:00"); // O getTimestamp según tu DAO
			when(resultSetMock.getInt("usuarioId")).thenReturn(2);

			List<MovimientoStock> resultado = movimientoStockDao.listar();
			
			assertNotNull(resultado, "La lista no debe ser nula");
			assertFalse(resultado.isEmpty(), "La lista no puede estar vacia");
			assertEquals(1, resultado.size(), "El tamaño de la lista debe ser 1");
			
			MovimientoStock ms = resultado.get(0);
			assertEquals(1, ms.getMovimientoStock());
			assertEquals(3, ms.getIdArticulo());
			assertEquals("ENTRADA", ms.getTipoMovimiento());
			assertEquals(50, ms.getCantidad());
			assertEquals("Compra de proveedor", ms.getMotivo());
			assertEquals(2, ms.getUsuarioId());
		}
	}
	
	@Test
	@DisplayName("Test Buscar Por ID - Retorna objeto cuando existe el ID")
	void testBuscarPorIdExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true);
			when(resultSetMock.getInt("movimientoStock")).thenReturn(1);
			when(resultSetMock.getInt("idArticulo")).thenReturn(3);
			when(resultSetMock.getString("tipoMovimiento")).thenReturn("ENTRADA");
			
			MovimientoStock ms = movimientoStockDao.buscarPorId(1);
			
			assertNotNull(ms, "El objeto devuelto no debe ser nulo");
			assertEquals(1, ms.getMovimientoStock());
			assertEquals(3, ms.getIdArticulo());
			assertEquals("ENTRADA", ms.getTipoMovimiento());
			verify(callableStatementMock).setInt(1, 1);					
		}
	}
	
	@Test
	@DisplayName("Test Insertar MovimientoStock - Inserción exitosa retorna true")
	void testInsertarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			LocalDateTime fechaHoraEjemplo = LocalDateTime.of(2026, 6, 6, 10, 0);
			MovimientoStock nuevo = new MovimientoStock(0, 3, "ENTRADA", 50, "Compra", fechaHoraEjemplo, 2);
			boolean resultado = movimientoStockDao.insertar(nuevo);
			
			assertTrue(resultado, "El método debe responder true al insertar");
			verify(callableStatementMock).setInt(1, 3);
			verify(callableStatementMock).setString(2, "ENTRADA");
			verify(callableStatementMock).setInt(3, 50);
			verify(callableStatementMock).setString(4, "Compra");
			verify(callableStatementMock).setTimestamp(eq(5), any(Timestamp.class));
			verify(callableStatementMock).setInt(6, 2);
		}
	}
}