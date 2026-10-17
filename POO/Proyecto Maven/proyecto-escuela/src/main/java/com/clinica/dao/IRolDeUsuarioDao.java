package com.clinica.dao;

import java.util.List;


import com.clinica.modelo.RolDeUsuario;

public interface IRolDeUsuarioDao {
	boolean insertar(RolDeUsuario r);
	List<RolDeUsuario> listar();
	RolDeUsuario buscarPorId(int id);
	boolean actualizar(RolDeUsuario r);
	boolean eliminar(int id);
}
