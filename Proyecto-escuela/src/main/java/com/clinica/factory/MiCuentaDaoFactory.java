package com.clinica.factory;

import com.clinica.dao.IMiCuentaDao;
import com.clinica.dao.MiCuentaDaoImpl;

public class MiCuentaDaoFactory {

    private MiCuentaDaoFactory() {
    }

    public static IMiCuentaDao getMiCuentaDao() {
        return new MiCuentaDaoImpl();
    }
}
