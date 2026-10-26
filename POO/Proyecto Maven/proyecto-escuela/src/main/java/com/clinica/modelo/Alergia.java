package com.clinica.modelo;

public class Alergia {
	private int idAlergia;
	private String nombre;
	private String descripcion;
	
	public Alergia() {

	}
	
	public Alergia(int idAlergia, String nombre, String descripcion) {
		this.idAlergia = idAlergia;
		this.nombre = nombre;
		this.descripcion = descripcion;
	}

	public int getIdAlergia() {
		return idAlergia;
	}

	public void setIdAlergia(int idAlergia) {
		this.idAlergia = idAlergia;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
}
