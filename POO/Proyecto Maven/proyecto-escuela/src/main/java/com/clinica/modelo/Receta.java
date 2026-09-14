package com.clinica.modelo;

import java.time.LocalDate;

public class Receta {
	private int idReceta;
	private LocalDate fecha;
	private String indicacion;
	private int consultaId;
	
	public Receta(int idReceta, LocalDate fecha, String indicacion, int consultaId) {
		this.idReceta = idReceta;
		this.fecha = fecha;
		this.indicacion = indicacion;
		this.consultaId = consultaId;
	}

	public int getIdReceta() {
		return idReceta;
	}

	public void setIdReceta(int idReceta) {
		this.idReceta = idReceta;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public String getIndicacion() {
		return indicacion;
	}

	public void setIndicacion(String indicacion) {
		this.indicacion = indicacion;
	}

	public int getConsultaId() {
		return consultaId;
	}

	public void setConsultaId(int consultaId) {
		this.consultaId = consultaId;
	}
	
	
}
