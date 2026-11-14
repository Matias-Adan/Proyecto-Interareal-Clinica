package com.clinica.dao;

import com.clinica.modelo.AuditoriaClinica;
import java.util.List;

public interface IAuditoriaClinicaDao {

    boolean registrar(AuditoriaClinica auditoria);

    List<AuditoriaClinica> listar();
}
