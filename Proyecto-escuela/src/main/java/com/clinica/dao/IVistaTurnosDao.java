package com.clinica.dao;

import java.util.List;
import com.clinica.modelo.VistaTurnos;

public interface IVistaTurnosDao {
	List<VistaTurnos> listar();
	List<VistaTurnos> listarPorDni(String dni);

}
