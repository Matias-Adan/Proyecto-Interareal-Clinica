package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.Medicamento;

public interface IMedicamentoDao {
	boolean insertar(Medicamento m);
	List<Medicamento> listar();
	boolean actualizar(Medicamento m);
	boolean eliminar(int id);
}