-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,
NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema todo
-- -----------------------------------------------------

CREATE SCHEMA IF NOT EXISTS `todo` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `todo`;

-- -----------------------------------------------------
-- Table `todo`.`role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todo`.`role` (
  `role_id` INT NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`role_id`)
)
ENGINE = InnoDB
DEFAULT CHARSET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `todo`.`status`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todo`.`status` (
  `status_id` INT NOT NULL AUTO_INCREMENT,
  `status_name` VARCHAR(100) NOT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`status_id`)
)
ENGINE = InnoDB
DEFAULT CHARSET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `todo`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todo`.`user` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `full_name` VARCHAR(255) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `role_id` INT NOT NULL,
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC),
  INDEX `fk_user_role` (`role_id` ASC),
  CONSTRAINT `fk_user_role`
    FOREIGN KEY (`role_id`)
    REFERENCES `todo`.`role` (`role_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
)
ENGINE = InnoDB
DEFAULT CHARSET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `todo`.`workspace`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todo`.`workspace` (
  `workspace_id` INT NOT NULL AUTO_INCREMENT,
  `workspace_name` VARCHAR(255) NOT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `owner_id` INT NOT NULL,
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`workspace_id`),
  INDEX `fk_workspace_owner` (`owner_id` ASC),
  CONSTRAINT `fk_workspace_owner`
    FOREIGN KEY (`owner_id`)
    REFERENCES `todo`.`user` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
ENGINE = InnoDB
DEFAULT CHARSET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `todo`.`user_workspace`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todo`.`user_workspace` (
  `user_workspace_id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `workspace_id` INT NOT NULL,
  `role_id` INT NOT NULL,
  `joined_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_workspace_id`),
  INDEX `fk_userworkspace_user` (`user_id` ASC),
  INDEX `fk_userworkspace_workspace` (`workspace_id` ASC),
  INDEX `fk_userworkspace_role` (`role_id` ASC),
  CONSTRAINT `fk_userworkspace_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `todo`.`user` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_userworkspace_workspace`
    FOREIGN KEY (`workspace_id`)
    REFERENCES `todo`.`workspace` (`workspace_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_userworkspace_role`
    FOREIGN KEY (`role_id`)
    REFERENCES `todo`.`role` (`role_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
)
ENGINE = InnoDB
DEFAULT CHARSET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `todo`.`project`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todo`.`project` (
  `project_id` INT NOT NULL AUTO_INCREMENT,
  `workspace_id` INT NOT NULL,
  `project_name` VARCHAR(100) NOT NULL,
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` INT NOT NULL,
  PRIMARY KEY (`project_id`),
  INDEX `fk_project_workspace` (`workspace_id` ASC),
  INDEX `fk_project_createdby` (`created_by` ASC),
  CONSTRAINT `fk_project_workspace`
    FOREIGN KEY (`workspace_id`)
    REFERENCES `todo`.`workspace` (`workspace_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_project_createdby`
    FOREIGN KEY (`created_by`)
    REFERENCES `todo`.`user` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
ENGINE = InnoDB
DEFAULT CHARSET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `todo`.`task`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todo`.`task` (
  `task_id` INT NOT NULL AUTO_INCREMENT,
  `project_id` INT NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `status_id` INT NOT NULL,
  `created_by` INT NOT NULL,
  `assigned_to` INT NULL DEFAULT NULL,
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`task_id`),
  INDEX `fk_task_project` (`project_id` ASC),
  INDEX `fk_task_status` (`status_id` ASC),
  INDEX `fk_task_createdby` (`created_by` ASC),
  INDEX `fk_task_assignedto` (`assigned_to` ASC),
  CONSTRAINT `fk_task_project`
    FOREIGN KEY (`project_id`)
    REFERENCES `todo`.`project` (`project_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_task_status`
    FOREIGN KEY (`status_id`)
    REFERENCES `todo`.`status` (`status_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_task_createdby`
    FOREIGN KEY (`created_by`)
    REFERENCES `todo`.`user` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_task_assignedto`
    FOREIGN KEY (`assigned_to`)
    REFERENCES `todo`.`user` (`user_id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE
)
ENGINE = InnoDB
DEFAULT CHARSET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `todo`.`comment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todo`.`comment` (
  `comment_id` INT NOT NULL AUTO_INCREMENT,
  `task_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `content` TEXT NOT NULL,
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`comment_id`),
  INDEX `fk_comment_task` (`task_id` ASC),
  INDEX `fk_comment_user` (`user_id` ASC),
  CONSTRAINT `fk_comment_task`
    FOREIGN KEY (`task_id`)
    REFERENCES `todo`.`task` (`task_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_comment_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `todo`.`user` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
ENGINE = InnoDB
DEFAULT CHARSET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- DEFAULT DATA
-- -----------------------------------------------------
INSERT INTO `todo`.`role` (`role_name`) VALUES
('Admin'), ('Manager'), ('Member'), ('Viewer');

INSERT INTO `todo`.`status` (`status_name`, `description`) VALUES
('To Do', 'Task chưa thực hiện'),
('In Progress', 'Đang tiến hành'),
('Review', 'Đang kiểm tra'),
('Done', 'Hoàn thành'),
('Archived', 'Lưu trữ');


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
