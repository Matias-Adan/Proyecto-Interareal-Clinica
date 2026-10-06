package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.Articulos;

public interface IArticulosDao {
	boolean insertar(Articulos a);
	List<Articulos> listar();
	boolean actualizar(Articulos a);
	boolean eliminar(int id);
	Articulos buscarPorId(int id);
}