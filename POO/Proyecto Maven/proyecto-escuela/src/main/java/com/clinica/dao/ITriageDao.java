package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.Triage;

public interface ITriageDao {
	boolean insertar(Triage t);
	List<Triage> listar();
	boolean actualizar(Triage t);
	boolean eliminar(int id);
}
