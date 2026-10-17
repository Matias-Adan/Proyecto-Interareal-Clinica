package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.Turno;

public interface ITurnoDao {
	boolean insertar(Turno t);
	List<Turno> listar();
	Turno buscarPorId(int id);
	boolean actualizar(Turno t);
	boolean eliminar(int id);
}
