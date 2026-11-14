package com.clinica.factory;

import com.clinica.dao.IVistaTurnosDao;
import com.clinica.dao.VistaTurnosDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class VistaTurnosDaoFactory {
// Retorna la interfaz IMedicamentoDao ocultando la implementación concreta
	public static IVistaTurnosDao getVistaTurnosDao() {
		return new VistaTurnosDaoImpl();
	}
}
