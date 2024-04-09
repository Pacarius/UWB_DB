-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: mydb
-- ------------------------------------------------------
-- Server version	8.0.36

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
-- Table structure for table `lamppostinfo`
--
DROP TABLE IF EXISTS `lamppostinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lamppostinfo` (
  `LamppostID` varchar(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `GridX` int NOT NULL,
  `GridY` int NOT NULL,
  `Street_Location` varchar(98) NOT NULL,
  PRIMARY KEY (`LamppostID`),
  UNIQUE KEY `LamppostID_UNIQUE` (`LamppostID`),
  KEY `fk_LamppostInfo_Street1_idx` (`Street_Location`),
  CONSTRAINT `fk_LamppostInfo_Street1` FOREIGN KEY (`Street_Location`) REFERENCES `street` (`Location`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lamppostinfo`
--

LOCK TABLES `lamppostinfo` WRITE;
/*!40000 ALTER TABLE `lamppostinfo` DISABLE KEYS */;
INSERT INTO `lamppostinfo` VALUES ('TEST0',10,17,'TEST'),('TEST1',46,11,'TEST');
/*!40000 ALTER TABLE `lamppostinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logcache`
--

DROP TABLE IF EXISTS `logcache`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `logcache` (
  `GridX` varchar(45) NOT NULL,
  `GridY` varchar(45) NOT NULL,
  `Location` varchar(98) NOT NULL,
  `Value` int DEFAULT '0',
  `VehicleInfo_VehicleID` varchar(8) NOT NULL,
  PRIMARY KEY (`VehicleInfo_VehicleID`),
  CONSTRAINT `fk_LogCache_VehicleInfo1` FOREIGN KEY (`VehicleInfo_VehicleID`) REFERENCES `vehicleinfo` (`VehicleID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logcache`
--

LOCK TABLES `logcache` WRITE;
/*!40000 ALTER TABLE `logcache` DISABLE KEYS */;
INSERT INTO `logcache` VALUES ('44.92708206','14.74190331','TEST',0,'00000000');
/*!40000 ALTER TABLE `logcache` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `street`
--

DROP TABLE IF EXISTS `street`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `street` (
  `Location` varchar(98) NOT NULL,
  PRIMARY KEY (`Location`),
  UNIQUE KEY `Location_UNIQUE` (`Location`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `street`
--

LOCK TABLES `street` WRITE;
/*!40000 ALTER TABLE `street` DISABLE KEYS */;
INSERT INTO `street` VALUES ('TEST');
/*!40000 ALTER TABLE `street` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `streetbounds`
--

DROP TABLE IF EXISTS `streetbounds`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `streetbounds` (
  `Street_Location` varchar(98) NOT NULL,
  `StreetBoundsID` int NOT NULL AUTO_INCREMENT,
  `StartX` int NOT NULL,
  `StartY` int NOT NULL,
  `EndX` int NOT NULL,
  `EndY` int NOT NULL,
  PRIMARY KEY (`StreetBoundsID`),
  UNIQUE KEY `StreetBoundsID_UNIQUE` (`StreetBoundsID`),
  KEY `fk_StreetBounds_Street1_idx` (`Street_Location`),
  CONSTRAINT `fk_StreetBounds_Street1` FOREIGN KEY (`Street_Location`) REFERENCES `street` (`Location`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `streetbounds`
--

LOCK TABLES `streetbounds` WRITE;
/*!40000 ALTER TABLE `streetbounds` DISABLE KEYS */;
INSERT INTO `streetbounds` VALUES ('TEST',1,6,14,47,15),('TEST',2,12,17,41,13);
/*!40000 ALTER TABLE `streetbounds` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vehicleinfo`
--

DROP TABLE IF EXISTS `vehicleinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vehicleinfo` (
  `RegistrationMark` varchar(45) NOT NULL,
  `Class` enum('Private Car','Light Goods Vehicle','Motorcycle','Private Light Bus','Public Light Bus','Taxi','Private Bus','Public Bus','Invalid Carriage','Government Vehicle','Public Bus - Franchised','Medium Goods Vehicle','Heavy Goods Vehicle','Articulated Vehicle','Special Purpose Vehicle','Motor Tricycle') NOT NULL,
  `FirstRegDate` date NOT NULL,
  `ManufactureYear` smallint NOT NULL,
  `EngineNumber` varchar(45) NOT NULL,
  `ChassisNo` varchar(45) NOT NULL,
  `ExpirationDate` date NOT NULL,
  `CC` smallint NOT NULL,
  `RatedPower` smallint NOT NULL,
  `Colour` varchar(45) NOT NULL DEFAULT 'ISNT LISTED',
  `SeatingCapacity` tinyint NOT NULL,
  `VehicleID` varchar(8) NOT NULL,
  `OwnerName` varchar(45) NOT NULL,
  `OwnerID` varchar(45) NOT NULL,
  PRIMARY KEY (`VehicleID`),
  UNIQUE KEY `RegistrationMark_UNIQUE` (`RegistrationMark`),
  UNIQUE KEY `EngineNumber_UNIQUE` (`EngineNumber`),
  UNIQUE KEY `VehicleID_UNIQUE` (`VehicleID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehicleinfo`
--

LOCK TABLES `vehicleinfo` WRITE;
/*!40000 ALTER TABLE `vehicleinfo` DISABLE KEYS */;
INSERT INTO `vehicleinfo` VALUES ('','Private Car','2004-01-01',0,'','','2004-01-01',0,0,'',0,'00000000','','');
INSERT INTO `vehicleinfo` VALUES ('a','Private Car','2004-01-01',1,'a','a','2004-01-01',0,0,'',0,'11111111','','');
/*!40000 ALTER TABLE `vehicleinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vehiclelog`
--

DROP TABLE IF EXISTS `vehiclelog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vehiclelog` (
  `VehicleInfo_VehicleID` varchar(8) NOT NULL,
  `LogUUID` int unsigned NOT NULL AUTO_INCREMENT,
  `GridX` decimal(11,8) NOT NULL,
  `GridY` decimal(11,8) NOT NULL,
  `Location` varchar(98) NOT NULL,
  `DateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`LogUUID`),
  UNIQUE KEY `LogUUID_UNIQUE` (`LogUUID`),
  KEY `fk_VehicleLog_VehicleInfo` (`VehicleInfo_VehicleID`),
  CONSTRAINT `fk_VehicleLog_VehicleInfo` FOREIGN KEY (`VehicleInfo_VehicleID`) REFERENCES `vehicleinfo` (`VehicleID`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehiclelog`
--

LOCK TABLES `vehiclelog` WRITE;
/*!40000 ALTER TABLE `vehiclelog` DISABLE KEYS */;
/*!40000 ALTER TABLE `vehiclelog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vehiclewarning`
--

DROP TABLE IF EXISTS `vehiclewarning`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vehiclewarning` (
  `VehicleInfo_VehicleID` varchar(8) NOT NULL,
  `DateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Reason` varchar(45) NOT NULL DEFAULT 'AUTOMATIC' COMMENT 'AUTOMATIC||',
  `WarningID` int NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`WarningID`),
  KEY `fk_VehicleWarning_VehicleInfo1` (`VehicleInfo_VehicleID`),
  CONSTRAINT `fk_VehicleWarning_VehicleInfo1` FOREIGN KEY (`VehicleInfo_VehicleID`) REFERENCES `vehicleinfo` (`VehicleID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehiclewarning`
--

LOCK TABLES `vehiclewarning` WRITE;
/*!40000 ALTER TABLE `vehiclewarning` DISABLE KEYS */;
/*!40000 ALTER TABLE `vehiclewarning` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-03-19 18:09:40
