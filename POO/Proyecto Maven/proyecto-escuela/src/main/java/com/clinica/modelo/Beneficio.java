package com.clinica.modelo;

public class Beneficio {
	private int idBeneficio;
	private String cobertura;
	private String descripcion;
	private int obraSocialId;
	
	public Beneficio(int idBeneficio, String cobertura, String descripcion, int obraSocialId) {
		this.idBeneficio = idBeneficio;
		this.cobertura = cobertura;
		this.descripcion = descripcion;
		this.obraSocialId = obraSocialId;
	}

	public int getIdBeneficio() {
		return idBeneficio;
	}

	public void setIdBeneficio(int idBeneficio) {
		this.idBeneficio = idBeneficio;
	}

	public String getCobertura() {
		return cobertura;
	}

	public void setCobertura(String cobertura) {
		this.cobertura = cobertura;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getObraSocialId() {
		return obraSocialId;
	}

	public void setObraSocialId(int obraSocialId) {
		this.obraSocialId = obraSocialId;
	}
	
	
}
