package com.clinica.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Evita que el navegador use copias viejas del front-end (HTML/JS/CSS).
 *
 * Sin este filtro, Tomcat sirve los archivos estáticos sin cabeceras de
 * caché y el navegador los guarda "por heurística": mientras el archivo no
 * cambie puede seguir usando una copia vieja sin volver a preguntarle al
 * servidor. Eso hacía que una pestaña abierta desde antes siguiera entrando
 * a una vista que ya no le correspondía (el chequeo de sesión nunca corría
 * porque el navegador ni siquiera volvía a descargar el HTML o el JS).
 *
 * Con "no-cache" el navegador siempre revalida antes de usar su copia:
 * si el archivo no cambió, el servidor responde 304 (rápido); si cambió,
 * baja la versión nueva. Nunca más una pestaña corriendo código viejo.
 */
@WebFilter(urlPatterns = "/*")
public class CacheControlFilter implements Filter {

    @Override
    public void init(FilterConfig config) throws ServletException {
        // No requiere inicialización.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            HttpServletResponse respuesta = (HttpServletResponse) response;
            respuesta.setHeader("Cache-Control", "no-cache, must-revalidate");
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Nada que liberar.
    }
}
