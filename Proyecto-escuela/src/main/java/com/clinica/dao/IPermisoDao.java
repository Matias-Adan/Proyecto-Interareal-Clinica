package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.Permiso;

public interface IPermisoDao {
	boolean insertar(Permiso p);
	List<Permiso> listar();
	Permiso buscarPorId(int id);
	boolean actualizar(Permiso p);

}