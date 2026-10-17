package com.clinica.dao;

import java.util.List;

import com.clinica.modelo.Usuario;

public interface IUsuarioDao {
	boolean insertar(Usuario u);
	List<Usuario> listar();
	Usuario buscarPorId(int id);
	boolean actualizar(Usuario u);
	boolean eliminar(int id);
	Usuario login(String usuario);
}
