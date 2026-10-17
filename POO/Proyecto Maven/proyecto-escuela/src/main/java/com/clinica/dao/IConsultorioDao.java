package com.clinica.dao;

import java.util.List;
import com.clinica.modelo.Consultorio;

public interface IConsultorioDao {
	boolean insertar(Consultorio c);
	List<Consultorio> listar();
	Consultorio buscarPorId(int id);
	boolean actualizar(Consultorio c);

}
