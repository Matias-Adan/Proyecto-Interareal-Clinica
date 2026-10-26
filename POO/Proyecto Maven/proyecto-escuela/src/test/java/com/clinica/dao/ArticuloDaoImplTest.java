package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Articulo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

public class ArticuloDaoImplTest {
	
	private ArticuloDaoImpl ArticuloDao;
	
	@Mock
	private Conexion conexionMock;
	
	@Mock
	private Connection connectionMock;
	
	@Mock
	private CallableStatement callableStatementMock;
	
	@Mock
	private ResultSet resultSetMock;
	
	@BeforeEach
	void setUp() throws SQLException{
		//inicializa las anotaciones de Mockito antes de cada prueba 
		MockitoAnnotations.openMocks(this);
		ArticuloDao = new ArticuloDaoImpl();
		
		//Encadena comportamientos por defecto para simular JDBC
		when(conexionMock.getConexion()).thenReturn(connectionMock);
		when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
	}
	
	@Test
	@DisplayName("Test Listar Medicamentos - Retorna lista con datos simulados")
	void testListar() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true, false);
			when(resultSetMock.getInt("idArticulo")).thenReturn(1);
			when(resultSetMock.getString("nombre")).thenReturn("Ibuprofeno");
			when(resultSetMock.getString("descripcion")).thenReturn("Analgesico 600mg");
			when(resultSetMock.getString("presentacion")).thenReturn("Caja x10");
			when(resultSetMock.getString("tipoArticulo")).thenReturn("Medicamento");
			when(resultSetMock.getInt("stockActual")).thenReturn(50);
			when(resultSetMock.getInt("stockMinimo")).thenReturn(10);
			when(resultSetMock.getDouble("temperaturaIdealEnCelcius")).thenReturn(25.0);
				

			List<Articulo> resultado = ArticuloDao.listar();
			
			assertNotNull(resultado, "La lista no debe ser nula");
			assertFalse(resultado.isEmpty(), "La lista no puede estar vacia");
			assertEquals(1, resultado.size(), "El tamaño de la lista debe ser 1");
			
			Articulo articulo = resultado.get(0);
			assertEquals(1, articulo.getIdArticulo());
			assertEquals("Ibuprofeno", articulo.getNombre());
			assertEquals("Analgesico 600mg", articulo.getDescripcion());
			assertEquals("Caja x10", articulo.getPresentacion());
			assertEquals("Medicamento", articulo.getTipoArticulo());
			assertEquals(50, articulo.getStockActual());
			assertEquals(10, articulo.getStockMinimo());
			assertEquals(25.0, articulo.getTemperaturaIdealEnCelcius());
		}
	}
	
	@Test
	@DisplayName("Test Buscar Por ID - Retorna objeto cuando existe el ID")
	void testBuscarPorIdExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true);
			when(resultSetMock.getInt("idArticulo")).thenReturn(5);
			when(resultSetMock.getString("nombre")).thenReturn("Paracetamol");
			
			Articulo m = ArticuloDao.buscarPorId(5);
			
			assertNotNull(m, "El objeto devuelto no debe ser nulo");
			assertEquals(5, m.getIdArticulo());
			assertEquals("Paracetamol", m.getNombre());
			verify(callableStatementMock).setInt(1, 5);					
		}
	}
	
	@Test
	@DisplayName("Test Insertar Medicamento - Inserción exitosa retorna true")
	void testInsertarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			Articulo nuevo = new Articulo(0, "Amoxicilina", "Antibiótico", "Caja x10", "Medicamento",  100, 20, 25.0);
			boolean resultado = ArticuloDao.insertar(nuevo);
			
			assertTrue(resultado, "El método debe responder true al insertar");
			verify(callableStatementMock).setString(1, "Amoxicilina");
			verify(callableStatementMock).setString(2, "Antibiótico");
			verify(callableStatementMock).setString(3, "Caja x10");
			verify(callableStatementMock).setString(4, "Medicamento");
			verify(callableStatementMock).setInt(5, 100);
			verify(callableStatementMock).setInt(6, 20);
			verify(callableStatementMock).setDouble(7, 25.0);
		}
	}
	
	@Test
	@DisplayName("Test Eliminar Medicamento - Retorna true al eliminar correctamente")
	void testEliminarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			boolean resultado = ArticuloDao.eliminar(10);
			
			assertTrue(resultado, "Debe ser true tras eliminar");
			verify(callableStatementMock).setInt(1, 10);
		}
	}
}