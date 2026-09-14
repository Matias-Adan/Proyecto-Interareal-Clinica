package com.clinica.dao;
import java.util.List;
import com.clinica.modelo.Beneficio;

public interface IBeneficioDao {
	boolean insertar(Beneficio b);
	List<Beneficio> listar();
	boolean actualizar(Beneficio b);
	boolean eliminar(int id);
}
