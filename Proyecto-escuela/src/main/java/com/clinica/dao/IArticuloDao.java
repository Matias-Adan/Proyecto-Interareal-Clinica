package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.Articulo;

public interface IArticuloDao {
	boolean insertar(Articulo a);
	Articulo buscarPorId(int id);
	List<Articulo> listar();
	boolean actualizar(Articulo a);
	boolean eliminar(int id);

}