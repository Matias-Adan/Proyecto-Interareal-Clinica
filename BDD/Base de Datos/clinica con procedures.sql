DROP DATABASE IF EXISTS `clinica`;
CREATE DATABASE IF NOT EXISTS `clinica`;
USE `clinica`;

-- ========================================================
-- 1. TABLAS
-- ========================================================

CREATE TABLE `obrassociales` (
  `idObraSocial` INT AUTO_INCREMENT PRIMARY KEY,
  `nombre` VARCHAR(100) NOT NULL UNIQUE,
  `numeroConvenio` VARCHAR(50) DEFAULT NULL,
  `beneficios` TEXT DEFAULT NULL,
  `cobertura` VARCHAR(100) DEFAULT NULL
);

CREATE TABLE `roles` (
  `idRol` INT AUTO_INCREMENT PRIMARY KEY,
  `nombre` VARCHAR(100) NOT NULL UNIQUE,
  `descripcion` TEXT DEFAULT NULL
);

CREATE TABLE `usuarios` (
  `idUsuario` INT AUTO_INCREMENT PRIMARY KEY,
  `nombre` VARCHAR(100) NOT NULL,
  `apellido` VARCHAR(100) NOT NULL,
  `dni` VARCHAR(20) NOT NULL UNIQUE,
  `fechaNacimiento` DATE DEFAULT NULL,
  `telefono` VARCHAR(30) DEFAULT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `obraSocialId` INT DEFAULT NULL,
  `rolId` INT NOT NULL DEFAULT 5,
  `activo` TINYINT(1) NOT NULL DEFAULT 1,
  CONSTRAINT `fk_usuarios_obrasocial` FOREIGN KEY (`obraSocialId`) REFERENCES `obrassociales` (`idObraSocial`) ON DELETE SET NULL,
  CONSTRAINT `fk_usuarios_rol` FOREIGN KEY (`rolId`) REFERENCES `roles` (`idRol`) ON DELETE RESTRICT
);

CREATE TABLE `especialidades` (
  `idEspecialidad` INT AUTO_INCREMENT PRIMARY KEY,
  `nombre` VARCHAR(100) NOT NULL UNIQUE,
  `descripcion` TEXT DEFAULT NULL
);

