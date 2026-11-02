package com.clinica.factory;

import com.clinica.dao.IRolDeUsuarioDao;
import com.clinica.dao.RolDeUsuarioDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class RolDeUsuarioDaoFactory {
// Retorna la interfaz IMedicamentoDao ocultando la implementación concreta
	public static IRolDeUsuarioDao getRolDeUsuarioDao() {
		return new RolDeUsuarioDaoImpl();
	}
}