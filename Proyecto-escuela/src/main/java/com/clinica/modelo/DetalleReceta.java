package com.clinica.modelo;

public class DetalleReceta {
	private int idDetalle;
	private int cantidad;
	private String dosis;
	private String frecuencia;
	private String duracion;
	private int recetaId;
	private int articuloId;
	
	public DetalleReceta() {

	}
	
	public DetalleReceta(int idReceta, int cantidad, String dosis, String frecuencia, String duracion, int recetaId,
			int articuloId) {
		this.idDetalle = idReceta;
		this.cantidad = cantidad;
		this.dosis = dosis;
		this.frecuencia = frecuencia;
		this.duracion = duracion;
		this.recetaId = recetaId;
		this.articuloId = articuloId;
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

	public String getDosis() {
		return dosis;
	}

	public void setDosis(String dosis) {
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

	public int getArticuloId() {
		return articuloId;
	}

	public void setArticuloId(int articuloId) {
		this.articuloId = articuloId;
	}
	
	
}
