package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.Factura;

public interface IFacturaDao {
	boolean insertar(Factura f);
	List<Factura> listar();
	Factura buscarPorId(int id);
	boolean actualizar(Factura f);

}
