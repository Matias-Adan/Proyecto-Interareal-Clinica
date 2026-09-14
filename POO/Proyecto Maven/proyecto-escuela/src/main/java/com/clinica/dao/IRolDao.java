package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.Rol;

public interface IRolDao {
	boolean insertar(Rol r);
	List<Rol> listar();
	boolean actualizar(Rol r);
	boolean eliminar(int id);
}
