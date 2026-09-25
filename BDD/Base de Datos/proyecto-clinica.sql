-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 25-09-2026 a las 23:03:59
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `clinica`
--

DELIMITER $$
--
-- Procedimientos
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `actualizar` (IN `nombre_tabla` VARCHAR(64), IN `nombre_columna_id` VARCHAR(64), IN `cambios_set` TEXT, IN `id_actualizar` INT)   BEGIN
    SET @sql = CONCAT('UPDATE `', REPLACE(nombre_tabla, '`',''), '` SET ', cambios_set, ' WHERE `', REPLACE(nombre_columna_id, '`',''), '` = ?');
    PREPARE stmt FROM @sql;
    SET @id = id_actualizar;
    EXECUTE stmt USING @id;
    DEALLOCATE PREPARE stmt;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `borrar` (IN `nombre_tabla` VARCHAR(64), IN `nombre_columna_id` VARCHAR(64), IN `id_borrar` INT)   BEGIN
    SET @sql = CONCAT('DELETE FROM `', REPLACE(nombre_tabla, '`',''), '` WHERE `', REPLACE(nombre_columna_id, '`',''), '` = ?');
    PREPARE stmt FROM @sql;
    SET @id = id_borrar;
    EXECUTE stmt USING @id;
    DEALLOCATE PREPARE stmt;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `Insertar` (IN `nombre_tabla` VARCHAR(64), IN `columnas_lista` TEXT, IN `valores_lista` TEXT)   BEGIN
    -- Ejemplo final: INSERT INTO tabla (col1, col2) VALUES (val1, val2)
    SET @sql = CONCAT('INSERT INTO `', REPLACE(nombre_tabla, '`',''), '` (', columnas_lista, ') VALUES (', valores_lista, ')');
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `registro` (IN `nombre_U` VARCHAR(40), IN `apellido_U` VARCHAR(40), IN `dni_U` VARCHAR(20), IN `fechaNacimiento_U` DATE, IN `telefono_U` VARCHAR(40), IN `email_U` VARCHAR(50), IN `obraSocialId_U` INT)   BEGIN
	INSERT INTO usuarios(nombre, apellido, dni, fechaNacimiento, telefono, email, obraSocialId)
    	VALUES(nombre_U, apellido_U, dni_U, fechaNacimiento_U, telefono_U, email_U, obraScocialId_U);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `seleccionar` (IN `nombre_tabla` VARCHAR(64))   BEGIN
	SET @sql = CONCAT('SELECT * FROM `', REPLACE(nombre_tabla, '`',''), '`');
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `seleccionarPorId` (IN `nombre_tabla` VARCHAR(64), IN `nombre_columna_id` VARCHAR(64), IN `id_buscar` INT)   BEGIN
    SET @sql = CONCAT('SELECT * FROM `', REPLACE(nombre_tabla, '`',''), '` WHERE `', REPLACE(nombre_columna_id, '`',''), '` = ?');
    PREPARE stmt FROM @sql;
    SET @id = id_buscar;
    EXECUTE stmt USING @id;
    DEALLOCATE PREPARE stmt;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `beneficios`
--

