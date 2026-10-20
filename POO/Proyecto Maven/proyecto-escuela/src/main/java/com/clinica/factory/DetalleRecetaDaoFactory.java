package com.clinica.factory;

import com.clinica.dao.IDetalleRecetaDao;
import com.clinica.dao.DetalleRecetaDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class DetalleRecetaDaoFactory {
// Retorna la interfaz IMedicamentoDao ocultando la implementación concreta
	public static IDetalleRecetaDao getDetalleRecetaDao() {
		return new DetalleRecetaDaoImpl();
	}
}