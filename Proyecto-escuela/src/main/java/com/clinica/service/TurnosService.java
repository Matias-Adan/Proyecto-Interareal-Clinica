package com.clinica.service;

import com.clinica.dao.TurnoDaoImpl;
import com.clinica.modelo.Turno;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TurnosService {
	
	private TurnoDaoImpl turnoDao = new TurnoDaoImpl();
	
	public boolean solicitarTurno(Turno turno) {
		
		LocalDate fechaActual = LocalDate.now();
		LocalTime horaActual = LocalTime.now();
		
		if(turno.getFecha() == null) {
			throw new IllegalArgumentException
			("La fecha del turno no puede ser nula.");
		}
		
		if(turno.getHoraInicio() == null) {
			throw new IllegalArgumentException
			("La hora del turno no puede ser nula.");
		}
		
		if(turno.getFecha().isBefore(fechaActual)) {
			throw new IllegalArgumentException
			("No se puede agendar un turno para una fecha pasada.");
		}
		
		if(turno.getFecha().isEqual(fechaActual) && turno.getHoraInicio().isBefore(horaActual)) {
			throw new IllegalArgumentException
			("La hora de fin no puede ser anterior a la hora de inicio.");
		}
		
		if(turno.getHoraFin() != null && turno.getHoraFin().isBefore(turno.getHoraInicio())) {
			throw new IllegalArgumentException
			("Fecha invalida");
		}
		
		List<Turno> turnosExistentes = turnoDao.listar();{
		
			for (Turno t : turnosExistentes) {
				
				if("Cancelado".equalsIgnoreCase(t.getEstado())){
					continue;
				}
				
				boolean mismoDiaYHora = t.getFecha().isEqual(turno.getFecha()) && t.getHoraInicio().equals(turno.getHoraInicio());
				
				if(mismoDiaYHora) {
					if(t.getMedicoId() == turno.getMedicoId()) {
						throw new IllegalArgumentException
						("El medico ya tiene un turno asignado en ese horario.");
		                        
					}
				}
				
				if(t.getPacienteId() == turno.getPacienteId()) {
					throw new IllegalArgumentException
					("El paciente ya tiene un turno asignado en ese horario.");
				}
			
			
			}
			
		}
		
		return turnoDao.insertar(turno);
	}
}
