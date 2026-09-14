package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.HistoriaClinica;

public interface IHistoriaClinicaDao {
	boolean insertar(HistoriaClinica h);
	List<HistoriaClinica> listar();
	boolean actualizar(HistoriaClinica h);
	boolean eliminar(int id);
}
