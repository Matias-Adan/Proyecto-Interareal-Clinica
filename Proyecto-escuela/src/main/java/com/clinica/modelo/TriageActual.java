package com.clinica.modelo;

import java.time.LocalDateTime;

/**
 * Fila de la vista v_triage_actual: un paciente que sigue en el triage,
 * con los datos que necesita el médico para ubicarlo y priorizarlo.
 */
public class TriageActual {

    private int idTriage;
    private LocalDateTime fecha;
    private String prioridad;
    private String estado;
    private String observacion;
    private int pacienteId;
    private String pacienteNombre;
    private String pacienteDni;
    private int consultorioId;
    private String consultorioNumero;
    private int enfermeroId;
    private String enfermeroNombre;

    public TriageActual() {
    }

    public TriageActual(int idTriage, LocalDateTime fecha, String prioridad, String estado,
            String observacion, int pacienteId, String pacienteNombre, String pacienteDni,
            int consultorioId, String consultorioNumero, int enfermeroId,
            String enfermeroNombre) {
        this.idTriage = idTriage;
        this.fecha = fecha;
        this.prioridad = prioridad;
        this.estado = estado;
        this.observacion = observacion;
        this.pacienteId = pacienteId;
        this.pacienteNombre = pacienteNombre;
        this.pacienteDni = pacienteDni;
        this.consultorioId = consultorioId;
        this.consultorioNumero = consultorioNumero;
        this.enfermeroId = enfermeroId;
        this.enfermeroNombre = enfermeroNombre;
    }

    public int getIdTriage() {
        return idTriage;
    }

    public void setIdTriage(int idTriage) {
        this.idTriage = idTriage;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public int getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(int pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getPacienteNombre() {
        return pacienteNombre;
    }

    public void setPacienteNombre(String pacienteNombre) {
        this.pacienteNombre = pacienteNombre;
    }

    public String getPacienteDni() {
        return pacienteDni;
    }

    public void setPacienteDni(String pacienteDni) {
        this.pacienteDni = pacienteDni;
    }

    public int getConsultorioId() {
        return consultorioId;
    }

    public void setConsultorioId(int consultorioId) {
        this.consultorioId = consultorioId;
    }

    public String getConsultorioNumero() {
        return consultorioNumero;
    }

    public void setConsultorioNumero(String consultorioNumero) {
        this.consultorioNumero = consultorioNumero;
    }

    public int getEnfermeroId() {
        return enfermeroId;
    }

    public void setEnfermeroId(int enfermeroId) {
        this.enfermeroId = enfermeroId;
    }

    public String getEnfermeroNombre() {
        return enfermeroNombre;
    }

    public void setEnfermeroNombre(String enfermeroNombre) {
        this.enfermeroNombre = enfermeroNombre;
    }
}
