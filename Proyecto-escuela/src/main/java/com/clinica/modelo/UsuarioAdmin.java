package com.clinica.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Usuario activo de la vista del administrador.
 *
 * No incluye la contraseña: la consulta usa la vista v_admin_usuarios, que
 * tampoco la expone.
 */
public class UsuarioAdmin {

    private int idUsuario;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String dni;
    private String email;
    private String telefono;
    private String obraSocial;
    private List<RolUsuarioAdmin> roles = new ArrayList<>();

    public UsuarioAdmin() {
    }

    public UsuarioAdmin(int idUsuario, String nombre, String apellido, String nombreCompleto,
            String dni, String email, String telefono, String obraSocial) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
        this.email = email;
        this.telefono = telefono;
        this.obraSocial = obraSocial;
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

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getObraSocial() {
        return obraSocial;
    }

    public void setObraSocial(String obraSocial) {
        this.obraSocial = obraSocial;
    }

    public List<RolUsuarioAdmin> getRoles() {
        return roles;
    }

    public void setRoles(List<RolUsuarioAdmin> roles) {
        this.roles = roles == null ? new ArrayList<>() : roles;
    }
}
