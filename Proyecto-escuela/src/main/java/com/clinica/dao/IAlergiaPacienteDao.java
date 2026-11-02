package com.clinica.dao;
import java.util.List;
import com.clinica.modelo.AlergiaPaciente;

public interface IAlergiaPacienteDao {
	boolean insertar(AlergiaPaciente a);
	AlergiaPaciente buscarPorId(int id);
	List<AlergiaPaciente> listar();
	boolean actualizar(AlergiaPaciente a);
	boolean eliminar(int id);

}
