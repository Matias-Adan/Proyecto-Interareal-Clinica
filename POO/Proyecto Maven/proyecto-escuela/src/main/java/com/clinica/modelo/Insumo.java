package com.clinica.modelo;

public class Insumo {
	private int idInsumo;
	private String nombre;
	private String descripcion;
	private String tipo;
	private int stockActual;
	private int stockMinimo;
	
	public Insumo(int idInsumo, String nombre, String descripcion, String tipo, int stockActual, int stockMinimo) {
		this.idInsumo = idInsumo;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.tipo = tipo;
		this.stockActual = stockActual;
		this.stockMinimo = stockMinimo;
	}

	public int getIdInsumo() {
		return idInsumo;
	}

	public void setIdInsumo(int idInsumo) {
		this.idInsumo = idInsumo;
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

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public int getStockActual() {
		return stockActual;
	}

	public void setStockActual(int stockActual) {
		this.stockActual = stockActual;
	}

	public int getStockMinimo() {
		return stockMinimo;
	}

	public void setStockMinimo(int stockMinimo) {
		this.stockMinimo = stockMinimo;
	}
	
	
}
