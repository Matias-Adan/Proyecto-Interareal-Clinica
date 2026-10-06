package com.clinica.controller;

import com.clinica.dao.ArticulosDaoImpl;
import com.clinica.dao.IArticulosDao;
import com.clinica.modelo.Articulos;
import java.util.List;
import java.util.Scanner;

public class ArticulosController {

    private final IArticulosDao articuloDao;
    private final Scanner scanner;

    public ArticulosController() {
        this.articuloDao = new ArticulosDaoImpl();
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n=== MENU DE PRUEBAS: ARTICULOS ===");
            System.out.println("1. Listar todos los artículos");
            System.out.println("2. Buscar artículo por ID");
            System.out.println("3. Insertar nuevo artículo");
            System.out.println("4. Actualizar un artículo");
            System.out.println("5. Eliminar un artículo");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            
            while (!scanner.hasNextInt()) {
                System.out.print("Por favor, ingrese un número válido: ");
                scanner.next();
            }
            opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer

            switch (opcion) {
                case 1 -> testListar();
                case 2 -> testBuscarPorId();
                case 3 -> testInsertar();
                case 4 -> testActualizar();
                case 5 -> testEliminar();
                case 0 -> System.out.println("👋 Saliendo del sistema de pruebas...");
                default -> System.out.println("⚠️ Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void testListar() {
        System.out.println("\n--- Ejecutando sp_listar_articulos() ---");
        List<Articulos> lista = articuloDao.listar();
        if (lista.isEmpty()) {
            System.out.println("No se encontraron artículos en la base de datos.");
        } else {
            lista.forEach(a -> System.out.printf("ID: %d | %s | Pres: %s | Tipo: %s | Stock: %d/%d\n",
                    a.getIdArticulo(), a.getNombre(), a.getPresentacion(), a.getTipoArticulo(), a.getStockActual(), a.getStockMinimo()));
        }
    }

    private void testBuscarPorId() {
        System.out.println("\n--- Ejecutando sp_buscar_articulo(?) ---");
        System.out.print("Ingrese el ID del artículo a buscar: ");
        int id = scanner.nextInt();
        
        Articulos a = articuloDao.buscarPorId(id);
        if (a != null) {
            System.out.println("✨ Artículo encontrado:");
            System.out.println("Nombre: " + a.getNombre());
            System.out.println("Descripción: " + a.getDescripcion());
            System.out.println("Presentación: " + a.getPresentacion());
            System.out.println("Tipo: " + a.getTipoArticulo());
            System.out.println("Stock Actual: " + a.getStockActual());
            System.out.println("Stock Mínimo: " + a.getStockMinimo());
        } else {
            System.out.println("❌ No se encontró ningún artículo con el ID: " + id);
        }
    }

    private void testInsertar() {
        System.out.println("\n--- Ejecutando sp_insertar_articulo(6 params) ---");
        System.out.print("Nombre: "); String nombre = scanner.nextLine();
        System.out.print("Presentación (ej: Blíster x10, Frasco): "); String pres = scanner.nextLine();
        System.out.print("Tipo de Artículo: "); String tipo = scanner.nextLine();
        System.out.print("Descripción: "); String desc = scanner.nextLine();
        System.out.print("Stock Actual: "); int stockAct = scanner.nextInt();
        System.out.print("Stock Mínimo: "); int stockMin = scanner.nextInt();

        // Enviamos 0 en el ID ya que la base de datos debería autogenerarlo (Auto_increment / Identity)
        Articulos nuevo = new Articulos(0, nombre, pres, tipo, desc, stockAct, stockMin);
        
        if (articuloDao.insertar(nuevo)) {
            System.out.println("✅ Artículo insertado correctamente.");
        } else {
            System.out.println("❌ Error al intentar insertar el artículo.");
        }
    }

    private void testActualizar() {
        System.out.println("\n--- Ejecutando sp_actualizar_articulo(7 params) ---");
        System.out.print("Ingrese el ID del artículo que desea modificar: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        // Primero verificamos si existe
        Articulos existente = articuloDao.buscarPorId(id);
        if (existente == null) {
            System.out.println("❌ No existe un artículo con ese ID para actualizar.");
            return;
        }

        System.out.print("Nuevo Nombre (" + existente.getNombre() + "): "); String nombre = scanner.nextLine();
        System.out.print("Nueva Presentación (" + existente.getPresentacion() + "): "); String pres = scanner.nextLine();
        System.out.print("Nuevo Tipo (" + existente.getTipoArticulo() + "): "); String tipo = scanner.nextLine();
        System.out.print("Nueva Descripción (" + existente.getDescripcion() + "): "); String desc = scanner.nextLine();
        System.out.print("Nuevo Stock Actual (" + existente.getStockActual() + "): "); int stockAct = scanner.nextInt();
        System.out.print("Nuevo Stock Mínimo (" + existente.getStockMinimo() + "): "); int stockMin = scanner.nextInt();

        Articulos actualizado = new Articulos(id, nombre, pres, tipo, desc, stockAct, stockMin);
        
        if (articuloDao.actualizar(actualizado)) {
            System.out.println("✅ Artículo actualizado correctamente.");
        } else {
            System.out.println("❌ Error al intentar actualizar el artículo.");
        }
    }

    private void testEliminar() {
        System.out.println("\n--- Ejecutando sp_eliminar_articulo(?) ---");
        System.out.print("Ingrese el ID del artículo a eliminar: ");
        int id = scanner.nextInt();

        if (articuloDao.eliminar(id)) {
            System.out.println("✅ Artículo eliminado con éxito.");
        } else {
            System.out.println("❌ No se pudo eliminar el artículo. Verifique si el ID existe.");
        }
    }

    // Método main para ejecutar directamente las pruebas
    public static void main(String[] args) {
        ArticulosController controller = new ArticulosController();
        controller.iniciar();
    }
}
