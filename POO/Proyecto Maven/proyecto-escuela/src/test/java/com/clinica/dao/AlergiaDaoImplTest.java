package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Alergia;
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

public class AlergiaDaoImplTest {
	
	private AlergiaDaoImpl alergiaDao;
	
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
		// Inicializa las anotaciones de Mockito antes de cada prueba 
		MockitoAnnotations.openMocks(this);
		alergiaDao = new AlergiaDaoImpl();
		
		// Encadena comportamientos por defecto para simular JDBC
		when(conexionMock.getConexion()).thenReturn(connectionMock);
		when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
	}
	
	@Test
	@DisplayName("Test Listar Alergias - Retorna lista con datos simulados")
	void testListar() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true, false);
			when(resultSetMock.getInt("idAlergia")).thenReturn(1);
			when(resultSetMock.getString("nombre")).thenReturn("Penicilina");
			when(resultSetMock.getString("descripcion")).thenReturn("Urticaria y sarpullido");

			List<Alergia> resultado = alergiaDao.listar();
			
			assertNotNull(resultado, "La lista no debe ser nula");
			assertFalse(resultado.isEmpty(), "La lista no puede estar vacia");
			assertEquals(1, resultado.size(), "El tamaño de la lista debe ser 1");
			
			Alergia alergia = resultado.get(0);
			assertEquals(1, alergia.getIdAlergia());
			assertEquals("Penicilina", alergia.getNombre());
			assertEquals("Urticaria y sarpullido", alergia.getDescripcion());
		}
	}
	
	@Test
	@DisplayName("Test Buscar Por ID - Retorna objeto cuando existe el ID")
	void testBuscarPorIdExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true);
			when(resultSetMock.getInt("idAlergia")).thenReturn(5);
			when(resultSetMock.getString("nombre")).thenReturn("Polen");
			when(resultSetMock.getString("descripcion")).thenReturn("Alergia estacional");
			
			Alergia a = alergiaDao.buscarPorId(5);
			
			assertNotNull(a, "El objeto devuelto no debe ser nulo");
			assertEquals(5, a.getIdAlergia());
			assertEquals("Polen", a.getNombre());
			assertEquals("Alergia estacional", a.getDescripcion());
			verify(callableStatementMock).setInt(1, 5);					
		}
	}
	
	@Test
	@DisplayName("Test Insertar Alergia - Inserción exitosa retorna true")
	void testInsertarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			Alergia nuevo = new Alergia(0, "Polen", "Alergia estacional");
			boolean resultado = alergiaDao.insertar(nuevo);
			
			assertTrue(resultado, "El método debe responder true al insertar");
			verify(callableStatementMock).setString(1, "Polen");
			verify(callableStatementMock).setString(2, "Alergia estacional");
		}
	}
	
	@Test
	@DisplayName("Test Actualizar Alergia - Actualización exitosa retorna true")
	void testActualizarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			Alergia alergia = new Alergia(1, "Polen", "Alergia estacional actualizada");
			boolean resultado = alergiaDao.actualizar(alergia);
			
			assertTrue(resultado, "El método debe responder true al actualizar");
			verify(callableStatementMock).setInt(1, 1);
			verify(callableStatementMock).setString(2, "Polen");
			verify(callableStatementMock).setString(3, "Alergia estacional actualizada");
		}
	}
}