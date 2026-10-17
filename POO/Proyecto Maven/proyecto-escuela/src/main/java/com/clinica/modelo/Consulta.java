package com.clinica.modelo;

import java.time.LocalDateTime;

public class Consulta {
	private int idConsulta;
	private LocalDateTime fechaHora;
	private String motivo;
	private String diagnostico;
	private String obvservacion;
	private int historialId;
	private int medicoId;
	
	public Consulta(int idConsulta, LocalDateTime fechaHora, String motivo, String diagnostico,
			String obvservacion, int historialId, int medicoId) {
		this.idConsulta = idConsulta;
		this.fechaHora = fechaHora;
		this.motivo = motivo;
		this.diagnostico = diagnostico;
		this.obvservacion = obvservacion;
		this.historialId = historialId;
		this.medicoId = medicoId;
	}

	public int getIdConsulta() {
		return idConsulta;
	}

	public void setIdConsulta(int idConsulta) {
		this.idConsulta = idConsulta;
	}

	public LocalDateTime getFecha() {
		return fechaHora;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fechaHora = fecha;
	}
	
	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}

	public String getObvservacion() {
		return obvservacion;
	}

	public void setObvservacion(String obvservacion) {
		this.obvservacion = obvservacion;
	}

	public int getHistorialId() {
		return historialId;
	}

	public void setHistorialId(int historialId) {
		this.historialId = historialId;
	}

	public int getMedicoId() {
		return medicoId;
	}

	public void setMedicoId(int medicoId) {
		this.medicoId = medicoId;
	}
	
	
}
