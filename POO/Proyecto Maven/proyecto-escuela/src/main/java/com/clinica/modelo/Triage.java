package com.clinica.modelo;

import java.time.LocalDate;

public class Triage {
	private int idTriage;
	private LocalDate fecha;
	private String prioridad;
	private String estado;
	private String observacion;
	private int pacienteId;
	private int guardiaId;
	
	public Triage(int idTriage, LocalDate fecha, String prioridad, String estado, String observacion, int pacienteId,
			int guardiaId) {
		this.idTriage = idTriage;
		this.fecha = fecha;
		this.prioridad = prioridad;
		this.estado = estado;
		this.observacion = observacion;
		this.pacienteId = pacienteId;
		this.guardiaId = guardiaId;
	}

	public int getIdTriage() {
		return idTriage;
	}

	public void setIdTriage(int idTriage) {
		this.idTriage = idTriage;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public String getPrioridad() {
		return prioridad;
	}

	public void setPrioridad(String prioridad) {
		this.prioridad = prioridad;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public int getPacienteId() {
		return pacienteId;
	}

	public void setPacienteId(int pacienteId) {
		this.pacienteId = pacienteId;
	}

	public int getGuardiaId() {
		return guardiaId;
	}

	public void setGuardiaId(int guardiaId) {
		this.guardiaId = guardiaId;
	}
	
	
}
