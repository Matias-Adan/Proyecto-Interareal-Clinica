package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Permiso;
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

public class PermisoDaoImplTest {
	
	private PermisoDaoImpl permisoDao;
	
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
		permisoDao = new PermisoDaoImpl();
		
		// Encadena comportamientos por defecto para simular JDBC
		when(conexionMock.getConexion()).thenReturn(connectionMock);
		when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
	}
	
	@Test
	@DisplayName("Test Listar Permiso - Retorna lista con datos simulados")
	void testListar() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true, false);
			when(resultSetMock.getInt("idPermiso")).thenReturn(1);
			when(resultSetMock.getString("nombre")).thenReturn("GESTIONAR_USUARIOS");
			when(resultSetMock.getString("descripcion")).thenReturn("Permite crear, editar y eliminar usuarios");

			List<Permiso> resultado = permisoDao.listar();
			
			assertNotNull(resultado, "La lista no debe ser nula");
			assertFalse(resultado.isEmpty(), "La lista no puede estar vacia");
			assertEquals(1, resultado.size(), "El tamaño de la lista debe ser 1");
			
			Permiso p = resultado.get(0);
			assertEquals(1, p.getIdPermiso());
			assertEquals("GESTIONAR_USUARIOS", p.getNombre());
			assertEquals("Permite crear, editar y eliminar usuarios", p.getDescripcion());
		}
	}
	
	@Test
	@DisplayName("Test Buscar Por ID - Retorna objeto cuando existe el ID")
	void testBuscarPorIdExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true);
			when(resultSetMock.getInt("idPermiso")).thenReturn(1);
			when(resultSetMock.getString("nombre")).thenReturn("GESTIONAR_USUARIOS");
			when(resultSetMock.getString("descripcion")).thenReturn("Permite crear, editar y eliminar usuarios");
			
			Permiso p = permisoDao.buscarPorId(1);
			
			assertNotNull(p, "El objeto devuelto no debe ser nulo");
			assertEquals(1, p.getIdPermiso());
			assertEquals("GESTIONAR_USUARIOS", p.getNombre());
			verify(callableStatementMock).setInt(1, 1);					
		}
	}
	
	@Test
	@DisplayName("Test Insertar Permiso - Inserción exitosa retorna true")
	void testInsertarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			Permiso nuevo = new Permiso(0, "VER_REPORTES", "Permite visualizar reportes estadísticos");
			boolean resultado = permisoDao.insertar(nuevo);
			
			assertTrue(resultado, "El método debe responder true al insertar");
			verify(callableStatementMock).setString(1, "VER_REPORTES");
			verify(callableStatementMock).setString(2, "Permite visualizar reportes estadísticos");
		}
	}
	
	@Test
	@DisplayName("Test Actualizar Permiso - Actualización exitosa retorna true")
	void testActualizarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			Permiso p = new Permiso(1, "VER_REPORTES_MOD", "Permite visualizar y exportar reportes");
			boolean resultado = permisoDao.actualizar(p);
			
			assertTrue(resultado, "El método debe responder true al actualizar");
			verify(callableStatementMock).setInt(1, 1);
			verify(callableStatementMock).setString(2, "VER_REPORTES_MOD");
			verify(callableStatementMock).setString(3, "Permite visualizar y exportar reportes");
		}
	}
}