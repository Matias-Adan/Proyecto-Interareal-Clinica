package com.clinica.modelo;

public class RolDeUsuario {
	private int idRolUsuario;
	private int usuarioId;
	private int rolId;
	
	public RolDeUsuario(int idRolUsuario, int usuarioId, int rolId) {
		this.idRolUsuario = idRolUsuario;
		this.usuarioId = usuarioId;
		this.rolId = rolId;
	}

	public int getIdRolUsuario() {
		return idRolUsuario;
	}

	public void setIdRolUsuario(int idRolUsuario) {
		this.idRolUsuario = idRolUsuario;
	}

	public int getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(int usuarioId) {
		this.usuarioId = usuarioId;
	}

	public int getRolId() {
		return rolId;
	}

	public void setRolId(int rolId) {
		this.rolId = rolId;
	}
	
	
}
