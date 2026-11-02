package com.clinica.factory;

import com.clinica.dao.ITriageDao;
import com.clinica.dao.TriageDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class TriageDaoFactory {
// Retorna la interfaz IMedicamentoDao ocultando la implementación concreta
	public static ITriageDao getTriageDao() {
		return new TriageDaoImpl();
	}
}