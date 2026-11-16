package com.clinica.modelo;

/**
 * Alergia actual de un paciente, tomada de la vista v_mis_alergias.
 */
public class MiAlergia {

    private int idAlergiaPaciente;
    private int idAlergia;
    private String alergiaNombre;
    private String alergiaDescripcion;

    public MiAlergia() {
    }

    public MiAlergia(int idAlergiaPaciente, int idAlergia, String alergiaNombre,
            String alergiaDescripcion) {
        this.idAlergiaPaciente = idAlergiaPaciente;
        this.idAlergia = idAlergia;
        this.alergiaNombre = alergiaNombre;
        this.alergiaDescripcion = alergiaDescripcion;
    }

    public int getIdAlergiaPaciente() {
        return idAlergiaPaciente;
    }

    public void setIdAlergiaPaciente(int idAlergiaPaciente) {
        this.idAlergiaPaciente = idAlergiaPaciente;
    }

    public int getIdAlergia() {
        return idAlergia;
    }

    public void setIdAlergia(int idAlergia) {
        this.idAlergia = idAlergia;
    }

    public String getAlergiaNombre() {
        return alergiaNombre;
    }

    public void setAlergiaNombre(String alergiaNombre) {
        this.alergiaNombre = alergiaNombre;
    }

    public String getAlergiaDescripcion() {
        return alergiaDescripcion;
    }

    public void setAlergiaDescripcion(String alergiaDescripcion) {
        this.alergiaDescripcion = alergiaDescripcion;
    }
}
