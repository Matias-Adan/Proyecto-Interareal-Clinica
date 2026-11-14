package com.clinica.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class VistaTurnos {
	private int idTurno;
	private LocalDate fecha;
	private LocalTime horaInicio;
	private LocalTime horaFin;
	private String estadoTurno;
	private String motivo;
	private String medicoNombre;
	private String pacienteNombre;
	private String pacienteDni;
	private String consultorioNumero;
	
	public VistaTurnos() {
		
	}
	
	public VistaTurnos(int idTurno, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, String estadoTurno,
			String motivo, String medicoNombre, String pacienteNombre, String pacienteDni,
			String consultorioNumero) {
		this.idTurno = idTurno;
		this.fecha = fecha;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
		this.estadoTurno = estadoTurno;
		this.motivo = motivo;
		this.medicoNombre = medicoNombre;
		this.pacienteNombre = pacienteNombre;
		this.pacienteDni = pacienteDni;
		this.consultorioNumero = consultorioNumero;
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
	
	public void setEstadoTurno(String estadoTurno) {
		this.estadoTurno = estadoTurno;
	}
	
	public String getEstadoTurno() {
		return estadoTurno;
	}
	public String getMotivo() {
		return motivo;
	}
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	public String getMedicoNombre() {
		return medicoNombre;
	}
	public void setMedicoNombre(String medicoNombre) {
		this.medicoNombre = medicoNombre;
	}
	public String getPacienteNombre() {
		return pacienteNombre;
	}
	public void setPacienteNombre(String PacienteNombre) {
		this.pacienteNombre = PacienteNombre;
	}
	public String getPacienteDni() {
		return pacienteDni;
	}
	public void setPacienteDni(String PacienteDni) {
		this.pacienteDni = PacienteDni;
	}
	public String getConsultorioNumero() {
		return consultorioNumero;
	}
	public void setConsultorioNumero(String consultorioNumero) {
		this.consultorioNumero = consultorioNumero;
	}
	
	
	
}
