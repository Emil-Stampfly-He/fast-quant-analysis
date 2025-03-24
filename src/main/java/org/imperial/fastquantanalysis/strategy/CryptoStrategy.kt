package org.imperial.fastquantanalysis.strategy

import io.finnhub.api.infrastructure.toMultiValue
import jakarta.annotation.Resource
import net.finmath.timeseries.TimeSeries
import org.imperial.fastquantanalysis.constant.StrategyName
import org.imperial.fastquantanalysis.entity.QuantStrategy
import org.imperial.fastquantanalysis.util.RedisIdUtil
import org.springframework.stereotype.Component
import org.ta4j.core.Bar
import org.ta4j.core.BaseBar
import org.ta4j.core.BaseStrategy
import org.ta4j.core.Strategy
import org.ta4j.core.criteria.MaximumDrawdownCriterion
import org.ta4j.core.criteria.helpers.StandardDeviationCriterion
import org.ta4j.core.indicators.SMAIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.num.DoubleNum
import org.ta4j.core.rules.OverIndicatorRule
import org.ta4j.core.rules.UnderIndicatorRule
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.pow
import kotlin.math.sqrt

@Component
class CryptoStrategy (
    @Resource
    private val redisIdUtil: RedisIdUtil
) {
    /**
     * Donchian channel strategy
     * @param prices prices (usually close prices) list
     * @param lookback window size
     * @return QuantStrategy object
     */
    fun donchianChannel(prices: MutableList<Double>, lookback: Int = 20): QuantStrategy {
        val strategyId: String = redisIdUtil.nextId(StrategyName.DONCHIAN_CHANNEL)
        val strategyName: String = StrategyName.DONCHIAN_CHANNEL
        val startDate: LocalDateTime = LocalDateTime.now()
        val endDate: LocalDateTime = LocalDateTime.now()

        var position: Int = 0 // 0: short; 1: long
        var entryPrice: Double = 0.0
        var cumulativeReturn: Double = 1.0
        var peak: Double = cumulativeReturn // Peak of cumulative return, to calculate max drawdown
        var maxDrawdown: Double = 0.0
        var tradeCount: Int = 0

        val returns = mutableListOf<Double>()

        for (i in lookback until prices.size) {
            val window: MutableList<Double> = prices.subList(i - lookback, i)
            val upperBand: Double = window.maxOrNull() ?: prices[i]
            val lowerBand: Double = window.minOrNull() ?: prices[i]
            val currentPrice: Double = prices[i]
            val previousPrice: Double = prices[i - 1]

            val dailyReturn: Double = (currentPrice / previousPrice) - 1
            returns.add(dailyReturn)

            // Signal
            if (position == 0 && currentPrice > upperBand) {
                // Enter the market and long
                position = 1
                entryPrice = currentPrice
                tradeCount++
            } else if (position == 1 && currentPrice < lowerBand) {
                // Close long
                val tradeReturn: Double = currentPrice / entryPrice
                cumulativeReturn *= tradeReturn
                position = 0
            }

            // Update cumulative return peak and calculate max drawdown
            if (cumulativeReturn > peak) {
                peak = cumulativeReturn
            }
            val drawdown: Double = (peak - cumulativeReturn) / peak
            if (drawdown > maxDrawdown) {
                maxDrawdown = drawdown
            }
        }

        // If the position is still held at the end of the data, the position is closed
        if (position == 1) {
            val tradeReturn: Double = prices.last() / entryPrice
            cumulativeReturn *= tradeReturn
            position = 0
        }

        // Calculate volatility
        val meanReturn: Double = if (returns.isNotEmpty()) returns.average() else 0.0
        val variance: Double = returns.fold(0.0) {acc, r -> acc + (r - meanReturn) * (r - meanReturn)} / returns.size
        val volatility: Double = sqrt(variance)

        // Sharpe ratio
        // Risk free rate = 0
        val sharpeRatio: Double = if (volatility != 0.0) meanReturn / volatility else 0.0

        // Annualized return
        val tradingDays: Double = (prices.size - lookback).toDouble()
        val annualizedReturn: Double = cumulativeReturn.pow(365.0 / tradingDays) - 1

        return QuantStrategy(
            strategyId,
            strategyName,
            startDate,
            endDate,
            annualizedReturn,
            cumulativeReturn,
            maxDrawdown,
            volatility,
            sharpeRatio,
            tradeCount
        )
    }

    /**
     * Pair trading strategy
     * @param barPrices1 price list for crypto 1
     * @param barPrices2 price list for crypto 2
     * @param window window size
     * @param zScoreThreshold z-score threshold
     * @param x previous x days
     * @return QuantStrategy object
     */
    fun pairTrading(barPrices1: MutableList<Double>, barPrices2: MutableList<Double>,
                    window: Int, zScoreThreshold: Double, x: Int): QuantStrategy {
        val strategyId: String = redisIdUtil.nextId(StrategyName.PAIR_TRADING)
        val strategyName: String = StrategyName.PAIR_TRADING
        val startDate: LocalDateTime = LocalDateTime.now()
        val endDate: LocalDateTime = LocalDateTime.now()

        val n: Int = barPrices1.size
        // x cannot be larger than the number of total days
        if (x > n) {
            return QuantStrategy(
                strategyId,
                strategyName,
                startDate,
                endDate,
                Double.NaN,
                Double.NaN,
                Double.NaN,
                Double.NaN,
                Double.NaN,
                0
            )
        }

        val beta: Double = calculateBeta(barPrices1, barPrices2)
        val spread: MutableList<Double> = MutableList(n) { Double.NaN }

        for (i in 0 until n) {
            spread[i] = barPrices1[i] - beta * barPrices2[i]
        }

        val rollMean: MutableList<Double> = rollingMean(spread, window)
        val rollStd: MutableList<Double> = rollingStd(spread, rollMean, window)

        val signal: MutableList<Int> = MutableList(n) { 0 }
        val zScore: MutableList<Double> = MutableList(n) { Double.NaN }
        for (i in 0 until n) {
            if (i < window - 1 || rollStd[i].isNaN() || rollStd[i] == 0.0) {
                zScore[i] = Double.NaN
                signal[i] = 0
            } else {
                zScore[i] = (spread[i] - rollMean[i]) / rollStd[i]
                signal[i] = when {
                    zScore[i] > zScoreThreshold -> -1
                    zScore[i] < -zScoreThreshold -> 1
                    else -> 0
                }
            }
        }

        // Strategy return = position * (spread[t] - spread[t - x]
        // Previous x day's signal is used for current day's position
        val strategyReturns: MutableList<Double> = MutableList(n) { 0.0 }
        for (i in x until n) {
            strategyReturns[i] = spread[i - x] * spread[i] - spread[i - x]
        }

        // Equity curve, starting money: 1.0 (this is to get the percentage)
        val equityCurve = MutableList(n) { 1.0 }
        for (i in 1 until n) {
            equityCurve[i] = equityCurve[i - 1] * (1 + strategyReturns[i])
        }

        // Cumulative return = final equity - 1
        val cumulativeReturn = equityCurve.last() - 1

        // Max drawdown
        var peak = equityCurve[0]
        var maxDrawdown = 0.0
        for (e in equityCurve) {
            if (e > peak) {
                peak = e
            }
            val drawdown = (peak - e) / peak
            if (drawdown > maxDrawdown) {
                maxDrawdown = drawdown
            }
        }

        // Volatility
        // Number of trading days: 365
        val dailyReturns = strategyReturns.drop(1) // Remove the first meaningless 0 gain
        val avgDailyReturn = dailyReturns.average()
        val dailyVol = sqrt(dailyReturns.map { (it - avgDailyReturn).pow(2) }.average())
        val annualizedVolatility = dailyVol * sqrt(365.0)
        val annualizedReturn = equityCurve.last().pow(365.0 / (n - 1)) - 1

        // Sharpe ratio
        // Risk free rate = 0
        val sharpeRatio = if (annualizedVolatility != 0.0) annualizedReturn / annualizedVolatility else 0.0

        // Trading count
        var tradeCount = 0
        for (i in 1 until n) {
            if (signal[i] != signal[i - 1] && signal[i] != 0) {
                tradeCount++
            }
        }

        return QuantStrategy(
            strategyId,
            strategyName,
            startDate,
            endDate,
            annualizedReturn,
            cumulativeReturn,
            maxDrawdown,
            annualizedVolatility,
            sharpeRatio,
            tradeCount
        )

    }

    private fun mean(prices: MutableList<Double>): Double {
        return prices.average();
    }

    private fun std(prices: MutableList<Double>, mean: Double): Double {
        var sum = 0.0
        for (price in prices) {
            sum += (price - mean).pow(2.0)
        }
        return sqrt(sum / mean)
    }

    // beta = Cov(crypto2, crypto1) / Var(crypto2)
    private fun calculateBeta(barPrices1: MutableList<Double>, barPrices2: MutableList<Double>): Double {
        val n = barPrices1.size
        val mean1 = mean(barPrices1)
        val mean2 = mean(barPrices2)
        var cov = 0.0
        var variance = 0.0

        for (i in 0 until n) {
            cov += (barPrices2[i] - mean2) * (barPrices1[i] - mean1)
            variance += (barPrices2[i] - mean2).pow(2.0)
        }

        return cov / variance
    }

    private fun rollingMean(barPrices: MutableList<Double>, window: Int): MutableList<Double> {
        val n = barPrices.size
        val rollMean = MutableList(n) { Double.NaN }

        for (i in barPrices.indices) {
            if (i >= window - 1) {
                var sum = 0.0
                for (j in i - window + 1..i) {
                    sum += barPrices[j]
                }

                rollMean[i] = sum / window
            }
        }

        return rollMean
    }

    private fun rollingStd(barPrices: MutableList<Double>, rollMean:MutableList<Double>, window: Int): MutableList<Double> {
        val n = barPrices.size
        val rollStd = MutableList(n) { Double.NaN }

        for (i in barPrices.indices) {
            if (i >= window - 1) {
                var sum = 0.0
                for (j in i - window + 1..i) {
                    sum += (barPrices[j] - rollMean[i]).pow(2.0)
                }

                rollStd[i] = sqrt(sum / window)
            }
        }

        return rollStd
    }



//    fun simpleSMA(barPrices: MutableList<MutableList<Double>>): QuantStrategy {
//        val strategyId: String = redisIdUtil.nextId(StrategyName.DONCHIAN_CHANNEL)
//        val strategyName: String = StrategyName.DONCHIAN_CHANNEL
//        val startDate: LocalDateTime = LocalDateTime.now()
//        val endDate: LocalDateTime = LocalDateTime.now()
//
//        val bars: MutableList<Bar> = mutableListOf<Bar>()
//        var endTime: ZonedDateTime = endDate.atZone(ZoneId.systemDefault())
//        for (i in 0 until barPrices[0].size) {
//            val bar: Bar = BaseBar(
//                Duration.ofDays(1),
//                endDate.atZone(ZoneId.systemDefault()),
//                barPrices[0][i], // open
//                barPrices[1][i], // high
//                barPrices[2][i], // low
//                barPrices[3][i], // close
//                barPrices[4][i], // volume
//            )
//
//            bars.add(bar)
//            endTime = endTime.minusDays(1);
//
//            bars.reverse()
//
//            // 创建时间序列
//            val series: TimeSeries = ("price_series", bars)
//
//            // 创建收盘价指标
//            val closePriceIndicator = ClosePriceIndicator(series)
//
//            // 计算简单移动平均（SMA），窗口期设为3
//            val smaPeriod = 3
//            val smaIndicator = SMAIndicator(closePriceIndicator, smaPeriod)
//
//            // 定义交易规则：
//            // 买入规则：当收盘价上穿 SMA 时触发
//            val entryRule = OverIndicatorRule(closePriceIndicator, smaIndicator)
//            // 卖出规则：当收盘价下穿 SMA 时触发
//            val exitRule = UnderIndicatorRule(closePriceIndicator, smaIndicator)
//
//            // 创建策略
//            val strategy: Strategy = BaseStrategy(entryRule, exitRule)
//
//            // 使用 TimeSeriesManager 回测策略
//            val seriesManager = TimeSeriesManager(series)
//            val tradingRecord = seriesManager.run(strategy)
//
//            // 使用 TA4J 的评估标准计算指标
//            val totalProfitCriterion = TotalProfitCriterion()
//            val annualizedReturnCriterion = AnnualizedReturnCriterion()
//            val maximumDrawdownCriterion = MaximumDrawdownCriterion()
//            val standardDeviationCriterion = StandardDeviationCriterion()  // 用于衡量波动率
//            val sharpeRatioCriterion = SharpeRatioCriterion()
//            val numberOfTradesCriterion = NumberOfTradesCriterion()
//
//            // 注意：TA4J计算的结果是Num类型，转换为Double后更易处理
//            val cumulativeReturn = totalProfitCriterion.calculate(series, tradingRecord).toDouble()
//            val annualizedReturn = annualizedReturnCriterion.calculate(series, tradingRecord).toDouble()
//            val maxDrawdown = maximumDrawdownCriterion.calculate(series, tradingRecord).toDouble()
//            val volatility = standardDeviationCriterion.calculate(series, tradingRecord).toDouble()
//            val sharpeRatio = sharpeRatioCriterion.calculate(series, tradingRecord).toDouble()
//            val tradeCount = numberOfTradesCriterion.calculate(series, tradingRecord).toDouble().toInt()
//
//        }
//    }
}