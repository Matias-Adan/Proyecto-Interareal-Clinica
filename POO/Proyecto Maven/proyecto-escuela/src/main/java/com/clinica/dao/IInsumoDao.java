package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.Insumo;

public interface IInsumoDao {
	boolean insertar(Insumo i);
	List<Insumo> listar();
	boolean actualizar(Insumo i);
	boolean eliminar(int id);
}