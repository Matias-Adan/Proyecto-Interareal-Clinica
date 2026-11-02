package com.clinica.factory;

import com.clinica.dao.IPermisoRolDao;
import com.clinica.dao.PermisoRolDaoImpl;

public class PermisoRolDaoFactory {
    
    public static IPermisoRolDao getPermisoRolDao() {
        return new PermisoRolDaoImpl();
    }
}