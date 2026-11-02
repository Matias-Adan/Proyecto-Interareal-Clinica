package com.clinica.factory;

import com.clinica.dao.IConsultorioDao;
import com.clinica.dao.ConsultorioDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class ConsultorioDaoFactory {
// Retorna la interfaz IMedicamentoDao ocultando la implementación concreta
	public static IConsultorioDao getConsultorioDao() {
		return new ConsultorioDaoImpl();
	}
}