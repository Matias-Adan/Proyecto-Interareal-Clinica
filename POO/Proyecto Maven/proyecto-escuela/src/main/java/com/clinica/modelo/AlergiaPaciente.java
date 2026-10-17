package com.clinica.modelo;

public class AlergiaPaciente {
	private int idAlergiaPaciente;
	private int pacienteId;
	private int alergiaId;
	
	public AlergiaPaciente(int idAlergiaPaciente, int pacienteId, int alergiaId) {
		this.idAlergiaPaciente = idAlergiaPaciente;
		this.pacienteId = pacienteId;
		this.alergiaId = alergiaId;
	}

	public int getIdAlergiaPaciente() {
		return idAlergiaPaciente;
	}

	public void setIdAlergiaPaciente(int idAlergiaPaciente) {
		this.idAlergiaPaciente = idAlergiaPaciente;
	}

	public int getPacienteId() {
		return pacienteId;
	}

	public void setPacienteId(int pacienteId) {
		this.pacienteId = pacienteId;
	}

	public int getAlergiaId() {
		return alergiaId;
	}

	public void setAlergiaId(int alergiaId) {
		this.alergiaId = alergiaId;
	}
	
	
}
