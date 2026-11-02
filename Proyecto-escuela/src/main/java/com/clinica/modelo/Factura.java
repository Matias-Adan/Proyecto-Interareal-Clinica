package com.clinica.modelo;

import java.time.LocalDateTime;

public class Factura {
	private int idFactura;
	private LocalDateTime fecha;
	private double importeTotal;
	private String estado;
	private int pacienteId;
	private int obraSocialId;
	
	public Factura() {
		
	}
	
	public Factura(int idFactura, LocalDateTime fecha, double importeTotal, String estado, int pacienteId,
			int obraSocialId) {
		this.idFactura = idFactura;
		this.fecha = fecha;
		this.importeTotal = importeTotal;
		this.estado = estado;
		this.pacienteId = pacienteId;
		this.obraSocialId = obraSocialId;
	}

	public int getIdFactura() {
		return idFactura;
	}

	public void setIdFactura(int idFactura) {
		this.idFactura = idFactura;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	public double getImporteTotal() {
		return importeTotal;
	}

	public void setImporteTotal(double importeTotal) {
		this.importeTotal = importeTotal;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public int getPacienteId() {
		return pacienteId;
	}

	public void setPacienteId(int pacienteId) {
		this.pacienteId = pacienteId;
	}

	public int getObraSocialId() {
		return obraSocialId;
	}

	public void setObraSocialId(int obraSocialId) {
		this.obraSocialId = obraSocialId;
	}
	
	
}
