package com.clinica.service;

import com.clinica.dao.FacturaDaoImpl;
import com.clinica.modelo.Factura;

public class FacturacionService {
	
	private FacturaDaoImpl facturaDao = new FacturaDaoImpl();
		
		public boolean crearFactura(Factura factura) {
			factura.setEstado("Pendiente");
			return facturaDao.insertar(factura);
		}
		
	public boolean anularFactura(int facturaId) {
		Factura factura = facturaDao.buscarPorId(facturaId);
		
		if(factura == null) {
			throw new IllegalArgumentException
			("La factura no existe.");
		}
		
		if ("Pagada".equalsIgnoreCase(factura.getEstado())) {
			throw new IllegalStateException
			("Accion denegada.");
			
		}
		
		factura.setEstado("Anulada");
		return facturaDao.actualizar(factura);
	}
	
	public boolean registrarPago(int facturaId) {
		Factura factura = facturaDao.buscarPorId(facturaId);
		
		if (factura == null) {
			throw new IllegalArgumentException
			("La factura no existe.");
		}
		
		if("Pagada".equalsIgnoreCase(factura.getEstado())) {
			throw new IllegalStateException
			("La factura ya esta pagada");
		}
		
		factura.setEstado("Pagada");
		return facturaDao.actualizar(factura);
	}
	
}

		

			
	
	

