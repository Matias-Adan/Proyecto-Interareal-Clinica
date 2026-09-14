package com.clinica.modelo;

import java.time.LocalDate;

public class HistoriaClinica {
	private int idHistoria;
	private LocalDate fechaApertura;
	private String antecedentes;
	private String alergia;
	private String obvservacion;
	private int pacienteId;
	
	public HistoriaClinica(int idHistoria, LocalDate fechaApertura, String antecedentes, String alergia,
			String obvservacion, int pacienteId) {
		this.idHistoria = idHistoria;
		this.fechaApertura = fechaApertura;
		this.antecedentes = antecedentes;
		this.alergia = alergia;
		this.obvservacion = obvservacion;
		this.pacienteId = pacienteId;
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

	public String getAlergia() {
		return alergia;
	}

	public void setAlergia(String alergia) {
		this.alergia = alergia;
	}

	public String getObvservacion() {
		return obvservacion;
	}

	public void setObvservacion(String obvservacion) {
		this.obvservacion = obvservacion;
	}

	public int getPacienteId() {
		return pacienteId;
	}

	public void setPacienteId(int pacienteId) {
		this.pacienteId = pacienteId;
	}
	
	
}
