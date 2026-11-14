package com.clinica.factory;

import com.clinica.dao.AuditoriaClinicaDaoImpl;
import com.clinica.dao.IAuditoriaClinicaDao;

public class AuditoriaClinicaDaoFactory {

    public static IAuditoriaClinicaDao getAuditoriaClinicaDao() {
        return new AuditoriaClinicaDaoImpl();
    }
}
