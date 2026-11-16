package com.clinica.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Detalle de una receta de un paciente, tomado de la vista
 * v_mis_recetas_detalle.
 *
 * Cada fila representa un medicamento (detalle) de una receta.
 */
public class MiRecetaDetalle {

    private int idReceta;
    private LocalDate fecha;
    private String indicacion;
    private int idDetalle;
    private int cantidad;
    private String dosis;
    private String frecuencia;
    private String duracion;
    private int idArticulo;
    private String articuloNombre;
    private String presentacion;
    private int idConsulta;
    private LocalDateTime fechaConsulta;
    private String diagnostico;
    private String medicoNombre;

    public MiRecetaDetalle() {
    }

    public MiRecetaDetalle(int idReceta, LocalDate fecha, String indicacion, int idDetalle,
            int cantidad, String dosis, String frecuencia, String duracion, int idArticulo,
            String articuloNombre, String presentacion, int idConsulta,
            LocalDateTime fechaConsulta, String diagnostico, String medicoNombre) {
        this.idReceta = idReceta;
        this.fecha = fecha;
        this.indicacion = indicacion;
        this.idDetalle = idDetalle;
        this.cantidad = cantidad;
        this.dosis = dosis;
        this.frecuencia = frecuencia;
        this.duracion = duracion;
        this.idArticulo = idArticulo;
        this.articuloNombre = articuloNombre;
        this.presentacion = presentacion;
        this.idConsulta = idConsulta;
        this.fechaConsulta = fechaConsulta;
        this.diagnostico = diagnostico;
        this.medicoNombre = medicoNombre;
    }

    public int getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(int idReceta) {
        this.idReceta = idReceta;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getIndicacion() {
        return indicacion;
    }

    public void setIndicacion(String indicacion) {
        this.indicacion = indicacion;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getDosis() {
        return dosis;
    }

    public void setDosis(String dosis) {
        this.dosis = dosis;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public int getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(int idArticulo) {
        this.idArticulo = idArticulo;
    }

    public String getArticuloNombre() {
        return articuloNombre;
    }

    public void setArticuloNombre(String articuloNombre) {
        this.articuloNombre = articuloNombre;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public int getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(int idConsulta) {
        this.idConsulta = idConsulta;
    }

    public LocalDateTime getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(LocalDateTime fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getMedicoNombre() {
        return medicoNombre;
    }

    public void setMedicoNombre(String medicoNombre) {
        this.medicoNombre = medicoNombre;
    }
}
