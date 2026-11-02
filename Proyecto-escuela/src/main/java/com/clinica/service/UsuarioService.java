package com.clinica.service;

import com.clinica.dao.UsuarioDaoImpl;
import com.clinica.dao.RolDeUsuarioDaoImpl;
import com.clinica.dao.RolDaoImpl;
import com.clinica.modelo.Rol;
import com.clinica.modelo.RolDeUsuario;
import com.clinica.modelo.Usuario;

import java.time.LocalDate;
import java.util.List;

public class UsuarioService {

    private UsuarioDaoImpl usuarioDao = new UsuarioDaoImpl();
    private RolDeUsuarioDaoImpl rolDelUsuarioDao = new RolDeUsuarioDaoImpl();
    private RolDaoImpl rolDao = new RolDaoImpl();

    public boolean registrarUsuario(Usuario usuario) {
        // Regla: El DNI debe tener 7 u 8 dígitos
        String dniStr = String.valueOf(usuario.getDni());
        if (!dniStr.matches("\\d{7,8}")) {
            throw new IllegalArgumentException
            ("El DNI debe tener 7 u 8 dígitos.");
        }

        // Regla: El email debe tener formato válido
        if (usuario.getEmail() == null || !usuario.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException
            ("El formato del email no es válido.");
        }

        // Regla: La fecha de nacimiento debe ser anterior a la fecha actual
        if (usuario.getFechaNacimiento() == null || usuario.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException
            ("La fecha de nacimiento debe ser anterior a la fecha actual.");
        }

        // Regla: Verificar unicidad de DNI y email consultando la base de datos
        List<Usuario> usuarios = usuarioDao.listar();
        for (Usuario u : usuarios) {
            if (u.getDni() == usuario.getDni()) {
                throw new IllegalArgumentException
                ("El DNI ya se encuentra registrado.");
            }
            if (u.getEmail().equalsIgnoreCase(usuario.getEmail())) {
                throw new IllegalArgumentException
                ("El Email ya se encuentra registrado.");
            }
        }

        return usuarioDao.insertar(usuario);
    }

    public boolean asignarRolAUsuario(int idUsuario, int idRol) {
        // Verificamos que el rol exista en la base de datos usando el RolDaoImpl
        Rol rol = rolDao.buscarPorId(idRol);
        if (rol == null) {
            throw new IllegalArgumentException
            ("El rol que intentas asignar no existe.");
        }

        // Creamos la instancia para la tabla intermedia (RolDeUsuario)
        RolDeUsuario rolDeUsuario = new RolDeUsuario();
        rolDeUsuario.setUsuarioId(idUsuario);
        rolDeUsuario.setRolId(idRol);

        // Insertamos la relación mediante el DAO correspondiente
        return rolDelUsuarioDao.insertar(rolDeUsuario);
    }
}