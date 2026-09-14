package com.clinica.modelo;

import java.time.LocalDate;

public class Estudio {
	private int idEstudio;
	private String tipo;
	private LocalDate fecha;
	private String resultado;
	private String archivo;
	private int pacienteId;
	private int empleadoId;
	
	public Estudio(int idEstudio, String tipo, LocalDate fecha, String resultado, String archivo, int pacienteId,
			int empleadoId) {
		this.idEstudio = idEstudio;
		this.tipo = tipo;
		this.fecha = fecha;
		this.resultado = resultado;
		this.archivo = archivo;
		this.pacienteId = pacienteId;
		this.empleadoId = empleadoId;
	}

	public int getIdEstudio() {
		return idEstudio;
	}

	public void setIdEstudio(int idEstudio) {
		this.idEstudio = idEstudio;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}

	public String getArchivo() {
		return archivo;
	}

	public void setArchivo(String archivo) {
		this.archivo = archivo;
	}

	public int getPacienteId() {
		return pacienteId;
	}

	public void setPacienteId(int pacienteId) {
		this.pacienteId = pacienteId;
	}

	public int getEmpleadoId() {
		return empleadoId;
	}

	public void setEmpleadoId(int empleadoId) {
		this.empleadoId = empleadoId;
	}
	
	
}
