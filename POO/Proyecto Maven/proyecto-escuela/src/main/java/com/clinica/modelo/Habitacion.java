package com.clinica.modelo;

public class Habitacion {
	private int idHabitacion;
	private int numero;
	private String tipo;
	private String estado;
	
	public Habitacion(int idHabitacion, int numero, String tipo, String estado) {
		this.idHabitacion = idHabitacion;
		this.numero = numero;
		this.tipo = tipo;
		this.estado = estado;
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
	
	
}
