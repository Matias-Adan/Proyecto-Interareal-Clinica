package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.MovimientoStock;

public interface IMovimientoStockDao {
	boolean insertar(MovimientoStock m);
	MovimientoStock buscarPorId(int id);
	List<MovimientoStock> listar();
}
