package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Receta;
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

public class RecetaDaoImplTest {
	
	private RecetaDaoImpl recetaDao;
	
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
		recetaDao = new RecetaDaoImpl();
		
		// Encadena comportamientos por defecto para simular JDBC
		when(conexionMock.getConexion()).thenReturn(connectionMock);
		when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
	}
	
	@Test
	@DisplayName("Test Listar Receta - Retorna lista con datos simulados")
	void testListar() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true, false);
			when(resultSetMock.getInt("idReceta")).thenReturn(1);
			when(resultSetMock.getString("fecha")).thenReturn("2026-06-06"); // O getObject/getDate según tu DAO
			when(resultSetMock.getString("indicacion")).thenReturn("Tomar cada 8 horas");
			when(resultSetMock.getInt("consultaId")).thenReturn(15);

			List<Receta> resultado = recetaDao.listar();
			
			assertNotNull(resultado, "La lista no debe ser nula");
			assertFalse(resultado.isEmpty(), "La lista no puede estar vacia");
			assertEquals(1, resultado.size(), "El tamaño de la lista debe ser 1");
			
			Receta r = resultado.get(0);
			assertEquals(1, r.getIdReceta());
			assertEquals("Tomar cada 8 horas", r.getIndicacion());
			assertEquals(15, r.getConsultaId());
		}
	}
	
	@Test
	@DisplayName("Test Buscar Por ID - Retorna objeto cuando existe el ID")
	void testBuscarPorIdExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
			when(resultSetMock.next()).thenReturn(true);
			when(resultSetMock.getInt("idReceta")).thenReturn(1);
			when(resultSetMock.getString("indicacion")).thenReturn("Tomar cada 8 horas");
			when(resultSetMock.getInt("consultaId")).thenReturn(15);
			
			Receta r = recetaDao.buscarPorId(1);
			
			assertNotNull(r, "El objeto devuelto no debe ser nulo");
			assertEquals(1, r.getIdReceta());
			assertEquals("Tomar cada 8 horas", r.getIndicacion());
			assertEquals(15, r.getConsultaId());
			verify(callableStatementMock).setInt(1, 1);					
		}
	}
	
	@Test
	@DisplayName("Test Insertar Receta - Inserción exitosa retorna true")
	void testInsertarExitoso() throws SQLException {
		try (MockedStatic<Conexion> conexionStatic = mockStatic(Conexion.class)) {
			conexionStatic.when(Conexion::getInstancia).thenReturn(conexionMock);
			
			when(callableStatementMock.executeUpdate()).thenReturn(1);
			
			LocalDate fechaEjemplo = LocalDate.of(2026, 6, 6);
			Receta nuevo = new Receta(0, fechaEjemplo, "Aplicar en zona afectada", 15);
			boolean resultado = recetaDao.insertar(nuevo);
			
			assertTrue(resultado, "El método debe responder true al insertar");
			verify(callableStatementMock).setDate(eq(1), any(java.sql.Date.class));
			verify(callableStatementMock).setString(2, "Aplicar en zona afectada");
			verify(callableStatementMock).setInt(3, 15);
		}
	}
}