-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema todo
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `todo` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ;
USE `todo` ;

-- -----------------------------------------------------
-- Table `user_group`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_group` (
  `group_id` INT NOT NULL AUTO_INCREMENT,
  `group_name` VARCHAR(100) NOT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(100) NOT NULL,
  `phone_number` VARCHAR(20) NULL DEFAULT NULL,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `reset_token` VARCHAR(100) NULL DEFAULT NULL,
  `reset_expires` DATETIME NULL DEFAULT NULL,
  `address` VARCHAR(100) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `birthday` DATE NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `email` (`email` ASC),
  UNIQUE INDEX `phone_number` (`phone_number` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `group_member`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `group_member` (
  `group_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `role` ENUM('Owner', 'Member') NULL DEFAULT 'Member',
  `joined_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`group_id`, `user_id`),
  INDEX `user_id` (`user_id` ASC),
  CONSTRAINT `group_member_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `user_group` (`group_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `group_member_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `workflow`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `workflow` (
  `workflow_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `project_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`workflow_id`),
  INDEX `project_id` (`project_id` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `project`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `project` (
  `project_id` INT NOT NULL AUTO_INCREMENT,
  `project_name` VARCHAR(100) NOT NULL,
  `project_status` ENUM('Working', 'Done') NULL DEFAULT 'Working',
  `created_date` DATE NOT NULL,
  `start_date` DATE NULL DEFAULT NULL,
  `due_date` DATE NULL DEFAULT NULL,
  `owner_id` INT NOT NULL,
  `workflow_id` INT NULL DEFAULT NULL,
  `group_id` INT NOT NULL, -- Thêm quan hệ 1 group
  PRIMARY KEY (`project_id`),
  INDEX `owner_id` (`owner_id` ASC),
  INDEX `workflow_id` (`workflow_id` ASC),
  INDEX `group_id` (`group_id` ASC),
  CONSTRAINT `project_ibfk_1` FOREIGN KEY (`owner_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `project_ibfk_2` FOREIGN KEY (`workflow_id`) REFERENCES `workflow` (`workflow_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `project_ibfk_3` FOREIGN KEY (`group_id`) REFERENCES `user_group` (`group_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `milestone`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `milestone` (
  `milestone_id` INT NOT NULL AUTO_INCREMENT,
  `project_id` INT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `start_date` DATE NULL DEFAULT NULL,
  `due_date` DATE NULL DEFAULT NULL,
  `status` ENUM('Planned', 'In Progress', 'Completed') NULL DEFAULT 'Planned',
  PRIMARY KEY (`milestone_id`),
  INDEX `project_id` (`project_id` ASC),
  CONSTRAINT `milestone_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `workflow_step`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `workflow_step` (
  `step_id` INT NOT NULL AUTO_INCREMENT,
  `workflow_id` INT NOT NULL,
  `step_name` VARCHAR(100) NOT NULL,
  `step_order` INT NOT NULL,
  PRIMARY KEY (`step_id`),
  INDEX `workflow_id` (`workflow_id` ASC),
  CONSTRAINT `workflow_step_ibfk_1` FOREIGN KEY (`workflow_id`) REFERENCES `workflow` (`workflow_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `task`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `task` (
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
  UNIQUE INDEX `unique_task_per_project` (`task_name` ASC, `project_id` ASC),
  INDEX `project_id` (`project_id` ASC),
  INDEX `workflow_id` (`workflow_id` ASC),
  INDEX `step_id` (`step_id` ASC),
  INDEX `fk_task_created_by` (`created_by` ASC),
  INDEX `fk_task_assigned_to` (`assigned_to` ASC),
  CONSTRAINT `fk_task_assigned_to` FOREIGN KEY (`assigned_to`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_task_created_by` FOREIGN KEY (`created_by`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `task_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `task_ibfk_2` FOREIGN KEY (`workflow_id`) REFERENCES `workflow` (`workflow_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `task_ibfk_3` FOREIGN KEY (`step_id`) REFERENCES `workflow_step` (`step_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `milestone_task`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `milestone_task` (
  `milestone_id` INT NOT NULL,
  `task_id` INT NOT NULL,
  PRIMARY KEY (`milestone_id`, `task_id`),
  INDEX `task_id` (`task_id` ASC),
  CONSTRAINT `milestone_task_ibfk_1` FOREIGN KEY (`milestone_id`) REFERENCES `milestone` (`milestone_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `milestone_task_ibfk_2` FOREIGN KEY (`task_id`) REFERENCES `task` (`task_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `project_member`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `project_member` (
  `project_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `role` ENUM('Owner', 'Member', 'Viewer') NULL DEFAULT 'Member',
  `joined_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`project_id`, `user_id`),
  INDEX `user_id` (`user_id` ASC),
  CONSTRAINT `project_member_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `project_member_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `refresh_token`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `refresh_token` (
  `refresh_token_id` INT NOT NULL AUTO_INCREMENT,
  `token` VARCHAR(512) NOT NULL,
  `user_id` INT NOT NULL,
  `expires_at` DATETIME NOT NULL,
  `is_revoked` TINYINT(1) NOT NULL DEFAULT '0',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`refresh_token_id`),
  UNIQUE INDEX `token` (`token` ASC),
  INDEX `idx_user_id` (`user_id` ASC),
  CONSTRAINT `fk_refresh_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `subtask`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `subtask` (
  `subtask_id` INT NOT NULL AUTO_INCREMENT,
  `subtask_name` VARCHAR(100) NOT NULL,
  `subtask_description` TEXT NULL DEFAULT NULL,
  `subtask_status` ENUM('Working', 'Done') NULL DEFAULT 'Working',
  `created_date` DATE NOT NULL,
  `task_id` INT NOT NULL,
  PRIMARY KEY (`subtask_id`),
  INDEX `task_id` (`task_id` ASC),
  CONSTRAINT `subtask_ibfk_1` FOREIGN KEY (`task_id`) REFERENCES `task` (`task_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `performance_record`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `performance_record` (
  `performance_id` INT NOT NULL AUTO_INCREMENT,
  `group_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `score` DECIMAL(5,2) NOT NULL,           -- Điểm đánh giá (0 - 100 hoặc 0 - 10)
  `comment` TEXT NULL DEFAULT NULL,         -- Nhận xét
  `created_by` INT NOT NULL,                -- Ai đánh giá (Leader / Owner)
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`performance_id`),

  INDEX `idx_group_id` (`group_id` ASC),
  INDEX `idx_user_id` (`user_id` ASC),
  INDEX `idx_created_by` (`created_by` ASC),

  CONSTRAINT `fk_performance_group`
    FOREIGN KEY (`group_id`) REFERENCES `user_group` (`group_id`)
    ON DELETE CASCADE ON UPDATE CASCADE,

  CONSTRAINT `fk_performance_user`
    FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE,

  CONSTRAINT `fk_performance_created_by`
    FOREIGN KEY (`created_by`) REFERENCES `user` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
