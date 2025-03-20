package org.imperial.fastquantanalysis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Quant strategy entity
 *
 * @author Emil S. He
 * @since 2025-03-20
 */
@Data
@AllArgsConstructor
@TableName("quant_strategy")
public class QuantStrategy {

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
