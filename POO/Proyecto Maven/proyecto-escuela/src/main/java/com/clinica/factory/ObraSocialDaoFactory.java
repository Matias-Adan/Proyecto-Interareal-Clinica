package com.clinica.factory;

import com.clinica.dao.IObraSocialDao;
import com.clinica.dao.ObraSocialDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class ObraSocialDaoFactory {
// Retorna la interfaz IMedicamentoDao ocultando la implementación concreta
	public static IObraSocialDao getObraSocialDao() {
		return new ObraSocialDaoImpl();
	}
}