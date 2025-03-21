package org.imperial.fastquantanalysis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Quant strategy entity
 *
 * @author Emil S. He
 * @since 2025-03-20
 */
@Data
@TableName("quant_strategy")
public class QuantStrategy {

    public QuantStrategy() {}

    // Kotlin cannot see lombok annotation @AllArgsConstructor, thus it cannot be used
    public QuantStrategy(String strategyId, String strategyName,
                         LocalDateTime startDate, LocalDateTime endDate,
                         Double annualizedReturn, Double cumulativeReturn,
                         Double maxDrawdown, Double volatility,
                         Double sharpeRatio, Integer tradeCount) {
        this.strategyId = strategyId;
        this.strategyName = strategyName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.annualizedReturn = annualizedReturn;
        this.cumulativeReturn = cumulativeReturn;
        this.maxDrawdown = maxDrawdown;
        this.volatility = volatility;
        this.sharpeRatio = sharpeRatio;
        this.tradeCount = tradeCount;
    }

    // Basic info about strategy
    @TableId(value = "strategy_id", type = IdType.ASSIGN_ID)
    private String strategyId;

    @TableField("strategy_name")
    private String strategyName;

    @TableField("start_date")
    private LocalDateTime startDate;

    @TableField("end_date")
    private LocalDateTime endDate;

    // Return indices
    @TableField("annualized_return")
    private Double annualizedReturn;

    @TableField("cumulative_return")
    private Double cumulativeReturn;

    // Risk indices
    @TableField("max_drawdown")
    private Double maxDrawdown;

    @TableField("volatility")
    private Double volatility;

    // Other indices
    @TableField("sharpe_ratio")
    private Double sharpeRatio;

    // Statistics
    @TableField("trade_count")
    private Integer tradeCount;
}
