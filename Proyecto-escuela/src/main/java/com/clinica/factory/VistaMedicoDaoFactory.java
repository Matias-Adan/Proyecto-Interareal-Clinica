package com.clinica.factory;

import com.clinica.dao.IVistaMedicoDao;
import com.clinica.dao.VistaMedicoDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class VistaMedicoDaoFactory {

    // Retorna la interfaz IVistaMedicoDao ocultando la implementación concreta.
    public static IVistaMedicoDao getVistaMedicoDao() {
        return new VistaMedicoDaoImpl();
    }
}