CREATE TABLE `beneficios` (
  `idBeneficio` int(11) NOT NULL,
  `cobertura` varchar(100) DEFAULT NULL,
  `descripcion` text DEFAULT NULL,
  `obraSocialID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `beneficios`
--

INSERT INTO `beneficios` (`idBeneficio`, `cobertura`, `descripcion`, `obraSocialID`) VALUES
(1, '100% Pediatría', 'Cobertura completa para menores de 12 años', 1),
(2, 'Asistencia al Viajero', 'Cobertura médica nacional de urgencia', 2),
(3, 'Descuento Farmacia', '50% en medicamentos de vademécum general', 3),
(4, 'Internación', 'Habitación individual sin cargo extra', 2),
(5, 'Prótesis Dental', 'Reintegro anual estipulado por contrato', 5);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `consultas`
--

CREATE TABLE `consultas` (
  `idConsulta` int(11) NOT NULL,
  `fechaHora` datetime NOT NULL,
  `motivo` text DEFAULT NULL,
  `diagnostico` text DEFAULT NULL,
  `observacion` text DEFAULT NULL,
  `historiaId` int(11) NOT NULL,
  `empleadoId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `consultas`
--

INSERT INTO `consultas` (`idConsulta`, `fechaHora`, `motivo`, `diagnostico`, `observacion`, `historiaId`, `empleadoId`) VALUES
(1, '2026-09-14 09:30:00', 'Palpitaciones ocasionales', 'Hipertensión arterial controlada', 'Se ajusta medicación diaria.', 1, 2),
(2, '2026-09-14 10:15:00', 'Dolor precordial fuerte', 'Angina inestable', 'Derivado de urgencia a Unidad Coronaria.', 2, 2),
(3, '2026-09-14 14:00:00', 'Tos y congestión nasal', 'Gripe estacional', 'Reposo domiciliario por 48 horas.', 3, 2),
(4, '2026-09-14 15:45:00', 'Revisión de laboratorio', 'Dislipidemia leve', 'Se sugiere dieta baja en grasas y caminatas.', 1, 2),
(5, '2026-09-14 19:30:00', 'Traumatismo por caída', 'Esguince de muñeca izquierdo', 'Inmovilización con férula por 7 días.', 3, 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `consultorio`
--

CREATE TABLE `consultorio` (
  `idHabitacion` int(11) NOT NULL,
  `numero` varchar(20) NOT NULL,
  `piso` int(11) DEFAULT NULL,
  `tipo` varchar(50) DEFAULT NULL,
  `estado` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `consultorio`
--

INSERT INTO `consultorio` (`idHabitacion`, `numero`, `piso`, `tipo`, `estado`) VALUES
(1, '101', 1, 'Pediatría', 'Disponible'),
(2, '102', 1, 'Consultas Generales', 'Disponible'),
(3, '201', 2, 'Cardiología', 'En mantenimiento'),
(4, '202', 2, 'Ecografías y Rayos', 'Disponible'),
(5, 'G-01', 0, 'Shockroom Guardia', 'Ocupado');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detallesreceta`
--

CREATE TABLE `detallesreceta` (
  `idDetalle` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `dosis` varchar(100) DEFAULT NULL,
  `frecuencia` varchar(100) DEFAULT NULL,
  `duracion` varchar(100) DEFAULT NULL,
  `recetaId` int(11) NOT NULL,
  `medicamentoId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `detallesreceta`
--

INSERT INTO `detallesreceta` (`idDetalle`, `cantidad`, `dosis`, `frecuencia`, `duracion`, `recetaId`, `medicamentoId`) VALUES
(1, 2, '50mg', 'Cada 24 horas', '30 días', 1, 4),
(2, 1, '1g', 'Cada 8 horas', '3 días', 3, 3),
(3, 1, '500mg', 'Cada 8 horas', '7 días', 4, 2),
(4, 1, '600mg', 'Cada 12 horas', '5 días', 5, 1),
(5, 1, '0.5mg', 'Cada 24 horas', '15 días', 5, 5);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `estudios`
--

CREATE TABLE `estudios` (
  `idEstudio` int(11) NOT NULL,
  `tipo` varchar(100) DEFAULT NULL,
  `fecha` datetime NOT NULL,
  `resultado` text DEFAULT NULL,
  `archivo` varchar(255) DEFAULT NULL,
  `pacienteId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `estudios`
--

INSERT INTO `estudios` (`idEstudio`, `tipo`, `fecha`, `resultado`, `archivo`, `pacienteId`) VALUES
(1, 'Electrocardiograma', '2026-09-01 10:30:00', 'Ritmo sinusal normal. Sin anomalías.', 'ecg_user1_2026.pdf', 1),
(2, 'Análisis de Sangre Completo', '2026-09-02 07:15:00', 'Glucemia ligeramente elevada (110 mg/dL).', 'lab_user3_2026.pdf', 3),
(3, 'Radiografía de Tórax', '2026-09-05 14:00:00', 'Campos pulmonares limpios. Silueta cardíaca normal.', 'rx_user5_2026.png', 5),
(4, 'Ecocardiograma', '2026-09-10 11:00:00', 'Fracción de eyección dentro de los límites.', 'echo_user1_2026.pdf', 1),
(5, 'Orina Completa', '2026-09-12 08:00:00', 'Valores normales, densidad adecuada.', 'uri_user3_2026.pdf', 3);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `facturas`
--

CREATE TABLE `facturas` (
  `idFactura` int(11) NOT NULL,
  `fecha` datetime NOT NULL,
  `importeTotal` decimal(10,2) NOT NULL,
  `estado` varchar(50) DEFAULT NULL,
  `pacienteId` int(11) NOT NULL,
  `ObraSocialId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `facturas`
--

INSERT INTO `facturas` (`idFactura`, `fecha`, `importeTotal`, `estado`, `pacienteId`, `ObraSocialId`) VALUES
(1, '2026-09-14 09:00:00', 4500.00, 'Pagada', 1, 1),
(2, '2026-09-14 10:15:00', 12000.50, 'Pendiente', 3, 4),
(3, '2026-09-14 11:30:00', 0.00, 'Pagada', 5, 5),
(4, '2026-09-14 12:00:00', 3200.00, 'Cancelada', 1, 1),
(5, '2026-09-14 13:45:00', 8500.00, 'Pendiente', 3, 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `guardia`
--

CREATE TABLE `guardia` (
  `idGuardia` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `horaInicio` time DEFAULT NULL,
  `horaFin` time DEFAULT NULL,
  `estado` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `guardia`
--

INSERT INTO `guardia` (`idGuardia`, `fecha`, `horaInicio`, `horaFin`, `estado`) VALUES
(1, '2026-09-14', '08:00:00', '16:00:00', 'Activa'),
(2, '2026-09-14', '16:00:00', '00:00:00', 'Programada'),
(3, '2026-09-15', '00:00:00', '08:00:00', 'Programada'),
(4, '2026-09-15', '08:00:00', '16:00:00', 'Programada'),
(5, '2026-09-15', '16:00:00', '00:00:00', 'Programada');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `historiasclinicas`
--

CREATE TABLE `historiasclinicas` (
  `idHistoria` int(11) NOT NULL,
  `fechaApertura` date NOT NULL,
  `antecedentes` text DEFAULT NULL,
  `alergia` text DEFAULT NULL,
  `observacion` text DEFAULT NULL,
  `usuarioId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `historiasclinicas`
--

INSERT INTO `historiasclinicas` (`idHistoria`, `fechaApertura`, `antecedentes`, `alergia`, `observacion`, `usuarioId`) VALUES
(1, '2020-03-15', 'Hipertenso crónico, padre con antecedentes cardíacos.', 'Penicilina', 'Paciente regular', 1),
(2, '2024-01-10', 'Cirugía de apéndice en 2018.', 'Ninguna', 'Ninguna', 3),
(3, '2025-06-22', 'Asma infantil controlado.', 'Aspirina', 'Fumador social', 5),
(4, '2026-02-11', 'Diabetes Tipo 2 controlada.', 'Sulfas', 'Paciente requiere control trimestral', 1),
(5, '2026-08-01', 'Ninguno relevante.', 'Ninguna', 'Control anual laboral', 3);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `insumos`
--

CREATE TABLE `insumos` (
  `idInsumo` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `tipo` varchar(50) DEFAULT NULL,
  `stockActual` int(11) DEFAULT 0,
  `stockMinimo` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `insumos`
--

INSERT INTO `insumos` (`idInsumo`, `nombre`, `descripcion`, `tipo`, `stockActual`, `stockMinimo`) VALUES
(1, 'Gasa estéril 10x10', 'Paquetes de gasas hidrófilas', 'Descartable', 450, 100),
(2, 'Jeringas 5ml', 'Jeringas con aguja descartable', 'Descartable', 320, 50),
(3, 'Guantes de látex M', 'Cajas de 100 unidades', 'Protección', 85, 20),
(4, 'Alcohol en gel 500ml', 'Dispensador desinfectante', 'Sanitizante', 40, 15),
(5, 'Barbijos quirúrgicos', 'Cajas de 50 unidades tricapa', 'Protección', 120, 30);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `medicamentos`
--

CREATE TABLE `medicamentos` (
  `medicamentoId` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `presentacion` varchar(100) DEFAULT NULL,
  `stockActual` int(11) DEFAULT 0,
  `stockMinimo` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `medicamentos`
--

INSERT INTO `medicamentos` (`medicamentoId`, `nombre`, `descripcion`, `presentacion`, `stockActual`, `stockMinimo`) VALUES
(1, 'Ibuprofeno 600mg', 'Analgésico y antiinflamatorio', 'Comprimidos x 20', 150, 30),
(2, 'Amoxicilina 500mg', 'Antibiótico de amplio espectro', 'Cápsulas x 16', 90, 20),
(3, 'Paracetamol 1g', 'Antipirético y analgésico', 'Comprimidos x 40', 210, 45),
(4, 'Losartán 50mg', 'Antihipertensivo crónico', 'Comprimidos x 30', 80, 25),
(5, 'Clonazepam 0.5mg', 'Ansiolítico bajo receta archivada', 'Comprimidos x 30', 45, 15);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `obrassociales`
--

CREATE TABLE `obrassociales` (
  `idObraSocial` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `numeroConvenio` varchar(50) DEFAULT NULL,
  `beneficios` text DEFAULT NULL,
  `cobertura` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `obrassociales`
--

INSERT INTO `obrassociales` (`idObraSocial`, `nombre`, `numeroConvenio`, `beneficios`, `cobertura`) VALUES
(1, 'OSDE 210', 'CO-88421', 'Consultas ilimitadas, 40% en medicamentos', '80%'),
(2, 'Swiss Medical', 'CO-99312', 'Guardia libre, odontología preventiva', '100%'),
(3, 'Galeno Silver', 'CO-55214', 'Kinesiología hasta 20 sesiones/año', '70%'),
(4, 'PAMI', 'CO-11200', 'Medicamentos gratis crónicos, prótesis', '100%'),
(5, 'OSECAC', 'CO-44122', 'Plan materno infantil, médicos de cartilla', '60%');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `permisorol`
--

CREATE TABLE `permisorol` (
  `idPermisoRol` int(11) NOT NULL,
  `rolId` int(11) NOT NULL,
  `permisoId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `permisorol`
--

INSERT INTO `permisorol` (`idPermisoRol`, `rolId`, `permisoId`) VALUES
(1, 1, 1),
(2, 1, 5),
(3, 2, 2),
(4, 2, 3),
(5, 4, 4);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `permisos`
--

CREATE TABLE `permisos` (
  `idPermiso` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `permisos`
--

INSERT INTO `permisos` (`idPermiso`, `nombre`, `descripcion`) VALUES
(1, 'crear_usuario', 'Permite registrar nuevos pacientes o empleados'),
(2, 'ver_historia_clinica', 'Acceso de lectura a fichas médicas confidenciales'),
(3, 'editar_historia_clinica', 'Permite agregar consultas y diagnósticos'),
(4, 'emitir_factura', 'Acceso al módulo de caja y cobros'),
(5, 'gestionar_inventario', 'Modificar stock de insumos y remedios');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `personal`
--

CREATE TABLE `personal` (
  `idEspecialidad` int(11) NOT NULL,
  `trabajo` varchar(100) DEFAULT NULL,
  `descripcion` text DEFAULT NULL,
  `rolId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `personal`
--

INSERT INTO `personal` (`idEspecialidad`, `trabajo`, `descripcion`, `rolId`) VALUES
(1, 'Cardiología', 'Especialista en alta complejidad cardíaca', 2),
(2, 'Pediatría Clínica', 'Atención infantil y neonatal', 2),
(3, 'Enfermería General', 'Asistencia en piso e internaciones', 3),
(4, 'Enfermería de Triage', 'Clasificación de urgencia en guardia', 3),
(5, 'Administración General', 'Gestión de turnos y facturación', 4);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `recetas`
--

CREATE TABLE `recetas` (
  `idReceta` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `indicacion` text DEFAULT NULL,
  `consultaId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `recetas`
--

INSERT INTO `recetas` (`idReceta`, `fecha`, `indicacion`, `consultaId`) VALUES
(1, '2026-09-14', 'Tomar por la mañana en ayunas de forma crónica.', 1),
(2, '2026-09-14', 'Administrar vía intravenosa en la internación.', 2),
(3, '2026-09-14', 'Tomar cada 8 horas si hay fiebre o dolor corporal.', 3),
(4, '2026-09-14', 'Completar el esquema de 7 días estrictos.', 3),
(5, '2026-09-14', 'Tomar por las noches antes de dormir.', 4);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roldelusuario`
--

CREATE TABLE `roldelusuario` (
  `idRolUsuario` int(11) NOT NULL,
  `usuarioId` int(11) NOT NULL,
  `rolId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `roldelusuario`
--

INSERT INTO `roldelusuario` (`idRolUsuario`, `usuarioId`, `rolId`) VALUES
(1, 1, 5),
(2, 2, 2),
(3, 3, 5),
(4, 4, 3),
(5, 5, 5);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roles`
--

CREATE TABLE `roles` (
  `idRol` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `roles`
--

INSERT INTO `roles` (`idRol`, `nombre`, `descripcion`) VALUES
(1, 'Administrador', 'Control total de la configuración del sistema de la clínica'),
(2, 'Médico', 'Profesionales de salud que atienden y recetan'),
(3, 'Enfermero', 'Personal técnico de asistencia y triage de guardia'),
(4, 'Recepcionista', 'Atención al público, turnos y facturación'),
(5, 'Paciente', 'Usuarios que acceden a sus propios turnos y estudios');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `triage`
--

CREATE TABLE `triage` (
  `idTriage` int(11) NOT NULL,
  `fecha` datetime NOT NULL,
  `prioridad` int(11) DEFAULT NULL,
  `estado` varchar(50) DEFAULT NULL,
  `observacion` text DEFAULT NULL,
  `pacienteId` int(11) NOT NULL,
  `guardiaId` int(11) NOT NULL,
  `enfermeroId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `triage`
--

INSERT INTO `triage` (`idTriage`, `fecha`, `prioridad`, `estado`, `observacion`, `pacienteId`, `guardiaId`, `enfermeroId`) VALUES
(1, '2026-09-14 08:15:00', 3, 'Finalizado', 'Verde. Paciente estable con dolor leve de garganta.', 1, 1, 4),
(2, '2026-09-14 10:00:00', 1, 'Atendido', 'Rojo. Dolor agudo precordial, sudoración.', 3, 1, 4),
(3, '2026-09-14 11:45:00', 2, 'En espera', 'Amarillo. Fiebre alta persistente en adulto.', 5, 1, 4),
(4, '2026-09-14 16:30:00', 3, 'Cancelado', 'Verde. Se retira antes de ser llamado.', 1, 2, 4),
(5, '2026-09-14 19:10:00', 2, 'Atendido', 'Amarillo. Posible fractura menor de muñeca.', 5, 2, 4);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `turnos`
--

CREATE TABLE `turnos` (
  `idTurno` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `hora` time NOT NULL,
  `estado` varchar(50) DEFAULT NULL,
  `motivo` text DEFAULT NULL,
  `pacienteId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `turnos`
--

INSERT INTO `turnos` (`idTurno`, `fecha`, `hora`, `estado`, `motivo`, `pacienteId`) VALUES
(1, '2026-09-14', '09:30:00', 'Atendido', 'Control de rutina por presión', 1),
(2, '2026-09-14', '11:00:00', 'Ausente', 'Revisión de análisis', 3),
(3, '2026-09-15', '08:30:00', 'Confirmado', 'Dolor de pecho esporádico', 5),
(4, '2026-09-15', '10:00:00', 'Confirmado', 'Renovación de recetas fijas', 1),
(5, '2026-09-16', '16:15:00', 'Pendiente', 'Consulta general', 3);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ubicacionpaciente`
--

CREATE TABLE `ubicacionpaciente` (
  `idUbicacion` int(11) NOT NULL,
  `tipo` varchar(50) DEFAULT NULL,
  `fechaIngreso` datetime NOT NULL,
  `fechaSalida` datetime DEFAULT NULL,
  `usuarioId` int(11) NOT NULL,
  `consultorioId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `ubicacionpaciente`
--

INSERT INTO `ubicacionpaciente` (`idUbicacion`, `tipo`, `fechaIngreso`, `fechaSalida`, `usuarioId`, `consultorioId`) VALUES
(1, 'Consulta Ambulatoria', '2026-09-14 09:20:00', '2026-09-14 09:50:00', 1, 2),
(2, 'Internación Guardia', '2026-09-14 10:05:00', NULL, 3, 5),
(3, 'Estudio Programado', '2026-09-14 13:45:00', '2026-09-14 14:15:00', 5, 4),
(4, 'Consulta Guardia', '2026-09-14 15:30:00', '2026-09-14 16:10:00', 1, 1),
(5, 'Observación', '2026-09-14 18:00:00', NULL, 5, 5);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `idUsuario` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  `dni` varchar(20) NOT NULL,
  `fechaNacimiento` date DEFAULT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `obraSocialId` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`idUsuario`, `nombre`, `apellido`, `dni`, `fechaNacimiento`, `telefono`, `email`, `obraSocialId`) VALUES
(1, 'Juan Carlos', 'Gómez', '25412874', '1976-05-12', '1145887412', 'juan.gomez@email.com', 1),
(2, 'María Paula', 'Rodríguez', '38451254', '1994-08-22', '1169854712', 'maria.médica@clinica.com', NULL),
(3, 'Andrés Luis', 'Fernández', '14258963', '1955-11-02', '1130251478', 'andres.fer@email.com', 4),
(4, 'Laura Inés', 'López', '32145874', '1986-02-14', '1158741254', 'laura.enfermera@clinica.com', NULL),
(5, 'Carlos Javier', 'Pérez', '42154879', '2000-01-30', '1121548796', 'carlos.perez@email.com', 5);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `beneficios`
--
ALTER TABLE `beneficios`
  ADD PRIMARY KEY (`idBeneficio`),
  ADD KEY `obraSocialID` (`obraSocialID`);

--
-- Indices de la tabla `consultas`
--
ALTER TABLE `consultas`
  ADD PRIMARY KEY (`idConsulta`),
  ADD KEY `historiaId` (`historiaId`),
  ADD KEY `empleadoId` (`empleadoId`);

--
-- Indices de la tabla `consultorio`
--
ALTER TABLE `consultorio`
  ADD PRIMARY KEY (`idHabitacion`);

--
-- Indices de la tabla `detallesreceta`
--
ALTER TABLE `detallesreceta`
  ADD PRIMARY KEY (`idDetalle`),
  ADD KEY `recetaId` (`recetaId`),
  ADD KEY `medicamentoId` (`medicamentoId`);

--
-- Indices de la tabla `estudios`
--
ALTER TABLE `estudios`
  ADD PRIMARY KEY (`idEstudio`),
  ADD KEY `pacienteId` (`pacienteId`);

--
-- Indices de la tabla `facturas`
--
ALTER TABLE `facturas`
  ADD PRIMARY KEY (`idFactura`),
  ADD KEY `pacienteId` (`pacienteId`),
  ADD KEY `ObraSocialId` (`ObraSocialId`);

--
-- Indices de la tabla `guardia`
--
ALTER TABLE `guardia`
  ADD PRIMARY KEY (`idGuardia`);

--
-- Indices de la tabla `historiasclinicas`
--
ALTER TABLE `historiasclinicas`
  ADD PRIMARY KEY (`idHistoria`),
  ADD KEY `usuarioId` (`usuarioId`);

--
-- Indices de la tabla `insumos`
--
ALTER TABLE `insumos`
  ADD PRIMARY KEY (`idInsumo`);

--
-- Indices de la tabla `medicamentos`
--
ALTER TABLE `medicamentos`
  ADD PRIMARY KEY (`medicamentoId`);

--
-- Indices de la tabla `obrassociales`
--
ALTER TABLE `obrassociales`
  ADD PRIMARY KEY (`idObraSocial`);

--
-- Indices de la tabla `permisorol`
--
ALTER TABLE `permisorol`
  ADD PRIMARY KEY (`idPermisoRol`),
  ADD KEY `rolId` (`rolId`),
  ADD KEY `permisoId` (`permisoId`);

--
-- Indices de la tabla `permisos`
--
ALTER TABLE `permisos`
  ADD PRIMARY KEY (`idPermiso`);

--
-- Indices de la tabla `personal`
--
ALTER TABLE `personal`
  ADD PRIMARY KEY (`idEspecialidad`),
  ADD KEY `rolId` (`rolId`);

--
-- Indices de la tabla `recetas`
--
ALTER TABLE `recetas`
  ADD PRIMARY KEY (`idReceta`),
  ADD KEY `consultaId` (`consultaId`);

--
-- Indices de la tabla `roldelusuario`
--
ALTER TABLE `roldelusuario`
  ADD PRIMARY KEY (`idRolUsuario`),
  ADD KEY `usuarioId` (`usuarioId`),
  ADD KEY `rolId` (`rolId`);

--
-- Indices de la tabla `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`idRol`);

--
-- Indices de la tabla `triage`
--
ALTER TABLE `triage`
  ADD PRIMARY KEY (`idTriage`),
  ADD KEY `pacienteId` (`pacienteId`),
  ADD KEY `guardiaId` (`guardiaId`),
  ADD KEY `enfermeroId` (`enfermeroId`);

--
-- Indices de la tabla `turnos`
--
ALTER TABLE `turnos`
  ADD PRIMARY KEY (`idTurno`),
  ADD KEY `pacienteId` (`pacienteId`);

--
-- Indices de la tabla `ubicacionpaciente`
--
ALTER TABLE `ubicacionpaciente`
  ADD PRIMARY KEY (`idUbicacion`),
  ADD KEY `usuarioId` (`usuarioId`),
  ADD KEY `consultorioId` (`consultorioId`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`idUsuario`),
  ADD UNIQUE KEY `dni` (`dni`),
  ADD KEY `obraSocialId` (`obraSocialId`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `beneficios`
--
ALTER TABLE `beneficios`
  MODIFY `idBeneficio` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `consultas`
--
ALTER TABLE `consultas`
  MODIFY `idConsulta` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `consultorio`
--
ALTER TABLE `consultorio`
  MODIFY `idHabitacion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `detallesreceta`
--
ALTER TABLE `detallesreceta`
  MODIFY `idDetalle` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `estudios`
--
ALTER TABLE `estudios`
  MODIFY `idEstudio` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `facturas`
--
ALTER TABLE `facturas`
  MODIFY `idFactura` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `guardia`
--
ALTER TABLE `guardia`
  MODIFY `idGuardia` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `historiasclinicas`
--
ALTER TABLE `historiasclinicas`
  MODIFY `idHistoria` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `insumos`
--
ALTER TABLE `insumos`
  MODIFY `idInsumo` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `medicamentos`
--
ALTER TABLE `medicamentos`
  MODIFY `medicamentoId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `obrassociales`
--
ALTER TABLE `obrassociales`
  MODIFY `idObraSocial` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `permisorol`
--
ALTER TABLE `permisorol`
  MODIFY `idPermisoRol` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `permisos`
--
ALTER TABLE `permisos`
  MODIFY `idPermiso` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `personal`
--
ALTER TABLE `personal`
  MODIFY `idEspecialidad` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `recetas`
--
ALTER TABLE `recetas`
  MODIFY `idReceta` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `roldelusuario`
--
ALTER TABLE `roldelusuario`
  MODIFY `idRolUsuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `roles`
--
ALTER TABLE `roles`
  MODIFY `idRol` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `triage`
--
ALTER TABLE `triage`
  MODIFY `idTriage` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `turnos`
--
ALTER TABLE `turnos`
  MODIFY `idTurno` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `ubicacionpaciente`
--
ALTER TABLE `ubicacionpaciente`
  MODIFY `idUbicacion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `idUsuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `beneficios`
--
ALTER TABLE `beneficios`
  ADD CONSTRAINT `beneficios_ibfk_1` FOREIGN KEY (`obraSocialID`) REFERENCES `obrassociales` (`idObraSocial`) ON DELETE CASCADE;

--
-- Filtros para la tabla `consultas`
--
ALTER TABLE `consultas`
  ADD CONSTRAINT `consultas_ibfk_1` FOREIGN KEY (`historiaId`) REFERENCES `historiasclinicas` (`idHistoria`) ON DELETE CASCADE,
  ADD CONSTRAINT `consultas_ibfk_2` FOREIGN KEY (`empleadoId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE;

--
-- Filtros para la tabla `detallesreceta`
--
ALTER TABLE `detallesreceta`
  ADD CONSTRAINT `detallesreceta_ibfk_1` FOREIGN KEY (`recetaId`) REFERENCES `recetas` (`idReceta`) ON DELETE CASCADE,
  ADD CONSTRAINT `detallesreceta_ibfk_2` FOREIGN KEY (`medicamentoId`) REFERENCES `medicamentos` (`medicamentoId`) ON DELETE CASCADE;

--
-- Filtros para la tabla `estudios`
--
ALTER TABLE `estudios`
  ADD CONSTRAINT `estudios_ibfk_1` FOREIGN KEY (`pacienteId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE;

--
-- Filtros para la tabla `facturas`
--
ALTER TABLE `facturas`
  ADD CONSTRAINT `facturas_ibfk_1` FOREIGN KEY (`pacienteId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE,
  ADD CONSTRAINT `facturas_ibfk_2` FOREIGN KEY (`ObraSocialId`) REFERENCES `obrassociales` (`idObraSocial`) ON DELETE CASCADE;

--
-- Filtros para la tabla `historiasclinicas`
--
ALTER TABLE `historiasclinicas`
  ADD CONSTRAINT `historiasclinicas_ibfk_1` FOREIGN KEY (`usuarioId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE;

--
-- Filtros para la tabla `permisorol`
--
ALTER TABLE `permisorol`
  ADD CONSTRAINT `permisorol_ibfk_1` FOREIGN KEY (`rolId`) REFERENCES `roles` (`idRol`) ON DELETE CASCADE,
  ADD CONSTRAINT `permisorol_ibfk_2` FOREIGN KEY (`permisoId`) REFERENCES `permisos` (`idPermiso`) ON DELETE CASCADE;

--
-- Filtros para la tabla `personal`
--
ALTER TABLE `personal`
  ADD CONSTRAINT `personal_ibfk_1` FOREIGN KEY (`rolId`) REFERENCES `roles` (`idRol`) ON DELETE CASCADE;

--
-- Filtros para la tabla `recetas`
--
ALTER TABLE `recetas`
  ADD CONSTRAINT `recetas_ibfk_1` FOREIGN KEY (`consultaId`) REFERENCES `consultas` (`idConsulta`) ON DELETE CASCADE;

--
-- Filtros para la tabla `roldelusuario`
--
ALTER TABLE `roldelusuario`
  ADD CONSTRAINT `roldelusuario_ibfk_1` FOREIGN KEY (`usuarioId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE,
  ADD CONSTRAINT `roldelusuario_ibfk_2` FOREIGN KEY (`rolId`) REFERENCES `roles` (`idRol`) ON DELETE CASCADE;

--
-- Filtros para la tabla `triage`
--
ALTER TABLE `triage`
  ADD CONSTRAINT `triage_ibfk_1` FOREIGN KEY (`pacienteId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE,
  ADD CONSTRAINT `triage_ibfk_2` FOREIGN KEY (`guardiaId`) REFERENCES `guardia` (`idGuardia`) ON DELETE CASCADE,
  ADD CONSTRAINT `triage_ibfk_3` FOREIGN KEY (`enfermeroId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE;

--
-- Filtros para la tabla `turnos`
--
ALTER TABLE `turnos`
  ADD CONSTRAINT `turnos_ibfk_1` FOREIGN KEY (`pacienteId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE;

--
-- Filtros para la tabla `ubicacionpaciente`
--
ALTER TABLE `ubicacionpaciente`
  ADD CONSTRAINT `ubicacionpaciente_ibfk_1` FOREIGN KEY (`usuarioId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE,
  ADD CONSTRAINT `ubicacionpaciente_ibfk_2` FOREIGN KEY (`consultorioId`) REFERENCES `consultorio` (`idHabitacion`) ON DELETE CASCADE;

--
-- Filtros para la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`obraSocialId`) REFERENCES `obrassociales` (`idObraSocial`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
