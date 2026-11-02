package com.clinica.modelo;

import java.time.LocalDateTime;

public class Consulta {
	private int idConsulta;
	private LocalDateTime fechaHora;
	private String motivo;
	private String diagnostico;
	private String observacion;
	private int historiaId;
	private int medicoId;
	
	public Consulta() {
			
	}
	
	public Consulta(int idConsulta, LocalDateTime fechaHora, String motivo, String diagnostico,
			String observacion, int historiaId, int medicoId) {
		this.idConsulta = idConsulta;
		this.fechaHora = fechaHora; 
		this.motivo = motivo;
		this.diagnostico = diagnostico;
		this.observacion = observacion;
		this.historiaId = historiaId;
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

	public String getObservacion() {
		return observacion;
	}

	public void setObvservacion(String observacion) {
		this.observacion = observacion;
	}

	public int getHistoriaId() {
		return historiaId;
	}

	public void setHistorialId(int historiaId) {
		this.historiaId = historiaId;
	}

	public int getMedicoId() {
		return medicoId;
	}

	public void setMedicoId(int medicoId) {
		this.medicoId = medicoId;
	}
	
	
}
