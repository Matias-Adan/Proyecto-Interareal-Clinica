-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 01-09-2026 a las 08:47:29
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
-- Base de datos: `proyecto-clinica`
--

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

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `permisorol`
--

CREATE TABLE `permisorol` (
  `idRolUsuario` int(11) NOT NULL,
  `rolId` int(11) NOT NULL,
  `permisoId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `permisos`
--

CREATE TABLE `permisos` (
  `idPermiso` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roldelusuario`
--

CREATE TABLE `roldelusuario` (
  `idRolUsuario` int(11) NOT NULL,
  `usuarioId` int(11) NOT NULL,
  `rolId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roles`
--

CREATE TABLE `roles` (
  `idRol` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
  ADD PRIMARY KEY (`idRolUsuario`),
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
  MODIFY `idBeneficio` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `consultas`
--
ALTER TABLE `consultas`
  MODIFY `idConsulta` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `consultorio`
--
ALTER TABLE `consultorio`
  MODIFY `idHabitacion` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `detallesreceta`
--
ALTER TABLE `detallesreceta`
  MODIFY `idDetalle` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `estudios`
--
ALTER TABLE `estudios`
  MODIFY `idEstudio` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `facturas`
--
ALTER TABLE `facturas`
  MODIFY `idFactura` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `guardia`
--
ALTER TABLE `guardia`
  MODIFY `idGuardia` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `historiasclinicas`
--
ALTER TABLE `historiasclinicas`
  MODIFY `idHistoria` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `insumos`
--
ALTER TABLE `insumos`
  MODIFY `idInsumo` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `medicamentos`
--
ALTER TABLE `medicamentos`
  MODIFY `medicamentoId` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `obrassociales`
--
ALTER TABLE `obrassociales`
  MODIFY `idObraSocial` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `permisorol`
--
ALTER TABLE `permisorol`
  MODIFY `idRolUsuario` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `permisos`
--
ALTER TABLE `permisos`
  MODIFY `idPermiso` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `personal`
--
ALTER TABLE `personal`
  MODIFY `idEspecialidad` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `recetas`
--
ALTER TABLE `recetas`
  MODIFY `idReceta` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `roldelusuario`
--
ALTER TABLE `roldelusuario`
  MODIFY `idRolUsuario` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `roles`
--
ALTER TABLE `roles`
  MODIFY `idRol` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `triage`
--
ALTER TABLE `triage`
  MODIFY `idTriage` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `turnos`
--
ALTER TABLE `turnos`
  MODIFY `idTurno` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `ubicacionpaciente`
--
ALTER TABLE `ubicacionpaciente`
  MODIFY `idUbicacion` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `idUsuario` int(11) NOT NULL AUTO_INCREMENT;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `beneficios`
--
ALTER TABLE `beneficios`
  ADD CONSTRAINT `beneficios_ibfk_1` FOREIGN KEY (`obraSocialID`) REFERENCES `obrassociales` (`idObraSocial`);

--
-- Filtros para la tabla `consultas`
--
ALTER TABLE `consultas`
  ADD CONSTRAINT `consultas_ibfk_1` FOREIGN KEY (`historiaId`) REFERENCES `historiasclinicas` (`idHistoria`),
  ADD CONSTRAINT `consultas_ibfk_2` FOREIGN KEY (`empleadoId`) REFERENCES `usuarios` (`idUsuario`);

--
-- Filtros para la tabla `detallesreceta`
--
ALTER TABLE `detallesreceta`
  ADD CONSTRAINT `detallesreceta_ibfk_1` FOREIGN KEY (`recetaId`) REFERENCES `recetas` (`idReceta`),
  ADD CONSTRAINT `detallesreceta_ibfk_2` FOREIGN KEY (`medicamentoId`) REFERENCES `medicamentos` (`medicamentoId`);

--
-- Filtros para la tabla `estudios`
--
ALTER TABLE `estudios`
  ADD CONSTRAINT `estudios_ibfk_1` FOREIGN KEY (`pacienteId`) REFERENCES `usuarios` (`idUsuario`);

--
-- Filtros para la tabla `facturas`
--
ALTER TABLE `facturas`
  ADD CONSTRAINT `facturas_ibfk_1` FOREIGN KEY (`pacienteId`) REFERENCES `usuarios` (`idUsuario`),
  ADD CONSTRAINT `facturas_ibfk_2` FOREIGN KEY (`ObraSocialId`) REFERENCES `obrassociales` (`idObraSocial`);

--
-- Filtros para la tabla `historiasclinicas`
--
ALTER TABLE `historiasclinicas`
  ADD CONSTRAINT `historiasclinicas_ibfk_1` FOREIGN KEY (`usuarioId`) REFERENCES `usuarios` (`idUsuario`);

--
-- Filtros para la tabla `permisorol`
--
ALTER TABLE `permisorol`
  ADD CONSTRAINT `permisorol_ibfk_1` FOREIGN KEY (`rolId`) REFERENCES `roles` (`idRol`),
  ADD CONSTRAINT `permisorol_ibfk_2` FOREIGN KEY (`permisoId`) REFERENCES `permisos` (`idPermiso`);

--
-- Filtros para la tabla `personal`
--
ALTER TABLE `personal`
  ADD CONSTRAINT `personal_ibfk_1` FOREIGN KEY (`rolId`) REFERENCES `roles` (`idRol`);

--
-- Filtros para la tabla `recetas`
--
ALTER TABLE `recetas`
  ADD CONSTRAINT `recetas_ibfk_1` FOREIGN KEY (`consultaId`) REFERENCES `consultas` (`idConsulta`);

--
-- Filtros para la tabla `roldelusuario`
--
ALTER TABLE `roldelusuario`
  ADD CONSTRAINT `roldelusuario_ibfk_1` FOREIGN KEY (`usuarioId`) REFERENCES `usuarios` (`idUsuario`),
  ADD CONSTRAINT `roldelusuario_ibfk_2` FOREIGN KEY (`rolId`) REFERENCES `roles` (`idRol`);

--
-- Filtros para la tabla `triage`
--
ALTER TABLE `triage`
  ADD CONSTRAINT `triage_ibfk_1` FOREIGN KEY (`pacienteId`) REFERENCES `usuarios` (`idUsuario`),
  ADD CONSTRAINT `triage_ibfk_2` FOREIGN KEY (`guardiaId`) REFERENCES `guardia` (`idGuardia`),
  ADD CONSTRAINT `triage_ibfk_3` FOREIGN KEY (`enfermeroId`) REFERENCES `usuarios` (`idUsuario`);

--
-- Filtros para la tabla `turnos`
--
ALTER TABLE `turnos`
  ADD CONSTRAINT `turnos_ibfk_1` FOREIGN KEY (`pacienteId`) REFERENCES `usuarios` (`idUsuario`);

--
-- Filtros para la tabla `ubicacionpaciente`
--
ALTER TABLE `ubicacionpaciente`
  ADD CONSTRAINT `ubicacionpaciente_ibfk_1` FOREIGN KEY (`usuarioId`) REFERENCES `usuarios` (`idUsuario`),
  ADD CONSTRAINT `ubicacionpaciente_ibfk_2` FOREIGN KEY (`consultorioId`) REFERENCES `consultorio` (`idHabitacion`);

--
-- Filtros para la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`obraSocialId`) REFERENCES `obrassociales` (`idObraSocial`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
