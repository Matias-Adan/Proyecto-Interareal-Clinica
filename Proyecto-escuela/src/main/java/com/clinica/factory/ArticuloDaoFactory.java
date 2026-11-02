package com.clinica.factory;

import com.clinica.dao.IArticuloDao;
import com.clinica.dao.ArticuloDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class ArticuloDaoFactory {
// Retorna la interfaz IMedicamentoDao ocultando la implementación concreta
	public static IArticuloDao getArticuloDao() {
		return new ArticuloDaoImpl();
	}
}