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
-- Table structure for table `dataset_columnas`
--

DROP TABLE IF EXISTS `dataset_columnas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dataset_columnas` (
  `id` binary(16) NOT NULL,
  `dataset_id` binary(16) NOT NULL,
  `nombre_original` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_normalizado` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo_detectado` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `posicion` int unsigned NOT NULL,
  `es_obligatoria` tinyint(1) NOT NULL DEFAULT '0',
  `total_nulos` bigint unsigned NOT NULL DEFAULT '0',
  `total_unicos` bigint unsigned NOT NULL DEFAULT '0',
  `porcentaje_nulos` decimal(7,4) NOT NULL DEFAULT '0.0000',
  `ejemplo_valor` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estadisticas_json` json DEFAULT NULL,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_columna_posicion_dataset` (`dataset_id`,`posicion`),
  KEY `idx_columnas_dataset` (`dataset_id`),
  CONSTRAINT `fk_columnas_dataset` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_columnas_tipo` CHECK ((`tipo_detectado` in (_utf8mb4'TEXTO',_utf8mb4'ENTERO',_utf8mb4'DECIMAL',_utf8mb4'FECHA',_utf8mb4'BOOLEANO',_utf8mb4'COORDENADA',_utf8mb4'CATEGORIA')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dataset_columnas`
--

LOCK TABLES `dataset_columnas` WRITE;
/*!40000 ALTER TABLE `dataset_columnas` DISABLE KEYS */;
/*!40000 ALTER TABLE `dataset_columnas` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-09 12:52:32
