package com.clinica.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Guardia {
	private int idGuardia;
	private LocalDate fecha;
	private LocalTime horaInicio;
	private LocalTime horaFin;
	private String estado;
	
	public Guardia(int idGuardia, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, String estado) {
		this.idGuardia = idGuardia;
		this.fecha = fecha;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
		this.estado = estado;
	}

	public int getIdGuardia() {
		return idGuardia;
	}

	public void setIdGuardia(int idGuardia) {
		this.idGuardia = idGuardia;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public LocalTime getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(LocalTime horaInicio) {
		this.horaInicio = horaInicio;
	}

	public LocalTime getHoraFin() {
		return horaFin;
	}

	public void setHoraFin(LocalTime horaFin) {
		this.horaFin = horaFin;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	
}
