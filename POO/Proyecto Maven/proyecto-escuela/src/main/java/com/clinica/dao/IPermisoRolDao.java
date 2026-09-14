package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.PermisoRol;

public interface IPermisoRolDao {
	boolean insertar(PermisoRol p);
	List<PermisoRol> listar();
	boolean actualizar(PermisoRol p);
	boolean eliminar(int id);
}