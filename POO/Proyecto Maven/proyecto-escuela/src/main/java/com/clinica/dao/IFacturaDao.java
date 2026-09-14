package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.Factura;

public interface IFacturaDao {
	boolean insertar(Factura f);
	List<Factura> listar();
	boolean actualizar(Factura f);
	boolean eliminar(int id);
}
