package com.clinica.dao;

import java.util.List;
import com.clinica.modelo.DetalleReceta;

public interface IDetalleRecetaDao {
	boolean insertar(DetalleReceta d);
	List<DetalleReceta> listar();
	boolean actualizar(DetalleReceta d);
	boolean eliminar(int id);
}
