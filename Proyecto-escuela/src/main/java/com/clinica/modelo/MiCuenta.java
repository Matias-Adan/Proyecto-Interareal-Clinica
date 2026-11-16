package com.clinica.modelo;

import java.time.LocalDateTime;

/**
 * Datos de la cuenta de un usuario, tomados de la vista v_mi_cuenta.
 *
 * La misma clase sirve para cualquier rol: paciente, médico, administrador,
 * enfermero o recepcionista. El campo {@link #rol} indica cuál es.
 */
public class MiCuenta {

    private int usuarioId;
    private String dni;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private int obraSocialId;
    private String obraSocial;
    private String numeroConvenio;
    private String cobertura;
    private int rolId;
    private String rol;
    private LocalDateTime fechaAlta;

    public MiCuenta() {
    }

    public MiCuenta(int usuarioId, String dni, String nombre, String apellido,
            String nombreCompleto, String email, String telefono, int obraSocialId,
            String obraSocial, String numeroConvenio, String cobertura, int rolId,
            String rol, LocalDateTime fechaAlta) {
        this.usuarioId = usuarioId;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.telefono = telefono;
        this.obraSocialId = obraSocialId;
        this.obraSocial = obraSocial;
        this.numeroConvenio = numeroConvenio;
        this.cobertura = cobertura;
        this.rolId = rolId;
        this.rol = rol;
        this.fechaAlta = fechaAlta;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
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

    public int getObraSocialId() {
        return obraSocialId;
    }

    public void setObraSocialId(int obraSocialId) {
        this.obraSocialId = obraSocialId;
    }

    public String getObraSocial() {
        return obraSocial;
    }

    public void setObraSocial(String obraSocial) {
        this.obraSocial = obraSocial;
    }

    public String getNumeroConvenio() {
        return numeroConvenio;
    }

    public void setNumeroConvenio(String numeroConvenio) {
        this.numeroConvenio = numeroConvenio;
    }

    public String getCobertura() {
        return cobertura;
    }

    public void setCobertura(String cobertura) {
        this.cobertura = cobertura;
    }

    public int getRolId() {
        return rolId;
    }

    public void setRolId(int rolId) {
        this.rolId = rolId;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public LocalDateTime getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDateTime fechaAlta) {
        this.fechaAlta = fechaAlta;
    }
}
