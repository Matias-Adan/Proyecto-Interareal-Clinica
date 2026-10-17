package com.clinica.dao;

import java.util.List;
import com.clinica.modelo.DetalleReceta;

public interface IDetalleRecetaDao {
	boolean insertar(DetalleReceta d);
	DetalleReceta buscarPorId(int id);
	List<DetalleReceta> listar();
}
