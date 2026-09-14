package com.clinica.dao;

import java.util.List;
import com.clinica.modelo.Estudio;

public interface IEstudioDao {
	boolean insertar(Estudio e);
	List<Estudio> listar();
	boolean actualizar(Estudio e);
	boolean eliminar(int id);
}
