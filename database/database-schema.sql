SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `demand`;
DROP TABLE IF EXISTS `order_item`;
DROP TABLE IF EXISTS `order`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `component`;
DROP TABLE IF EXISTS `announcement`;
DROP TABLE IF EXISTS `password_event`;


CREATE TABLE `user` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NULL,
    `last_name` VARCHAR(255) NULL,
    `email` VARCHAR(255) NULL,
    `password` VARCHAR(255) NULL,
    `address` VARCHAR(500) NULL,
    `cpf` VARCHAR(14) NULL,
    `cep` CHAR(8) NULL,
    `phone` VARCHAR(20) NULL,
    `already_recurrent` TINYINT(1) DEFAULT 0,
    `in_first_login` TINYINT(1) DEFAULT 0,
    `avatar_url` VARCHAR(500) NULL,
    `has_whats_app` TINYINT(1) DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `user_type` ENUM('ADMINISTRATOR', 'CUSTOMER') NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_user_email` (`email`),
    INDEX `idx_user_cpf` (`cpf`),
    INDEX `idx_user_phone` (`phone`),
    INDEX `idx_user_type` (`user_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `order` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `user_id` INT NULL,
    `status` ENUM('PENDING', 'IN_ANALYSIS', 'COMPLETED', 'DELIVERED') NOT NULL DEFAULT 'PENDING',
    `total` DECIMAL(10, 2) NULL,
    `discount` DECIMAL(10, 2) NULL,
    `fabric_guarantee` TINYINT(1) DEFAULT 0,
    `return_guarantee` TINYINT(1) DEFAULT 0,
    `description` TEXT NULL,
    `nf` VARCHAR(255) NULL,
    `date` DATE NULL,
    `store` VARCHAR(255) NULL,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `received_at` DATE NULL,
    `completed_time` DATE NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_order_user_id` (`user_id`),
    INDEX `idx_order_status` (`status`),
    INDEX `idx_order_created_at` (`created_at`),
    CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`)
        REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `order_item` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `order_id` INT NULL,
    `image_url` VARCHAR(500) NULL,
    `observation` TEXT NULL,
    `volt` VARCHAR(50) NULL,
    `series` VARCHAR(255) NULL,
    `type` VARCHAR(255) NULL,
    `brand` VARCHAR(255) NULL,
    `model` VARCHAR(255) NULL,
    `labor_value` DECIMAL(10, 2) NULL,
    `completed_at` DATETIME NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_order_item_order_id` (`order_id`),
    INDEX `idx_order_item_brand` (`brand`),
    INDEX `idx_order_item_type` (`type`),
    CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`)
        REFERENCES `order` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `component` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `description` TEXT NULL,
    `brand` VARCHAR(255) NULL,
    `price` DECIMAL(10, 2) NULL,
    `supplier` VARCHAR(255) NULL,
    `category` VARCHAR(255) NULL,
    `quantity` BIGINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_component_name` (`name`),
    INDEX `idx_component_category` (`category`),
    INDEX `idx_component_brand` (`brand`),
    INDEX `idx_component_supplier` (`supplier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `demand` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `order_id` INT NULL,
    `component_id` INT NULL,
    `quantity` BIGINT DEFAULT 1,
    PRIMARY KEY (`id`),
    INDEX `idx_demand_order_id` (`order_id`),
    INDEX `idx_demand_component_id` (`component_id`),
    CONSTRAINT `fk_demand_order` FOREIGN KEY (`order_id`)
        REFERENCES `order` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_demand_component` FOREIGN KEY (`component_id`)
        REFERENCES `component` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `announcement` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(255) NOT NULL,
    `content` TEXT NULL,
    `type` ENUM('PROMOTIONS', 'HOLIDAY', 'WARNINGS', 'RECOMMENDATIONS', 'UPDATES') NOT NULL,
    `published_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_announcement_type` (`type`),
    INDEX `idx_announcement_published_at` (`published_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `password_event` (
    `id` CHAR(36) NOT NULL,
    `type` ENUM('RESET', 'CREATE') NOT NULL,
    `status` ENUM('ACTIVE', 'USED') NOT NULL DEFAULT 'ACTIVE',
    `value` VARCHAR(255) NULL,
    `user_phone` VARCHAR(20) NOT NULL,
    `user_agent` VARCHAR(500) NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_password_event_user_phone` (`user_phone`),
    INDEX `idx_password_event_status` (`status`),
    INDEX `idx_password_event_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;


