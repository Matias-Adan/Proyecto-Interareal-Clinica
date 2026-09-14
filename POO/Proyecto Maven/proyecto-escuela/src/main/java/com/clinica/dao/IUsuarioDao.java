package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.Usuario;

public interface IUsuarioDao {
	boolean insertar(Usuario u);
	List<Usuario> listar();
	boolean actualizar(Usuario u);
	boolean eliminar(int id);
}
