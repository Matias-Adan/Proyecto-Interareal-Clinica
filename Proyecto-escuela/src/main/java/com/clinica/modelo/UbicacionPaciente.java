package com.clinica.modelo;

import java.time.LocalDateTime;

public class UbicacionPaciente {
	private int idUbicacion;
	private String tipoAtencion;
	private LocalDateTime fechaIngreso;
	private LocalDateTime fechaSalida;
	private int pacienteId;
	private int medicoId;
	private int consultorioId;
	
	public UbicacionPaciente() {

	}

	
	public UbicacionPaciente(int idUbicacion, String tipoAtencion, LocalDateTime fechaIngreso, LocalDateTime fechaSalida,
			int pacienteId, int consultorioId) {
		this.idUbicacion = idUbicacion;
		this.tipoAtencion = tipoAtencion;
		this.fechaIngreso = fechaIngreso;
		this.fechaSalida = fechaSalida;
		this.pacienteId = pacienteId;
		this.consultorioId = consultorioId;
	}

	public int getIdUbicacion() {
		return idUbicacion;
	}

	public void setIdUbicacion(int idUbicacion) {
		this.idUbicacion = idUbicacion;
	}

	public String getTipoAtencion() {
		return tipoAtencion;
	}

	public void setTipoAtencion(String tipoAtencion) {
		this.tipoAtencion = tipoAtencion;
	}

	public LocalDateTime getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(LocalDateTime fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public LocalDateTime getFechaSalida() {
		return fechaSalida;
	}

	public void setFechaSalida(LocalDateTime fechaSalida) {
		this.fechaSalida = fechaSalida;
	}

	public int getPacienteId() {
		return pacienteId;
	}

	public void setPacienteId(int pacienteId) {
		this.pacienteId = pacienteId;
	}
	
	

	public int getMedicoId() {
		return medicoId;
	}

	public void setMedicoId(int medicoId) {
		this.medicoId = medicoId;
	}

	public int getConsultorioId() {
		return consultorioId;
	}

	public void setConsultorioId(int consultorioId) {
		this.consultorioId = consultorioId;
	}
	
	
}
