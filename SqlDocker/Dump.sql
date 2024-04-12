-- MySQL Script generated by MySQL Workbench
-- Wed Apr 10 19:13:05 2024
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8mb3 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`street`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`street` ;

CREATE TABLE IF NOT EXISTS `mydb`.`street` (
  `Location` VARCHAR(98) NOT NULL,
  PRIMARY KEY (`Location`),
  UNIQUE INDEX `Location_UNIQUE` (`Location` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`lamppostinfo`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`lamppostinfo` ;

CREATE TABLE IF NOT EXISTS `mydb`.`lamppostinfo` (
  `LamppostID` VARCHAR(6) CHARACTER SET 'utf8mb3' NOT NULL,
  `GridX` INT NOT NULL,
  `GridY` INT NOT NULL,
  `Street_Location` VARCHAR(98) NOT NULL,
  PRIMARY KEY (`LamppostID`),
  UNIQUE INDEX `LamppostID_UNIQUE` (`LamppostID` ASC) VISIBLE,
  INDEX `fk_LamppostInfo_Street1_idx` (`Street_Location` ASC) VISIBLE,
  CONSTRAINT `fk_LamppostInfo_Street1`
    FOREIGN KEY (`Street_Location`)
    REFERENCES `mydb`.`street` (`Location`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`logcache`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`logcache` ;

CREATE TABLE IF NOT EXISTS `mydb`.`logcache` (
  `GridX` VARCHAR(45) NOT NULL,
  `GridY` VARCHAR(45) NOT NULL,
  `Location` VARCHAR(98) NOT NULL,
  `Value` INT NULL DEFAULT '0',
  `VehicleID` VARCHAR(8) NOT NULL,
  PRIMARY KEY (`VehicleID`),
  UNIQUE INDEX `VehicleID_UNIQUE` (`VehicleID` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`streetbounds`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`streetbounds` ;

CREATE TABLE IF NOT EXISTS `mydb`.`streetbounds` (
  `Street_Location` VARCHAR(98) NOT NULL,
  `StreetBoundsID` INT NOT NULL AUTO_INCREMENT,
  `StartX` INT NOT NULL,
  `StartY` INT NOT NULL,
  `EndX` INT NOT NULL,
  `EndY` INT NOT NULL,
  PRIMARY KEY (`StreetBoundsID`),
  UNIQUE INDEX `StreetBoundsID_UNIQUE` (`StreetBoundsID` ASC) VISIBLE,
  INDEX `fk_StreetBounds_Street1_idx` (`Street_Location` ASC) VISIBLE,
  CONSTRAINT `fk_StreetBounds_Street1`
    FOREIGN KEY (`Street_Location`)
    REFERENCES `mydb`.`street` (`Location`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`vehicleinfo`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`vehicleinfo` ;

CREATE TABLE IF NOT EXISTS `mydb`.`vehicleinfo` (
  `RegistrationMark` VARCHAR(45) NOT NULL,
  `Class` ENUM('Private Car', 'Light Goods Vehicle', 'Motorcycle', 'Private Light Bus', 'Public Light Bus', 'Taxi', 'Private Bus', 'Public Bus', 'Invalid Carriage', 'Government Vehicle', 'Public Bus - Franchised', 'Medium Goods Vehicle', 'Heavy Goods Vehicle', 'Articulated Vehicle', 'Special Purpose Vehicle', 'Motor Tricycle') NOT NULL,
  `FirstRegDate` DATE NOT NULL,
  `ManufactureYear` SMALLINT NOT NULL,
  `EngineNumber` VARCHAR(45) NOT NULL,
  `ChassisNo` VARCHAR(45) NOT NULL,
  `ExpirationDate` DATE NOT NULL,
  `CC` SMALLINT NOT NULL,
  `RatedPower` SMALLINT NOT NULL,
  `Colour` VARCHAR(45) NOT NULL DEFAULT 'ISNT LISTED',
  `SeatingCapacity` TINYINT NOT NULL,
  `VehicleID` VARCHAR(8) NOT NULL,
  `OwnerName` VARCHAR(45) NOT NULL,
  `OwnerID` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`VehicleID`),
  UNIQUE INDEX `RegistrationMark_UNIQUE` (`RegistrationMark` ASC) VISIBLE,
  UNIQUE INDEX `EngineNumber_UNIQUE` (`EngineNumber` ASC) VISIBLE,
  UNIQUE INDEX `VehicleID_UNIQUE` (`VehicleID` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`vehiclelog`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`vehiclelog` ;

CREATE TABLE IF NOT EXISTS `mydb`.`vehiclelog` (
  `VehicleID` VARCHAR(8) NOT NULL,
  `LogUUID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `GridX` DECIMAL(11,8) NOT NULL,
  `GridY` DECIMAL(11,8) NOT NULL,
  `Location` VARCHAR(98) NOT NULL,
  `DateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`LogUUID`),
  UNIQUE INDEX `LogUUID_UNIQUE` (`LogUUID` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 12
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`vehiclewarning`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`vehiclewarning` ;

CREATE TABLE IF NOT EXISTS `mydb`.`vehiclewarning` (
  `VehicleID` VARCHAR(8) NOT NULL,
  `DateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Reason` VARCHAR(45) NOT NULL DEFAULT 'AUTOMATIC' COMMENT 'AUTOMATIC||',
  `WarningID` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`WarningID`),
  UNIQUE INDEX `WarningID_UNIQUE` (`WarningID` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`bookinginfo`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`bookinginfo` ;

CREATE TABLE IF NOT EXISTS `mydb`.`bookinginfo` (
  `BookingUUID` INT NOT NULL AUTO_INCREMENT,
  `StartingTime` DATETIME NOT NULL,
  `EndingTime` DATETIME NOT NULL,
  `Reason` VARCHAR(45) NOT NULL,
  `VehicleID` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`BookingUUID`),
  UNIQUE INDEX `BookingUUID_UNIQUE` (`BookingUUID` ASC) VISIBLE)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
