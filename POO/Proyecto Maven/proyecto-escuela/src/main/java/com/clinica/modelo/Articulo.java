package com.clinica.modelo;

public class Articulo {
	private int idArticulo;
	private String nombre;
	private String descripcion;
	private String presentacion;
	private String tipoArticulo;
	private int stockActual;
	private int stockMinimo;
	private double temperaturaIdealEnCelcius;

	public Articulo(int idArticulo, String nombre, String descripcion, String presentacion, String tipoArticulo,
			int stockActual, int stockMinimo, double temperaturaIdealEnCelcius) {
		this.idArticulo = idArticulo;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.presentacion = presentacion;
		this.tipoArticulo = tipoArticulo;
		this.stockActual = stockActual;
		this.stockMinimo = stockMinimo;
		this.temperaturaIdealEnCelcius = temperaturaIdealEnCelcius;
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

	public double getTemperaturaIdealEnCelcius() {
		return temperaturaIdealEnCelcius;
	}

	public void setTemperaturaIdealEnCelcius(double temperaturaIdealEnCelcius) {
		this.temperaturaIdealEnCelcius = temperaturaIdealEnCelcius;
	}

	public void setIdArticulo(int idArticulo) {
		this.idArticulo = idArticulo;
	}
	
	
	
	
}
