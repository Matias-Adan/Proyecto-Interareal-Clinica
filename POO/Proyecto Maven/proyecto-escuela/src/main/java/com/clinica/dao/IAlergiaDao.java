package com.clinica.dao;
import java.util.List;
import com.clinica.modelo.Alergia;

public interface IAlergiaDao {
	boolean insertar(Alergia a);
	List<Alergia> listar();
	Alergia buscarPorId(int id);
	boolean actualizar(Alergia a);
}
