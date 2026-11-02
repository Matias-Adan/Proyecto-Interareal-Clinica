package com.clinica.service;

import com.clinica.dao.ArticuloDaoImpl;
import com.clinica.modelo.Articulo;
import com.clinica.modelo.DetalleReceta;
import java.util.List;

public class StockService {

	private ArticuloDaoImpl articuloDao = new ArticuloDaoImpl();

	public void validarStock(List<DetalleReceta> detalles) {
		if(detalles == null || detalles.isEmpty()){
			throw new IllegalArgumentException
			("La receta debe tener al menos un detalle.");
		}
		
		for(DetalleReceta detalle : detalles) {
			if (detalle.getCantidad() <= 0) {
				throw new IllegalArgumentException
				("La cantidad en cada detalle debe ser mayor a cero.");
			}
			
			Articulo articulo = articuloDao.buscarPorId(detalle.getArticuloId());
			
			if (articulo == null) {
				throw new IllegalStateException
				("El artículo no existe o no hay stock.");
			}
			
			if(articulo.getStockActual() < detalle.getCantidad()) {
				throw new IllegalStateException("Stock insuficiente");
			}					
		}				
	}
	
	public void descontarStock(List<DetalleReceta> detalles) {
		for(DetalleReceta detalle : detalles) {
			Articulo articulo = articuloDao.buscarPorId(detalle.getArticuloId());
			
			if(articulo != null) {
				int nuevoStock = articulo.getStockActual() - detalle.getCantidad();
				articulo.setStockActual(nuevoStock);
				articuloDao.actualizar(articulo);
			}
		}		
	}	
}
