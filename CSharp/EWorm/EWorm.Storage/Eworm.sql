SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `eworm` DEFAULT CHARACTER SET utf8 ;
USE `eworm` ;

-- -----------------------------------------------------
-- Table `eworm`.`t_goods`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `eworm`.`t_goods` ;

CREATE  TABLE IF NOT EXISTS `eworm`.`t_goods` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `title` VARCHAR(512) NOT NULL ,
  `sellingurl` VARCHAR(512) NOT NULL ,
  `updatetime` DATE NOT NULL ,
  `imagepath` VARCHAR(512) NULL DEFAULT NULL ,
  `price` DECIMAL(10,0) NOT NULL DEFAULT '0' ,
  `sellercredit` INT(11) NOT NULL DEFAULT '0' ,
  `sellamount` INT(11) NOT NULL DEFAULT '0' ,
  PRIMARY KEY (`id`) ,
  INDEX `title` USING BTREE (`title`(333) ASC) )
ENGINE = MyISAM
AUTO_INCREMENT = 1000
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `eworm`.`t_properties`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `eworm`.`t_properties` ;

CREATE  TABLE IF NOT EXISTS `eworm`.`t_properties` (
  `goodsid` INT(11) NOT NULL ,
  `propertyname` VARCHAR(100) NOT NULL ,
  `propertyvalue` VARCHAR(500) NOT NULL ,
  `propertytype` VARCHAR(40) NOT NULL ,
  INDEX `fk_goods` (`goodsid` ASC) ,
  CONSTRAINT `fk_goods`
    FOREIGN KEY (`goodsid` )
    REFERENCES `eworm`.`t_goods` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
