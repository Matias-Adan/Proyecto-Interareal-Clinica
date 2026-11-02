package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.PermisoRol;

public interface IPermisoRolDao {
	boolean insertar(PermisoRol p);
	List<PermisoRol> listar();
	PermisoRol buscarPorId(int id);
	boolean eliminar(int id);
}