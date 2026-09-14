package com.clinica.modelo;

public class Medicamento {
	private int idMedicamento;
	private String nombre;
	private String descripcion;
	private int stockActual;
	private int stockMinimo;
	
	public Medicamento(int idMedicamento, String nombre, String descripcion, int stockAnual, int stockMinimo) {
		this.idMedicamento = idMedicamento;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.stockActual = stockAnual;
		this.stockMinimo = stockMinimo;
	}

	public int getIdMedicamento() {
		return idMedicamento;
	}

	public void setIdMedicamento(int idMedicamento) {
		this.idMedicamento = idMedicamento;
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

	public int getStockActual() {
		return stockActual;
	}

	public void setStockActual(int stockAnual) {
		this.stockActual = stockAnual;
	}

	public int getStockMinimo() {
		return stockMinimo;
	}

	public void setStockMinimo(int stockMinimo) {
		this.stockMinimo = stockMinimo;
	}
	
	
}
