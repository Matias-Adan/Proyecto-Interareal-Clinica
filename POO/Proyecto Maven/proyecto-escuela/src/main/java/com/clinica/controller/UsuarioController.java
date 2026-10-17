package com.clinica.controller;

import com.clinica.dao.IUsuarioDao;
import com.clinica.dao.UsuarioDaoImpl;
import com.clinica.modelo.Usuario;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class UsuarioController {

    private final IUsuarioDao usuarioDao;
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter;

    public UsuarioController() {
        this.usuarioDao = new UsuarioDaoImpl();
        this.scanner = new Scanner(System.in);
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n=== MENU DE PRUEBAS: USUARIOS ===");
            System.out.println("1. Listar todos los usuarios");
            System.out.println("2. Buscar usuario por ID");
            System.out.println("3. Insertar nuevo usuario");
            System.out.println("4. Actualizar un usuario");
            System.out.println("5. Eliminar un usuario");
            System.out.println("6. Probar inicio de sesión (Login)");
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
                case 6 -> testLogin();
                case 0 -> System.out.println("👋 Saliendo del sistema de pruebas de usuarios...");
                default -> System.out.println("⚠️ Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void testListar() {
        System.out.println("\n--- Ejecutando listar usuarios ---");
        List<Usuario> lista = usuarioDao.listar();
        if (lista.isEmpty()) {
            System.out.println("No se encontraron usuarios en la base de datos.");
        } else {
            lista.forEach(u -> System.out.printf("ID: %d | %s %s | DNI: %d | Email: %s | Obra Social ID: %d | Borrado: %b\n",
                    u.getIdUsuario(), u.getNombre(), u.getApellido(), u.getDni(), u.getEmail(), u.getObraSocialId(), u.isFlagBorrado()));
        }
    }

    private void testBuscarPorId() {
        System.out.println("\n--- Ejecutando sp_usuarios_listar(?) ---");
        System.out.print("Ingrese el ID del usuario a buscar: ");
        int id = scanner.nextInt();
        
        Usuario u = usuarioDao.buscarPorId(id);
        if (u != null) {
            System.out.println("✨ Usuario encontrado:");
            System.out.println("Nombre y Apellido: " + u.getNombre() + " " + u.getApellido());
            System.out.println("DNI: " + u.getDni());
            System.out.println("Fecha de Nacimiento: " + u.getFechaNacimiento());
            System.out.println("Teléfono: " + u.getTelefono());
            System.out.println("Email: " + u.getEmail());
            System.out.println("Obra Social ID: " + u.getObraSocialId());
            System.out.println("Flag Borrado: " + u.isFlagBorrado());
        } else {
            System.out.println("❌ No se encontró ningún usuario con el ID: " + id);
        }
    }

    private void testInsertar() {
        System.out.println("\n--- Ejecutando sp_usuarios_insertar(...) ---");
        System.out.print("Nombre: "); String nombre = scanner.nextLine();
        System.out.print("Apellido: "); String apellido = scanner.nextLine();
        System.out.print("DNI: "); int dni = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer
        
        LocalDate fechaNac = leerFecha("Fecha de Nacimiento (YYYY-MM-DD): ");

        System.out.print("Teléfono: "); int telefono = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer
        System.out.print("Email: "); String email = scanner.nextLine();
        System.out.print("Password: "); String password = scanner.nextLine();
        System.out.print("ID de Obra Social: "); int obraSocialId = scanner.nextInt();
        System.out.print("¿Está borrado? (true/false): "); boolean flagBorrado = scanner.nextBoolean();
        scanner.nextLine(); // Limpiar buffer

        // ID en 0 para autoincrementable
        Usuario nuevo = new Usuario(0, nombre, apellido, dni, fechaNac, telefono, email, password, obraSocialId, flagBorrado);
        
        if (usuarioDao.insertar(nuevo)) {
            System.out.println("✅ Usuario insertado correctamente.");
        } else {
            System.out.println("❌ Error al intentar insertar el usuario.");
        }
    }

    private void testActualizar() {
        System.out.println("\n--- Ejecutando sp_usuarios_actualizar(...) ---");
        System.out.print("Ingrese el ID del usuario que desea modificar: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        Usuario existente = usuarioDao.buscarPorId(id);
        if (existente == null) {
            System.out.println("❌ No existe un usuario con ese ID para actualizar.");
            return;
        }

        System.out.print("Nuevo Nombre (" + existente.getNombre() + "): "); String nombre = scanner.nextLine();
        System.out.print("Nuevo Apellido (" + existente.getApellido() + "): "); String apellido = scanner.nextLine();
        System.out.print("Nuevo DNI (" + existente.getDni() + "): "); int dni = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        LocalDate fechaNac = leerFecha("Nueva Fecha de Nacimiento (" + existente.getFechaNacimiento() + ") [YYYY-MM-DD]: ");

        System.out.print("Nuevo Teléfono (" + existente.getTelefono() + "): "); int telefono = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer
        System.out.print("Nuevo Email (" + existente.getEmail() + "): "); String email = scanner.nextLine();
        System.out.print("Nuevo Password: "); String password = scanner.nextLine();
        System.out.print("Nuevo ID de Obra Social (" + existente.getObraSocialId() + "): "); int obraSocialId = scanner.nextInt();
        System.out.print("¿Flag Borrado (" + existente.isFlagBorrado() + ") [true/false]: "); boolean flagBorrado = scanner.nextBoolean();
        scanner.nextLine(); // Limpiar buffer

        Usuario actualizado = new Usuario(id, nombre, apellido, dni, fechaNac, telefono, email, password, obraSocialId, flagBorrado);
        
        if (usuarioDao.actualizar(actualizado)) {
            System.out.println("✅ Usuario actualizado correctamente.");
        } else {
            System.out.println("❌ Error al intentar actualizar el usuario.");
        }
    }

    private void testEliminar() {
        System.out.println("\n--- Ejecutando sp_usuarios_eliminar(?) ---");
        System.out.print("Ingrese el ID del usuario a eliminar: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        if (usuarioDao.eliminar(id)) {
            System.out.println("✅ Usuario eliminado con éxito.");
        } else {
            System.out.println("❌ No se pudo eliminar el usuario. Verifique si el ID existe.");
        }
    }

    private void testLogin() {
        System.out.println("\n--- Ejecutando sp_usuarios_login(?) ---");
        System.out.print("Ingrese el email o usuario para iniciar sesión: ");
        String credencial = scanner.nextLine();

        Usuario u = usuarioDao.login(credencial);
        if (u != null) {
            System.out.println("🎉 ¡Login exitoso! Bienvenido/a, " + u.getNombre() + " " + u.getApellido());
        } else {
            System.out.println("❌ Credenciales incorrectas o usuario no encontrado.");
        }
    }

    // Auxiliar para leer fechas de forma segura evitando fallos de formato
    private LocalDate leerFecha(String mensaje) {
        LocalDate fecha = null;
        while (fecha == null) {
            System.out.print(mensaje);
            String input = scanner.nextLine();
            try {
                fecha = LocalDate.parse(input, dateFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("⚠️ Formato de fecha incorrecto. Use el formato YYYY-MM-DD.");
            }
        }
        return fecha;
    }

    // Método main para ejecutar directamente las pruebas
    public static void main(String[] args) {
        UsuarioController controller = new UsuarioController();
        controller.iniciar();
    }
}