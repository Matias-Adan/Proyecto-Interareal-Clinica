package com.clinica.factory;

import com.clinica.dao.IPermisoDao;
import com.clinica.dao.PermisoDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class PermisoDaoFactory {
// Retorna la interfaz IMedicamentoDao ocultando la implementación concreta
	public static IPermisoDao getPermisoDao() {
		return new PermisoDaoImpl();
	}
}