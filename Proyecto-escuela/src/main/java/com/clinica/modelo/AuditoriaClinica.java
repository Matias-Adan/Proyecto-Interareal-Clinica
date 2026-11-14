package com.clinica.modelo;

import java.time.LocalDateTime;

/**
 * Registro inmutable de una corrección sobre un dato clínico.
 * Guarda el JSON completo del estado anterior para poder reconstruir
 * la historia de cambios.
 */
public class AuditoriaClinica {

    private int idAuditoria;
    private String tabla;
    private int registroId;
    private String accion;
    private String datosAnteriores;
    private int usuarioId;
    private LocalDateTime fechaCambio;

    public AuditoriaClinica() {
    }

    public AuditoriaClinica(int idAuditoria, String tabla, int registroId, String accion,
            String datosAnteriores, int usuarioId, LocalDateTime fechaCambio) {
        this.idAuditoria = idAuditoria;
        this.tabla = tabla;
        this.registroId = registroId;
        this.accion = accion;
        this.datosAnteriores = datosAnteriores;
        this.usuarioId = usuarioId;
        this.fechaCambio = fechaCambio;
    }

    public int getIdAuditoria() {
        return idAuditoria;
    }

    public void setIdAuditoria(int idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public String getTabla() {
        return tabla;
    }

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    public int getRegistroId() {
        return registroId;
    }

    public void setRegistroId(int registroId) {
        this.registroId = registroId;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getDatosAnteriores() {
        return datosAnteriores;
    }

    public void setDatosAnteriores(String datosAnteriores) {
        this.datosAnteriores = datosAnteriores;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }
}
