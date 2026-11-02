package com.clinica.service;

import com.clinica.dao.ConsultaDaoImpl;
import com.clinica.dao.DetalleRecetaDaoImpl;
import com.clinica.dao.RecetaDaoImpl;
import com.clinica.dao.RolDeUsuarioDaoImpl; 
import com.clinica.modelo.Consulta;
import com.clinica.modelo.RolDeUsuario;
import com.clinica.modelo.Receta;
import com.clinica.modelo.DetalleReceta;
import java.time.LocalDateTime;
import java.util.List;

public class AtencionMedicaService {
	
	private ConsultaDaoImpl consultaDao = new ConsultaDaoImpl();
	private RolDeUsuarioDaoImpl rolDao = new RolDeUsuarioDaoImpl();
	
	public boolean registrarConsulta(Consulta consulta, Receta receta, List<DetalleReceta> detallesReceta,  int idUsuarioLogueado) {
		
		List<RolDeUsuario> rolesAsignados = rolDao.listar();
		boolean esMedico = false;
		boolean esPacienteValido = false;
		
		for(RolDeUsuario ru : rolesAsignados) {
			if(ru.getUsuarioId() == idUsuarioLogueado && ru.getRolId() == 2) {
				esMedico = true;
			}
			
			if(ru.getUsuarioId() == idUsuarioLogueado && ru.getRolId() == 5) {
				esPacienteValido = true;
			}
		}
		
		if (!esMedico) {
			throw new SecurityException
			("Acceso denegado: No cuenta con los roles necesarios.");
		}
		
		if (!esPacienteValido) {
			throw new SecurityException
			("Acceso denegado: No cuenta con los roles necesarios");
		}
		
		if(consulta.getMotivo() == null || consulta.getMotivo().trim().isEmpty()) {
			throw new IllegalArgumentException
			("El motivo de la consulta es obligatorio.");
		}
		
		if (consulta.getDiagnostico() == null || consulta.getDiagnostico().trim().isEmpty()) {
            throw new IllegalArgumentException
            ("El diagnóstico es obligatorio.");
		}
		
		if(consulta.getFecha() == null) {
			consulta.setFecha(LocalDateTime.now());
		}
		
		if(receta != null) {
			if(detallesReceta == null || detallesReceta.isEmpty()) {
				throw new IllegalArgumentException
				("La receta debe tener al menos un detalle.");
			}
			
			for(DetalleReceta detalle : detallesReceta) {
				if(detalle.getCantidad() <=0 ) {
					throw new IllegalArgumentException
					("La cantidad en cada detalle de la receta debe ser mayor a cero.");
				}
			}
		}
		
		boolean resultadoConsulta = consultaDao.insertar(consulta);          
		
		if(resultadoConsulta && receta != null) {
			RecetaDaoImpl recetaDao = new RecetaDaoImpl();
			
			boolean resultadoReceta = recetaDao.insertar(receta);
			
			if(resultadoReceta  && detallesReceta != null) {
				DetalleRecetaDaoImpl detalleDao = new DetalleRecetaDaoImpl();
				for(DetalleReceta detalle: detallesReceta) {
					detalleDao.insertar(detalle);
				}	
			}
		}
		
		return resultadoConsulta;
	}
}
