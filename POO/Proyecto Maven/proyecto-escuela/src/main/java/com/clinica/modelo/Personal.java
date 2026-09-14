package com.clinica.modelo;

public class Personal {
	private int idEspecialidad;
	private String trabajo;
	private String descripcion;
	private int rolId;

	public Personal(int idEspecialidad, String trabajo, String descripcion, int rolId) {
		this.idEspecialidad = idEspecialidad;
		this.trabajo = trabajo;
		this.descripcion = descripcion;
		this.rolId = rolId;
	}

	public int getIdEspecialidad() {
		return idEspecialidad;
	}

	public void setIdEspecialidad(int idEspecialidad) {
		this.idEspecialidad = idEspecialidad;
	}

	public String getTrabajo() {
		return trabajo;
	}

	public void setTrabajo(String trabajo) {
		this.trabajo = trabajo;
	}

	public int getRolId() {
		return rolId;
	}

	public void setRolId(int rolId) {
		this.rolId = rolId;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
	
}
