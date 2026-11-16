package com.clinica.dao;

import com.clinica.modelo.TriageActual;
import com.clinica.modelo.VistaTurnos;

import java.util.List;

/**
 * Acceso a las vistas de la sección "Agenda y pacientes" del médico.
 */
public interface IVistaMedicoDao {

    /** Turnos asignados al médico indicado. */
    List<VistaTurnos> listarTurnos(int medicoId);

    /** Pacientes que continúan en el triage. */
    List<TriageActual> listarTriageActual();
}
