package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.Receta;

public interface IRecetaDao {
	boolean insertar(Receta r);
	List<Receta> listar();
	Receta buscarPorId(int id);
}

