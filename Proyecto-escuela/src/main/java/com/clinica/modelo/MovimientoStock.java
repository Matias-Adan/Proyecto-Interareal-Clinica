package com.clinica.modelo;

import java.time.LocalDateTime;

public class MovimientoStock {
	private int idMovimiento;
	private int idArticulo;
	private String tipoMovimiento;
	private int cantidad;
	private String motivo;
	private LocalDateTime fechaHora;
	private int usuarioId;
	
	public MovimientoStock() {

	}
	
	public MovimientoStock(int idMovimiento, int idArticulo, String tipoMovimiento, int cantidad, String motivo,
			LocalDateTime fechaHora, int usuarioId) {
		this.idMovimiento = idMovimiento;
		this.idArticulo = idArticulo;
		this.tipoMovimiento = tipoMovimiento;
		this.cantidad = cantidad;
		this.motivo = motivo;
		this.fechaHora = fechaHora;
		this.usuarioId = usuarioId;
	}

	public int getidMovimiento() {
		return idMovimiento;
	}

	public void setidMovimiento(int idMovimiento) {
		this.idMovimiento = idMovimiento;
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
