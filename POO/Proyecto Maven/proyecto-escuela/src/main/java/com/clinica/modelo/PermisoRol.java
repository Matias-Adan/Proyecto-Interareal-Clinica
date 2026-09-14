package com.clinica.modelo;

public class PermisoRol {
	private int idUsuario;
	private int rolId;
	private int PermisoId;
	
	public PermisoRol(int idUsuario, int rolId, int permisoId) {
		this.idUsuario = idUsuario;
		this.rolId = rolId;
		PermisoId = permisoId;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public int getRolId() {
		return rolId;
	}

	public void setRolId(int rolId) {
		this.rolId = rolId;
	}

	public int getPermisoId() {
		return PermisoId;
	}

	public void setPermisoId(int permisoId) {
		PermisoId = permisoId;
	}
	
	
}
