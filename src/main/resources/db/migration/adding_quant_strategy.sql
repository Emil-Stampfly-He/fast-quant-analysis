USE fast_quant_analysis;

CREATE TABLE quant_strategy (
                                strategy_id VARCHAR(50) NOT NULL,
                                strategy_name VARCHAR(100) NOT NULL,
                                start_date DATETIME NOT NULL,
                                end_date DATETIME NOT NULL,
                                annualized_return DOUBLE DEFAULT NULL,
                                cumulative_return DOUBLE DEFAULT NULL,
                                max_drawdown DOUBLE DEFAULT NULL,
                                volatility DOUBLE DEFAULT NULL,
                                sharpe_ratio DOUBLE DEFAULT NULL,
                                trade_count INT DEFAULT NULL,
                                PRIMARY KEY (strategy_id)
);
