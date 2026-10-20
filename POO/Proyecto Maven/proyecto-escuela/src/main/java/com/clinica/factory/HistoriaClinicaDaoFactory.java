package com.clinica.factory;

import com.clinica.dao.IHistoriaClinicaDao;
import com.clinica.dao.HistoriaClinicaDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class HistoriaClinicaDaoFactory {
// Retorna la interfaz IMedicamentoDao ocultando la implementación concreta
	public static IHistoriaClinicaDao getHistoriaClinicaDao() {
		return new HistoriaClinicaDaoImpl();
	}
}