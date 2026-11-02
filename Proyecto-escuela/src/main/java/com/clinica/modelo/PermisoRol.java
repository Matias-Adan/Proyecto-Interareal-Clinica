package com.clinica.modelo;

public class PermisoRol {
	private int idPermisoRol;
	private int rolId;
	private int PermisoId;
	
	public PermisoRol() {

	}
	
	public PermisoRol(int idPermisoRol, int rolId, int permisoId) {
		this.idPermisoRol = idPermisoRol;
		this.rolId = rolId;
		PermisoId = permisoId;
	}

	public int getIdPermisoRol() {
		return idPermisoRol;
	}

	public void setIdPermisoRol(int idPermisoRol) {
		this.idPermisoRol = idPermisoRol;
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
