package com.clinica.factory;

import com.clinica.dao.IRecetaDao;
import com.clinica.dao.RecetaDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class RecetaDaoFactory {
// Retorna la interfaz IMedicamentoDao ocultando la implementación concreta
	public static IRecetaDao getRecetaDao() {
		return new RecetaDaoImpl();
	}
}