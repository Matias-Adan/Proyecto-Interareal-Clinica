package com.clinica.factory;

import com.clinica.dao.IVistaAdminDao;
import com.clinica.dao.VistaAdminDaoImpl;

/**
 * Factoria para la centralización e instanciación de Objetos de Acceso a Datos
 * (DAO).
 */
public class VistaAdminDaoFactory {

    // Retorna la interfaz IVistaAdminDao ocultando la implementación concreta.
    public static IVistaAdminDao getVistaAdminDao() {
        return new VistaAdminDaoImpl();
    }
}
