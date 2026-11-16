package com.clinica.modelo;

/**
 * Artículo de la vista del administrador, con la marca de stock bajo.
 */
public class ArticuloAdmin {

    private int idArticulo;
    private String nombre;
    private String descripcion;
    private String presentacion;
    private String tipoArticulo;
    private int stockActual;
    private int stockMinimo;
    private boolean stockBajo;
    private int cantidadAReponer;

    public ArticuloAdmin() {
    }

    public ArticuloAdmin(int idArticulo, String nombre, String descripcion, String presentacion,
            String tipoArticulo, int stockActual, int stockMinimo, boolean stockBajo,
            int cantidadAReponer) {
        this.idArticulo = idArticulo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.presentacion = presentacion;
        this.tipoArticulo = tipoArticulo;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.stockBajo = stockBajo;
        this.cantidadAReponer = cantidadAReponer;
    }

    public int getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(int idArticulo) {
        this.idArticulo = idArticulo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public String getTipoArticulo() {
        return tipoArticulo;
    }

    public void setTipoArticulo(String tipoArticulo) {
        this.tipoArticulo = tipoArticulo;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public boolean isStockBajo() {
        return stockBajo;
    }

    public void setStockBajo(boolean stockBajo) {
        this.stockBajo = stockBajo;
    }

    public int getCantidadAReponer() {
        return cantidadAReponer;
    }

    public void setCantidadAReponer(int cantidadAReponer) {
        this.cantidadAReponer = cantidadAReponer;
    }
}
