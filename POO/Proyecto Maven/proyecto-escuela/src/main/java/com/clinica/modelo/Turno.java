package com.clinica.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Turno {
	private int idTurno;
	private LocalDate fecha;
	private LocalTime horaInicio;
	private LocalTime horaFin;
	private String estado;
	private String motivo;
	private int pacienteId;
	private int personalId;
	private int especialidadId;
	private int consultorioId;
	
	

	public Turno(int idTurno, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, String estado, String motivo,
			int pacienteId, int personalId, int especialidadId, int consultorioId) {
		this.idTurno = idTurno;
		this.fecha = fecha;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
		this.estado = estado;
		this.motivo = motivo;
		this.pacienteId = pacienteId;
		this.personalId = personalId;
		this.especialidadId = especialidadId;
		this.consultorioId = consultorioId;
	}

	public int getIdTurno() {
		return idTurno;
	}

	public void setIdTurno(int idTurno) {
		this.idTurno = idTurno;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
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

	public int getPersonalId() {
		return personalId;
	}

	public void setPersonalId(int personalId) {
		this.personalId = personalId;
	}

	public LocalTime getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(LocalTime horaInicio) {
		this.horaInicio = horaInicio;
	}

	public LocalTime getHoraFin() {
		return horaFin;
	}

	public void setHoraFin(LocalTime horaFin) {
		this.horaFin = horaFin;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public int getEspecialidadId() {
		return especialidadId;
	}

	public void setEspecialidadId(int especialidadId) {
		this.especialidadId = especialidadId;
	}

	public int getConsultorioId() {
		return consultorioId;
	}

	public void setConsultorioId(int consultorioId) {
		this.consultorioId = consultorioId;
	}

	
	
}
