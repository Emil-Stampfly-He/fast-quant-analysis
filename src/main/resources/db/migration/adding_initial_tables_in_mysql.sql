USE fast_quant_analysis;

-- 表：users
CREATE TABLE users (
                       id CHAR(36) PRIMARY KEY,
                       email_id VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(150) NOT NULL,
                       first_name VARCHAR(50) NOT NULL,
                       last_name VARCHAR(50) NOT NULL,
                       date_of_birth TIMESTAMP NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 表：total_balances
CREATE TABLE total_balances (
                                id CHAR(36) PRIMARY KEY,
                                user_id CHAR(36) NOT NULL,
                                created_at TIMESTAMP NOT NULL,
                                updated_at TIMESTAMP NOT NULL,
                                FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 表：balance_modes
CREATE TABLE balance_modes (
                               id CHAR(36) PRIMARY KEY,
                               total_balance_id CHAR(36) NOT NULL,
                               mode_type VARCHAR(30) NOT NULL,
                               name VARCHAR(50) NOT NULL,
                               value DECIMAL NOT NULL,
                               is_active BOOLEAN NOT NULL,
                               created_at TIMESTAMP NOT NULL,
                               updated_at TIMESTAMP NOT NULL,
                               FOREIGN KEY (total_balance_id) REFERENCES total_balances(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 表：completed_tickets
CREATE TABLE completed_tickets (
                                   id CHAR(36) PRIMARY KEY,
                                   user_id CHAR(36) NOT NULL,
                                   balance_mode_id CHAR(36) NOT NULL,
                                   name VARCHAR(50) NOT NULL,
                                   description VARCHAR(500) NOT NULL,
                                   ticket_type VARCHAR(10) NOT NULL,
                                   value DECIMAL NOT NULL,
                                   ticket_completion_date TIMESTAMP NOT NULL,
                                   created_at TIMESTAMP NOT NULL,
                                   updated_at TIMESTAMP NOT NULL,
                                   FOREIGN KEY (user_id) REFERENCES users(id),
                                   FOREIGN KEY (balance_mode_id) REFERENCES balance_modes(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 表：future_tickets
CREATE TABLE future_tickets (
                                id CHAR(36) PRIMARY KEY,
                                user_id CHAR(36) NOT NULL,
                                balance_mode_id CHAR(36) NOT NULL,
                                name VARCHAR(50) NOT NULL,
                                description VARCHAR(500) NOT NULL,
                                ticket_type VARCHAR(10) NOT NULL,
                                value DECIMAL NOT NULL,
                                ticket_completion_date TIMESTAMP NULL,
                                created_at TIMESTAMP NOT NULL,
                                updated_at TIMESTAMP NOT NULL,
                                FOREIGN KEY (user_id) REFERENCES users(id),
                                FOREIGN KEY (balance_mode_id) REFERENCES balance_modes(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 表：financial_goals
CREATE TABLE financial_goals (
                                 id CHAR(36) PRIMARY KEY,
                                 user_id CHAR(36) NOT NULL,
                                 title VARCHAR(100) NOT NULL,
                                 description VARCHAR(1000) NOT NULL,
                                 is_active BOOLEAN NOT NULL,
                                 created_at TIMESTAMP NOT NULL,
                                 updated_at TIMESTAMP NOT NULL,
                                 FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 表：financial_notes
CREATE TABLE financial_notes (
                                 id CHAR(36) PRIMARY KEY,
                                 user_id CHAR(36) NOT NULL,
                                 title VARCHAR(100) NOT NULL,
                                 description VARCHAR(1000) NOT NULL,
                                 is_active BOOLEAN NOT NULL,
                                 created_at TIMESTAMP NOT NULL,
                                 updated_at TIMESTAMP NOT NULL,
                                 FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 表：master_tags
CREATE TABLE master_tags (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             name VARCHAR(50) NOT NULL UNIQUE,
                             created_at TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 表：note_tag_mappings
CREATE TABLE note_tag_mappings (
                                   id INT AUTO_INCREMENT PRIMARY KEY,
                                   tag_id INT NOT NULL,
                                   note_id CHAR(36) NOT NULL,
                                   created_at TIMESTAMP NOT NULL,
                                   UNIQUE KEY (tag_id, note_id),
                                   FOREIGN KEY (tag_id) REFERENCES master_tags(id),
                                   FOREIGN KEY (note_id) REFERENCES financial_notes(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 表：ticket_tag_mappings
CREATE TABLE ticket_tag_mappings (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     tag_id INT NOT NULL,
                                     ticket_type VARCHAR(20) NOT NULL,
                                     ticket_id CHAR(36) NOT NULL,
                                     created_at TIMESTAMP NOT NULL,
                                     FOREIGN KEY (tag_id) REFERENCES master_tags(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
