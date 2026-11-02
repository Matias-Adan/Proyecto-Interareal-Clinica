package com.clinica.modelo;

import java.time.LocalDate;

public class HistoriaClinica {
	private int idHistoria;
	private LocalDate fechaApertura;
	private String antecedentes;
	private String observacion;
	private int pacienteId;
	private boolean flagBorrado;
	
	public HistoriaClinica() {
			
	}
	
	public HistoriaClinica(int idHistoria, LocalDate fechaApertura, String antecedentes,
	 String obvseracion, int pacienteId, boolean flagBorrado) {
		this.idHistoria = idHistoria;
		this.fechaApertura = fechaApertura;
		this.antecedentes = antecedentes;
		this.observacion = obvseracion;
		this.pacienteId = pacienteId;
		this.flagBorrado = flagBorrado;
	}

	public int getIdHistoria() {
		return idHistoria;
	}

	public void setIdHistoria(int idHistoria) {
		this.idHistoria = idHistoria;
	}

	public LocalDate getFechaApertura() {
		return fechaApertura;
	}

	public void setFechaApertura(LocalDate fechaApertura) {
		this.fechaApertura = fechaApertura;
	}

	public String getAntecedentes() {
		return antecedentes;
	}

	public void setAntecedentes(String antecedentes) {
		this.antecedentes = antecedentes;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObvservacion(String observacion) {
		this.observacion = observacion;
	}

	public int getPacienteId() {
		return pacienteId;
	}

	public void setPacienteId(int pacienteId) {
		this.pacienteId = pacienteId;
	}

	public boolean getFlagBorrado() {
		return flagBorrado;
	}

	public void setFlagBorrado(boolean flagBorrado) {
		this.flagBorrado = flagBorrado;
	}
	
	
	
}
