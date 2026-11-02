package com.clinica.service;

import com.clinica.dao.ConsultorioDaoImpl;
import com.clinica.dao.UbicacionPacienteDaoImpl;
import com.clinica.modelo.Consultorio;
import com.clinica.modelo.UbicacionPaciente;
import java.util.List;

public class ConsultorioService {
	
	private ConsultorioDaoImpl consultorioDao = new ConsultorioDaoImpl();
	private UbicacionPacienteDaoImpl ubicacionDao = new UbicacionPacienteDaoImpl();
	
	public boolean asignarConsultorio(UbicacionPaciente nuevaUbicacion) {
		
		Consultorio consultorio = consultorioDao.buscarPorId(nuevaUbicacion.getConsultorioId());
		
		if (consultorio == null) {
			throw new IllegalArgumentException
			("El consultorio no existe.");
		}
		
		if ("Ocupado".equalsIgnoreCase(consultorio.getEstado())) {
			throw new IllegalStateException
			("El consultorio esta siendo ocupado.");
		}
		
		List<UbicacionPaciente> ubicaciones = ubicacionDao.listar();
		for (UbicacionPaciente up : ubicaciones) {
			if (up.getFechaSalida() == null) {
				if (up.getPacienteId() == nuevaUbicacion.getPacienteId()) {
					throw new IllegalStateException
					("El paciente ya se encuentra en un consultorio.");
				}
			}
		}
		
		boolean asignado = ubicacionDao.insertar(nuevaUbicacion);
		
		if (asignado) {
			consultorio.setEstado("Ocupado");
			consultorioDao.actualizar(consultorio);
		}
		
		return asignado;
		
	}
	
	public boolean actualizarUbicacion(UbicacionPaciente ubicacionActualizada) {
		boolean actualizado = ubicacionDao.actualizar(ubicacionActualizada);
		
		if (actualizado && ubicacionActualizada.getFechaSalida() != null) {
			Consultorio consultorio = consultorioDao.buscarPorId(ubicacionActualizada.getConsultorioId());
			if (consultorio != null) {
				consultorio.setEstado("Disponible");
				consultorioDao.actualizar(consultorio);
			}	
		}
		
		return actualizado;
	}
}
