package com.clinica.security;

import com.clinica.dao.DataAccessException;
import com.clinica.modelo.Usuario;
import com.clinica.service.AutorizacionService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Normalizer;

/**
 * Carga la identidad (usuario y/o rol) de la petición en el contexto de
 * seguridad y traduce las excepciones de negocio a códigos HTTP:
 *
 *   401 -> no se indicó usuario ni rol
 *   403 -> el rol/permiso no alcanza para la acción
 *   400 -> datos inválidos
 *   409 -> conflicto con una regla de negocio
 *   500 -> error de base de datos
 *   501 -> operación no permitida (por ejemplo, borrar datos clínicos)
 *
 * La identidad puede venir de la sesión (login normal) o, para poder probar
 * sin iniciar sesión, de estas cabeceras:
 *
 *   X-Usuario-Id : id del usuario que ejecuta la acción. El rol se resuelve
 *                  desde la base (tabla de roles del usuario).
 *   X-Rol-Id     : id de rol explícito (1 admin, 2 médico, 3 enfermero,
 *                  4 recepcionista, 5 paciente). No requiere usuario.
 *   X-Rol        : nombre del rol (admin, medico, enfermero, recepcionista,
 *                  paciente). Alternativa a X-Rol-Id.
 *
 * Si se envían cabeceras, tienen prioridad sobre la sesión. Así se puede
 * simular cualquier rol desde Postman.
 */
@WebFilter(urlPatterns = "/*")
public class ContextoUsuarioFilter implements Filter {

    public static final String CABECERA_USUARIO = "X-Usuario-Id";
    public static final String CABECERA_ROL = "X-Rol-Id";
    public static final String CABECERA_ROL_NOMBRE = "X-Rol";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest peticion = (HttpServletRequest) request;
        HttpServletResponse respuesta = (HttpServletResponse) response;

        Integer idHeader = leerEntero(peticion.getHeader(CABECERA_USUARIO));
        Integer rolHeader = leerEntero(peticion.getHeader(CABECERA_ROL));
        if (rolHeader == null) {
            rolHeader = leerRolPorNombre(peticion.getHeader(CABECERA_ROL_NOMBRE));
        }

        Integer idUsuario = null;
        HttpSession sesion = peticion.getSession(false);
        if (sesion != null) {
            Object atributo = sesion.getAttribute("usuarioLogueado");
            if (atributo instanceof Usuario) {
                idUsuario = ((Usuario) atributo).getIdUsuario();
            }
        }

        // Las cabeceras permiten identificar la petición sin login.
        if (idHeader != null) {
            idUsuario = idHeader;
        }

        UsuarioActual.set(idUsuario, rolHeader);

        try {
            String ruta = peticion.getServletPath();
            if (peticion.getPathInfo() != null) {
                ruta += peticion.getPathInfo();
            }

            PoliticaAcceso.exigir(ruta, peticion.getMethod());
            chain.doFilter(request, response);

        } catch (NoAutenticadoException e) {
            escribirError(respuesta, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (SecurityException e) {
            escribirError(respuesta, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            escribirError(respuesta, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            escribirError(respuesta, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (UnsupportedOperationException e) {
            escribirError(respuesta, HttpServletResponse.SC_NOT_IMPLEMENTED, e.getMessage());
        } catch (DataAccessException e) {
            escribirError(respuesta, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            UsuarioActual.limpiar();
        }
    }

    @Override
    public void destroy() {
    }

    private Integer leerEntero(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer leerRolPorNombre(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }

        Integer numero = leerEntero(valor);
        if (numero != null) {
            return numero;
        }

        switch (normalizar(valor)) {
            case "admin":
            case "administrador":
            case "administradora":
                return AutorizacionService.ADMINISTRADOR;
            case "medico":
            case "medica":
            case "doctor":
            case "doctora":
                return AutorizacionService.MEDICO;
            case "enfermero":
            case "enfermera":
            case "enfermeria":
                return AutorizacionService.ENFERMERO;
            case "recepcionista":
            case "recepcion":
                return AutorizacionService.RECEPCIONISTA;
            case "paciente":
                return AutorizacionService.PACIENTE;
            default:
                return null;
        }
    }

    private String normalizar(String texto) {
        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return sinAcentos.trim().toLowerCase();
    }

    private void escribirError(HttpServletResponse respuesta, int estado, String mensaje)
            throws IOException {
        if (respuesta.isCommitted()) {
            return;
        }
        respuesta.reset();
        respuesta.setStatus(estado);
        respuesta.setContentType("application/json;charset=UTF-8");
        PrintWriter out = respuesta.getWriter();
        out.print("{\"error\": \"" + escapar(mensaje) + "\"}");
        out.flush();
    }

    private String escapar(String texto) {
        if (texto == null) {
            return "Error";
        }
        return texto.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", " ")
                .replace("\n", " ");
    }
}
