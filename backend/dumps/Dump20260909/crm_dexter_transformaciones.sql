-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: crm_dexter
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `transformaciones`
--

DROP TABLE IF EXISTS `transformaciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transformaciones` (
  `id` binary(16) NOT NULL,
  `dataset_id` binary(16) NOT NULL,
  `dataset_columna_id` binary(16) DEFAULT NULL,
  `ejecucion_id` binary(16) DEFAULT NULL,
  `created_by` binary(16) NOT NULL,
  `tipo_transformacion` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `configuracion_json` json DEFAULT NULL,
  `orden_ejecucion` int unsigned NOT NULL DEFAULT '1',
  `filas_afectadas` bigint unsigned NOT NULL DEFAULT '0',
  `estado` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDIENTE',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_transformaciones_dataset` (`dataset_id`),
  KEY `idx_transformaciones_ejecucion` (`ejecucion_id`),
  KEY `fk_transformaciones_columna` (`dataset_columna_id`),
  KEY `fk_transformaciones_usuario` (`created_by`),
  CONSTRAINT `fk_transformaciones_columna` FOREIGN KEY (`dataset_columna_id`) REFERENCES `dataset_columnas` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_transformaciones_dataset` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_transformaciones_ejecucion` FOREIGN KEY (`ejecucion_id`) REFERENCES `ejecuciones_procesamiento` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_transformaciones_usuario` FOREIGN KEY (`created_by`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `chk_transformaciones_estado` CHECK ((`estado` in (_utf8mb4'PENDIENTE',_utf8mb4'APLICADA',_utf8mb4'REVERTIDA',_utf8mb4'ERROR')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transformaciones`
--

LOCK TABLES `transformaciones` WRITE;
/*!40000 ALTER TABLE `transformaciones` DISABLE KEYS */;
/*!40000 ALTER TABLE `transformaciones` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-09 12:52:33
