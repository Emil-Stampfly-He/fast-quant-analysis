USE fast_quant_analysis;

-- 表：current_monthly_spending_threshold_limits
CREATE TABLE current_monthly_spending_threshold_limits (
                                                           id INT AUTO_INCREMENT PRIMARY KEY,
                                                           user_id CHAR(36) NOT NULL,
                                                           limit_value DECIMAL NOT NULL,
                                                           is_active BOOLEAN NOT NULL,
                                                           FOREIGN KEY (user_id) REFERENCES users(id)
                                                               ON UPDATE NO ACTION
                                                               ON DELETE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 表：spending_threshold_records
CREATE TABLE spending_threshold_records (
                                            id INT AUTO_INCREMENT PRIMARY KEY,
                                            user_id CHAR(36) NOT NULL,
                                            limit_value DECIMAL NOT NULL,
                                            value_spent DECIMAL NOT NULL,
                                            month VARCHAR(10) NOT NULL,
                                            year VARCHAR(4) NOT NULL,
                                            FOREIGN KEY (user_id) REFERENCES users(id)
                                                ON UPDATE NO ACTION
                                                ON DELETE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
