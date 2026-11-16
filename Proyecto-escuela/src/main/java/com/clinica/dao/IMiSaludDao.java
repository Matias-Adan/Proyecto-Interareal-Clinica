package com.clinica.dao;

import com.clinica.modelo.MiAlergia;
import com.clinica.modelo.MiRecetaDetalle;

import java.util.List;

/**
 * Acceso a las vistas de la sección "Mi salud" del paciente.
 */
public interface IMiSaludDao {

    /** Alergias actuales del paciente. */
    List<MiAlergia> listarAlergias(String dni);

    /** Recetas del paciente con el detalle de cada medicamento. */
    List<MiRecetaDetalle> listarRecetas(String dni);
}
