package com.clinica.modelo;

import java.time.LocalDate;

public class Usuario {
	private int idUsuario;
	private String nombre;
	private String apellido;
	private int dni;
	private LocalDate fechaNacimiento;
	private int telefono;
	private String email;
	private String password;
	private int obraSocialId;

	public Usuario(int idUsuario, String nombre, String apellido, int dni, LocalDate fechaNacimiento, int telefono,
			String email, String password, int obraSocialId) {
		this.idUsuario = idUsuario;
		this.nombre = nombre;
		this.apellido = apellido;
		this.dni = dni;
		this.fechaNacimiento = fechaNacimiento;
		this.telefono = telefono;
		this.email = email;
		this.password = password;
		this.obraSocialId = obraSocialId;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public int getDni() {
		return dni;
	}

	public void setDni(int dni) {
		this.dni = dni;
	}

	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(LocalDate fechaNaciemiento) {
		this.fechaNacimiento = fechaNaciemiento;
	}

	public int getTelefono() {
		return telefono;
	}

	public void setTelefono(int telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getObraSocialId() {
		return obraSocialId;
	}

	public void setObraSocialId(int obraSocialId) {
		this.obraSocialId = obraSocialId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
