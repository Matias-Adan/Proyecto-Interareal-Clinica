package com.clinica.modelo;

/**
 * Rol asignado a un usuario, tal como lo muestra la vista del administrador.
 *
 * Incluye el id de la asignación (roldeusuario) porque es el dato que se
 * necesita para quitarle el rol a un usuario.
 */
public class RolUsuarioAdmin {

    private int idRolUsuario;
    private int idRol;
    private String nombre;
    private String descripcion;

    public RolUsuarioAdmin() {
    }

    public RolUsuarioAdmin(int idRolUsuario, int idRol, String nombre, String descripcion) {
        this.idRolUsuario = idRolUsuario;
        this.idRol = idRol;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getIdRolUsuario() {
        return idRolUsuario;
    }

    public void setIdRolUsuario(int idRolUsuario) {
        this.idRolUsuario = idRolUsuario;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
