package com.clinica.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Turno {
	private int idTurno;
	private LocalDate fecha;
	private LocalTime hora;
	private String estado;
	private int pacienteId;
	private int personalId;
	
	public Turno(int idTurno, LocalDate fecha, LocalTime hora, String estado, int pacienteId, int personalId) {
		this.idTurno = idTurno;
		this.fecha = fecha;
		this.hora = hora;
		this.estado = estado;
		this.pacienteId = pacienteId;
		this.personalId = personalId;
	}

	public int getIdTurno() {
		return idTurno;
	}

	public void setIdTurno(int idTurno) {
		this.idTurno = idTurno;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public LocalTime getHora() {
		return hora;
	}

	public void setHora(LocalTime hora) {
		this.hora = hora;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public int getPacienteId() {
		return pacienteId;
	}

	public void setPacienteId(int pacienteId) {
		this.pacienteId = pacienteId;
	}

	public int getPersonalId() {
		return personalId;
	}

	public void setPersonalId(int personalId) {
		this.personalId = personalId;
	}

	
	
}
