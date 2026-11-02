package com.clinica.factory;

import com.clinica.dao.IRolDao;
import com.clinica.dao.RolDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class RolDaoFactory {
// Retorna la interfaz IMedicamentoDao ocultando la implementación concreta
	public static IRolDao getRolDao() {
		return new RolDaoImpl();
	}
}