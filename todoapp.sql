-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema todo
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema todo
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `todo` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ;
USE `todo` ;

-- -----------------------------------------------------
-- Table `todo`.`user_group`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todo`.`user_group` (
  `group_id` INT NOT NULL AUTO_INCREMENT,
  `group_name` VARCHAR(100) NOT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`group_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `todo`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todo`.`user` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(100) NOT NULL,
  `phone_number` VARCHAR(20) NULL DEFAULT NULL,
  `password` VARCHAR(255) NOT NULL,
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `reset_token` VARCHAR(100) NULL DEFAULT NULL,
  `reset_expires` DATETIME NULL DEFAULT NULL,
  `address` VARCHAR(100) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `birthday` DATE NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `email` (`email` ASC) VISIBLE,
  UNIQUE INDEX `phone_number` (`phone_number` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `todo`.`group_member`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todo`.`group_member` (
  `group_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `role` ENUM('Owner', 'Member') NULL DEFAULT 'Member',
  `joined_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`group_id`, `user_id`),
  INDEX `user_id` (`user_id` ASC) VISIBLE,
  CONSTRAINT `group_member_ibfk_1`
    FOREIGN KEY (`group_id`)
    REFERENCES `todo`.`user_group` (`group_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `group_member_ibfk_2`
    FOREIGN KEY (`user_id`)
    REFERENCES `todo`.`user` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `todo`.`workflow`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todo`.`workflow` (
  `workflow_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `project_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`workflow_id`),
  INDEX `project_id` (`project_id` ASC) VISIBLE,
  CONSTRAINT `workflow_ibfk_1`
    FOREIGN KEY (`project_id`)
    REFERENCES `todo`.`project` (`project_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `todo`.`project`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todo`.`project` (
  `project_id` INT NOT NULL AUTO_INCREMENT,
  `project_name` VARCHAR(100) NOT NULL,
  `project_status` ENUM('Working', 'Done') NULL DEFAULT 'Working',
  `created_date` DATE NOT NULL,
  `start_date` DATE NULL DEFAULT NULL,
  `due_date` DATE NULL DEFAULT NULL,
  `owner_id` INT NOT NULL,
  `workflow_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`project_id`),
  INDEX `owner_id` (`owner_id` ASC) VISIBLE,
  INDEX `workflow_id` (`workflow_id` ASC) VISIBLE,
  CONSTRAINT `project_ibfk_1`
    FOREIGN KEY (`owner_id`)
    REFERENCES `todo`.`user` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `project_ibfk_2`
    FOREIGN KEY (`workflow_id`)
    REFERENCES `todo`.`workflow` (`workflow_id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `todo`.`milestone`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todo`.`milestone` (
  `milestone_id` INT NOT NULL AUTO_INCREMENT,
  `project_id` INT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `start_date` DATE NULL DEFAULT NULL,
  `due_date` DATE NULL DEFAULT NULL,
  `status` ENUM('Planned', 'In Progress', 'Completed') NULL DEFAULT 'Planned',
  PRIMARY KEY (`milestone_id`),
  INDEX `project_id` (`project_id` ASC) VISIBLE,
  CONSTRAINT `milestone_ibfk_1`
    FOREIGN KEY (`project_id`)
    REFERENCES `todo`.`project` (`project_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `todo`.`workflow_step`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todo`.`workflow_step` (
  `step_id` INT NOT NULL AUTO_INCREMENT,
  `workflow_id` INT NOT NULL,
  `step_name` VARCHAR(100) NOT NULL,
  `step_order` INT NOT NULL,
  PRIMARY KEY (`step_id`),
  INDEX `workflow_id` (`workflow_id` ASC) VISIBLE,
  CONSTRAINT `workflow_step_ibfk_1`
    FOREIGN KEY (`workflow_id`)
    REFERENCES `todo`.`workflow` (`workflow_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `todo`.`task`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todo`.`task` (
  `task_id` INT NOT NULL AUTO_INCREMENT,
  `task_name` VARCHAR(100) NOT NULL,
  `task_description` TEXT NULL DEFAULT NULL,
  `task_status` ENUM('ToDo', 'Working', 'Done') NULL DEFAULT 'ToDo',
  `task_progress` INT NULL DEFAULT '0',
  `created_date` DATE NOT NULL,
  `start_date` DATE NULL DEFAULT NULL,
  `due_date` DATE NULL DEFAULT NULL,
  `project_id` INT NOT NULL,
  `workflow_id` INT NULL DEFAULT NULL,
  `step_id` INT NULL DEFAULT NULL,
  `created_by` INT NOT NULL,
  `assigned_to` INT NULL DEFAULT NULL,
  PRIMARY KEY (`task_id`),
  UNIQUE INDEX `unique_task_per_project` (`task_name` ASC, `project_id` ASC) VISIBLE,
  INDEX `project_id` (`project_id` ASC) VISIBLE,
  INDEX `workflow_id` (`workflow_id` ASC) VISIBLE,
  INDEX `step_id` (`step_id` ASC) VISIBLE,
  INDEX `fk_task_created_by` (`created_by` ASC) VISIBLE,
  INDEX `fk_task_assigned_to` (`assigned_to` ASC) VISIBLE,
  CONSTRAINT `fk_task_assigned_to`
    FOREIGN KEY (`assigned_to`)
    REFERENCES `todo`.`user` (`user_id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `fk_task_created_by`
    FOREIGN KEY (`created_by`)
    REFERENCES `todo`.`user` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `task_ibfk_1`
    FOREIGN KEY (`project_id`)
    REFERENCES `todo`.`project` (`project_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `task_ibfk_2`
    FOREIGN KEY (`workflow_id`)
    REFERENCES `todo`.`workflow` (`workflow_id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `task_ibfk_3`
    FOREIGN KEY (`step_id`)
    REFERENCES `todo`.`workflow_step` (`step_id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `todo`.`milestone_task`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todo`.`milestone_task` (
  `milestone_id` INT NOT NULL,
  `task_id` INT NOT NULL,
  PRIMARY KEY (`milestone_id`, `task_id`),
  INDEX `task_id` (`task_id` ASC) VISIBLE,
  CONSTRAINT `milestone_task_ibfk_1`
    FOREIGN KEY (`milestone_id`)
    REFERENCES `todo`.`milestone` (`milestone_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `milestone_task_ibfk_2`
    FOREIGN KEY (`task_id`)
    REFERENCES `todo`.`task` (`task_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `todo`.`project_member`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todo`.`project_member` (
  `project_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `role` ENUM('Owner', 'Member', 'Viewer') NULL DEFAULT 'Member',
  `joined_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`project_id`, `user_id`),
  INDEX `user_id` (`user_id` ASC) VISIBLE,
  CONSTRAINT `project_member_ibfk_1`
    FOREIGN KEY (`project_id`)
    REFERENCES `todo`.`project` (`project_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `project_member_ibfk_2`
    FOREIGN KEY (`user_id`)
    REFERENCES `todo`.`user` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `todo`.`refresh_token`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todo`.`refresh_token` (
  `refresh_token_id` INT NOT NULL AUTO_INCREMENT,
  `token` VARCHAR(512) NOT NULL,
  `user_id` INT NOT NULL,
  `expires_at` DATETIME NOT NULL,
  `is_revoked` TINYINT(1) NOT NULL DEFAULT '0',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`refresh_token_id`),
  UNIQUE INDEX `token` (`token` ASC) VISIBLE,
  INDEX `idx_user_id` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_refresh_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `todo`.`user` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `todo`.`subtask`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todo`.`subtask` (
  `subtask_id` INT NOT NULL AUTO_INCREMENT,
  `subtask_name` VARCHAR(100) NOT NULL,
  `subtask_description` TEXT NULL DEFAULT NULL,
  `subtask_status` ENUM('Working', 'Done') NULL DEFAULT 'Working',
  `created_date` DATE NOT NULL,
  `task_id` INT NOT NULL,
  PRIMARY KEY (`subtask_id`),
  INDEX `task_id` (`task_id` ASC) VISIBLE,
  CONSTRAINT `subtask_ibfk_1`
    FOREIGN KEY (`task_id`)
    REFERENCES `todo`.`task` (`task_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
