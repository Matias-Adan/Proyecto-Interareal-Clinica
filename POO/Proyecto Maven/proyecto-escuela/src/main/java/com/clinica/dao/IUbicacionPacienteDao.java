package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.UbicacionPaciente;

public interface IUbicacionPacienteDao {
	boolean insertar(UbicacionPaciente u);
	List<UbicacionPaciente> listar();
	boolean actualizar(UbicacionPaciente u);
	boolean eliminar(int id);
}
