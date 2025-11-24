SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

CREATE SCHEMA IF NOT EXISTS `todo`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `todo`;
-- -----------------------------------------------------
-- Users
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `users` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `username` VARCHAR(50),
  `password` VARCHAR(255) NOT NULL,
  `phone_number` VARCHAR(20) UNIQUE,
  `address` VARCHAR(100),
  `birthday` DATE,
  `role` ENUM('Admin', 'User') NOT NULL DEFAULT 'User',   -- 👉 THÊM NGAY Ở ĐÂY
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `reset_token` VARCHAR(10),
  `reset_expires` DATETIME,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Workspaces
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `workspaces` (
  `workspace_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `owner_id` INT NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`workspace_id`),
  INDEX `idx_workspace_owner` (`owner_id`),
  CONSTRAINT `fk_workspace_owner`
    FOREIGN KEY (`owner_id`)
    REFERENCES `users` (`user_id`)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Workspace Members
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `workspace_members` (
  `workspace_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `role` ENUM('Owner', 'Admin', 'Member', 'Viewer') NOT NULL DEFAULT 'Member',
  `joined_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`workspace_id`, `user_id`),
  INDEX `idx_wm_user` (`user_id`),
  CONSTRAINT `fk_wm_workspace`
    FOREIGN KEY (`workspace_id`)
    REFERENCES `workspaces` (`workspace_id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_wm_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `users` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Groups
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `groups` (
  `group_id` INT NOT NULL AUTO_INCREMENT,
  `workspace_id` INT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`group_id`),
  INDEX `idx_group_workspace` (`workspace_id`),
  CONSTRAINT `fk_group_workspace`
    FOREIGN KEY (`workspace_id`)
    REFERENCES `workspaces` (`workspace_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Group Members
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `group_members` (
  `group_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `role` ENUM('Owner', 'Manager', 'Member', 'Viewer') NOT NULL DEFAULT 'Member',
  `joined_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`group_id`, `user_id`),
  INDEX `idx_gm_user` (`user_id`),
  CONSTRAINT `fk_gm_group`
    FOREIGN KEY (`group_id`)
    REFERENCES `groups` (`group_id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_gm_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `users` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Workflow
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `workflows` (
  `workflow_id` INT NOT NULL AUTO_INCREMENT,
  `group_id` INT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  PRIMARY KEY (`workflow_id`),
  INDEX `idx_wf_group` (`group_id`),
  CONSTRAINT `fk_wf_group`
    FOREIGN KEY (`group_id`)
    REFERENCES `groups` (`group_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `workflow_steps` (
  `step_id` INT NOT NULL AUTO_INCREMENT,
  `workflow_id` INT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `step_order` INT NOT NULL,
  PRIMARY KEY (`step_id`),
  INDEX `idx_ws_wf` (`workflow_id`),
  CONSTRAINT `fk_ws_wf`
    FOREIGN KEY (`workflow_id`)
    REFERENCES `workflows` (`workflow_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Projects
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `projects` (
  `project_id` INT NOT NULL AUTO_INCREMENT,
  `group_id` INT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `description` TEXT,
  `status` ENUM('Active', 'On Hold', 'Completed', 'Archived') NOT NULL DEFAULT 'Active',
  `owner_id` INT NOT NULL,
  `workflow_id` INT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `start_date` DATE,
  `due_date` DATE,
  PRIMARY KEY (`project_id`),
  INDEX `idx_proj_group` (`group_id`),
  INDEX `idx_proj_owner` (`owner_id`),
  INDEX `idx_proj_workflow` (`workflow_id`),
  CONSTRAINT `fk_proj_group`
    FOREIGN KEY (`group_id`)
    REFERENCES `groups` (`group_id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_proj_owner`
    FOREIGN KEY (`owner_id`)
    REFERENCES `users` (`user_id`)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_proj_workflow`
    FOREIGN KEY (`workflow_id`)
    REFERENCES `workflows` (`workflow_id`)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Project Members
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `project_members` (
  `project_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `role` ENUM('Owner', 'Manager', 'Contributor', 'Viewer') NOT NULL DEFAULT 'Contributor',
  `joined_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`project_id`, `user_id`),
  INDEX `idx_pm_user` (`user_id`),
  CONSTRAINT `fk_pm_project`
    FOREIGN KEY (`project_id`)
    REFERENCES `projects` (`project_id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_pm_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `users` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Milestones
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `milestones` (
  `milestone_id` INT NOT NULL AUTO_INCREMENT,
  `project_id` INT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `due_date` DATE,
  `is_completed` TINYINT(1) DEFAULT 0,
  `completed_at` DATETIME DEFAULT NULL,
  PRIMARY KEY (`milestone_id`),
  INDEX `idx_mil_project` (`project_id`),
  CONSTRAINT `fk_mil_project`
    FOREIGN KEY (`project_id`)
    REFERENCES `projects` (`project_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Tasks
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `tasks` (
  `task_id` INT NOT NULL AUTO_INCREMENT,
  `project_id` INT NOT NULL,
  `milestone_id` INT NULL,
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT,
  `status` ENUM('To Do', 'In Progress', 'Review', 'Done', 'Blocked') NOT NULL DEFAULT 'To Do',
  `task_progress` DECIMAL(5,2) DEFAULT 0.00,
  `created_by` INT NOT NULL,
  `assigned_to` INT NULL,
  `step_id` INT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `start_date` DATE,
  `due_date` DATE,
  PRIMARY KEY (`task_id`),
  INDEX `idx_task_project` (`project_id`),
  INDEX `idx_task_milestone` (`milestone_id`),
  INDEX `idx_task_created_by` (`created_by`),
  INDEX `idx_task_assigned_to` (`assigned_to`),
  INDEX `idx_task_step` (`step_id`),
  CONSTRAINT `fk_task_project`
    FOREIGN KEY (`project_id`)
    REFERENCES `projects` (`project_id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_task_milestone`
    FOREIGN KEY (`milestone_id`)
    REFERENCES `milestones` (`milestone_id`)
    ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_task_created_by`
    FOREIGN KEY (`created_by`)
    REFERENCES `users` (`user_id`)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_task_assigned_to`
    FOREIGN KEY (`assigned_to`)
    REFERENCES `users` (`user_id`)
    ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_task_step`
    FOREIGN KEY (`step_id`)
    REFERENCES `workflow_steps` (`step_id`)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Subtasks
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `subtasks` (
  `subtask_id` INT NOT NULL AUTO_INCREMENT,
  `task_id` INT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `status` ENUM('To Do', 'In Progress', 'Review', 'Done', 'Blocked') NOT NULL DEFAULT 'To Do',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`subtask_id`),
  INDEX `idx_subtask_task` (`task_id`),
  CONSTRAINT `fk_subtask_task`
    FOREIGN KEY (`task_id`)
    REFERENCES `tasks` (`task_id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Task History
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `task_history` (
  `history_id` INT NOT NULL AUTO_INCREMENT,
  `task_id` INT NOT NULL,
  `changed_by_user_id` INT NOT NULL,
  `field_name` VARCHAR(50) NOT NULL,
  `old_value` TEXT,
  `new_value` TEXT,
  `changed_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`history_id`),
  INDEX `idx_th_task` (`task_id`),
  INDEX `idx_th_changed_by` (`changed_by_user_id`),
  CONSTRAINT `fk_th_task`
    FOREIGN KEY (`task_id`)
    REFERENCES `tasks` (`task_id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_th_user`
    FOREIGN KEY (`changed_by_user_id`)
    REFERENCES `users` (`user_id`)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Comments
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `comment` (
  `comment_id` INT NOT NULL AUTO_INCREMENT,
  `task_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `content` TEXT NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`comment_id`),
  INDEX `idx_comment_task` (`task_id`),
  INDEX `idx_comment_user` (`user_id`),
  CONSTRAINT `fk_comment_task`
    FOREIGN KEY (`task_id`)
    REFERENCES `tasks` (`task_id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_comment_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `users` (`user_id`)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Comment History
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `comment_history` (
  `comment_history_id` INT NOT NULL AUTO_INCREMENT,
  `comment_id` INT NOT NULL,
  `edited_by_user_id` INT NOT NULL,
  `old_content` TEXT NOT NULL,
  `edited_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`comment_history_id`),
  INDEX `idx_ch_comment` (`comment_id`),
  INDEX `idx_ch_user` (`edited_by_user_id`),
  CONSTRAINT `fk_ch_comment`
    FOREIGN KEY (`comment_id`)
    REFERENCES `comment` (`comment_id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ch_user`
    FOREIGN KEY (`edited_by_user_id`)
    REFERENCES `users` (`user_id`)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Performance Records
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `performance_record` (
  `performance_id` INT NOT NULL AUTO_INCREMENT,
  `group_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `score` DECIMAL(5,2) NOT NULL,
  `comment` TEXT DEFAULT NULL,
  `created_by` INT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`performance_id`),
  INDEX `idx_pr_group` (`group_id`),
  INDEX `idx_pr_user` (`user_id`),
  INDEX `idx_pr_created_by` (`created_by`),
  CONSTRAINT `fk_pr_group`
    FOREIGN KEY (`group_id`)
    REFERENCES `groups` (`group_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_pr_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `users` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_pr_created_by`
    FOREIGN KEY (`created_by`)
    REFERENCES `users` (`user_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `refresh_token` (
  `refresh_token_id` INT NOT NULL AUTO_INCREMENT,
  `token` VARCHAR(512) NOT NULL UNIQUE,
  `user_id` INT NOT NULL,
  `expires_at` DATETIME NOT NULL,
  `is_revoked` TINYINT(1) NOT NULL DEFAULT 0,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`refresh_token_id`),
  INDEX `idx_user_id` (`user_id`),
  CONSTRAINT `fk_refresh_token_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `users` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `workspace_invitations` (
  `invitation_id` INT NOT NULL AUTO_INCREMENT,
  `workspace_id` INT NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `role` ENUM('Admin', 'Member', 'Viewer') NOT NULL DEFAULT 'Member',
  `invited_by` INT NOT NULL,
  `token` VARCHAR(255) NOT NULL UNIQUE,
  `status` ENUM('pending', 'accepted', 'expired') DEFAULT 'pending',
  `expires_at` DATETIME NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`invitation_id`),
  INDEX `idx_workspace_invite` (`workspace_id`),
  CONSTRAINT `fk_invite_workspace`
      FOREIGN KEY (`workspace_id`)
      REFERENCES `workspaces` (`workspace_id`)
      ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_invite_user`
      FOREIGN KEY (`invited_by`)
      REFERENCES `users` (`user_id`)
      ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
