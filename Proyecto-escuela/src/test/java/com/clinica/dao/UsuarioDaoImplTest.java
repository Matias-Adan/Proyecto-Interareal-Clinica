package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Usuario;
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

public class UsuarioDaoImplTest {
	
	private UsuarioDaoImpl usuarioDao;
	
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
		usuarioDao = new UsuarioDaoImpl();
		
		// Encadena comportamientos por defecto para simular JDBC
		when(conexionMock.getConexion()).thenReturn(connectionMock);
		when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
	}
	
	@Test
	@DisplayName("Test Listar Usuario - Retorna lista con datos simulados")
	void testListar() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true, false);
			when(resultSetMock.getInt("idUsuario")).thenReturn(1);
			when(resultSetMock.getString("nombre")).thenReturn("Juan");
			when(resultSetMock.getString("apellido")).thenReturn("Pérez");
			when(resultSetMock.getInt("dni")).thenReturn(12345678);
			when(resultSetMock.getString("fechaNacimiento")).thenReturn("1990-01-01");
			when(resultSetMock.getInt("telefono")).thenReturn(11223344);
			when(resultSetMock.getString("email")).thenReturn("juan.perez@mail.com");
			when(resultSetMock.getString("password")).thenReturn("123456");
			when(resultSetMock.getInt("obraSocialId")).thenReturn(2);
			when(resultSetMock.getBoolean("flagBorrado")).thenReturn(false);

			List<Usuario> resultado = usuarioDao.listar();
			
			assertNotNull(resultado, "La lista no debe ser nula");
			assertFalse(resultado.isEmpty(), "La lista no puede estar vacia");
			assertEquals(1, resultado.size(), "El tamaño de la lista debe ser 1");
			
			Usuario u = resultado.get(0);
			assertEquals(1, u.getIdUsuario());
			assertEquals("Juan", u.getNombre());
			assertEquals("Pérez", u.getApellido());
			assertEquals(12345678, u.getDni());
			assertEquals("juan.perez@mail.com", u.getEmail());
			assertFalse(u.isFlagBorrado());
		}
	}
	
	@Test
	@DisplayName("Test Buscar Por ID - Retorna objeto cuando existe el ID")
	void testBuscarPorIdExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true);
			when(resultSetMock.getInt("idUsuario")).thenReturn(1);
			when(resultSetMock.getString("nombre")).thenReturn("Juan");
			when(resultSetMock.getInt("dni")).thenReturn(12345678);
			
			Usuario u = usuarioDao.buscarPorId(1);
			
			assertNotNull(u, "El objeto devuelto no debe ser nulo");
			assertEquals(1, u.getIdUsuario());
			assertEquals("Juan", u.getNombre());
			verify(callableStatementMock).setInt(1, 1);					
		}
	}
	
	@Test
	@DisplayName("Test Insertar Usuario - Inserción exitosa retorna true")
	void testInsertarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			Usuario nuevo = new Usuario(0, "María", "Gómez", 87654321, LocalDate.of(1995, 5, 10), 99887766, "maria.gomez@mail.com", "abcdef", 1, false);
			
			boolean resultado = usuarioDao.insertar(nuevo);
			
			assertTrue(resultado, "El método debe responder true al insertar");
			verify(callableStatementMock).setString(1, "María");
			verify(callableStatementMock).setString(2, "Gómez");
			verify(callableStatementMock).setInt(3, 87654321);
		}
	}
	
	@Test
	@DisplayName("Test Actualizar Usuario - Actualización exitosa retorna true")
	void testActualizarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			Usuario u = new Usuario(1, "María Actualizada", "Gómez", 87654321, LocalDate.of(1995, 5, 10), 99887766, "maria.gomez@mail.com", "abcdef", 1, false);
			
			boolean resultado = usuarioDao.actualizar(u);
			
			assertTrue(resultado, "El método debe responder true al actualizar");
			verify(callableStatementMock).setInt(1, 1);
			verify(callableStatementMock).setString(2, "María Actualizada");
		}
	}
}