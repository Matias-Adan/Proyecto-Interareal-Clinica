package com.clinica.factory;

import com.clinica.dao.IMovimientoStockDao;
import com.clinica.dao.MovimientoStockDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class MovimientoStockDaoFactory {
// Retorna la interfaz IMedicamentoDao ocultando la implementación concreta
	public static IMovimientoStockDao getMovimientoStockDao() {
		return new MovimientoStockDaoImpl();
	}
}