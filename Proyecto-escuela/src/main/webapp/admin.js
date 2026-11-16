// Vista del administrador.
//
// Carga usuarios activos, roles y artículos desde GET /vista-admin y dibuja:
//   - la tabla de usuarios con sus roles y acciones de alta/baja de roles,
//   - la tabla de artículos con la alerta de stock bajo.
//
// Las escrituras reutilizan los endpoints existentes:
//   POST   /roles-usuario         { usuarioId, rolId }
//   DELETE /roles-usuario?id=...  (id de la asignación)
//   DELETE /usuarios?id=...       (baja lógica)
(function () {
    'use strict';

    const API_BASE = window.location.pathname.replace(/\/[^/]*$/, '');
    const CLAVE_USUARIO = 'usuarioLogueado';
    const ROL_ADMINISTRADOR = 1;

    const cuerpoUsuarios = document.getElementById('tablaUsuarios');
    const mensajeUsuarios = document.getElementById('usuariosMensaje');
    const buscadorUsuarios = document.getElementById('buscarUsuarios');

    const cuerpoArticulos = document.getElementById('tablaArticulos');
    const mensajeArticulos = document.getElementById('articulosMensaje');
    const buscadorArticulos = document.getElementById('buscarArticulos');
    const avisoStock = document.getElementById('avisoStock');
    const avisoStockTexto = document.getElementById('avisoStockTexto');

    let usuarioActualId = null;
    let usuarios = [];
    let roles = [];
    let articulos = [];

    // ------------------------------------------------------------------
    // Utilidades
    // ------------------------------------------------------------------

    function mostrarMensaje(elemento, texto, tipo) {
        if (!elemento) return;
        elemento.textContent = texto || '';
        elemento.classList.remove('error', 'ok');
        if (tipo) elemento.classList.add(tipo);
    }

    function textoODefecto(valor) {
        return valor == null || String(valor).trim() === '' ? '—' : String(valor);
    }

    function normalizar(valor) {
        return String(valor == null ? '' : valor).trim().toLowerCase();
    }

    async function leerJson(respuesta) {
        try {
            return await respuesta.json();
        } catch (error) {
            return null;
        }
    }

    function mensajeError(datos, porDefecto) {
        return datos && datos.error ? datos.error : porDefecto;
    }

    function usuarioActualGuardado() {
        try {
            const guardado = sessionStorage.getItem(CLAVE_USUARIO);
            if (!guardado) return null;
            const usuario = JSON.parse(guardado);
            return usuario && usuario.idUsuario ? Number(usuario.idUsuario) : null;
        } catch (error) {
            return null;
        }
    }

    function usuarioPorId(id) {
        return usuarios.find(usuario => Number(usuario.idUsuario) === Number(id)) || null;
    }

    function rolPorId(id) {
        return roles.find(rol => Number(rol.idRol) === Number(id)) || null;
    }

    function nombreUsuario(usuario) {
        if (!usuario) return 'el usuario';
        return usuario.nombreCompleto || (usuario.nombre + ' ' + usuario.apellido).trim();
    }

    function crearCelda(texto) {
        const celda = document.createElement('td');
        celda.textContent = textoODefecto(texto);
        return celda;
    }

    function crearCeldaNumero(valor) {
        const celda = crearCelda(valor);
        celda.className = 'num';
        return celda;
    }

    function mensajeVacio(cuerpo, columnas, texto) {
        cuerpo.innerHTML = '';
        const fila = document.createElement('tr');
        const celda = document.createElement('td');
        celda.colSpan = columnas;
        celda.textContent = texto;
        fila.appendChild(celda);
        cuerpo.appendChild(fila);
    }

    function crearBoton(texto, clase, accion, datos) {
        const boton = document.createElement('button');
        boton.type = 'button';
        boton.className = clase;
        boton.textContent = texto;
        boton.dataset.accion = accion;
        Object.keys(datos || {}).forEach(clave => {
            boton.dataset[clave] = String(datos[clave]);
        });
        return boton;
    }

    // ------------------------------------------------------------------
    // Usuarios y roles
    // ------------------------------------------------------------------

    function puedeQuitarRol(usuario, rol) {
        // El administrador no puede quitarse su propio rol Administrador:
        // perdería el acceso a esta vista.
        return !(usuario.idUsuario === usuarioActualId
            && Number(rol.idRol) === ROL_ADMINISTRADOR);
    }

    function crearCeldaRoles(usuario) {
        const celda = document.createElement('td');

        if (!Array.isArray(usuario.roles) || !usuario.roles.length) {
            celda.textContent = 'Sin rol';
            celda.className = 'muted';
            return celda;
        }

        usuario.roles.forEach(rol => {
            const etiqueta = document.createElement('span');
            etiqueta.className = 'tag rol';
            etiqueta.appendChild(document.createTextNode(
                rol.nombre || ('Rol ' + rol.idRol)));

            if (puedeQuitarRol(usuario, rol)) {
                const quitar = crearBoton('×', 'quitar-rol', 'quitar-rol', {
                    usuario: usuario.idUsuario,
                    idAsignacion: rol.idRolUsuario,
                    nombreRol: rol.nombre || ('Rol ' + rol.idRol)
                });
                quitar.title = 'Quitar el rol ' + (rol.nombre || rol.idRol);
                quitar.setAttribute('aria-label', 'Quitar el rol '
                    + (rol.nombre || rol.idRol) + ' a ' + nombreUsuario(usuario));
                etiqueta.appendChild(quitar);
            }

            celda.appendChild(etiqueta);
        });

        return celda;
    }

    function crearCeldaAsignar(usuario) {
        const celda = document.createElement('td');
        const asignados = Array.isArray(usuario.roles) ? usuario.roles : [];
        const disponibles = roles.filter(rol => !asignados.some(asignado =>
            Number(asignado.idRol) === Number(rol.idRol)));

        const contenedor = document.createElement('div');
        contenedor.className = 'asignar-rol';

        const select = document.createElement('select');
        select.className = 'rol-select';
        select.disabled = disponibles.length === 0;
        select.setAttribute('aria-label', 'Rol para ' + nombreUsuario(usuario));

        if (!disponibles.length) {
            const opcion = document.createElement('option');
            opcion.value = '';
            opcion.textContent = 'Sin roles disponibles';
            select.appendChild(opcion);
        } else {
            const inicial = document.createElement('option');
            inicial.value = '';
            inicial.textContent = 'Elegí un rol';
            select.appendChild(inicial);

            disponibles.forEach(rol => {
                const opcion = document.createElement('option');
                opcion.value = String(rol.idRol);
                opcion.textContent = rol.nombre;
                select.appendChild(opcion);
            });
        }

        const boton = crearBoton('Asignar', 'btn-mini', 'asignar-rol', {
            usuario: usuario.idUsuario
        });
        boton.disabled = disponibles.length === 0;

        contenedor.appendChild(select);
        contenedor.appendChild(boton);
        celda.appendChild(contenedor);

        return celda;
    }

    function crearCeldaAcciones(usuario) {
        const celda = document.createElement('td');

        if (usuario.idUsuario === usuarioActualId) {
            const etiqueta = document.createElement('span');
            etiqueta.className = 'tag';
            etiqueta.textContent = 'Tu cuenta';
            celda.appendChild(etiqueta);
            return celda;
        }

        const boton = crearBoton('Dar de baja', 'btn-mini alerta', 'baja-usuario', {
            usuario: usuario.idUsuario,
            nombre: nombreUsuario(usuario)
        });
        boton.title = 'Dar de baja a ' + nombreUsuario(usuario);
        celda.appendChild(boton);

        return celda;
    }

    function pintarUsuarios(lista, filtrado) {
        cuerpoUsuarios.innerHTML = '';

        if (!Array.isArray(lista) || !lista.length) {
            mensajeVacio(cuerpoUsuarios, 6, filtrado
                ? 'Ningún usuario coincide con la búsqueda.'
                : 'No hay usuarios activos.');
            return;
        }

        lista.forEach(usuario => {
            const fila = document.createElement('tr');

            const celdaNombre = crearCelda(usuario.nombreCompleto);
            if (usuario.email) celdaNombre.title = usuario.email;
            fila.appendChild(celdaNombre);

            fila.appendChild(crearCelda(usuario.dni));
            fila.appendChild(crearCelda(usuario.email));
            fila.appendChild(crearCeldaRoles(usuario));
            fila.appendChild(crearCeldaAsignar(usuario));
            fila.appendChild(crearCeldaAcciones(usuario));

            cuerpoUsuarios.appendChild(fila);
        });
    }

    function aplicarFiltroUsuarios() {
        const termino = normalizar(buscadorUsuarios ? buscadorUsuarios.value : '');
        const filtrados = termino
            ? usuarios.filter(usuario => [
                usuario.nombreCompleto,
                usuario.dni,
                usuario.email
            ].some(valor => normalizar(valor).indexOf(termino) !== -1))
            : usuarios;

        pintarUsuarios(filtrados, termino !== '');
    }

    async function asignarRol(usuarioId, rolId, boton) {
        const usuario = usuarioPorId(usuarioId);
        const rol = rolPorId(rolId);

        if (!usuario || !rol) {
            mostrarMensaje(mensajeUsuarios, 'No se pudo identificar el usuario o el rol.', 'error');
            return;
        }

        if (boton) boton.disabled = true;
        mostrarMensaje(mensajeUsuarios, 'Asignando rol...', null);

        try {
            const respuesta = await fetch(API_BASE + '/roles-usuario', {
                method: 'POST',
                credentials: 'same-origin',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json;charset=UTF-8'
                },
                body: JSON.stringify({
                    usuarioId: Number(usuarioId),
                    rolId: Number(rolId)
                })
            });

            const datos = await leerJson(respuesta);
            if (!respuesta.ok) {
                throw new Error(mensajeError(datos, 'No se pudo asignar el rol.'));
            }

            await cargarVista();
            mostrarMensaje(mensajeUsuarios,
                'Rol ' + rol.nombre + ' asignado a ' + nombreUsuario(usuario) + '.', 'ok');
        } catch (error) {
            console.error('Error al asignar el rol:', error);
            mostrarMensaje(mensajeUsuarios,
                error.message || 'No se pudo asignar el rol.', 'error');
            if (boton) boton.disabled = false;
        }
    }

    async function quitarRol(idAsignacion, usuarioId, nombreRol) {
        const usuario = usuarioPorId(usuarioId);

        if (!confirm('¿Quitar el rol "' + nombreRol + '" a '
                + nombreUsuario(usuario) + '?')) {
            return;
        }

        mostrarMensaje(mensajeUsuarios, 'Quitando rol...', null);

        try {
            const respuesta = await fetch(
                API_BASE + '/roles-usuario?id=' + encodeURIComponent(idAsignacion),
                {
                    method: 'DELETE',
                    credentials: 'same-origin',
                    headers: { 'Accept': 'application/json' }
                }
            );

            const datos = await leerJson(respuesta);
            if (!respuesta.ok) {
                throw new Error(mensajeError(datos, 'No se pudo quitar el rol.'));
            }

            await cargarVista();
            mostrarMensaje(mensajeUsuarios,
                'Rol ' + nombreRol + ' quitado a ' + nombreUsuario(usuario) + '.', 'ok');
        } catch (error) {
            console.error('Error al quitar el rol:', error);
            mostrarMensaje(mensajeUsuarios,
                error.message || 'No se pudo quitar el rol.', 'error');
        }
    }

    async function darDeBaja(usuarioId) {
        const usuario = usuarioPorId(usuarioId);

        if (!usuario) {
            mostrarMensaje(mensajeUsuarios, 'No se pudo identificar el usuario.', 'error');
            return;
        }

        if (usuario.idUsuario === usuarioActualId) {
            mostrarMensaje(mensajeUsuarios,
                'No podés dar de baja tu propia cuenta.', 'error');
            return;
        }

        if (!confirm('¿Dar de baja a ' + nombreUsuario(usuario)
                + '? El usuario dejará de aparecer en el listado.')) {
            return;
        }

        mostrarMensaje(mensajeUsuarios, 'Dando de baja al usuario...', null);

        try {
            const respuesta = await fetch(
                API_BASE + '/usuarios?id=' + encodeURIComponent(usuarioId),
                {
                    method: 'DELETE',
                    credentials: 'same-origin',
                    headers: { 'Accept': 'application/json' }
                }
            );

            const datos = await leerJson(respuesta);
            if (!respuesta.ok) {
                throw new Error(mensajeError(datos, 'No se pudo dar de baja al usuario.'));
            }

            const nombre = nombreUsuario(usuario);
            await cargarVista();
            mostrarMensaje(mensajeUsuarios, nombre + ' fue dado de baja.', 'ok');
        } catch (error) {
            console.error('Error al dar de baja al usuario:', error);
            mostrarMensaje(mensajeUsuarios,
                error.message || 'No se pudo dar de baja al usuario.', 'error');
        }
    }

    // ------------------------------------------------------------------
    // Artículos y stock
    // ------------------------------------------------------------------

    function pintarArticulos(lista, filtrado) {
        cuerpoArticulos.innerHTML = '';

        if (!Array.isArray(lista) || !lista.length) {
            mensajeVacio(cuerpoArticulos, 7, filtrado
                ? 'Ningún artículo coincide con la búsqueda.'
                : 'No hay artículos cargados.');
            return;
        }

        lista.forEach(articulo => {
            const fila = document.createElement('tr');
            if (articulo.stockBajo) {
                fila.classList.add('fila-stock-bajo');
            }

            const celdaNombre = crearCelda(articulo.nombre);
            if (articulo.descripcion) {
                celdaNombre.title = articulo.descripcion;
            }
            fila.appendChild(celdaNombre);

            fila.appendChild(crearCelda(articulo.tipoArticulo));
            fila.appendChild(crearCelda(articulo.presentacion));
            fila.appendChild(crearCeldaNumero(articulo.stockActual));
            fila.appendChild(crearCeldaNumero(articulo.stockMinimo));

            const celdaEstado = document.createElement('td');
            const etiqueta = document.createElement('span');
            etiqueta.className = articulo.stockBajo ? 'tag alert' : 'tag ok';
            etiqueta.textContent = articulo.stockBajo ? 'Stock bajo' : 'Normal';
            celdaEstado.appendChild(etiqueta);
            fila.appendChild(celdaEstado);

            const celdaReponer = crearCeldaNumero(
                articulo.stockBajo ? articulo.cantidadAReponer : '—');
            if (articulo.stockBajo) {
                celdaReponer.title = 'Cantidad para alcanzar el stock mínimo';
            }
            fila.appendChild(celdaReponer);

            cuerpoArticulos.appendChild(fila);
        });
    }

    function aplicarFiltroArticulos() {
        const termino = normalizar(buscadorArticulos ? buscadorArticulos.value : '');
        const filtrados = termino
            ? articulos.filter(articulo => [
                articulo.nombre,
                articulo.descripcion,
                articulo.presentacion,
                articulo.tipoArticulo
            ].some(valor => normalizar(valor).indexOf(termino) !== -1))
            : articulos;

        pintarArticulos(filtrados, termino !== '');
    }

    function actualizarAvisoStock() {
        if (!avisoStock || !avisoStockTexto) return;

        const bajos = articulos.filter(articulo => articulo.stockBajo);
        avisoStock.hidden = bajos.length === 0;

        if (!bajos.length) {
            avisoStockTexto.textContent = '';
            return;
        }

        avisoStockTexto.textContent = bajos.length === 1
            ? '1 artículo está por debajo del stock mínimo y necesita reposición.'
            : bajos.length + ' artículos están por debajo del stock mínimo y necesitan reposición.';
    }

    // ------------------------------------------------------------------
    // Carga e inicio
    // ------------------------------------------------------------------

    async function cargarVista() {
        mostrarMensaje(mensajeUsuarios, 'Cargando usuarios...', null);
        mostrarMensaje(mensajeArticulos, 'Cargando artículos...', null);

        try {
            const respuesta = await fetch(API_BASE + '/vista-admin', {
                method: 'GET',
                credentials: 'same-origin',
                cache: 'no-store',
                headers: { 'Accept': 'application/json' }
            });

            const datos = await leerJson(respuesta);
            if (!respuesta.ok) {
                throw new Error(mensajeError(datos,
                    'No se pudo cargar la vista (HTTP ' + respuesta.status + ').'));
            }

            usuarios = Array.isArray(datos && datos.usuarios) ? datos.usuarios : [];
            roles = Array.isArray(datos && datos.roles) ? datos.roles : [];
            articulos = Array.isArray(datos && datos.articulos) ? datos.articulos : [];

            usuarioActualId = datos && datos.usuarioActualId != null
                ? Number(datos.usuarioActualId)
                : usuarioActualGuardado();

            aplicarFiltroUsuarios();
            aplicarFiltroArticulos();
            actualizarAvisoStock();

            mostrarMensaje(mensajeUsuarios, '', null);
            mostrarMensaje(mensajeArticulos, '', null);
        } catch (error) {
            console.error('Error al cargar la vista de administrador:', error);
            const texto = error.message || 'No se pudo cargar la vista.';

            mostrarMensaje(mensajeUsuarios, texto, 'error');
            mostrarMensaje(mensajeArticulos, texto, 'error');
            mensajeVacio(cuerpoUsuarios, 6, 'No se pudieron cargar los usuarios.');
            mensajeVacio(cuerpoArticulos, 7, 'No se pudieron cargar los artículos.');
            if (avisoStock) avisoStock.hidden = true;
        }
    }

    function iniciar() {
        if (!cuerpoUsuarios || !cuerpoArticulos) return;

        if (buscadorUsuarios) {
            buscadorUsuarios.addEventListener('input', aplicarFiltroUsuarios);
        }
        if (buscadorArticulos) {
            buscadorArticulos.addEventListener('input', aplicarFiltroArticulos);
        }

        cuerpoUsuarios.addEventListener('click', evento => {
            const boton = evento.target.closest('button[data-accion]');
            if (!boton) return;

            const accion = boton.dataset.accion;
            const usuarioId = Number(boton.dataset.usuario);

            if (accion === 'asignar-rol') {
                const celda = boton.closest('td');
                const select = celda ? celda.querySelector('select') : null;
                const rolId = select ? Number(select.value) : 0;

                if (!rolId) {
                    mostrarMensaje(mensajeUsuarios,
                        'Elegí un rol antes de asignarlo.', 'error');
                    if (select) select.focus();
                    return;
                }
                asignarRol(usuarioId, rolId, boton);
            } else if (accion === 'quitar-rol') {
                quitarRol(Number(boton.dataset.idAsignacion), usuarioId,
                    boton.dataset.nombreRol);
            } else if (accion === 'baja-usuario') {
                darDeBaja(usuarioId);
            }
        });

        cargarVista();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', iniciar);
    } else {
        iniciar();
    }
})();
