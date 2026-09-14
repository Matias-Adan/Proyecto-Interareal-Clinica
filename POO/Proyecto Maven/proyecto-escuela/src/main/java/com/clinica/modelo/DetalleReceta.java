package com.clinica.modelo;

public class DetalleReceta {
	private int idDetalle;
	private int cantidad;
	private int dosis;
	private String frecuencia;
	private String duracion;
	private int recetaId;
	private int medicamentoId;
	
	public DetalleReceta(int idReceta, int cantidad, int dosis, String frecuencia, String duracion, int recetaId,
			int medicamentoId) {
		this.idDetalle = idReceta;
		this.cantidad = cantidad;
		this.dosis = dosis;
		this.frecuencia = frecuencia;
		this.duracion = duracion;
		this.recetaId = recetaId;
		this.medicamentoId = medicamentoId;
	}

	public int getIdReceta() {
		return idDetalle;
	}

	public void setIdReceta(int idReceta) {
		this.idDetalle = idReceta;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public int getDosis() {
		return dosis;
	}

	public void setDosis(int dosis) {
		this.dosis = dosis;
	}

	public String getFrecuencia() {
		return frecuencia;
	}

	public void setFrecuencia(String frecuencia) {
		this.frecuencia = frecuencia;
	}

	public String getDuracion() {
		return duracion;
	}

	public void setDuracion(String duracion) {
		this.duracion = duracion;
	}

	public int getRecetaId() {
		return recetaId;
	}

	public void setRecetaId(int recetaId) {
		this.recetaId = recetaId;
	}

	public int getMedicamentoId() {
		return medicamentoId;
	}

	public void setMedicamentoId(int medicamentoId) {
		this.medicamentoId = medicamentoId;
	}
	
	
}
