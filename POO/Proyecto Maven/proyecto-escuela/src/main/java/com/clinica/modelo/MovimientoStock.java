package com.clinica.modelo;

import java.time.LocalDateTime;

public class MovimientoStock {
	private int movimientoStock;
	private int idArticulo;
	private String tipoMovimiento;
	private int cantidad;
	private String motivo;
	private LocalDateTime fechaHora;
	private int usuarioId;
	
	public MovimientoStock(int movimientoStock, int idArticulo, String tipoMovimiento, int cantidad, String motivo,
			LocalDateTime fechaHora, int usuarioId) {
		this.movimientoStock = movimientoStock;
		this.idArticulo = idArticulo;
		this.tipoMovimiento = tipoMovimiento;
		this.cantidad = cantidad;
		this.motivo = motivo;
		this.fechaHora = fechaHora;
		this.usuarioId = usuarioId;
	}

	public int getMovimientoStock() {
		return movimientoStock;
	}

	public void setMovimientoStock(int movimientoStock) {
		this.movimientoStock = movimientoStock;
	}

	public int getIdArticulo() {
		return idArticulo;
	}

	public void setIdArticulo(int idArticulo) {
		this.idArticulo = idArticulo;
	}

	public String getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(String tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public LocalDateTime getFechaHora() {
		return fechaHora;
	}

	public void setFechaHora(LocalDateTime fechaHora) {
		this.fechaHora = fechaHora;
	}

	public int getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(int usuarioId) {
		this.usuarioId = usuarioId;
	}
	
}
