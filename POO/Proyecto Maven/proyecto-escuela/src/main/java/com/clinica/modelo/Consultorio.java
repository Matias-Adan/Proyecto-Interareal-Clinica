package com.clinica.modelo;

public class Consultorio {
	private int idHabitacion;
	private int numero;
	private int piso;
	private String tipo;
	private String estado;
	
	public Consultorio(int idHabitacion, int numero, String tipo, String estado, int piso) {
		this.idHabitacion = idHabitacion;
		this.numero = numero;
		this.tipo = tipo;
		this.estado = estado;
		this.piso = piso;
	}

	public int getIdHabitacion() {
		return idHabitacion;
	}

	public void setIdHabitacion(int idHabitacion) {
		this.idHabitacion = idHabitacion;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public int getPiso() {
		return piso;
	}

	public void setPiso(int piso) {
		this.piso = piso;
	}
	
	
	
	
}
