package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.ObraSocial;
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

public class ObraSocialDaoImplTest {
	
	private ObraSocialDaoImpl obraSocialDao;
	
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
		obraSocialDao = new ObraSocialDaoImpl();
		
		// Encadena comportamientos por defecto para simular JDBC
		when(conexionMock.getConexion()).thenReturn(connectionMock);
		when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
	}
	
	@Test
	@DisplayName("Test Listar ObraSocial - Retorna lista con datos simulados")
	void testListar() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true, false);
			when(resultSetMock.getInt("idObraSocial")).thenReturn(1);
			when(resultSetMock.getString("nombre")).thenReturn("Swiss Medical");
			when(resultSetMock.getInt("numeroConvenio")).thenReturn(4589);
			when(resultSetMock.getString("beneficios")).thenReturn("Descuentos en farmacia");
			when(resultSetMock.getString("cobertura")).thenReturn("Plan Total");
			when(resultSetMock.getBoolean("flagBorrado")).thenReturn(false);

			List<ObraSocial> resultado = obraSocialDao.listar();
			
			assertNotNull(resultado, "La lista no debe ser nula");
			assertFalse(resultado.isEmpty(), "La lista no puede estar vacia");
			assertEquals(1, resultado.size(), "El tamaño de la lista debe ser 1");
			
			ObraSocial os = resultado.get(0);
			assertEquals(1, os.getIdObraSocial());
			assertEquals("Swiss Medical", os.getNombre());
			assertEquals(4589, os.getNumeroConvenio());
			assertEquals("Descuentos en farmacia", os.getBeneficios());
			assertEquals("Plan Total", os.getCobertura());
			assertFalse(os.isFlagBorrado());
		}
	}
	
	@Test
	@DisplayName("Test Buscar Por ID - Retorna objeto cuando existe el ID")
	void testBuscarPorIdExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true);
			when(resultSetMock.getInt("idObraSocial")).thenReturn(1);
			when(resultSetMock.getString("nombre")).thenReturn("Swiss Medical");
			when(resultSetMock.getInt("numeroConvenio")).thenReturn(4589);
			
			ObraSocial os = obraSocialDao.buscarPorId(1);
			
			assertNotNull(os, "El objeto devuelto no debe ser nulo");
			assertEquals(1, os.getIdObraSocial());
			assertEquals("Swiss Medical", os.getNombre());
			assertEquals(4589, os.getNumeroConvenio());
			verify(callableStatementMock).setInt(1, 1);					
		}
	}
	
	@Test
	@DisplayName("Test Insertar ObraSocial - Inserción exitosa retorna true")
	void testInsertarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			ObraSocial nuevo = new ObraSocial(0, "OSDE", 1234, "Cobertura nacional", "Plan 210", false);
			boolean resultado = obraSocialDao.insertar(nuevo);
			
			assertTrue(resultado, "El método debe responder true al insertar");
			verify(callableStatementMock).setString(1, "OSDE");
			verify(callableStatementMock).setInt(2, 1234);
			verify(callableStatementMock).setString(3, "Cobertura nacional");
			verify(callableStatementMock).setString(4, "Plan 210");
			verify(callableStatementMock).setBoolean(5, false);
		}
	}
	
	@Test
	@DisplayName("Test Actualizar ObraSocial - Actualización exitosa retorna true")
	void testActualizarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			ObraSocial os = new ObraSocial(1, "OSDE Modificado", 1234, "Cobertura total", "Plan 310", false);
			boolean resultado = obraSocialDao.actualizar(os);
			
			assertTrue(resultado, "El método debe responder true al actualizar");
			verify(callableStatementMock).setInt(1, 1);
			verify(callableStatementMock).setString(2, "OSDE Modificado");
			verify(callableStatementMock).setInt(3, 1234);
			verify(callableStatementMock).setString(4, "Cobertura total");
			verify(callableStatementMock).setString(5, "Plan 310");
			verify(callableStatementMock).setBoolean(6, false);
		}
	}
}