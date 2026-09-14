package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.Guardia;

public interface IGuardiaDao {
	boolean insertar(Guardia g);
	List<Guardia> listar();
	boolean actualizar(Guardia g);
	boolean eliminar(int id);
}