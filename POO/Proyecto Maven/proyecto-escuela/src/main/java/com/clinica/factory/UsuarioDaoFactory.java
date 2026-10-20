package com.clinica.factory;

import com.clinica.dao.IUsuarioDao;
import com.clinica.dao.UsuarioDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class UsuarioDaoFactory {
// Retorna la interfaz IMedicamentoDao ocultando la implementación concreta
	public static IUsuarioDao getUsuarioDao() {
		return new UsuarioDaoImpl();
	}
}