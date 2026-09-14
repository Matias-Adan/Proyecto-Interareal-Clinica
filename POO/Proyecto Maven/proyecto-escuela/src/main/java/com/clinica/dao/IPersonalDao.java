package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.Personal;

public interface IPersonalDao {
	boolean insertar(Personal p);
	List<Personal> listar();
	boolean actualizar(Personal p);
	boolean eliminar(int id);
}