CREATE TABLE `medico_especialidad` (
  `idMedicoEspecialidad` INT AUTO_INCREMENT PRIMARY KEY,
  `medicoId` INT NOT NULL,
  `especialidadId` INT NOT NULL,
  CONSTRAINT `uq_medico_especialidad` UNIQUE (`medicoId`, `especialidadId`),
  CONSTRAINT `fk_medicoesp_medico` FOREIGN KEY (`medicoId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE,
  CONSTRAINT `fk_medicoesp_especialidad` FOREIGN KEY (`especialidadId`) REFERENCES `especialidades` (`idEspecialidad`) ON DELETE CASCADE
);

CREATE TABLE `consultorio` (
  `idHabitacion` INT AUTO_INCREMENT PRIMARY KEY,
  `numero` VARCHAR(20) NOT NULL UNIQUE,
  `piso` INT DEFAULT NULL,
  `tipo` VARCHAR(50) DEFAULT NULL,
  `estado` ENUM('Disponible', 'Ocupado', 'En mantenimiento') DEFAULT 'Disponible'
);

CREATE TABLE `articulos` (
  `idArticulo` INT AUTO_INCREMENT PRIMARY KEY,
  `nombre` VARCHAR(100) NOT NULL,
  `descripcion` TEXT DEFAULT NULL,
  `presentacion` VARCHAR(100) DEFAULT NULL,
  `tipoArticulo` ENUM('MEDICAMENTO','INSUMO') NOT NULL,
  `stockActual` INT DEFAULT 0,
  `stockMinimo` INT DEFAULT 0
);

CREATE TABLE `movimientos_stock` (
  `idMovimiento` INT AUTO_INCREMENT PRIMARY KEY,
  `idArticulo` INT NOT NULL,
  `tipoMovimiento` ENUM('ENTRADA','SALIDA','AJUSTE') NOT NULL,
  `cantidad` INT NOT NULL,
  `motivo` TEXT NOT NULL,
  `fechaHora` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `usuarioId` INT NOT NULL,
  CONSTRAINT `fk_mov_articulo` FOREIGN KEY (`idArticulo`) REFERENCES `articulos` (`idArticulo`) ON DELETE RESTRICT,
  CONSTRAINT `fk_mov_usuario` FOREIGN KEY (`usuarioId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE
);

CREATE TABLE `historiasclinicas` (
  `idHistoria` INT AUTO_INCREMENT PRIMARY KEY,
  `fechaApertura` DATE NOT NULL,
  `antecedentes` TEXT DEFAULT NULL,
  `alergia` TEXT DEFAULT NULL,
  `observacion` TEXT DEFAULT NULL,
  `usuarioId` INT NOT NULL UNIQUE,
  CONSTRAINT `fk_hc_usuario` FOREIGN KEY (`usuarioId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE
);

CREATE TABLE `consultas` (
  `idConsulta` INT AUTO_INCREMENT PRIMARY KEY,
  `fechaHora` DATETIME NOT NULL,
  `motivo` TEXT DEFAULT NULL,
  `diagnostico` TEXT DEFAULT NULL,
  `observacion` TEXT DEFAULT NULL,
  `historiaId` INT NOT NULL,
  `medicoId` INT NOT NULL,
  CONSTRAINT `fk_consultas_historia` FOREIGN KEY (`historiaId`) REFERENCES `historiasclinicas` (`idHistoria`) ON DELETE CASCADE,
  CONSTRAINT `fk_consultas_medico` FOREIGN KEY (`medicoId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE
);

CREATE TABLE `recetas` (
  `idReceta` INT AUTO_INCREMENT PRIMARY KEY,
  `fecha` DATE NOT NULL,
  `indicacion` TEXT DEFAULT NULL,
  `consultaId` INT NOT NULL,
  CONSTRAINT `fk_recetas_consulta` FOREIGN KEY (`consultaId`) REFERENCES `consultas` (`idConsulta`) ON DELETE CASCADE
);

CREATE TABLE `detallesreceta` (
  `idDetalle` INT AUTO_INCREMENT PRIMARY KEY,
  `cantidad` INT NOT NULL,
  `dosis` VARCHAR(100) DEFAULT NULL,
  `frecuencia` VARCHAR(100) DEFAULT NULL,
  `duracion` VARCHAR(100) DEFAULT NULL,
  `recetaId` INT NOT NULL,
  `articuloId` INT NOT NULL,
  CONSTRAINT `fk_detreceta_receta` FOREIGN KEY (`recetaId`) REFERENCES `recetas` (`idReceta`) ON DELETE CASCADE,
  CONSTRAINT `fk_detreceta_articulo` FOREIGN KEY (`articuloId`) REFERENCES `articulos` (`idArticulo`) ON DELETE RESTRICT
);

CREATE TABLE `turnos` (
  `idTurno` INT AUTO_INCREMENT PRIMARY KEY,
  `fecha` DATE NOT NULL,
  `horaInicio` TIME NOT NULL,
  `horaFin` TIME NOT NULL,
  `estado` ENUM('Pendiente', 'Atendido', 'Ausente', 'Cancelado', 'Anulado') DEFAULT 'Pendiente',
  `motivo` TEXT DEFAULT NULL,
  `pacienteId` INT NOT NULL,
  `medicoId` INT NOT NULL,
  `especialidadId` INT DEFAULT NULL,
  `consultorioId` INT DEFAULT NULL,
  CONSTRAINT `fk_turnos_paciente` FOREIGN KEY (`pacienteId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE,
  CONSTRAINT `fk_turnos_medico` FOREIGN KEY (`medicoId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE,
  CONSTRAINT `fk_turnos_especialidad` FOREIGN KEY (`especialidadId`) REFERENCES `especialidades` (`idEspecialidad`) ON DELETE SET NULL,
  CONSTRAINT `fk_turnos_consultorio` FOREIGN KEY (`consultorioId`) REFERENCES `consultorio` (`idHabitacion`) ON DELETE SET NULL
);

CREATE TABLE `ubicacionpaciente` (
  `idUbicacion` INT AUTO_INCREMENT PRIMARY KEY,
  `tipoAtencion` ENUM('TURNO_SIMPLE','EMERGENCIA','INTERNACION') NOT NULL DEFAULT 'TURNO_SIMPLE',
  `fechaIngreso` DATETIME NOT NULL,
  `fechaSalida` DATETIME DEFAULT NULL,
  `pacienteId` INT NOT NULL,
  `medicoId` INT DEFAULT NULL,
  `consultorioId` INT NOT NULL,
  CONSTRAINT `fk_ubicacion_paciente` FOREIGN KEY (`pacienteId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE,
  CONSTRAINT `fk_ubicacion_medico` FOREIGN KEY (`medicoId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE SET NULL,
  CONSTRAINT `fk_ubicacion_consultorio` FOREIGN KEY (`consultorioId`) REFERENCES `consultorio` (`idHabitacion`) ON DELETE RESTRICT
);

CREATE TABLE `guardia` (
  `idGuardia` INT AUTO_INCREMENT PRIMARY KEY,
  `fecha` DATE NOT NULL,
  `horaInicio` TIME DEFAULT NULL,
  `horaFin` TIME DEFAULT NULL,
  `estado` ENUM('Activa', 'Finalizada', 'Suspendida') DEFAULT 'Activa'
);

CREATE TABLE `triage` (
  `idTriage` INT AUTO_INCREMENT PRIMARY KEY,
  `fecha` DATETIME NOT NULL,
  `prioridad` INT DEFAULT NULL,
  `estado` VARCHAR(50) DEFAULT NULL,
  `observacion` TEXT DEFAULT NULL,
  `pacienteId` INT NOT NULL,
  `guardiaId` INT NOT NULL,
  `enfermeroId` INT NOT NULL,
  CONSTRAINT `fk_triage_paciente` FOREIGN KEY (`pacienteId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE,
  CONSTRAINT `fk_triage_guardia` FOREIGN KEY (`guardiaId`) REFERENCES `guardia` (`idGuardia`) ON DELETE CASCADE,
  CONSTRAINT `fk_triage_enfermero` FOREIGN KEY (`enfermeroId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE
);

CREATE TABLE `estudios` (
  `idEstudio` INT AUTO_INCREMENT PRIMARY KEY,
  `tipo` VARCHAR(100) DEFAULT NULL,
  `fecha` DATETIME NOT NULL,
  `resultado` TEXT DEFAULT NULL,
  `archivo` VARCHAR(255) DEFAULT NULL,
  `pacienteId` INT NOT NULL,
  CONSTRAINT `fk_estudios_paciente` FOREIGN KEY (`pacienteId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE
);

CREATE TABLE `facturas` (
  `idFactura` INT AUTO_INCREMENT PRIMARY KEY,
  `fecha` DATETIME NOT NULL,
  `importeTotal` DECIMAL(10,2) NOT NULL,
  `estado` ENUM('Pendiente', 'Pagada', 'Anulada') DEFAULT 'Pendiente',
  `pacienteId` INT NOT NULL,
  `obraSocialId` INT NOT NULL,
  CONSTRAINT `fk_facturas_paciente` FOREIGN KEY (`pacienteId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE,
  CONSTRAINT `fk_facturas_obrasocial` FOREIGN KEY (`obraSocialId`) REFERENCES `obrassociales` (`idObraSocial`) ON DELETE RESTRICT
);


-- ========================================================
-- 2. INSERTS
-- ========================================================

INSERT INTO `obrassociales` (`idObraSocial`, `nombre`, `numeroConvenio`, `beneficios`, `cobertura`) VALUES
(1, 'OSDE 210', 'CO-88421', 'Consultas ilimitadas, 40% en medicamentos', '80%'),
(2, 'Swiss Medical', 'CO-99312', 'Guardia libre, odontología preventiva', '100%'),
(3, 'Galeno Silver', 'CO-55214', 'Kinesiología hasta 20 sesiones/año', '70%'),
(4, 'PAMI', 'CO-11200', 'Medicamentos gratis crónicos, prótesis', '100%'),
(5, 'OSECAC', 'CO-44122', 'Plan materno infantil, médicos de cartilla', '60%');

INSERT INTO `roles` (`idRol`, `nombre`, `descripcion`) VALUES
(1, 'Administrador', 'Control total del sistema'),
(2, 'Médico', 'Atención clínica y prescripción'),
(3, 'Enfermero', 'Asistencia e ingresos'),
(4, 'Recepcionista', 'Gestión de turnos y caja'),
(5, 'Paciente', 'Usuario consumidor final'),
(6, 'Personal de Limpieza', 'Mantenimiento e higiene de consultorios');

INSERT INTO `usuarios` (`idUsuario`, `nombre`, `apellido`, `dni`, `fechaNacimiento`, `telefono`, `email`, `obraSocialId`, `rolId`) VALUES
(1, 'Juan Carlos', 'Gómez', '25412874', '1976-05-12', '1145887412', 'juan.gomez@email.com', 1, 5),
(2, 'María Paula', 'Rodríguez', '38451254', '1994-08-22', '1169854712', 'maria.medica@clinica.com', NULL, 2),
(3, 'Andrés Luis', 'Fernández', '14258963', '1955-11-02', '1130251478', 'andres.fer@email.com', 4, 5),
(4, 'Laura Inés', 'López', '32145874', '1986-02-14', '1158741254', 'laura.enfermera@clinica.com', NULL, 3),
(5, 'Carlos Javier', 'Pérez', '42154879', '2000-01-30', '1121548796', 'carlos.perez@email.com', 5, 5),
(6, 'Roberto', 'Sánchez', '28999111', '1980-04-10', '1122334455', 'roberto.limpieza@clinica.com', NULL, 6);

INSERT INTO `especialidades` (`idEspecialidad`, `nombre`, `descripcion`) VALUES
(1, 'Cardiología', 'Especialista en alta complejidad cardíaca'),
(2, 'Pediatría Clínica', 'Atención infantil y neonatal'),
(3, 'Enfermería General', 'Asistencia en piso e internaciones'),
(4, 'Enfermería de Triage', 'Clasificación de urgencia en guardia'),
(5, 'Medicina General', 'Atención primaria y clínica médica');

INSERT INTO `medico_especialidad` (`medicoId`, `especialidadId`) VALUES 
(2, 1);

INSERT INTO `consultorio` (`idHabitacion`, `numero`, `piso`, `tipo`, `estado`) VALUES
(1, '101', 1, 'Pediatría', 'Disponible'),
(2, '102', 1, 'Consultas Generales', 'Disponible'),
(3, '201', 2, 'Cardiología', 'En mantenimiento'),
(4, '202', 2, 'Ecografías y Rayos', 'Disponible'),
(5, 'G-01', 0, 'Shockroom Guardia', 'Ocupado');

INSERT INTO `articulos` (`idArticulo`, `nombre`, `descripcion`, `presentacion`, `tipoArticulo`, `stockActual`, `stockMinimo`) VALUES
(1, 'Ibuprofeno 600mg', 'Analgésico y antiinflamatorio', 'Comprimidos x 20', 'MEDICAMENTO', 150, 30),
(2, 'Amoxicilina 500mg', 'Antibiótico de amplio espectro', 'Cápsulas x 16', 'MEDICAMENTO', 90, 20),
(3, 'Paracetamol 1g', 'Antipirético y analgésico', 'Comprimidos x 40', 'MEDICAMENTO', 210, 45),
(4, 'Losartán 50mg', 'Antihipertensivo crónico', 'Comprimidos x 30', 'MEDICAMENTO', 80, 25),
(5, 'Clonazepam 0.5mg', 'Ansiolítico bajo receta archivada', 'Comprimidos x 30', 'MEDICAMENTO', 45, 15),
(6, 'Gasa estéril 10x10', 'Paquetes de gasas hidrófilas', 'Descartable', 'INSUMO', 450, 100),
(7, 'Jeringas 5ml', 'Jeringas con aguja descartable', 'Descartable', 'INSUMO', 320, 50),
(8, 'Guantes de látex M', 'Cajas de 100 unidades', 'Protección', 'INSUMO', 85, 20),
(9, 'Alcohol en gel 500ml', 'Dispensador desinfectante', 'Sanitizante', 'INSUMO', 40, 15),
(10, 'Barbijos quirúrgicos', 'Cajas de 50 unidades tricapa', 'Protección', 'INSUMO', 120, 30);

INSERT INTO `historiasclinicas` (`idHistoria`, `fechaApertura`, `antecedentes`, `alergia`, `observacion`, `usuarioId`) VALUES
(1, '2020-03-15', 'Hipertenso crónico, padre con antecedentes cardíacos.', 'Penicilina', 'Paciente regular', 1),
(2, '2024-01-10', 'Cirugía de apéndice en 2018.', 'Ninguna', 'Ninguna', 3),
(3, '2025-06-22', 'Asma infantil controlado.', 'Aspirina', 'Fumador social', 5);

INSERT INTO `consultas` (`idConsulta`, `fechaHora`, `motivo`, `diagnostico`, `observacion`, `historiaId`, `medicoId`) VALUES
(1, '2026-09-14 09:30:00', 'Palpitaciones ocasionales', 'Hipertensión arterial controlada', 'Se ajusta medicación diaria.', 1, 2),
(2, '2026-09-14 10:15:00', 'Dolor precordial fuerte', 'Angina inestable', 'Derivado de urgencia a Unidad Coronaria.', 2, 2);

INSERT INTO `recetas` (`idReceta`, `fecha`, `indicacion`, `consultaId`) VALUES
(1, '2026-09-14', 'Tomar por la mañana en ayunas de forma crónica.', 1),
(2, '2026-09-14', 'Administrar vía intravenosa en la internación.', 2);

INSERT INTO `detallesreceta` (`idDetalle`, `cantidad`, `dosis`, `frecuencia`, `duracion`, `recetaId`, `articuloId`) VALUES
(1, 2, '50mg', 'Cada 24 horas', '30 días', 1, 4),
(2, 1, '1g', 'Cada 8 horas', '3 días', 2, 3);

INSERT INTO `turnos` (`idTurno`, `fecha`, `horaInicio`, `horaFin`, `estado`, `motivo`, `pacienteId`, `medicoId`, `especialidadId`, `consultorioId`) VALUES
(1, '2026-09-14', '09:30:00', '10:00:00', 'Atendido', 'Control de rutina por presión', 1, 2, 1, 3),
(2, '2026-09-14', '11:00:00', '11:30:00', 'Ausente', 'Revisión de análisis', 3, 2, 1, 3);

INSERT INTO `ubicacionpaciente` (`idUbicacion`, `tipoAtencion`, `fechaIngreso`, `fechaSalida`, `pacienteId`, `medicoId`, `consultorioId`) VALUES
(1, 'TURNO_SIMPLE', '2026-09-14 09:20:00', '2026-09-14 09:50:00', 1, 2, 2),
(2, 'INTERNACION', '2026-09-14 10:05:00', NULL, 3, 2, 5),
(3, 'EMERGENCIA', '2026-09-14 13:45:00', '2026-09-14 14:15:00', 5, 2, 4);

INSERT INTO `guardia` (`idGuardia`, `fecha`, `horaInicio`, `horaFin`, `estado`) VALUES
(1, '2026-09-14', '08:00:00', '16:00:00', 'Activa');


-- ========================================================
-- 3. TRIGGERS
-- ========================================================

DELIMITER $$

-- --------------------------------------------------------
-- Turnos
-- --------------------------------------------------------
-- 1
CREATE TRIGGER `validar_turnos_duplicados_insert`
BEFORE INSERT ON `turnos`
FOR EACH ROW
BEGIN
    DECLARE v_rolMedico INT;
    DECLARE v_rolPaciente INT;

    IF NEW.horaFin <= NEW.horaInicio THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La hora de fin debe ser mayor a la hora de inicio del turno.';
    END IF;

    SELECT `rolId` INTO v_rolMedico FROM `usuarios` WHERE `idUsuario` = NEW.medicoId;
    IF v_rolMedico IS NULL OR v_rolMedico != 2 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario asignado como medico no posee el rol de Medico.';
    END IF;

    SELECT `rolId` INTO v_rolPaciente FROM `usuarios` WHERE `idUsuario` = NEW.pacienteId;
    IF v_rolPaciente IS NULL OR v_rolPaciente != 5 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario asignado como paciente no posee el rol de Paciente.';
    END IF;

    IF EXISTS (
        SELECT 1 FROM `turnos`
        WHERE `medicoId` = NEW.medicoId
          AND `fecha` = NEW.fecha
          AND `estado` NOT IN ('Cancelado', 'Anulado')
          AND (NEW.horaInicio < `horaFin` AND NEW.horaFin > `horaInicio`)
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El medico ya tiene un turno asignado en ese rango horario.';
    END IF;

    IF EXISTS (
        SELECT 1 FROM `turnos`
        WHERE `pacienteId` = NEW.pacienteId
          AND `fecha` = NEW.fecha
          AND `estado` NOT IN ('Cancelado', 'Anulado')
          AND (NEW.horaInicio < `horaFin` AND NEW.horaFin > `horaInicio`)
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El paciente ya posee un turno asignado en ese rango horario.';
    END IF;

    IF NEW.consultorioId IS NOT NULL AND EXISTS (
        SELECT 1 FROM `turnos`
        WHERE `consultorioId` = NEW.consultorioId
          AND `fecha` = NEW.fecha
          AND `estado` NOT IN ('Cancelado', 'Anulado')
          AND (NEW.horaInicio < `horaFin` AND NEW.horaFin > `horaInicio`)
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El consultorio ya esta reservado en ese rango horario.';
    END IF;
END$$

-- 2
CREATE TRIGGER `validar_turnos_duplicados_update`
BEFORE UPDATE ON `turnos`
FOR EACH ROW
BEGIN
    DECLARE v_rolMedico INT;
    DECLARE v_rolPaciente INT;

    IF NEW.horaFin <= NEW.horaInicio THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La hora de fin debe ser mayor a la hora de inicio del turno.';
    END IF;

    SELECT `rolId` INTO v_rolMedico FROM `usuarios` WHERE `idUsuario` = NEW.medicoId;
    IF v_rolMedico IS NULL OR v_rolMedico != 2 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario asignado como medico no posee el rol de Medico.';
    END IF;

    SELECT `rolId` INTO v_rolPaciente FROM `usuarios` WHERE `idUsuario` = NEW.pacienteId;
    IF v_rolPaciente IS NULL OR v_rolPaciente != 5 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario asignado como paciente no posee el rol de Paciente.';
    END IF;

    IF EXISTS (
        SELECT 1 FROM `turnos`
        WHERE `medicoId` = NEW.medicoId
          AND `fecha` = NEW.fecha
          AND `idTurno` != NEW.idTurno
          AND `estado` NOT IN ('Cancelado', 'Anulado')
          AND (NEW.horaInicio < `horaFin` AND NEW.horaFin > `horaInicio`)
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El medico ya tiene un turno asignado en ese rango horario.';
    END IF;

    IF EXISTS (
        SELECT 1 FROM `turnos`
        WHERE `pacienteId` = NEW.pacienteId
          AND `fecha` = NEW.fecha
          AND `idTurno` != NEW.idTurno
          AND `estado` NOT IN ('Cancelado', 'Anulado')
          AND (NEW.horaInicio < `horaFin` AND NEW.horaFin > `horaInicio`)
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El paciente ya posee un turno asignado en ese rango horario.';
    END IF;

    IF NEW.consultorioId IS NOT NULL AND EXISTS (
        SELECT 1 FROM `turnos`
        WHERE `consultorioId` = NEW.consultorioId
          AND `fecha` = NEW.fecha
          AND `idTurno` != NEW.idTurno
          AND `estado` NOT IN ('Cancelado', 'Anulado')
          AND (NEW.horaInicio < `horaFin` AND NEW.horaFin > `horaInicio`)
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El consultorio ya esta reservado en ese rango horario.';
    END IF;
END$$

-- --------------------------------------------------------
-- Stock
-- --------------------------------------------------------
-- 3
CREATE TRIGGER `validar_stock_antes_insert`
BEFORE INSERT ON `movimientos_stock`
FOR EACH ROW
BEGIN
    DECLARE v_stockActual INT;

    SELECT `stockActual` INTO v_stockActual FROM `articulos` WHERE `idArticulo` = NEW.idArticulo;

    IF NEW.tipoMovimiento = 'SALIDA' AND v_stockActual < NEW.cantidad THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Stock insuficiente para realizar el retiro del articulo.';
    END IF;

    IF NEW.tipoMovimiento = 'AJUSTE' AND NEW.cantidad < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El valor del ajuste de stock no puede ser negativo.';
    END IF;
END$$

-- 4
CREATE TRIGGER `actualizar_stock_despues_insert`
AFTER INSERT ON `movimientos_stock`
FOR EACH ROW
BEGIN
    IF NEW.tipoMovimiento = 'ENTRADA' THEN
        UPDATE `articulos` 
        SET `stockActual` = `stockActual` + NEW.cantidad 
        WHERE `idArticulo` = NEW.idArticulo;

    ELSEIF NEW.tipoMovimiento = 'SALIDA' THEN
        UPDATE `articulos` 
        SET `stockActual` = `stockActual` - NEW.cantidad 
        WHERE `idArticulo` = NEW.idArticulo;

    ELSEIF NEW.tipoMovimiento = 'AJUSTE' THEN
        UPDATE `articulos` 
        SET `stockActual` = NEW.cantidad 
        WHERE `idArticulo` = NEW.idArticulo;
    END IF;
END$$

-- --------------------------------------------------------
-- Ubicación Pacientes
-- --------------------------------------------------------
-- 5
CREATE TRIGGER `validar_ubicacion_paciente_insert`
BEFORE INSERT ON `ubicacionpaciente`
FOR EACH ROW
BEGIN
    IF EXISTS (
        SELECT 1 FROM `ubicacionpaciente`
        WHERE `pacienteId` = NEW.pacienteId
          AND `fechaSalida` IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El paciente ya figura activo en otro consultorio/habitación.';
    END IF;

    IF EXISTS (
        SELECT 1 FROM `ubicacionpaciente`
        WHERE `consultorioId` = NEW.consultorioId
          AND `fechaSalida` IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El consultorio seleccionado ya se encuentra ocupado por otro paciente.';
    END IF;
END$$

-- 6
CREATE TRIGGER `validar_ubicacion_paciente_update`
BEFORE UPDATE ON `ubicacionpaciente`
FOR EACH ROW
BEGIN
    IF EXISTS (
        SELECT 1 FROM `ubicacionpaciente`
        WHERE `pacienteId` = NEW.pacienteId
          AND `idUbicacion` != NEW.idUbicacion
          AND `fechaSalida` IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El paciente ya figura activo en otro consultorio/habitación.';
    END IF;

    IF EXISTS (
        SELECT 1 FROM `ubicacionpaciente`
        WHERE `consultorioId` = NEW.consultorioId
          AND `idUbicacion` != NEW.idUbicacion
          AND `fechaSalida` IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El consultorio seleccionado ya se encuentra ocupado por otro paciente.';
    END IF;
END$$

-- --------------------------------------------------------
-- Triage
-- --------------------------------------------------------
-- 7
CREATE TRIGGER `validar_triage_enfermero`
BEFORE INSERT ON `triage`
FOR EACH ROW
BEGIN
    DECLARE v_rolEnfermero INT;

    SELECT `rolId` INTO v_rolEnfermero FROM `usuarios` WHERE `idUsuario` = NEW.enfermeroId;
    IF v_rolEnfermero IS NULL OR v_rolEnfermero != 3 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario registrado en Triage debe poseer el rol de Enfermero.';
    END IF;
END$$

DELIMITER ;

-- ========================================================
-- 4. STORED PROCEDURES (CRUD)
-- ========================================================
-- Convenciones:
--   * Nomenclatura: sp_<tabla>_insertar | _actualizar | _listar | _eliminar
--   * INSERT y UPDATE en TODAS las tablas.
--   * SELECT ("listar") en TODAS las tablas, con parametros opcionales
--     (si se pasan en NULL, el procedure no filtra por ese campo). Esto
--     permite RECICLAR un mismo procedure para "traer todo", "traer uno
--     por id" o "traer filtrado", en vez de escribir uno para cada caso.
--   * DELETE solo en tablas "catalogo" de bajo riesgo (obrassociales,
--     roles, especialidades, medico_especialidad, consultorio, articulos).
--     En tablas historicas/clinicas/contables (usuarios, historiasclinicas,
--     consultas, recetas, detallesreceta, movimientos_stock, turnos,
--     ubicacionpaciente, guardia, triage, estudios, facturas) NO se borra
--     fisicamente: se actualiza el campo `estado` (Cancelado/Anulado/etc.)
--     para conservar la trazabilidad/auditoria de la clinica.
--   * Procedures reciclados explicitamente (se llaman desde otros
--     procedures o sirven para varios casos de uso a la vez):
--       - sp_usuarios_listar          -> reutilizado para listar medicos,
--                                        pacientes, enfermeros, etc. (filtro por rolId)
--       - sp_movimientos_stock_insertar -> llamado directamente y tambien
--                                        reutilizado dentro de sp_detallesreceta_insertar
--                                        para descontar stock al dispensar una receta
--       - sp_turnos_cambiar_estado    -> reutilizado para cancelar, anular,
--                                        marcar ausente o atendido un turno
--       - sp_ubicacionpaciente_dar_alta -> reutilizado para dar de alta a
--                                        un paciente sin importar el tipoAtencion

DELIMITER $$

-- ========================================================
-- 4.1 obrassociales
-- ========================================================
CREATE PROCEDURE `sp_obrassociales_insertar` (
    IN p_nombre VARCHAR(100),
    IN p_numeroConvenio VARCHAR(50),
    IN p_beneficios TEXT,
    IN p_cobertura VARCHAR(100),
    OUT p_nuevoId INT
)
BEGIN
    INSERT INTO `obrassociales` (`nombre`, `numeroConvenio`, `beneficios`, `cobertura`)
    VALUES (p_nombre, p_numeroConvenio, p_beneficios, p_cobertura);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE PROCEDURE `sp_obrassociales_actualizar` (
    IN p_id INT,
    IN p_nombre VARCHAR(100),
    IN p_numeroConvenio VARCHAR(50),
    IN p_beneficios TEXT,
    IN p_cobertura VARCHAR(100)
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `obrassociales` WHERE `idObraSocial` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La obra social indicada no existe.';
    END IF;

    UPDATE `obrassociales`
    SET `nombre` = p_nombre,
        `numeroConvenio` = p_numeroConvenio,
        `beneficios` = p_beneficios,
        `cobertura` = p_cobertura
    WHERE `idObraSocial` = p_id;
END$$

CREATE PROCEDURE `sp_obrassociales_listar` (
    IN p_id INT
)
BEGIN
    SELECT * FROM `obrassociales`
    WHERE (p_id IS NULL OR `idObraSocial` = p_id);
END$$

CREATE PROCEDURE `sp_obrassociales_eliminar` (
    IN p_id INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `obrassociales` WHERE `idObraSocial` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La obra social indicada no existe.';
    END IF;
    IF EXISTS (SELECT 1 FROM `usuarios` WHERE `obraSocialId` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se puede eliminar: hay usuarios afiliados a esta obra social.';
    END IF;
    IF EXISTS (SELECT 1 FROM `facturas` WHERE `obraSocialId` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se puede eliminar: existen facturas asociadas a esta obra social.';
    END IF;
    DELETE FROM `obrassociales` WHERE `idObraSocial` = p_id;
END$$

-- ========================================================
-- 4.2 roles
-- ========================================================
CREATE PROCEDURE `sp_roles_insertar` (
    IN p_nombre VARCHAR(100),
    IN p_descripcion TEXT,
    OUT p_nuevoId INT
)
BEGIN
    INSERT INTO `roles` (`nombre`, `descripcion`) VALUES (p_nombre, p_descripcion);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE PROCEDURE `sp_roles_actualizar` (
    IN p_id INT,
    IN p_nombre VARCHAR(100),
    IN p_descripcion TEXT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `roles` WHERE `idRol` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El rol indicado no existe.';
    END IF;
    UPDATE `roles` SET `nombre` = p_nombre, `descripcion` = p_descripcion
    WHERE `idRol` = p_id;
END$$

CREATE PROCEDURE `sp_roles_listar` (
    IN p_id INT
)
BEGIN
    SELECT * FROM `roles` WHERE (p_id IS NULL OR `idRol` = p_id);
END$$

CREATE PROCEDURE `sp_roles_eliminar` (
    IN p_id INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `roles` WHERE `idRol` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El rol indicado no existe.';
    END IF;
    IF EXISTS (SELECT 1 FROM `usuarios` WHERE `rolId` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se puede eliminar: hay usuarios con este rol asignado.';
    END IF;
    DELETE FROM `roles` WHERE `idRol` = p_id;
END$$

-- ========================================================
-- 4.3 usuarios
-- (sin DELETE: se conserva historial clinico/administrativo. Para dar de
--  baja a alguien se sugiere agregar en el futuro un campo `activo`.)
-- ========================================================
CREATE PROCEDURE `sp_usuarios_insertar` (
    IN p_nombre VARCHAR(100),
    IN p_apellido VARCHAR(100),
    IN p_dni VARCHAR(20),
    IN p_fechaNacimiento DATE,
    IN p_telefono VARCHAR(30),
    IN p_email VARCHAR(100),
    IN p_obraSocialId INT,
    IN p_rolId INT,
    OUT p_nuevoId INT
)
BEGIN
    INSERT INTO `usuarios`
        (`nombre`, `apellido`, `dni`, `fechaNacimiento`, `telefono`, `email`, `obraSocialId`, `rolId`)
    VALUES
        (p_nombre, p_apellido, p_dni, p_fechaNacimiento, p_telefono, p_email, p_obraSocialId, p_rolId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

-- Reciclado: sirve tanto para dar de baja (p_activo = 0) como para
-- reactivar (p_activo = 1) a un usuario, sin borrarlo fisicamente y sin
-- perder su historial clinico/administrativo asociado.
CREATE PROCEDURE `sp_usuarios_cambiar_estado` (
    IN p_id INT,
    IN p_activo TINYINT(1)
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `usuarios` WHERE `idUsuario` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El usuario indicado no existe.';
    END IF;
    UPDATE `usuarios` SET `activo` = p_activo WHERE `idUsuario` = p_id;
END$$

CREATE PROCEDURE `sp_usuarios_actualizar` (
    IN p_id INT,
    IN p_nombre VARCHAR(100),
    IN p_apellido VARCHAR(100),
    IN p_dni VARCHAR(20),
    IN p_fechaNacimiento DATE,
    IN p_telefono VARCHAR(30),
    IN p_email VARCHAR(100),
    IN p_obraSocialId INT,
    IN p_rolId INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `usuarios` WHERE `idUsuario` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El usuario indicado no existe.';
    END IF;

    UPDATE `usuarios`
    SET `nombre` = p_nombre,
        `apellido` = p_apellido,
        `dni` = p_dni,
        `fechaNacimiento` = p_fechaNacimiento,
        `telefono` = p_telefono,
        `email` = p_email,
        `obraSocialId` = p_obraSocialId,
        `rolId` = p_rolId
    WHERE `idUsuario` = p_id;
END$$

-- Reciclado: sirve para "listar usuarios", "listar por id", listar medicos
-- (p_rolId = 2), pacientes (p_rolId = 5), enfermeros (p_rolId = 3), etc.,
-- y ademas para traer solo los activos (p_soloActivos = 1) en pantallas
-- donde no queres mostrar personal/pacientes dados de baja.
CREATE PROCEDURE `sp_usuarios_listar` (
    IN p_id INT,
    IN p_rolId INT,
    IN p_soloActivos TINYINT
)
BEGIN
    SELECT * FROM `usuarios`
    WHERE (p_id IS NULL OR `idUsuario` = p_id)
      AND (p_rolId IS NULL OR `rolId` = p_rolId)
      AND (p_soloActivos IS NULL OR p_soloActivos = 0 OR `activo` = 1);
END$$

-- ========================================================
-- 4.4 especialidades
-- ========================================================
CREATE PROCEDURE `sp_especialidades_insertar` (
    IN p_nombre VARCHAR(100),
    IN p_descripcion TEXT,
    OUT p_nuevoId INT
)
BEGIN
    INSERT INTO `especialidades` (`nombre`, `descripcion`) VALUES (p_nombre, p_descripcion);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE PROCEDURE `sp_especialidades_actualizar` (
    IN p_id INT,
    IN p_nombre VARCHAR(100),
    IN p_descripcion TEXT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `especialidades` WHERE `idEspecialidad` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La especialidad indicada no existe.';
    END IF;
    UPDATE `especialidades` SET `nombre` = p_nombre, `descripcion` = p_descripcion
    WHERE `idEspecialidad` = p_id;
END$$

CREATE PROCEDURE `sp_especialidades_listar` (
    IN p_id INT
)
BEGIN
    SELECT * FROM `especialidades` WHERE (p_id IS NULL OR `idEspecialidad` = p_id);
END$$

CREATE PROCEDURE `sp_especialidades_eliminar` (
    IN p_id INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `especialidades` WHERE `idEspecialidad` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La especialidad indicada no existe.';
    END IF;
    DELETE FROM `especialidades` WHERE `idEspecialidad` = p_id;
END$$

-- ========================================================
-- 4.5 medico_especialidad
-- ========================================================
CREATE PROCEDURE `sp_medicoespecialidad_insertar` (
    IN p_medicoId INT,
    IN p_especialidadId INT,
    OUT p_nuevoId INT
)
BEGIN
    IF (SELECT `rolId` FROM `usuarios` WHERE `idUsuario` = p_medicoId) != 2 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El usuario indicado no posee el rol de Medico.';
    END IF;

    INSERT INTO `medico_especialidad` (`medicoId`, `especialidadId`)
    VALUES (p_medicoId, p_especialidadId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE PROCEDURE `sp_medicoespecialidad_actualizar` (
    IN p_id INT,
    IN p_medicoId INT,
    IN p_especialidadId INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `medico_especialidad` WHERE `idMedicoEspecialidad` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La relacion medico-especialidad indicada no existe.';
    END IF;
    UPDATE `medico_especialidad`
    SET `medicoId` = p_medicoId, `especialidadId` = p_especialidadId
    WHERE `idMedicoEspecialidad` = p_id;
END$$

-- Reciclado: listar todo, uno por id, o todas las especialidades de un medico
CREATE PROCEDURE `sp_medicoespecialidad_listar` (
    IN p_id INT,
    IN p_medicoId INT
)
BEGIN
    SELECT me.`idMedicoEspecialidad`, me.`medicoId`, me.`especialidadId`, e.`nombre` AS especialidad
    FROM `medico_especialidad` me
    INNER JOIN `especialidades` e ON e.`idEspecialidad` = me.`especialidadId`
    WHERE (p_id IS NULL OR me.`idMedicoEspecialidad` = p_id)
      AND (p_medicoId IS NULL OR me.`medicoId` = p_medicoId);
END$$

CREATE PROCEDURE `sp_medicoespecialidad_eliminar` (
    IN p_id INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `medico_especialidad` WHERE `idMedicoEspecialidad` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La relacion medico-especialidad indicada no existe.';
    END IF;
    DELETE FROM `medico_especialidad` WHERE `idMedicoEspecialidad` = p_id;
END$$

-- ========================================================
-- 4.6 consultorio
-- ========================================================
CREATE PROCEDURE `sp_consultorio_insertar` (
    IN p_numero VARCHAR(20),
    IN p_piso INT,
    IN p_tipo VARCHAR(50),
    IN p_estado ENUM('Disponible', 'Ocupado', 'En mantenimiento'),
    OUT p_nuevoId INT
)
BEGIN
    INSERT INTO `consultorio` (`numero`, `piso`, `tipo`, `estado`)
    VALUES (p_numero, p_piso, p_tipo, COALESCE(p_estado, 'Disponible'));
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE PROCEDURE `sp_consultorio_actualizar` (
    IN p_id INT,
    IN p_numero VARCHAR(20),
    IN p_piso INT,
    IN p_tipo VARCHAR(50),
    IN p_estado ENUM('Disponible', 'Ocupado', 'En mantenimiento')
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `consultorio` WHERE `idHabitacion` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El consultorio indicado no existe.';
    END IF;
    UPDATE `consultorio`
    SET `numero` = p_numero, `piso` = p_piso, `tipo` = p_tipo, `estado` = p_estado
    WHERE `idHabitacion` = p_id;
END$$

-- Reciclado: listar todos, uno por id, o solo los disponibles (p_estado = 'Disponible')
CREATE PROCEDURE `sp_consultorio_listar` (
    IN p_id INT,
    IN p_estado ENUM('Disponible', 'Ocupado', 'En mantenimiento')
)
BEGIN
    SELECT * FROM `consultorio`
    WHERE (p_id IS NULL OR `idHabitacion` = p_id)
      AND (p_estado IS NULL OR `estado` = p_estado);
END$$

CREATE PROCEDURE `sp_consultorio_eliminar` (
    IN p_id INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `consultorio` WHERE `idHabitacion` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El consultorio indicado no existe.';
    END IF;
    IF EXISTS (SELECT 1 FROM `turnos` WHERE `consultorioId` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se puede eliminar: hay turnos asociados a este consultorio.';
    END IF;
    IF EXISTS (SELECT 1 FROM `ubicacionpaciente` WHERE `consultorioId` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se puede eliminar: existe historial de internaciones/atenciones en este consultorio.';
    END IF;
    DELETE FROM `consultorio` WHERE `idHabitacion` = p_id;
END$$

DELIMITER ;

DELIMITER $$

-- ========================================================
-- 4.7 articulos
-- Nota: `stockActual` NO se edita a mano en el UPDATE: siempre se mueve
-- a traves de `movimientos_stock` (ver 4.8), para que el stock sea
-- siempre trazable. El DELETE solo funciona con articulos sin
-- movimientos ni recetas asociadas: las FK de `movimientos_stock` y
-- `detallesreceta` son ON DELETE RESTRICT, y el procedure ademas valida
-- esto antes de intentar el borrado para devolver un mensaje claro.
-- ========================================================
CREATE PROCEDURE `sp_articulos_insertar` (
    IN p_nombre VARCHAR(100),
    IN p_descripcion TEXT,
    IN p_presentacion VARCHAR(100),
    IN p_tipoArticulo ENUM('MEDICAMENTO','INSUMO'),
    IN p_stockInicial INT,
    IN p_stockMinimo INT,
    OUT p_nuevoId INT
)
BEGIN
    INSERT INTO `articulos`
        (`nombre`, `descripcion`, `presentacion`, `tipoArticulo`, `stockActual`, `stockMinimo`)
    VALUES
        (p_nombre, p_descripcion, p_presentacion, p_tipoArticulo, COALESCE(p_stockInicial, 0), COALESCE(p_stockMinimo, 0));
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE PROCEDURE `sp_articulos_actualizar` (
    IN p_id INT,
    IN p_nombre VARCHAR(100),
    IN p_descripcion TEXT,
    IN p_presentacion VARCHAR(100),
    IN p_tipoArticulo ENUM('MEDICAMENTO','INSUMO'),
    IN p_stockMinimo INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `articulos` WHERE `idArticulo` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El articulo indicado no existe.';
    END IF;
    UPDATE `articulos`
    SET `nombre` = p_nombre,
        `descripcion` = p_descripcion,
        `presentacion` = p_presentacion,
        `tipoArticulo` = p_tipoArticulo,
        `stockMinimo` = p_stockMinimo
    WHERE `idArticulo` = p_id;
END$$

-- Reciclado: listar todos, uno por id, o solo los que estan bajo el minimo
-- (p_soloBajoStock = 1) para pantallas de alerta de reposicion.
CREATE PROCEDURE `sp_articulos_listar` (
    IN p_id INT,
    IN p_soloBajoStock TINYINT
)
BEGIN
    SELECT * FROM `articulos`
    WHERE (p_id IS NULL OR `idArticulo` = p_id)
      AND (p_soloBajoStock IS NULL OR p_soloBajoStock = 0 OR `stockActual` <= `stockMinimo`);
END$$

CREATE PROCEDURE `sp_articulos_eliminar` (
    IN p_id INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `articulos` WHERE `idArticulo` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El articulo indicado no existe.';
    END IF;
    IF EXISTS (SELECT 1 FROM `movimientos_stock` WHERE `idArticulo` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se puede eliminar: el articulo tiene movimientos de stock registrados.';
    END IF;
    IF EXISTS (SELECT 1 FROM `detallesreceta` WHERE `articuloId` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se puede eliminar: el articulo figura en detalles de recetas ya emitidas.';
    END IF;
    DELETE FROM `articulos` WHERE `idArticulo` = p_id;
END$$

-- ========================================================
-- 4.8 movimientos_stock
-- (sin DELETE: es un registro de auditoria de stock, no debe borrarse)
-- Este INSERT es el mas "reciclado" del sistema: se usa directo desde el
-- modulo de farmacia/deposito, y TAMBIEN se llama desde adentro de
-- `sp_detallesreceta_insertar` (ver 4.12) para descontar stock automaticamente
-- cuando se dispensa una receta, sin duplicar la logica de validacion/
-- actualizacion de stock que ya resuelven los triggers de la tabla.
-- ========================================================
CREATE PROCEDURE `sp_movimientos_stock_insertar` (
    IN p_idArticulo INT,
    IN p_tipoMovimiento ENUM('ENTRADA','SALIDA','AJUSTE'),
    IN p_cantidad INT,
    IN p_motivo TEXT,
    IN p_usuarioId INT,
    OUT p_nuevoId INT
)
BEGIN
    INSERT INTO `movimientos_stock` (`idArticulo`, `tipoMovimiento`, `cantidad`, `motivo`, `usuarioId`)
    VALUES (p_idArticulo, p_tipoMovimiento, p_cantidad, p_motivo, p_usuarioId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

-- Solo se permite corregir el motivo cargado: cantidad/tipo/articulo quedan
-- fijos para no romper la trazabilidad del stock ya actualizado por el trigger.
CREATE PROCEDURE `sp_movimientos_stock_actualizar` (
    IN p_id INT,
    IN p_motivo TEXT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `movimientos_stock` WHERE `idMovimiento` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El movimiento de stock indicado no existe.';
    END IF;
    UPDATE `movimientos_stock` SET `motivo` = p_motivo WHERE `idMovimiento` = p_id;
END$$

-- Reciclado: listar todo, uno por id, o el kardex/historial de un articulo puntual
CREATE PROCEDURE `sp_movimientos_stock_listar` (
    IN p_id INT,
    IN p_idArticulo INT
)
BEGIN
    SELECT * FROM `movimientos_stock`
    WHERE (p_id IS NULL OR `idMovimiento` = p_id)
      AND (p_idArticulo IS NULL OR `idArticulo` = p_idArticulo)
    ORDER BY `fechaHora` DESC;
END$$

-- ========================================================
-- 4.9 historiasclinicas
-- (sin DELETE: registro clinico permanente del paciente)
-- ========================================================
CREATE PROCEDURE `sp_historiasclinicas_insertar` (
    IN p_fechaApertura DATE,
    IN p_antecedentes TEXT,
    IN p_alergia TEXT,
    IN p_observacion TEXT,
    IN p_usuarioId INT,
    OUT p_nuevoId INT
)
BEGIN
    INSERT INTO `historiasclinicas` (`fechaApertura`, `antecedentes`, `alergia`, `observacion`, `usuarioId`)
    VALUES (p_fechaApertura, p_antecedentes, p_alergia, p_observacion, p_usuarioId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE PROCEDURE `sp_historiasclinicas_actualizar` (
    IN p_id INT,
    IN p_antecedentes TEXT,
    IN p_alergia TEXT,
    IN p_observacion TEXT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `historiasclinicas` WHERE `idHistoria` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La historia clinica indicada no existe.';
    END IF;
    UPDATE `historiasclinicas`
    SET `antecedentes` = p_antecedentes, `alergia` = p_alergia, `observacion` = p_observacion
    WHERE `idHistoria` = p_id;
END$$

-- Reciclado: listar todas, una por id de historia, o buscarla por el paciente
CREATE PROCEDURE `sp_historiasclinicas_listar` (
    IN p_id INT,
    IN p_usuarioId INT
)
BEGIN
    SELECT * FROM `historiasclinicas`
    WHERE (p_id IS NULL OR `idHistoria` = p_id)
      AND (p_usuarioId IS NULL OR `usuarioId` = p_usuarioId);
END$$

-- ========================================================
-- 4.10 consultas
-- (sin DELETE: registro clinico permanente)
-- ========================================================
CREATE PROCEDURE `sp_consultas_insertar` (
    IN p_fechaHora DATETIME,
    IN p_motivo TEXT,
    IN p_diagnostico TEXT,
    IN p_observacion TEXT,
    IN p_historiaId INT,
    IN p_medicoId INT,
    OUT p_nuevoId INT
)
BEGIN
    INSERT INTO `consultas` (`fechaHora`, `motivo`, `diagnostico`, `observacion`, `historiaId`, `medicoId`)
    VALUES (p_fechaHora, p_motivo, p_diagnostico, p_observacion, p_historiaId, p_medicoId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE PROCEDURE `sp_consultas_actualizar` (
    IN p_id INT,
    IN p_motivo TEXT,
    IN p_diagnostico TEXT,
    IN p_observacion TEXT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `consultas` WHERE `idConsulta` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La consulta indicada no existe.';
    END IF;
    UPDATE `consultas`
    SET `motivo` = p_motivo, `diagnostico` = p_diagnostico, `observacion` = p_observacion
    WHERE `idConsulta` = p_id;
END$$

-- Reciclado: listar todas, una por id, o filtradas por historia clinica / medico
CREATE PROCEDURE `sp_consultas_listar` (
    IN p_id INT,
    IN p_historiaId INT,
    IN p_medicoId INT
)
BEGIN
    SELECT * FROM `consultas`
    WHERE (p_id IS NULL OR `idConsulta` = p_id)
      AND (p_historiaId IS NULL OR `historiaId` = p_historiaId)
      AND (p_medicoId IS NULL OR `medicoId` = p_medicoId)
    ORDER BY `fechaHora` DESC;
END$$

-- ========================================================
-- 4.11 recetas
-- (sin DELETE: documento clinico/legal permanente)
-- ========================================================
CREATE PROCEDURE `sp_recetas_insertar` (
    IN p_fecha DATE,
    IN p_indicacion TEXT,
    IN p_consultaId INT,
    OUT p_nuevoId INT
)
BEGIN
    INSERT INTO `recetas` (`fecha`, `indicacion`, `consultaId`)
    VALUES (p_fecha, p_indicacion, p_consultaId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE PROCEDURE `sp_recetas_actualizar` (
    IN p_id INT,
    IN p_indicacion TEXT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `recetas` WHERE `idReceta` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La receta indicada no existe.';
    END IF;
    UPDATE `recetas` SET `indicacion` = p_indicacion WHERE `idReceta` = p_id;
END$$

-- Reciclado: listar todas, una por id, o todas las recetas de una consulta
CREATE PROCEDURE `sp_recetas_listar` (
    IN p_id INT,
    IN p_consultaId INT
)
BEGIN
    SELECT * FROM `recetas`
    WHERE (p_id IS NULL OR `idReceta` = p_id)
      AND (p_consultaId IS NULL OR `consultaId` = p_consultaId);
END$$

-- ========================================================
-- 4.12 detallesreceta
-- (sin DELETE: item de un documento clinico/legal ya emitido)
-- El INSERT reutiliza (RECICLA) `sp_movimientos_stock_insertar` para
-- descontar automaticamente el stock del articulo dispensado.
-- ========================================================
CREATE PROCEDURE `sp_detallesreceta_insertar` (
    IN p_cantidad INT,
    IN p_dosis VARCHAR(100),
    IN p_frecuencia VARCHAR(100),
    IN p_duracion VARCHAR(100),
    IN p_recetaId INT,
    IN p_articuloId INT,
    IN p_usuarioId INT,
    OUT p_nuevoId INT
)
BEGIN
    DECLARE v_idMovimiento INT;

    -- Sin esta transaccion, si `sp_movimientos_stock_insertar` falla por
    -- stock insuficiente, el INSERT en `detallesreceta` ya habria quedado
    -- guardado (cada statement se autocommitea por separado), dejando un
    -- item de receta sin el descuento de stock correspondiente.
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    INSERT INTO `detallesreceta`
        (`cantidad`, `dosis`, `frecuencia`, `duracion`, `recetaId`, `articuloId`)
    VALUES
        (p_cantidad, p_dosis, p_frecuencia, p_duracion, p_recetaId, p_articuloId);
    SET p_nuevoId = LAST_INSERT_ID();

    -- Reciclaje: se llama al procedure de movimientos de stock para
    -- registrar la SALIDA del articulo dispensado (el trigger de
    -- `movimientos_stock` valida stock suficiente y descuenta automaticamente).
    CALL `sp_movimientos_stock_insertar`(
        p_articuloId, 'SALIDA', p_cantidad,
        CONCAT('Dispensado por receta #', p_recetaId),
        p_usuarioId, v_idMovimiento
    );

    COMMIT;
END$$

CREATE PROCEDURE `sp_detallesreceta_actualizar` (
    IN p_id INT,
    IN p_dosis VARCHAR(100),
    IN p_frecuencia VARCHAR(100),
    IN p_duracion VARCHAR(100)
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `detallesreceta` WHERE `idDetalle` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El detalle de receta indicado no existe.';
    END IF;
    UPDATE `detallesreceta`
    SET `dosis` = p_dosis, `frecuencia` = p_frecuencia, `duracion` = p_duracion
    WHERE `idDetalle` = p_id;
END$$

-- Reciclado: listar todos, uno por id, o todos los detalles de una receta
CREATE PROCEDURE `sp_detallesreceta_listar` (
    IN p_id INT,
    IN p_recetaId INT
)
BEGIN
    SELECT * FROM `detallesreceta`
    WHERE (p_id IS NULL OR `idDetalle` = p_id)
      AND (p_recetaId IS NULL OR `recetaId` = p_recetaId);
END$$

DELIMITER ;

DELIMITER $$

-- ========================================================
-- 4.13 turnos
-- (sin DELETE fisico: se usa `estado` = Cancelado/Anulado, asi queda
-- registro de que el turno existio. Las validaciones de superposicion
-- de horarios ya las resuelven los triggers de la tabla.)
-- ========================================================
CREATE PROCEDURE `sp_turnos_insertar` (
    IN p_fecha DATE,
    IN p_horaInicio TIME,
    IN p_horaFin TIME,
    IN p_motivo TEXT,
    IN p_pacienteId INT,
    IN p_medicoId INT,
    IN p_especialidadId INT,
    IN p_consultorioId INT,
    OUT p_nuevoId INT
)
BEGIN
    INSERT INTO `turnos`
        (`fecha`, `horaInicio`, `horaFin`, `motivo`, `pacienteId`, `medicoId`, `especialidadId`, `consultorioId`)
    VALUES
        (p_fecha, p_horaInicio, p_horaFin, p_motivo, p_pacienteId, p_medicoId, p_especialidadId, p_consultorioId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE PROCEDURE `sp_turnos_actualizar` (
    IN p_id INT,
    IN p_fecha DATE,
    IN p_horaInicio TIME,
    IN p_horaFin TIME,
    IN p_motivo TEXT,
    IN p_consultorioId INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `turnos` WHERE `idTurno` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El turno indicado no existe.';
    END IF;
    UPDATE `turnos`
    SET `fecha` = p_fecha, `horaInicio` = p_horaInicio, `horaFin` = p_horaFin,
        `motivo` = p_motivo, `consultorioId` = p_consultorioId
    WHERE `idTurno` = p_id;
END$$

-- Reciclado: un unico procedure para cancelar, anular, marcar ausente o
-- marcar atendido un turno (evita 4 procedures casi identicos).
CREATE PROCEDURE `sp_turnos_cambiar_estado` (
    IN p_id INT,
    IN p_estado ENUM('Pendiente', 'Atendido', 'Ausente', 'Cancelado', 'Anulado')
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `turnos` WHERE `idTurno` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El turno indicado no existe.';
    END IF;
    UPDATE `turnos` SET `estado` = p_estado WHERE `idTurno` = p_id;
END$$

-- Reciclado: listar todos, uno por id, la agenda de un medico, los turnos
-- de un paciente, y/o filtrar por fecha/estado; todo con el mismo procedure.
CREATE PROCEDURE `sp_turnos_listar` (
    IN p_id INT,
    IN p_pacienteId INT,
    IN p_medicoId INT,
    IN p_fecha DATE,
    IN p_estado ENUM('Pendiente', 'Atendido', 'Ausente', 'Cancelado', 'Anulado')
)
BEGIN
    SELECT * FROM `turnos`
    WHERE (p_id IS NULL OR `idTurno` = p_id)
      AND (p_pacienteId IS NULL OR `pacienteId` = p_pacienteId)
      AND (p_medicoId IS NULL OR `medicoId` = p_medicoId)
      AND (p_fecha IS NULL OR `fecha` = p_fecha)
      AND (p_estado IS NULL OR `estado` = p_estado)
    ORDER BY `fecha`, `horaInicio`;
END$$

-- ========================================================
-- 4.14 ubicacionpaciente
-- (sin DELETE: es el historial de internaciones/atenciones del paciente)
-- ========================================================
CREATE PROCEDURE `sp_ubicacionpaciente_insertar` (
    IN p_tipoAtencion ENUM('TURNO_SIMPLE','EMERGENCIA','INTERNACION'),
    IN p_fechaIngreso DATETIME,
    IN p_pacienteId INT,
    IN p_medicoId INT,
    IN p_consultorioId INT,
    OUT p_nuevoId INT
)
BEGIN
    INSERT INTO `ubicacionpaciente`
        (`tipoAtencion`, `fechaIngreso`, `pacienteId`, `medicoId`, `consultorioId`)
    VALUES
        (COALESCE(p_tipoAtencion, 'TURNO_SIMPLE'), p_fechaIngreso, p_pacienteId, p_medicoId, p_consultorioId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE PROCEDURE `sp_ubicacionpaciente_actualizar` (
    IN p_id INT,
    IN p_medicoId INT,
    IN p_consultorioId INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `ubicacionpaciente` WHERE `idUbicacion` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El registro de ubicacion indicado no existe.';
    END IF;
    UPDATE `ubicacionpaciente`
    SET `medicoId` = p_medicoId, `consultorioId` = p_consultorioId
    WHERE `idUbicacion` = p_id;
END$$

-- Reciclado: procedure unico para dar de alta a un paciente sin importar
-- si es TURNO_SIMPLE, EMERGENCIA o INTERNACION (mismo cierre de fechaSalida).
CREATE PROCEDURE `sp_ubicacionpaciente_dar_alta` (
    IN p_id INT,
    IN p_fechaSalida DATETIME
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `ubicacionpaciente` WHERE `idUbicacion` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El registro de ubicacion indicado no existe.';
    END IF;
    UPDATE `ubicacionpaciente`
    SET `fechaSalida` = COALESCE(p_fechaSalida, NOW())
    WHERE `idUbicacion` = p_id;
END$$

-- Reciclado: listar todos, uno por id, historial de un paciente, o solo
-- los que estan actualmente activos (p_soloActivos = 1) para el dashboard
-- de ocupacion de consultorios/camas.
CREATE PROCEDURE `sp_ubicacionpaciente_listar` (
    IN p_id INT,
    IN p_pacienteId INT,
    IN p_soloActivos TINYINT
)
BEGIN
    SELECT * FROM `ubicacionpaciente`
    WHERE (p_id IS NULL OR `idUbicacion` = p_id)
      AND (p_pacienteId IS NULL OR `pacienteId` = p_pacienteId)
      AND (p_soloActivos IS NULL OR p_soloActivos = 0 OR `fechaSalida` IS NULL);
END$$

-- ========================================================
-- 4.15 guardia
-- (sin DELETE: si se borra en cascada se pierden los triage asociados;
-- para cerrarla se usa `estado` = Finalizada/Suspendida)
-- ========================================================
CREATE PROCEDURE `sp_guardia_insertar` (
    IN p_fecha DATE,
    IN p_horaInicio TIME,
    IN p_horaFin TIME,
    OUT p_nuevoId INT
)
BEGIN
    INSERT INTO `guardia` (`fecha`, `horaInicio`, `horaFin`)
    VALUES (p_fecha, p_horaInicio, p_horaFin);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE PROCEDURE `sp_guardia_actualizar` (
    IN p_id INT,
    IN p_horaInicio TIME,
    IN p_horaFin TIME,
    IN p_estado ENUM('Activa', 'Finalizada', 'Suspendida')
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `guardia` WHERE `idGuardia` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La guardia indicada no existe.';
    END IF;
    UPDATE `guardia`
    SET `horaInicio` = p_horaInicio, `horaFin` = p_horaFin, `estado` = p_estado
    WHERE `idGuardia` = p_id;
END$$

-- Reciclado: listar todas, una por id, o filtrar por estado (ej. 'Activa'
-- para saber la guardia vigente donde cargar un triage nuevo)
CREATE PROCEDURE `sp_guardia_listar` (
    IN p_id INT,
    IN p_estado ENUM('Activa', 'Finalizada', 'Suspendida')
)
BEGIN
    SELECT * FROM `guardia`
    WHERE (p_id IS NULL OR `idGuardia` = p_id)
      AND (p_estado IS NULL OR `estado` = p_estado);
END$$

-- ========================================================
-- 4.16 triage
-- (sin DELETE: registro clinico de la guardia)
-- ========================================================
CREATE PROCEDURE `sp_triage_insertar` (
    IN p_fecha DATETIME,
    IN p_prioridad INT,
    IN p_estado VARCHAR(50),
    IN p_observacion TEXT,
    IN p_pacienteId INT,
    IN p_guardiaId INT,
    IN p_enfermeroId INT,
    OUT p_nuevoId INT
)
BEGIN
    INSERT INTO `triage`
        (`fecha`, `prioridad`, `estado`, `observacion`, `pacienteId`, `guardiaId`, `enfermeroId`)
    VALUES
        (p_fecha, p_prioridad, p_estado, p_observacion, p_pacienteId, p_guardiaId, p_enfermeroId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE PROCEDURE `sp_triage_actualizar` (
    IN p_id INT,
    IN p_prioridad INT,
    IN p_estado VARCHAR(50),
    IN p_observacion TEXT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `triage` WHERE `idTriage` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El registro de triage indicado no existe.';
    END IF;
    UPDATE `triage`
    SET `prioridad` = p_prioridad, `estado` = p_estado, `observacion` = p_observacion
    WHERE `idTriage` = p_id;
END$$

-- Reciclado: listar todos, uno por id, todos los de una guardia, o el
-- historial de triage de un paciente puntual
CREATE PROCEDURE `sp_triage_listar` (
    IN p_id INT,
    IN p_guardiaId INT,
    IN p_pacienteId INT
)
BEGIN
    SELECT * FROM `triage`
    WHERE (p_id IS NULL OR `idTriage` = p_id)
      AND (p_guardiaId IS NULL OR `guardiaId` = p_guardiaId)
      AND (p_pacienteId IS NULL OR `pacienteId` = p_pacienteId)
    ORDER BY `prioridad` ASC, `fecha` ASC;
END$$

-- ========================================================
-- 4.17 estudios
-- (sin DELETE: registro clinico permanente)
-- ========================================================
CREATE PROCEDURE `sp_estudios_insertar` (
    IN p_tipo VARCHAR(100),
    IN p_fecha DATETIME,
    IN p_resultado TEXT,
    IN p_archivo VARCHAR(255),
    IN p_pacienteId INT,
    OUT p_nuevoId INT
)
BEGIN
    INSERT INTO `estudios` (`tipo`, `fecha`, `resultado`, `archivo`, `pacienteId`)
    VALUES (p_tipo, p_fecha, p_resultado, p_archivo, p_pacienteId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE PROCEDURE `sp_estudios_actualizar` (
    IN p_id INT,
    IN p_resultado TEXT,
    IN p_archivo VARCHAR(255)
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `estudios` WHERE `idEstudio` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El estudio indicado no existe.';
    END IF;
    UPDATE `estudios` SET `resultado` = p_resultado, `archivo` = p_archivo
    WHERE `idEstudio` = p_id;
END$$

-- Reciclado: listar todos, uno por id, o el historial de estudios de un paciente
CREATE PROCEDURE `sp_estudios_listar` (
    IN p_id INT,
    IN p_pacienteId INT
)
BEGIN
    SELECT * FROM `estudios`
    WHERE (p_id IS NULL OR `idEstudio` = p_id)
      AND (p_pacienteId IS NULL OR `pacienteId` = p_pacienteId)
    ORDER BY `fecha` DESC;
END$$

-- ========================================================
-- 4.18 facturas
-- (sin DELETE: documento contable; para anular se usa `estado` = Anulada)
-- ========================================================
CREATE PROCEDURE `sp_facturas_insertar` (
    IN p_fecha DATETIME,
    IN p_importeTotal DECIMAL(10,2),
    IN p_pacienteId INT,
    IN p_obraSocialId INT,
    OUT p_nuevoId INT
)
BEGIN
    INSERT INTO `facturas` (`fecha`, `importeTotal`, `pacienteId`, `obraSocialId`)
    VALUES (p_fecha, p_importeTotal, p_pacienteId, p_obraSocialId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

-- Reciclado: un mismo procedure sirve para corregir el importe mientras la
-- factura esta Pendiente, y tambien para marcarla Pagada o Anulada.
CREATE PROCEDURE `sp_facturas_actualizar` (
    IN p_id INT,
    IN p_importeTotal DECIMAL(10,2),
    IN p_estado ENUM('Pendiente', 'Pagada', 'Anulada')
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `facturas` WHERE `idFactura` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La factura indicada no existe.';
    END IF;
    UPDATE `facturas`
    SET `importeTotal` = COALESCE(p_importeTotal, `importeTotal`),
        `estado` = COALESCE(p_estado, `estado`)
    WHERE `idFactura` = p_id;
END$$

-- Reciclado: listar todas, una por id, todas las de un paciente, o filtrar
-- por estado (ej. 'Pendiente' para la pantalla de cobranzas)
CREATE PROCEDURE `sp_facturas_listar` (
    IN p_id INT,
    IN p_pacienteId INT,
    IN p_estado ENUM('Pendiente', 'Pagada', 'Anulada')
)
BEGIN
    SELECT * FROM `facturas`
    WHERE (p_id IS NULL OR `idFactura` = p_id)
      AND (p_pacienteId IS NULL OR `pacienteId` = p_pacienteId)
      AND (p_estado IS NULL OR `estado` = p_estado)
    ORDER BY `fecha` DESC;
END$$

DELIMITER ;
