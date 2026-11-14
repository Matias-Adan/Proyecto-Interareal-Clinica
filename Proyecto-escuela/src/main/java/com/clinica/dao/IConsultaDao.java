package com.clinica.dao;
import java.util.List;
import com.clinica.modelo.Consulta;

public interface IConsultaDao {
	boolean insertar(Consulta c);

	/**
	 * Inserta la consulta y devuelve el id generado.
	 * Devuelve 0 si no se pudo insertar.
	 */
	int insertarYRetornarId(Consulta c);

	List<Consulta> listar();
	Consulta buscarPorId(int id);
	boolean actualizar(Consulta c);
}
