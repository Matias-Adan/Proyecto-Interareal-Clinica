package com.clinica.modelo;

import java.time.LocalDate;

public class UbicacionPaciente {
	private int idUbicacion;
	private String tipo;
	private LocalDate fechaIngreso;
	private LocalDate fechaSalida;
	private int pacienteId;
	private int habitacionId;
	
	public UbicacionPaciente(int idUbicacion, String tipo, LocalDate fechaIngreso, LocalDate fechaSalida,
			int pacienteId, int habitacionId) {
		this.idUbicacion = idUbicacion;
		this.tipo = tipo;
		this.fechaIngreso = fechaIngreso;
		this.fechaSalida = fechaSalida;
		this.pacienteId = pacienteId;
		this.habitacionId = habitacionId;
	}

	public int getIdUbicacion() {
		return idUbicacion;
	}

	public void setIdUbicacion(int idUbicacion) {
		this.idUbicacion = idUbicacion;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public LocalDate getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(LocalDate fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public LocalDate getFechaSalida() {
		return fechaSalida;
	}

	public void setFechaSalida(LocalDate fechaSalida) {
		this.fechaSalida = fechaSalida;
	}

	public int getPacienteId() {
		return pacienteId;
	}

	public void setPacienteId(int pacienteId) {
		this.pacienteId = pacienteId;
	}

	public int getHabitacionId() {
		return habitacionId;
	}

	public void setHabitacionId(int habitacionId) {
		this.habitacionId = habitacionId;
	}
	
	
}
