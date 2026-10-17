package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.ObraSocial;

public interface IObraSocialDao {
	boolean insertar(ObraSocial o);
	List<ObraSocial> listar();
	ObraSocial buscarPorId(int id);
	boolean actualizar(ObraSocial o);
	boolean eliminar(int id);
}