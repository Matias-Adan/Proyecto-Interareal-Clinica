package com.clinica.factory;

import com.clinica.dao.IAlergiaPacienteDao;
import com.clinica.dao.AlergiaPacienteDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class AlergiaPacienteDaoFactory {
// Retorna la interfaz IMedicamentoDao ocultando la implementación concreta
	public static IAlergiaPacienteDao getAlergiaPacienteDao() {
		return new AlergiaPacienteDaoImpl();
	}
}