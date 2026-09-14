package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.ObraSocial;

public interface IObraSocialDao {
	boolean insertar(ObraSocial o);
	List<ObraSocial> listar();
	boolean actualizar(ObraSocial o);
	boolean eliminar(int id);
}