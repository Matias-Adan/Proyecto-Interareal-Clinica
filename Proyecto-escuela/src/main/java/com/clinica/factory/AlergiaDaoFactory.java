package com.clinica.factory;

import com.clinica.dao.IAlergiaDao;
import com.clinica.dao.AlergiaDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class AlergiaDaoFactory {
// Retorna la interfaz IMedicamentoDao ocultando la implementación concreta
	public static IAlergiaDao getAlergiaDao() {
		return new AlergiaDaoImpl();
	}
}