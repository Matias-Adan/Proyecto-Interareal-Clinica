package com.clinica.factory;

import com.clinica.dao.IFacturaDao;
import com.clinica.dao.FacturaDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class FacturaDaoFactory {
// Retorna la interfaz IMedicamentoDao ocultando la implementación concreta
	public static IFacturaDao getFacturaDao() {
		return new FacturaDaoImpl();
	}
}