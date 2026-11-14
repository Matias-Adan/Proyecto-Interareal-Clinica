package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.Receta;

public interface IRecetaDao {
	boolean insertar(Receta r);

	/**
	 * Inserta la receta y devuelve el id generado.
	 * Devuelve 0 si no se pudo insertar.
	 */
	int insertarYRetornarId(Receta r);

	List<Receta> listar();
	Receta buscarPorId(int id);
}

