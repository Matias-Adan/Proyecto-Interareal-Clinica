package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.Triage;

public interface ITriageDao {
	boolean insertar(Triage t);
	List<Triage> listar();
	Triage buscarPorId(int id);
	boolean actualizar(Triage t);
}
