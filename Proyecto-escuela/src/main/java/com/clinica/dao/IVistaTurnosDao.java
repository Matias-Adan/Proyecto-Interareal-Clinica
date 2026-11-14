package com.clinica.dao;

import java.util.List;
import com.clinica.modelo.VistaTurnos;

public interface IVistaTurnosDao {
	List<VistaTurnos> listar();
	VistaTurnos buscarPorDni(String dni);

}
