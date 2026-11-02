package com.clinica.dao;

import java.time.LocalDateTime;
import java.util.List;

import com.clinica.modelo.UbicacionPaciente;

public interface IUbicacionPacienteDao {
	boolean insertar(UbicacionPaciente u);
	List<UbicacionPaciente> listar();
	UbicacionPaciente buscarPorId(int id);
	boolean actualizar(UbicacionPaciente u);
	boolean darAlta(int id, LocalDateTime fechaSalida);
}
