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
-- Table structure for table `mapeos_columnas`
--

DROP TABLE IF EXISTS `mapeos_columnas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mapeos_columnas` (
  `id` binary(16) NOT NULL,
  `dataset_columna_id` binary(16) NOT NULL,
  `esquema_campo_id` binary(16) NOT NULL,
  `metodo` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `confianza` decimal(6,5) NOT NULL DEFAULT '0.00000',
  `estado` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'SUGERIDO',
  `es_confirmado` tinyint(1) NOT NULL DEFAULT '0',
  `confirmado_por` binary(16) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_mapeo_columna_campo` (`dataset_columna_id`,`esquema_campo_id`),
  KEY `idx_mapeos_campo` (`esquema_campo_id`),
  KEY `fk_mapeos_confirmado_por` (`confirmado_por`),
  CONSTRAINT `fk_mapeos_confirmado_por` FOREIGN KEY (`confirmado_por`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_mapeos_dataset_columna` FOREIGN KEY (`dataset_columna_id`) REFERENCES `dataset_columnas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_mapeos_esquema_campo` FOREIGN KEY (`esquema_campo_id`) REFERENCES `esquema_campos` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_mapeos_confianza` CHECK (((`confianza` >= 0) and (`confianza` <= 1))),
  CONSTRAINT `chk_mapeos_estado` CHECK ((`estado` in (_utf8mb4'SUGERIDO',_utf8mb4'CONFIRMADO',_utf8mb4'RECHAZADO')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mapeos_columnas`
--

LOCK TABLES `mapeos_columnas` WRITE;
/*!40000 ALTER TABLE `mapeos_columnas` DISABLE KEYS */;
/*!40000 ALTER TABLE `mapeos_columnas` ENABLE KEYS */;
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
