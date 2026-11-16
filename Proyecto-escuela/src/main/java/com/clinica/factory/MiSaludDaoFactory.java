package com.clinica.factory;

import com.clinica.dao.IMiSaludDao;
import com.clinica.dao.MiSaludDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class MiSaludDaoFactory {
    // Retorna la interfaz IMiSaludDao ocultando la implementación concreta.
    public static IMiSaludDao getMiSaludDao() {
        return new MiSaludDaoImpl();
    }
}
