package com.clinica.factory;

import com.clinica.dao.ITurnoDao;
import com.clinica.dao.TurnoDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class TurnoDaoFactory {
// Retorna la interfaz IMedicamentoDao ocultando la implementación concreta
	public static ITurnoDao getTurnoDao() {
		return new TurnoDaoImpl();
	}
}