package com.clinica.factory;

import com.clinica.dao.IConsultaDao;
import com.clinica.dao.ConsultaDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class ConsultaDaoFactory {
// Retorna la interfaz IMedicamentoDao ocultando la implementación concreta
	public static IConsultaDao getConsultaDao() {
		return new ConsultaDaoImpl();
	}
}