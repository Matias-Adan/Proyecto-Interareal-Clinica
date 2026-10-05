-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 05-10-2026 a las 21:05:20
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
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_articulos_actualizar` (IN `p_id` INT, IN `p_nombre` VARCHAR(100), IN `p_descripcion` TEXT, IN `p_presentacion` VARCHAR(100), IN `p_tipoArticulo` ENUM('MEDICAMENTO','INSUMO'), IN `p_stockMinimo` INT)   BEGIN
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

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_articulos_eliminar` (IN `p_id` INT)   BEGIN
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

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_articulos_insertar` (IN `p_nombre` VARCHAR(100), IN `p_descripcion` TEXT, IN `p_presentacion` VARCHAR(100), IN `p_tipoArticulo` ENUM('MEDICAMENTO','INSUMO'), IN `p_stockInicial` INT, IN `p_stockMinimo` INT, OUT `p_nuevoId` INT)   BEGIN
    INSERT INTO `articulos`
        (`nombre`, `descripcion`, `presentacion`, `tipoArticulo`, `stockActual`, `stockMinimo`)
    VALUES
        (p_nombre, p_descripcion, p_presentacion, p_tipoArticulo, COALESCE(p_stockInicial, 0), COALESCE(p_stockMinimo, 0));
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_articulos_listar` (IN `p_id` INT, IN `p_soloBajoStock` TINYINT)   BEGIN
    SELECT * FROM `articulos`
    WHERE (p_id IS NULL OR `idArticulo` = p_id)
      AND (p_soloBajoStock IS NULL OR p_soloBajoStock = 0 OR `stockActual` <= `stockMinimo`);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_consultas_actualizar` (IN `p_id` INT, IN `p_motivo` TEXT, IN `p_diagnostico` TEXT, IN `p_observacion` TEXT)   BEGIN
    IF NOT EXISTS (SELECT 1 FROM `consultas` WHERE `idConsulta` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La consulta indicada no existe.';
    END IF;
    UPDATE `consultas`
    SET `motivo` = p_motivo, `diagnostico` = p_diagnostico, `observacion` = p_observacion
    WHERE `idConsulta` = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_consultas_insertar` (IN `p_fechaHora` DATETIME, IN `p_motivo` TEXT, IN `p_diagnostico` TEXT, IN `p_observacion` TEXT, IN `p_historiaId` INT, IN `p_medicoId` INT, OUT `p_nuevoId` INT)   BEGIN
    INSERT INTO `consultas` (`fechaHora`, `motivo`, `diagnostico`, `observacion`, `historiaId`, `medicoId`)
    VALUES (p_fechaHora, p_motivo, p_diagnostico, p_observacion, p_historiaId, p_medicoId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_consultas_listar` (IN `p_id` INT, IN `p_historiaId` INT, IN `p_medicoId` INT)   BEGIN
    SELECT * FROM `consultas`
    WHERE (p_id IS NULL OR `idConsulta` = p_id)
      AND (p_historiaId IS NULL OR `historiaId` = p_historiaId)
      AND (p_medicoId IS NULL OR `medicoId` = p_medicoId)
    ORDER BY `fechaHora` DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_consultorio_actualizar` (IN `p_id` INT, IN `p_numero` VARCHAR(20), IN `p_piso` INT, IN `p_tipo` VARCHAR(50), IN `p_estado` ENUM('Disponible','Ocupado','En mantenimiento'))   BEGIN
    IF NOT EXISTS (SELECT 1 FROM `consultorio` WHERE `idHabitacion` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El consultorio indicado no existe.';
    END IF;
    UPDATE `consultorio`
    SET `numero` = p_numero, `piso` = p_piso, `tipo` = p_tipo, `estado` = p_estado
    WHERE `idHabitacion` = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_consultorio_eliminar` (IN `p_id` INT)   BEGIN
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

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_consultorio_insertar` (IN `p_numero` VARCHAR(20), IN `p_piso` INT, IN `p_tipo` VARCHAR(50), IN `p_estado` ENUM('Disponible','Ocupado','En mantenimiento'), OUT `p_nuevoId` INT)   BEGIN
    INSERT INTO `consultorio` (`numero`, `piso`, `tipo`, `estado`)
    VALUES (p_numero, p_piso, p_tipo, COALESCE(p_estado, 'Disponible'));
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_consultorio_listar` (IN `p_id` INT, IN `p_estado` ENUM('Disponible','Ocupado','En mantenimiento'))   BEGIN
    SELECT * FROM `consultorio`
    WHERE (p_id IS NULL OR `idHabitacion` = p_id)
      AND (p_estado IS NULL OR `estado` = p_estado);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_detallesreceta_actualizar` (IN `p_id` INT, IN `p_dosis` VARCHAR(100), IN `p_frecuencia` VARCHAR(100), IN `p_duracion` VARCHAR(100))   BEGIN
    IF NOT EXISTS (SELECT 1 FROM `detallesreceta` WHERE `idDetalle` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El detalle de receta indicado no existe.';
    END IF;
    UPDATE `detallesreceta`
    SET `dosis` = p_dosis, `frecuencia` = p_frecuencia, `duracion` = p_duracion
    WHERE `idDetalle` = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_detallesreceta_insertar` (IN `p_cantidad` INT, IN `p_dosis` VARCHAR(100), IN `p_frecuencia` VARCHAR(100), IN `p_duracion` VARCHAR(100), IN `p_recetaId` INT, IN `p_articuloId` INT, IN `p_usuarioId` INT, OUT `p_nuevoId` INT)   BEGIN
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

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_detallesreceta_listar` (IN `p_id` INT, IN `p_recetaId` INT)   BEGIN
    SELECT * FROM `detallesreceta`
    WHERE (p_id IS NULL OR `idDetalle` = p_id)
      AND (p_recetaId IS NULL OR `recetaId` = p_recetaId);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_especialidades_actualizar` (IN `p_id` INT, IN `p_nombre` VARCHAR(100), IN `p_descripcion` TEXT)   BEGIN
    IF NOT EXISTS (SELECT 1 FROM `especialidades` WHERE `idEspecialidad` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La especialidad indicada no existe.';
    END IF;
    UPDATE `especialidades` SET `nombre` = p_nombre, `descripcion` = p_descripcion
    WHERE `idEspecialidad` = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_especialidades_eliminar` (IN `p_id` INT)   BEGIN
    IF NOT EXISTS (SELECT 1 FROM `especialidades` WHERE `idEspecialidad` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La especialidad indicada no existe.';
    END IF;
    DELETE FROM `especialidades` WHERE `idEspecialidad` = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_especialidades_insertar` (IN `p_nombre` VARCHAR(100), IN `p_descripcion` TEXT, OUT `p_nuevoId` INT)   BEGIN
    INSERT INTO `especialidades` (`nombre`, `descripcion`) VALUES (p_nombre, p_descripcion);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_especialidades_listar` (IN `p_id` INT)   BEGIN
    SELECT * FROM `especialidades` WHERE (p_id IS NULL OR `idEspecialidad` = p_id);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_estudios_actualizar` (IN `p_id` INT, IN `p_resultado` TEXT, IN `p_archivo` VARCHAR(255))   BEGIN
    IF NOT EXISTS (SELECT 1 FROM `estudios` WHERE `idEstudio` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El estudio indicado no existe.';
    END IF;
    UPDATE `estudios` SET `resultado` = p_resultado, `archivo` = p_archivo
    WHERE `idEstudio` = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_estudios_insertar` (IN `p_tipo` VARCHAR(100), IN `p_fecha` DATETIME, IN `p_resultado` TEXT, IN `p_archivo` VARCHAR(255), IN `p_pacienteId` INT, OUT `p_nuevoId` INT)   BEGIN
    INSERT INTO `estudios` (`tipo`, `fecha`, `resultado`, `archivo`, `pacienteId`)
    VALUES (p_tipo, p_fecha, p_resultado, p_archivo, p_pacienteId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_estudios_listar` (IN `p_id` INT, IN `p_pacienteId` INT)   BEGIN
    SELECT * FROM `estudios`
    WHERE (p_id IS NULL OR `idEstudio` = p_id)
      AND (p_pacienteId IS NULL OR `pacienteId` = p_pacienteId)
    ORDER BY `fecha` DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_facturas_actualizar` (IN `p_id` INT, IN `p_importeTotal` DECIMAL(10,2), IN `p_estado` ENUM('Pendiente','Pagada','Anulada'))   BEGIN
    IF NOT EXISTS (SELECT 1 FROM `facturas` WHERE `idFactura` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La factura indicada no existe.';
    END IF;
    UPDATE `facturas`
    SET `importeTotal` = COALESCE(p_importeTotal, `importeTotal`),
        `estado` = COALESCE(p_estado, `estado`)
    WHERE `idFactura` = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_facturas_insertar` (IN `p_fecha` DATETIME, IN `p_importeTotal` DECIMAL(10,2), IN `p_pacienteId` INT, IN `p_obraSocialId` INT, OUT `p_nuevoId` INT)   BEGIN
    INSERT INTO `facturas` (`fecha`, `importeTotal`, `pacienteId`, `obraSocialId`)
    VALUES (p_fecha, p_importeTotal, p_pacienteId, p_obraSocialId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_facturas_listar` (IN `p_id` INT, IN `p_pacienteId` INT, IN `p_estado` ENUM('Pendiente','Pagada','Anulada'))   BEGIN
    SELECT * FROM `facturas`
    WHERE (p_id IS NULL OR `idFactura` = p_id)
      AND (p_pacienteId IS NULL OR `pacienteId` = p_pacienteId)
      AND (p_estado IS NULL OR `estado` = p_estado)
    ORDER BY `fecha` DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_guardia_actualizar` (IN `p_id` INT, IN `p_horaInicio` TIME, IN `p_horaFin` TIME, IN `p_estado` ENUM('Activa','Finalizada','Suspendida'))   BEGIN
    IF NOT EXISTS (SELECT 1 FROM `guardia` WHERE `idGuardia` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La guardia indicada no existe.';
    END IF;
    UPDATE `guardia`
    SET `horaInicio` = p_horaInicio, `horaFin` = p_horaFin, `estado` = p_estado
    WHERE `idGuardia` = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_guardia_insertar` (IN `p_fecha` DATE, IN `p_horaInicio` TIME, IN `p_horaFin` TIME, OUT `p_nuevoId` INT)   BEGIN
    INSERT INTO `guardia` (`fecha`, `horaInicio`, `horaFin`)
    VALUES (p_fecha, p_horaInicio, p_horaFin);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_guardia_listar` (IN `p_id` INT, IN `p_estado` ENUM('Activa','Finalizada','Suspendida'))   BEGIN
    SELECT * FROM `guardia`
    WHERE (p_id IS NULL OR `idGuardia` = p_id)
      AND (p_estado IS NULL OR `estado` = p_estado);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_historiasclinicas_actualizar` (IN `p_id` INT, IN `p_antecedentes` TEXT, IN `p_alergia` TEXT, IN `p_observacion` TEXT)   BEGIN
    IF NOT EXISTS (SELECT 1 FROM `historiasclinicas` WHERE `idHistoria` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La historia clinica indicada no existe.';
    END IF;
    UPDATE `historiasclinicas`
    SET `antecedentes` = p_antecedentes, `alergia` = p_alergia, `observacion` = p_observacion
    WHERE `idHistoria` = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_historiasclinicas_insertar` (IN `p_fechaApertura` DATE, IN `p_antecedentes` TEXT, IN `p_alergia` TEXT, IN `p_observacion` TEXT, IN `p_usuarioId` INT, OUT `p_nuevoId` INT)   BEGIN
    INSERT INTO `historiasclinicas` (`fechaApertura`, `antecedentes`, `alergia`, `observacion`, `usuarioId`)
    VALUES (p_fechaApertura, p_antecedentes, p_alergia, p_observacion, p_usuarioId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_historiasclinicas_listar` (IN `p_id` INT, IN `p_usuarioId` INT)   BEGIN
    SELECT * FROM `historiasclinicas`
    WHERE (p_id IS NULL OR `idHistoria` = p_id)
      AND (p_usuarioId IS NULL OR `usuarioId` = p_usuarioId);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_medicoespecialidad_actualizar` (IN `p_id` INT, IN `p_medicoId` INT, IN `p_especialidadId` INT)   BEGIN
    IF NOT EXISTS (SELECT 1 FROM `medico_especialidad` WHERE `idMedicoEspecialidad` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La relacion medico-especialidad indicada no existe.';
    END IF;
    UPDATE `medico_especialidad`
    SET `medicoId` = p_medicoId, `especialidadId` = p_especialidadId
    WHERE `idMedicoEspecialidad` = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_medicoespecialidad_eliminar` (IN `p_id` INT)   BEGIN
    IF NOT EXISTS (SELECT 1 FROM `medico_especialidad` WHERE `idMedicoEspecialidad` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La relacion medico-especialidad indicada no existe.';
    END IF;
    DELETE FROM `medico_especialidad` WHERE `idMedicoEspecialidad` = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_medicoespecialidad_insertar` (IN `p_medicoId` INT, IN `p_especialidadId` INT, OUT `p_nuevoId` INT)   BEGIN
    IF (SELECT `rolId` FROM `usuarios` WHERE `idUsuario` = p_medicoId) != 2 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El usuario indicado no posee el rol de Medico.';
    END IF;

    INSERT INTO `medico_especialidad` (`medicoId`, `especialidadId`)
    VALUES (p_medicoId, p_especialidadId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_medicoespecialidad_listar` (IN `p_id` INT, IN `p_medicoId` INT)   BEGIN
    SELECT me.`idMedicoEspecialidad`, me.`medicoId`, me.`especialidadId`, e.`nombre` AS especialidad
    FROM `medico_especialidad` me
    INNER JOIN `especialidades` e ON e.`idEspecialidad` = me.`especialidadId`
    WHERE (p_id IS NULL OR me.`idMedicoEspecialidad` = p_id)
      AND (p_medicoId IS NULL OR me.`medicoId` = p_medicoId);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_movimientos_stock_actualizar` (IN `p_id` INT, IN `p_motivo` TEXT)   BEGIN
    IF NOT EXISTS (SELECT 1 FROM `movimientos_stock` WHERE `idMovimiento` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El movimiento de stock indicado no existe.';
    END IF;
    UPDATE `movimientos_stock` SET `motivo` = p_motivo WHERE `idMovimiento` = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_movimientos_stock_insertar` (IN `p_idArticulo` INT, IN `p_tipoMovimiento` ENUM('ENTRADA','SALIDA','AJUSTE'), IN `p_cantidad` INT, IN `p_motivo` TEXT, IN `p_usuarioId` INT, OUT `p_nuevoId` INT)   BEGIN
    INSERT INTO `movimientos_stock` (`idArticulo`, `tipoMovimiento`, `cantidad`, `motivo`, `usuarioId`)
    VALUES (p_idArticulo, p_tipoMovimiento, p_cantidad, p_motivo, p_usuarioId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_movimientos_stock_listar` (IN `p_id` INT, IN `p_idArticulo` INT)   BEGIN
    SELECT * FROM `movimientos_stock`
    WHERE (p_id IS NULL OR `idMovimiento` = p_id)
      AND (p_idArticulo IS NULL OR `idArticulo` = p_idArticulo)
    ORDER BY `fechaHora` DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_obrassociales_actualizar` (IN `p_id` INT, IN `p_nombre` VARCHAR(100), IN `p_numeroConvenio` VARCHAR(50), IN `p_beneficios` TEXT, IN `p_cobertura` VARCHAR(100))   BEGIN
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

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_obrassociales_eliminar` (IN `p_id` INT)   BEGIN
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

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_obrassociales_insertar` (IN `p_nombre` VARCHAR(100), IN `p_numeroConvenio` VARCHAR(50), IN `p_beneficios` TEXT, IN `p_cobertura` VARCHAR(100), OUT `p_nuevoId` INT)   BEGIN
    INSERT INTO `obrassociales` (`nombre`, `numeroConvenio`, `beneficios`, `cobertura`)
    VALUES (p_nombre, p_numeroConvenio, p_beneficios, p_cobertura);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_obrassociales_listar` (IN `p_id` INT)   BEGIN
    SELECT * FROM `obrassociales`
    WHERE (p_id IS NULL OR `idObraSocial` = p_id);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_recetas_actualizar` (IN `p_id` INT, IN `p_indicacion` TEXT)   BEGIN
    IF NOT EXISTS (SELECT 1 FROM `recetas` WHERE `idReceta` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La receta indicada no existe.';
    END IF;
    UPDATE `recetas` SET `indicacion` = p_indicacion WHERE `idReceta` = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_recetas_insertar` (IN `p_fecha` DATE, IN `p_indicacion` TEXT, IN `p_consultaId` INT, OUT `p_nuevoId` INT)   BEGIN
    INSERT INTO `recetas` (`fecha`, `indicacion`, `consultaId`)
    VALUES (p_fecha, p_indicacion, p_consultaId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_recetas_listar` (IN `p_id` INT, IN `p_consultaId` INT)   BEGIN
    SELECT * FROM `recetas`
    WHERE (p_id IS NULL OR `idReceta` = p_id)
      AND (p_consultaId IS NULL OR `consultaId` = p_consultaId);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_roles_actualizar` (IN `p_id` INT, IN `p_nombre` VARCHAR(100), IN `p_descripcion` TEXT)   BEGIN
    IF NOT EXISTS (SELECT 1 FROM `roles` WHERE `idRol` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El rol indicado no existe.';
    END IF;
    UPDATE `roles` SET `nombre` = p_nombre, `descripcion` = p_descripcion
    WHERE `idRol` = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_roles_eliminar` (IN `p_id` INT)   BEGIN
    IF NOT EXISTS (SELECT 1 FROM `roles` WHERE `idRol` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El rol indicado no existe.';
    END IF;
    IF EXISTS (SELECT 1 FROM `usuarios` WHERE `rolId` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se puede eliminar: hay usuarios con este rol asignado.';
    END IF;
    DELETE FROM `roles` WHERE `idRol` = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_roles_insertar` (IN `p_nombre` VARCHAR(100), IN `p_descripcion` TEXT, OUT `p_nuevoId` INT)   BEGIN
    INSERT INTO `roles` (`nombre`, `descripcion`) VALUES (p_nombre, p_descripcion);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_roles_listar` (IN `p_id` INT)   BEGIN
    SELECT * FROM `roles` WHERE (p_id IS NULL OR `idRol` = p_id);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_triage_actualizar` (IN `p_id` INT, IN `p_prioridad` INT, IN `p_estado` VARCHAR(50), IN `p_observacion` TEXT)   BEGIN
    IF NOT EXISTS (SELECT 1 FROM `triage` WHERE `idTriage` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El registro de triage indicado no existe.';
    END IF;
    UPDATE `triage`
    SET `prioridad` = p_prioridad, `estado` = p_estado, `observacion` = p_observacion
    WHERE `idTriage` = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_triage_insertar` (IN `p_fecha` DATETIME, IN `p_prioridad` INT, IN `p_estado` VARCHAR(50), IN `p_observacion` TEXT, IN `p_pacienteId` INT, IN `p_guardiaId` INT, IN `p_enfermeroId` INT, OUT `p_nuevoId` INT)   BEGIN
    INSERT INTO `triage`
        (`fecha`, `prioridad`, `estado`, `observacion`, `pacienteId`, `guardiaId`, `enfermeroId`)
    VALUES
        (p_fecha, p_prioridad, p_estado, p_observacion, p_pacienteId, p_guardiaId, p_enfermeroId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_triage_listar` (IN `p_id` INT, IN `p_guardiaId` INT, IN `p_pacienteId` INT)   BEGIN
    SELECT * FROM `triage`
    WHERE (p_id IS NULL OR `idTriage` = p_id)
      AND (p_guardiaId IS NULL OR `guardiaId` = p_guardiaId)
      AND (p_pacienteId IS NULL OR `pacienteId` = p_pacienteId)
    ORDER BY `prioridad` ASC, `fecha` ASC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_turnos_actualizar` (IN `p_id` INT, IN `p_fecha` DATE, IN `p_horaInicio` TIME, IN `p_horaFin` TIME, IN `p_motivo` TEXT, IN `p_consultorioId` INT)   BEGIN
    IF NOT EXISTS (SELECT 1 FROM `turnos` WHERE `idTurno` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El turno indicado no existe.';
    END IF;
    UPDATE `turnos`
    SET `fecha` = p_fecha, `horaInicio` = p_horaInicio, `horaFin` = p_horaFin,
        `motivo` = p_motivo, `consultorioId` = p_consultorioId
    WHERE `idTurno` = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_turnos_cambiar_estado` (IN `p_id` INT, IN `p_estado` ENUM('Pendiente','Atendido','Ausente','Cancelado','Anulado'))   BEGIN
    IF NOT EXISTS (SELECT 1 FROM `turnos` WHERE `idTurno` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El turno indicado no existe.';
    END IF;
    UPDATE `turnos` SET `estado` = p_estado WHERE `idTurno` = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_turnos_insertar` (IN `p_fecha` DATE, IN `p_horaInicio` TIME, IN `p_horaFin` TIME, IN `p_motivo` TEXT, IN `p_pacienteId` INT, IN `p_medicoId` INT, IN `p_especialidadId` INT, IN `p_consultorioId` INT, OUT `p_nuevoId` INT)   BEGIN
    INSERT INTO `turnos`
        (`fecha`, `horaInicio`, `horaFin`, `motivo`, `pacienteId`, `medicoId`, `especialidadId`, `consultorioId`)
    VALUES
        (p_fecha, p_horaInicio, p_horaFin, p_motivo, p_pacienteId, p_medicoId, p_especialidadId, p_consultorioId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_turnos_listar` (IN `p_id` INT, IN `p_pacienteId` INT, IN `p_medicoId` INT, IN `p_fecha` DATE, IN `p_estado` ENUM('Pendiente','Atendido','Ausente','Cancelado','Anulado'))   BEGIN
    SELECT * FROM `turnos`
    WHERE (p_id IS NULL OR `idTurno` = p_id)
      AND (p_pacienteId IS NULL OR `pacienteId` = p_pacienteId)
      AND (p_medicoId IS NULL OR `medicoId` = p_medicoId)
      AND (p_fecha IS NULL OR `fecha` = p_fecha)
      AND (p_estado IS NULL OR `estado` = p_estado)
    ORDER BY `fecha`, `horaInicio`;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ubicacionpaciente_actualizar` (IN `p_id` INT, IN `p_medicoId` INT, IN `p_consultorioId` INT)   BEGIN
    IF NOT EXISTS (SELECT 1 FROM `ubicacionpaciente` WHERE `idUbicacion` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El registro de ubicacion indicado no existe.';
    END IF;
    UPDATE `ubicacionpaciente`
    SET `medicoId` = p_medicoId, `consultorioId` = p_consultorioId
    WHERE `idUbicacion` = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ubicacionpaciente_dar_alta` (IN `p_id` INT, IN `p_fechaSalida` DATETIME)   BEGIN
    IF NOT EXISTS (SELECT 1 FROM `ubicacionpaciente` WHERE `idUbicacion` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El registro de ubicacion indicado no existe.';
    END IF;
    UPDATE `ubicacionpaciente`
    SET `fechaSalida` = COALESCE(p_fechaSalida, NOW())
    WHERE `idUbicacion` = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ubicacionpaciente_insertar` (IN `p_tipoAtencion` ENUM('TURNO_SIMPLE','EMERGENCIA','INTERNACION'), IN `p_fechaIngreso` DATETIME, IN `p_pacienteId` INT, IN `p_medicoId` INT, IN `p_consultorioId` INT, OUT `p_nuevoId` INT)   BEGIN
    INSERT INTO `ubicacionpaciente`
        (`tipoAtencion`, `fechaIngreso`, `pacienteId`, `medicoId`, `consultorioId`)
    VALUES
        (COALESCE(p_tipoAtencion, 'TURNO_SIMPLE'), p_fechaIngreso, p_pacienteId, p_medicoId, p_consultorioId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ubicacionpaciente_listar` (IN `p_id` INT, IN `p_pacienteId` INT, IN `p_soloActivos` TINYINT)   BEGIN
    SELECT * FROM `ubicacionpaciente`
    WHERE (p_id IS NULL OR `idUbicacion` = p_id)
      AND (p_pacienteId IS NULL OR `pacienteId` = p_pacienteId)
      AND (p_soloActivos IS NULL OR p_soloActivos = 0 OR `fechaSalida` IS NULL);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_usuarios_actualizar` (IN `p_id` INT, IN `p_nombre` VARCHAR(100), IN `p_apellido` VARCHAR(100), IN `p_dni` VARCHAR(20), IN `p_fechaNacimiento` DATE, IN `p_telefono` VARCHAR(30), IN `p_email` VARCHAR(100), IN `p_obraSocialId` INT, IN `p_rolId` INT)   BEGIN
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

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_usuarios_cambiar_estado` (IN `p_id` INT, IN `p_activo` TINYINT(1))   BEGIN
    IF NOT EXISTS (SELECT 1 FROM `usuarios` WHERE `idUsuario` = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El usuario indicado no existe.';
    END IF;
    UPDATE `usuarios` SET `activo` = p_activo WHERE `idUsuario` = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_usuarios_insertar` (IN `p_nombre` VARCHAR(100), IN `p_apellido` VARCHAR(100), IN `p_dni` VARCHAR(20), IN `p_fechaNacimiento` DATE, IN `p_telefono` VARCHAR(30), IN `p_email` VARCHAR(100), IN `p_obraSocialId` INT, IN `p_rolId` INT, OUT `p_nuevoId` INT)   BEGIN
    INSERT INTO `usuarios`
        (`nombre`, `apellido`, `dni`, `fechaNacimiento`, `telefono`, `email`, `obraSocialId`, `rolId`)
    VALUES
        (p_nombre, p_apellido, p_dni, p_fechaNacimiento, p_telefono, p_email, p_obraSocialId, p_rolId);
    SET p_nuevoId = LAST_INSERT_ID();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_usuarios_listar` (IN `p_id` INT, IN `p_rolId` INT, IN `p_soloActivos` TINYINT)   BEGIN
    SELECT * FROM `usuarios`
    WHERE (p_id IS NULL OR `idUsuario` = p_id)
      AND (p_rolId IS NULL OR `rolId` = p_rolId)
      AND (p_soloActivos IS NULL OR p_soloActivos = 0 OR `activo` = 1);
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `articulos`
--

CREATE TABLE `articulos` (
  `idArticulo` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `presentacion` varchar(100) DEFAULT NULL,
  `tipoArticulo` enum('MEDICAMENTO','INSUMO') NOT NULL,
  `stockActual` int(11) DEFAULT 0,
  `stockMinimo` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `articulos`
--

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
  `medicoId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `consultas`
--

INSERT INTO `consultas` (`idConsulta`, `fechaHora`, `motivo`, `diagnostico`, `observacion`, `historiaId`, `medicoId`) VALUES
(1, '2026-09-14 09:30:00', 'Palpitaciones ocasionales', 'Hipertensión arterial controlada', 'Se ajusta medicación diaria.', 1, 2),
(2, '2026-09-14 10:15:00', 'Dolor precordial fuerte', 'Angina inestable', 'Derivado de urgencia a Unidad Coronaria.', 2, 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `consultorio`
--

CREATE TABLE `consultorio` (
  `idHabitacion` int(11) NOT NULL,
  `numero` varchar(20) NOT NULL,
  `piso` int(11) DEFAULT NULL,
  `tipo` varchar(50) DEFAULT NULL,
  `estado` enum('Disponible','Ocupado','En mantenimiento') DEFAULT 'Disponible'
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
  `articuloId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `detallesreceta`
--

INSERT INTO `detallesreceta` (`idDetalle`, `cantidad`, `dosis`, `frecuencia`, `duracion`, `recetaId`, `articuloId`) VALUES
(1, 2, '50mg', 'Cada 24 horas', '30 días', 1, 4),
(2, 1, '1g', 'Cada 8 horas', '3 días', 2, 3);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `especialidades`
--

CREATE TABLE `especialidades` (
  `idEspecialidad` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `especialidades`
--

INSERT INTO `especialidades` (`idEspecialidad`, `nombre`, `descripcion`) VALUES
(1, 'Cardiología', 'Especialista en alta complejidad cardíaca'),
(2, 'Pediatría Clínica', 'Atención infantil y neonatal'),
(3, 'Enfermería General', 'Asistencia en piso e internaciones'),
(4, 'Enfermería de Triage', 'Clasificación de urgencia en guardia'),
(5, 'Medicina General', 'Atención primaria y clínica médica');

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
  `estado` enum('Pendiente','Pagada','Anulada') DEFAULT 'Pendiente',
  `pacienteId` int(11) NOT NULL,
  `obraSocialId` int(11) NOT NULL
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
  `estado` enum('Activa','Finalizada','Suspendida') DEFAULT 'Activa'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `guardia`
--

INSERT INTO `guardia` (`idGuardia`, `fecha`, `horaInicio`, `horaFin`, `estado`) VALUES
(1, '2026-09-14', '08:00:00', '16:00:00', 'Activa');

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
(3, '2025-06-22', 'Asma infantil controlado.', 'Aspirina', 'Fumador social', 5);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `medico_especialidad`
--

CREATE TABLE `medico_especialidad` (
  `idMedicoEspecialidad` int(11) NOT NULL,
  `medicoId` int(11) NOT NULL,
  `especialidadId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `medico_especialidad`
--

INSERT INTO `medico_especialidad` (`idMedicoEspecialidad`, `medicoId`, `especialidadId`) VALUES
(1, 2, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `movimientos_stock`
--

CREATE TABLE `movimientos_stock` (
  `idMovimiento` int(11) NOT NULL,
  `idArticulo` int(11) NOT NULL,
  `tipoMovimiento` enum('ENTRADA','SALIDA','AJUSTE') NOT NULL,
  `cantidad` int(11) NOT NULL,
  `motivo` text NOT NULL,
  `fechaHora` datetime DEFAULT current_timestamp(),
  `usuarioId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Disparadores `movimientos_stock`
--
DELIMITER $$
CREATE TRIGGER `actualizar_stock_despues_insert` AFTER INSERT ON `movimientos_stock` FOR EACH ROW BEGIN
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
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `validar_stock_antes_insert` BEFORE INSERT ON `movimientos_stock` FOR EACH ROW BEGIN
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
END
$$
DELIMITER ;

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

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `permisos`
--

CREATE TABLE `permisos` (
  `idPermiso` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `descripcion` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `personal`
--

CREATE TABLE `personal` (
  `idPersonal` int(11) NOT NULL,
  `trabajo` varchar(40) NOT NULL,
  `descripcion` varchar(100) NOT NULL,
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

--
-- Volcado de datos para la tabla `recetas`
--

INSERT INTO `recetas` (`idReceta`, `fecha`, `indicacion`, `consultaId`) VALUES
(1, '2026-09-14', 'Tomar por la mañana en ayunas de forma crónica.', 1),
(2, '2026-09-14', 'Administrar vía intravenosa en la internación.', 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roldeusuario`
--

CREATE TABLE `roldeusuario` (
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

--
-- Volcado de datos para la tabla `roles`
--

INSERT INTO `roles` (`idRol`, `nombre`, `descripcion`) VALUES
(1, 'Administrador', 'Control total del sistema'),
(2, 'Médico', 'Atención clínica y prescripción'),
(3, 'Enfermero', 'Asistencia e ingresos'),
(4, 'Recepcionista', 'Gestión de turnos y caja'),
(5, 'Paciente', 'Usuario consumidor final'),
(6, 'Personal de Limpieza', 'Mantenimiento e higiene de consultorios');

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
-- Disparadores `triage`
--
DELIMITER $$
CREATE TRIGGER `validar_triage_enfermero` BEFORE INSERT ON `triage` FOR EACH ROW BEGIN
    DECLARE v_rolEnfermero INT;

    SELECT `rolId` INTO v_rolEnfermero FROM `usuarios` WHERE `idUsuario` = NEW.enfermeroId;
    IF v_rolEnfermero IS NULL OR v_rolEnfermero != 3 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario registrado en Triage debe poseer el rol de Enfermero.';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `turnos`
--

CREATE TABLE `turnos` (
  `idTurno` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `horaInicio` time NOT NULL,
  `horaFin` time NOT NULL,
  `estado` enum('Pendiente','Atendido','Ausente','Cancelado','Anulado') DEFAULT 'Pendiente',
  `motivo` text DEFAULT NULL,
  `pacienteId` int(11) NOT NULL,
  `medicoId` int(11) NOT NULL,
  `especialidadId` int(11) DEFAULT NULL,
  `consultorioId` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `turnos`
--

INSERT INTO `turnos` (`idTurno`, `fecha`, `horaInicio`, `horaFin`, `estado`, `motivo`, `pacienteId`, `medicoId`, `especialidadId`, `consultorioId`) VALUES
(1, '2026-09-14', '09:30:00', '10:00:00', 'Atendido', 'Control de rutina por presión', 1, 2, 1, 3),
(2, '2026-09-14', '11:00:00', '11:30:00', 'Ausente', 'Revisión de análisis', 3, 2, 1, 3);

--
-- Disparadores `turnos`
--
DELIMITER $$
CREATE TRIGGER `validar_turnos_duplicados_insert` BEFORE INSERT ON `turnos` FOR EACH ROW BEGIN
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
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `validar_turnos_duplicados_update` BEFORE UPDATE ON `turnos` FOR EACH ROW BEGIN
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
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ubicacionpaciente`
--

CREATE TABLE `ubicacionpaciente` (
  `idUbicacion` int(11) NOT NULL,
  `tipoAtencion` enum('TURNO_SIMPLE','EMERGENCIA','INTERNACION') NOT NULL DEFAULT 'TURNO_SIMPLE',
  `fechaIngreso` datetime NOT NULL,
  `fechaSalida` datetime DEFAULT NULL,
  `pacienteId` int(11) NOT NULL,
  `medicoId` int(11) DEFAULT NULL,
  `consultorioId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `ubicacionpaciente`
--

INSERT INTO `ubicacionpaciente` (`idUbicacion`, `tipoAtencion`, `fechaIngreso`, `fechaSalida`, `pacienteId`, `medicoId`, `consultorioId`) VALUES
(1, 'TURNO_SIMPLE', '2026-09-14 09:20:00', '2026-09-14 09:50:00', 1, 2, 2),
(2, 'INTERNACION', '2026-09-14 10:05:00', NULL, 3, 2, 5),
(3, 'EMERGENCIA', '2026-09-14 13:45:00', '2026-09-14 14:15:00', 5, 2, 4);

--
-- Disparadores `ubicacionpaciente`
--
DELIMITER $$
CREATE TRIGGER `validar_ubicacion_paciente_insert` BEFORE INSERT ON `ubicacionpaciente` FOR EACH ROW BEGIN
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
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `validar_ubicacion_paciente_update` BEFORE UPDATE ON `ubicacionpaciente` FOR EACH ROW BEGIN
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
END
$$
DELIMITER ;

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
  `obraSocialId` int(11) DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`idUsuario`, `nombre`, `apellido`, `dni`, `fechaNacimiento`, `telefono`, `email`, `obraSocialId`, `activo`) VALUES
(1, 'Juan Carlos', 'Gómez', '25412874', '1976-05-12', '1145887412', 'juan.gomez@email.com', 1, 1),
(2, 'María Paula', 'Rodríguez', '38451254', '1994-08-22', '1169854712', 'maria.medica@clinica.com', NULL, 1),
(3, 'Andrés Luis', 'Fernández', '14258963', '1955-11-02', '1130251478', 'andres.fer@email.com', 4, 1),
(4, 'Laura Inés', 'López', '32145874', '1986-02-14', '1158741254', 'laura.enfermera@clinica.com', NULL, 1),
(5, 'Carlos Javier', 'Pérez', '42154879', '2000-01-30', '1121548796', 'carlos.perez@email.com', 5, 1),
(6, 'Roberto', 'Sánchez', '28999111', '1980-04-10', '1122334455', 'roberto.limpieza@clinica.com', NULL, 1);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `articulos`
--
ALTER TABLE `articulos`
  ADD PRIMARY KEY (`idArticulo`);

--
-- Indices de la tabla `consultas`
--
ALTER TABLE `consultas`
  ADD PRIMARY KEY (`idConsulta`),
  ADD KEY `fk_consultas_historia` (`historiaId`),
  ADD KEY `fk_consultas_medico` (`medicoId`);

--
-- Indices de la tabla `consultorio`
--
ALTER TABLE `consultorio`
  ADD PRIMARY KEY (`idHabitacion`),
  ADD UNIQUE KEY `numero` (`numero`);

--
-- Indices de la tabla `detallesreceta`
--
ALTER TABLE `detallesreceta`
  ADD PRIMARY KEY (`idDetalle`),
  ADD KEY `fk_detreceta_receta` (`recetaId`),
  ADD KEY `fk_detreceta_articulo` (`articuloId`);

--
-- Indices de la tabla `especialidades`
--
ALTER TABLE `especialidades`
  ADD PRIMARY KEY (`idEspecialidad`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `estudios`
--
ALTER TABLE `estudios`
  ADD PRIMARY KEY (`idEstudio`),
  ADD KEY `fk_estudios_paciente` (`pacienteId`);

--
-- Indices de la tabla `facturas`
--
ALTER TABLE `facturas`
  ADD PRIMARY KEY (`idFactura`),
  ADD KEY `fk_facturas_paciente` (`pacienteId`),
  ADD KEY `fk_facturas_obrasocial` (`obraSocialId`);

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
  ADD UNIQUE KEY `usuarioId` (`usuarioId`);

--
-- Indices de la tabla `medico_especialidad`
--
ALTER TABLE `medico_especialidad`
  ADD PRIMARY KEY (`idMedicoEspecialidad`),
  ADD UNIQUE KEY `uq_medico_especialidad` (`medicoId`,`especialidadId`),
  ADD KEY `fk_medicoesp_especialidad` (`especialidadId`);

--
-- Indices de la tabla `movimientos_stock`
--
ALTER TABLE `movimientos_stock`
  ADD PRIMARY KEY (`idMovimiento`),
  ADD KEY `fk_mov_articulo` (`idArticulo`),
  ADD KEY `fk_mov_usuario` (`usuarioId`);

--
-- Indices de la tabla `obrassociales`
--
ALTER TABLE `obrassociales`
  ADD PRIMARY KEY (`idObraSocial`),
  ADD UNIQUE KEY `nombre` (`nombre`);

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
  ADD PRIMARY KEY (`idPersonal`),
  ADD KEY `rolId` (`rolId`);

--
-- Indices de la tabla `recetas`
--
ALTER TABLE `recetas`
  ADD PRIMARY KEY (`idReceta`),
  ADD KEY `fk_recetas_consulta` (`consultaId`);

--
-- Indices de la tabla `roldeusuario`
--
ALTER TABLE `roldeusuario`
  ADD PRIMARY KEY (`idRolUsuario`),
  ADD KEY `usuarioId` (`usuarioId`),
  ADD KEY `rolId` (`rolId`);

--
-- Indices de la tabla `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`idRol`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `triage`
--
ALTER TABLE `triage`
  ADD PRIMARY KEY (`idTriage`),
  ADD KEY `fk_triage_paciente` (`pacienteId`),
  ADD KEY `fk_triage_guardia` (`guardiaId`),
  ADD KEY `fk_triage_enfermero` (`enfermeroId`);

--
-- Indices de la tabla `turnos`
--
ALTER TABLE `turnos`
  ADD PRIMARY KEY (`idTurno`),
  ADD KEY `fk_turnos_paciente` (`pacienteId`),
  ADD KEY `fk_turnos_medico` (`medicoId`),
  ADD KEY `fk_turnos_especialidad` (`especialidadId`),
  ADD KEY `fk_turnos_consultorio` (`consultorioId`);

--
-- Indices de la tabla `ubicacionpaciente`
--
ALTER TABLE `ubicacionpaciente`
  ADD PRIMARY KEY (`idUbicacion`),
  ADD KEY `fk_ubicacion_paciente` (`pacienteId`),
  ADD KEY `fk_ubicacion_medico` (`medicoId`),
  ADD KEY `fk_ubicacion_consultorio` (`consultorioId`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`idUsuario`),
  ADD UNIQUE KEY `dni` (`dni`),
  ADD KEY `fk_usuarios_obrasocial` (`obraSocialId`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `articulos`
--
ALTER TABLE `articulos`
  MODIFY `idArticulo` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `consultas`
--
ALTER TABLE `consultas`
  MODIFY `idConsulta` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `consultorio`
--
ALTER TABLE `consultorio`
  MODIFY `idHabitacion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `detallesreceta`
--
ALTER TABLE `detallesreceta`
  MODIFY `idDetalle` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `especialidades`
--
ALTER TABLE `especialidades`
  MODIFY `idEspecialidad` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

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
  MODIFY `idGuardia` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `historiasclinicas`
--
ALTER TABLE `historiasclinicas`
  MODIFY `idHistoria` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `medico_especialidad`
--
ALTER TABLE `medico_especialidad`
  MODIFY `idMedicoEspecialidad` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `movimientos_stock`
--
ALTER TABLE `movimientos_stock`
  MODIFY `idMovimiento` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `obrassociales`
--
ALTER TABLE `obrassociales`
  MODIFY `idObraSocial` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `permisorol`
--
ALTER TABLE `permisorol`
  MODIFY `idPermisoRol` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `permisos`
--
ALTER TABLE `permisos`
  MODIFY `idPermiso` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `personal`
--
ALTER TABLE `personal`
  MODIFY `idPersonal` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `recetas`
--
ALTER TABLE `recetas`
  MODIFY `idReceta` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `roldeusuario`
--
ALTER TABLE `roldeusuario`
  MODIFY `idRolUsuario` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `roles`
--
ALTER TABLE `roles`
  MODIFY `idRol` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `triage`
--
ALTER TABLE `triage`
  MODIFY `idTriage` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `turnos`
--
ALTER TABLE `turnos`
  MODIFY `idTurno` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `ubicacionpaciente`
--
ALTER TABLE `ubicacionpaciente`
  MODIFY `idUbicacion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `idUsuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `consultas`
--
ALTER TABLE `consultas`
  ADD CONSTRAINT `fk_consultas_historia` FOREIGN KEY (`historiaId`) REFERENCES `historiasclinicas` (`idHistoria`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_consultas_medico` FOREIGN KEY (`medicoId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE;

--
-- Filtros para la tabla `detallesreceta`
--
ALTER TABLE `detallesreceta`
  ADD CONSTRAINT `fk_detreceta_articulo` FOREIGN KEY (`articuloId`) REFERENCES `articulos` (`idArticulo`),
  ADD CONSTRAINT `fk_detreceta_receta` FOREIGN KEY (`recetaId`) REFERENCES `recetas` (`idReceta`) ON DELETE CASCADE;

--
-- Filtros para la tabla `estudios`
--
ALTER TABLE `estudios`
  ADD CONSTRAINT `fk_estudios_paciente` FOREIGN KEY (`pacienteId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE;

--
-- Filtros para la tabla `facturas`
--
ALTER TABLE `facturas`
  ADD CONSTRAINT `fk_facturas_obrasocial` FOREIGN KEY (`obraSocialId`) REFERENCES `obrassociales` (`idObraSocial`),
  ADD CONSTRAINT `fk_facturas_paciente` FOREIGN KEY (`pacienteId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE;

--
-- Filtros para la tabla `historiasclinicas`
--
ALTER TABLE `historiasclinicas`
  ADD CONSTRAINT `fk_hc_usuario` FOREIGN KEY (`usuarioId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE;

--
-- Filtros para la tabla `medico_especialidad`
--
ALTER TABLE `medico_especialidad`
  ADD CONSTRAINT `fk_medicoesp_especialidad` FOREIGN KEY (`especialidadId`) REFERENCES `especialidades` (`idEspecialidad`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_medicoesp_medico` FOREIGN KEY (`medicoId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE;

--
-- Filtros para la tabla `movimientos_stock`
--
ALTER TABLE `movimientos_stock`
  ADD CONSTRAINT `fk_mov_articulo` FOREIGN KEY (`idArticulo`) REFERENCES `articulos` (`idArticulo`),
  ADD CONSTRAINT `fk_mov_usuario` FOREIGN KEY (`usuarioId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE;

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
  ADD CONSTRAINT `fk_recetas_consulta` FOREIGN KEY (`consultaId`) REFERENCES `consultas` (`idConsulta`) ON DELETE CASCADE;

--
-- Filtros para la tabla `roldeusuario`
--
ALTER TABLE `roldeusuario`
  ADD CONSTRAINT `roldeusuario_ibfk_1` FOREIGN KEY (`usuarioId`) REFERENCES `usuarios` (`idUsuario`),
  ADD CONSTRAINT `roldeusuario_ibfk_2` FOREIGN KEY (`rolId`) REFERENCES `roles` (`idRol`);

--
-- Filtros para la tabla `triage`
--
ALTER TABLE `triage`
  ADD CONSTRAINT `fk_triage_enfermero` FOREIGN KEY (`enfermeroId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_triage_guardia` FOREIGN KEY (`guardiaId`) REFERENCES `guardia` (`idGuardia`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_triage_paciente` FOREIGN KEY (`pacienteId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE;

--
-- Filtros para la tabla `turnos`
--
ALTER TABLE `turnos`
  ADD CONSTRAINT `fk_turnos_consultorio` FOREIGN KEY (`consultorioId`) REFERENCES `consultorio` (`idHabitacion`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_turnos_especialidad` FOREIGN KEY (`especialidadId`) REFERENCES `especialidades` (`idEspecialidad`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_turnos_medico` FOREIGN KEY (`medicoId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_turnos_paciente` FOREIGN KEY (`pacienteId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE;

--
-- Filtros para la tabla `ubicacionpaciente`
--
ALTER TABLE `ubicacionpaciente`
  ADD CONSTRAINT `fk_ubicacion_consultorio` FOREIGN KEY (`consultorioId`) REFERENCES `consultorio` (`idHabitacion`),
  ADD CONSTRAINT `fk_ubicacion_medico` FOREIGN KEY (`medicoId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_ubicacion_paciente` FOREIGN KEY (`pacienteId`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE;

--
-- Filtros para la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD CONSTRAINT `fk_usuarios_obrasocial` FOREIGN KEY (`obraSocialId`) REFERENCES `obrassociales` (`idObraSocial`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
