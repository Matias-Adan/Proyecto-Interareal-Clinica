package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.Permiso;

public interface IPermisoDao {
	boolean insertar(Permiso p);
	List<Permiso> listar();
	boolean actualizar(Permiso p);
	boolean eliminar(int id);
}