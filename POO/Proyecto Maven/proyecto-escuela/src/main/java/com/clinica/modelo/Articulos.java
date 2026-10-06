package com.clinica.modelo;

public class Articulos {
	private int idArticulo;
	private String nombre;
	private String descripcion;
	private String presentacion;
	private String tipoArticulo;
	private int stockActual;
	private int stockMinimo;

	public Articulos(int idArticulo, String nombre, String descripcion, String presentacion, String tipoArticulo,
			int stockActual, int stockMinimo) {
		this.idArticulo = idArticulo;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.presentacion = presentacion;
		this.tipoArticulo = tipoArticulo;
		this.stockActual = stockActual;
		this.stockMinimo = stockMinimo;
	}

	public int getIdArticulo() {
		return idArticulo;
	}

	public void setIdMedicamento(int idMedicamento) {
		this.idArticulo = idMedicamento;
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

	public String getPresentacion() {
		return presentacion;
	}

	public void setPresentacion(String presentacion) {
		this.presentacion = presentacion;
	}

	public String getTipoArticulo() {
		return tipoArticulo;
	}

	public void setTipoArticulo(String tipoArticulo) {
		this.tipoArticulo = tipoArticulo;
	}
	
	
	
	
}
