package com.clinica.dao;

import com.clinica.modelo.MiCuenta;

/**
 * Acceso a la vista v_mi_cuenta.
 *
 * La misma vista y el mismo DAO sirven para la cuenta de un paciente, de un
 * médico o de un administrador: sólo cambia el criterio de búsqueda.
 */
public interface IMiCuentaDao {

    /** Datos de la cuenta de un usuario por su DNI. */
    MiCuenta buscarPorDni(String dni);

    /** Datos de la cuenta de un usuario por su id. */
    MiCuenta buscarPorId(int usuarioId);
}
