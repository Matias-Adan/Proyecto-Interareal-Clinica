package com.clinica.modelo;

public class ObraSocial {
	private int idObraSocial;
	private String nombre;
	private int numeroConvenio;
	private String beneficios;
	private String cobertura;
	private boolean flagBorrado;

	public ObraSocial(int idObraSocial,  String nombre,  int numeroConvenio,  String beneficios, String cobertura, boolean flagBorrado) {
		this.idObraSocial = idObraSocial;
		this.nombre = nombre;
		this.numeroConvenio = numeroConvenio;
		this.beneficios = beneficios;
		this.cobertura = cobertura;
		this.flagBorrado = flagBorrado;
	}
	
	public int getIdObraSocial() {
		return idObraSocial;
	}


	public void setIdObraSocial(int idObraSocial) {
		this.idObraSocial = idObraSocial;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public int getNumeroConvenio() {
		return numeroConvenio;
	}
	

	public void setNumeroConvenio(int numeroConvenio) {
		this.numeroConvenio = numeroConvenio;
	}


	public String getBeneficios() {
		return beneficios;
	}


	public void setBeneficios(String beneficios) {
		this.beneficios = beneficios;
	}


	public String getCobertura() {
		return cobertura;
	}


	public void setCobertura(String cobertura) {
		this.cobertura = cobertura;
	}

	public boolean isFlagBorrado() {
		return flagBorrado;
	}

	public void setFlagBorrado(boolean flagBorrado) {
		this.flagBorrado = flagBorrado;
	}
	
	
	
}
