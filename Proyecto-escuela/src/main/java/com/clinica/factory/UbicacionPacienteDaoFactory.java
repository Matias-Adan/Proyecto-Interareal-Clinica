package com.clinica.factory;

import com.clinica.dao.IUbicacionPacienteDao;
import com.clinica.dao.UbicacionPacienteDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class UbicacionPacienteDaoFactory {
// Retorna la interfaz IMedicamentoDao ocultando la implementación concreta
	public static IUbicacionPacienteDao getUbicacionPacienteDao() {
		return new UbicacionPacienteDaoImpl();
	}
}