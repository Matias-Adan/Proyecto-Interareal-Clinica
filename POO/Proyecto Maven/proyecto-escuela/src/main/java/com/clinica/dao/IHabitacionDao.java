package com.clinica.dao;

import java.util.List;
import com.clinica.modelo.Habitacion;

public interface IHabitacionDao {
	boolean insertar(Habitacion h);
	List<Habitacion> listar();
	boolean actualizar(Habitacion h);
	boolean eliminar(int id);
}
