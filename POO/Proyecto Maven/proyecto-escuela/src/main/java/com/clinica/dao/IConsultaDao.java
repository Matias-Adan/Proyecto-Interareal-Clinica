package com.clinica.dao;
import java.util.List;
import com.clinica.modelo.Consulta;

public interface IConsultaDao {
	boolean insertar(Consulta c);
	List<Consulta> listar();
	Consulta buscarPorId(int id);
	boolean actualizar(Consulta c);
}
