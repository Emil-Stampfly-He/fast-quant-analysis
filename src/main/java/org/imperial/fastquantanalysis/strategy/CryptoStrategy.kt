package org.imperial.fastquantanalysis.strategy

import jakarta.annotation.Resource
import org.imperial.fastquantanalysis.constant.StrategyName
import org.imperial.fastquantanalysis.entity.QuantStrategy
import org.imperial.fastquantanalysis.util.RedisIdUtil
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * All crypto strategies
 *
 * @author Emil S. He
 * @since 2025-03-20
 */
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
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
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
        val cumulativeReturn = if (equityCurve.last().isInfinite()) 0.0 else equityCurve.last() - 1

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
        val annualizedReturn = if (equityCurve.last().isInfinite()) 0.0 else equityCurve.last().pow(365.0 / (n - 1)) - 1

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

    /**
     * EMA with fixed percentage stop loss
     * @param closePrices close price list
     * @param avgBarPrices average bar price list
     * @param emaPeriod EMA window size
     * @param stopLossPercent percentage of stop loss
     * @return QuantStrategy object
     */
    fun emaWithStopLossPercentage(closePrices: List<Double>, avgBarPrices: List<Double>,
                                  emaPeriod: Int, stopLossPercent: Double): QuantStrategy {
        val strategyId: String = redisIdUtil.nextId(StrategyName.EMA_WITH_STOP_LOSS_PERCENTAGE)
        val strategyName: String = StrategyName.EMA_WITH_STOP_LOSS_PERCENTAGE
        val startDate: LocalDateTime = LocalDateTime.now()
        val endDate: LocalDateTime = LocalDateTime.now()

        val initialCapital: Double = 1.0
        val tradingDaysPerYear: Int = 365

        val ema = calculateEMA(closePrices, emaPeriod) // Calculate EMA using close prices
        var tradeCount: Int = 0
        val equityCurve: MutableList<Double> = mutableListOf(initialCapital)
        var isHolding: Boolean = false
        var entryPrice: Double = 0.0
        var stopLossPrice: Double = 0.0
        var currentCapital: Double = initialCapital
        var sharesHeld: Double = 0.0

        for (i in emaPeriod until avgBarPrices.size) {
            val price = avgBarPrices[i]
            val currentEMA = ema[i - emaPeriod]
            val prevPrice = avgBarPrices[i - 1]
            val prevEMA = if (i > emaPeriod) ema[i - emaPeriod - 1] else 0.0

            if (isHolding) {
                currentCapital = sharesHeld * price
                equityCurve.add(currentCapital)
            } else {
                equityCurve.add(currentCapital)
            }

            if (!isHolding) {
                // Buying condition: price crossing above EMA
                if (prevPrice < prevEMA && price > currentEMA) {
                    isHolding = true
                    entryPrice = price
                    stopLossPrice = entryPrice * (1 - stopLossPercent / 100)
                    sharesHeld = currentCapital / price
                    tradeCount++
                }
            } else {
                // Check stop loss
                if (price <= stopLossPrice) {
                    isHolding = false
                    currentCapital = sharesHeld * price
                    tradeCount++
                } else if (prevPrice > prevEMA && price < currentEMA) {
                    // Selling condition: price crossing under EMA
                    isHolding = false
                    currentCapital = sharesHeld * price
                    tradeCount++
                }
            }
        }

        // Size of equity curve cannot be less than 2
        if (equityCurve.size < 2) {
            return QuantStrategy(
                strategyId,
                strategyName,
                startDate,
                endDate,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0
            )
        }

        val cumulativeReturn = (equityCurve.last() / initialCapital) - 1.0
        val totalDays = equityCurve.size - 1
        val annualizedReturn = if (totalDays > 0) {
            (1 + cumulativeReturn).pow(tradingDaysPerYear.toDouble() / totalDays) - 1
        } else 0.0

        var peak = equityCurve[0]
        var maxDrawdown = 0.0
        for (value in equityCurve) {
            if (value > peak) peak = value
            val drawdown = (peak - value) / peak
            if (drawdown > maxDrawdown) maxDrawdown = drawdown
        }

        val returns = equityCurve.zipWithNext().map { (prev, curr) -> (curr - prev) / prev }

        val meanReturn = returns.average()
        val variance = returns.map { (it - meanReturn).pow(2) }.average()
        val annualizedVolatility = sqrt(variance * tradingDaysPerYear)

        // Risk free rate = 0
        val sharpeRatio = if (annualizedVolatility != 0.0) {
            annualizedReturn / annualizedVolatility
        } else 0.0

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

    /**
     * EMA with dynamic stol loss strategy using ATR for crypto prices
     * @param barPrices bar price list
     * @param emaPeriod EMA window size
     * @param atrPeriod ATR window size
     * @param atrMultiplier ATR multiplier
     * @return QuantStrategy object
     */
    fun emaWithATRStopLoss(barPrices: List<List<Double>>,
                           emaPeriod: Int, atrPeriod: Int,
                           atrMultiplier: Double): QuantStrategy {
        val strategyId: String = redisIdUtil.nextId(StrategyName.EMA_WITH_ATR_STOP_LOSS)
        val strategyName: String = StrategyName.EMA_WITH_ATR_STOP_LOSS
        val startDate: LocalDateTime = LocalDateTime.now()
        val endDate: LocalDateTime = LocalDateTime.now()

        val ema = calculateEMA(barPrices[3], emaPeriod)
        val atr = calculateATR(barPrices, atrPeriod)
        var tradeCount: Int = 0
        val initialCapital: Double = 1.0
        val tradingDaysPerYear: Int = 365
        val equityCurve: MutableList<Double> = mutableListOf(initialCapital)
        var isHolding: Boolean = false
        var entryPrice: Double = 0.0
        var stopLossPrice: Double = 0.0
        var currentCapital: Double = initialCapital
        var sharesHeld: Double = 0.0

        val startIndex = max(emaPeriod, atrPeriod + 1) // ATR need n + 1 days
        if (barPrices[0].size < startIndex + 1) return QuantStrategy(
            strategyId,
            strategyName,
            startDate,
            endDate,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0
        )

        for (i in startIndex until barPrices[3].size) {
            val currentClosePrice = barPrices[3][i]

            // EMA index alignment
            val emaIndex = i - emaPeriod
            val currentEMA = ema[emaIndex]
            val prevEMA = ema[emaIndex - 1]
            val prevClose = barPrices[3][i - 1]

            // ATR index alignment
            val atrIndex = i - (atrPeriod + 1)
            val currentATR = atr[atrIndex]

            if (isHolding) {
                currentCapital = sharesHeld * currentClosePrice
                equityCurve.add(currentCapital)
            } else {
                equityCurve.add(currentCapital)
            }

            if (!isHolding) {
                // Buying condition: close price cross over EMA
                if (prevClose < prevEMA && currentClosePrice > currentEMA) {
                    isHolding = true
                    entryPrice = currentClosePrice
                    stopLossPrice = entryPrice - currentATR * atrMultiplier
                    sharesHeld = currentCapital / currentATR
                    tradeCount++
                }
            } else {
                // Check dynamic stop loss
                if (currentClosePrice <= stopLossPrice) {
                    isHolding = false
                    currentCapital = sharesHeld * currentClosePrice
                    tradeCount++
                } else if (prevClose > prevEMA && currentClosePrice < currentEMA) {
                    isHolding = false
                    currentCapital = sharesHeld * currentClosePrice
                    tradeCount++
                }
            }
        }

        val cumulativeReturn = (equityCurve.last() / initialCapital) - 1.0
        val totalDays = equityCurve.size - 1
        val annualizedReturn = if (totalDays > 0) {
            (1 + cumulativeReturn).pow(tradingDaysPerYear.toDouble() / totalDays) - 1
        } else 0.0

        var peak = equityCurve[0]
        var maxDrawdown = 0.0
        for (value in equityCurve) {
            if (value > peak) peak = value
            val drawdown = (peak - value) / peak
            if (drawdown > maxDrawdown) maxDrawdown = drawdown
        }

        val returns = equityCurve.zipWithNext().map { (prev, curr) -> (curr - prev) / prev }

        val meanReturn = returns.average()
        val variance = returns.map { (it - meanReturn).pow(2) }.average()
        val annualizedVolatility = sqrt(variance * tradingDaysPerYear)

        // Risk free rate = 0
        val sharpeRatio = if (annualizedVolatility != 0.0) {
            annualizedReturn / annualizedVolatility
        } else 0.0

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

    // Close price list should be used to calculate EMA
    private fun calculateEMA(prices: List<Double>, period: Int): List<Double> {
        if (prices.size < period) return emptyList()
        val ema = mutableListOf<Double>()
        val sma = prices.take(period).average()
        ema.add(sma)
        val k = 2.0 / (period + 1)
        for (i in period until prices.size) {
            ema.add(prices[i] * k + ema.last() * (1 - k))
        }
        return ema
    }

    private fun calculateATR(barPrices: List<List<Double>>, period: Int): List<Double> {
        if (barPrices.size < period + 1) return emptyList()
        val trueRanges = mutableListOf<Double>()

        for (i in 1 until barPrices.size) {
            val prevClose = barPrices[3][i - 1] // close price in previous day
            val currentHigh = barPrices[1][i] // current high price
            val currentLow = barPrices[2][i] // current low price
            val tr = maxOf(
                currentHigh - currentLow,
                abs(currentHigh - prevClose),
                abs(currentLow - prevClose)
            )
            trueRanges.add(tr)
        }

        // ATR
        val atr = mutableListOf<Double>()
        atr.add(trueRanges.take(period).average())
        for (i in period until trueRanges.size) {
            atr.add((atr.last() * (period - 1) + trueRanges[i]) / period)
        }

        return atr
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

    private fun rollingStd(barPrices: MutableList<Double>,
                           rollMean:MutableList<Double>, window: Int): MutableList<Double> {
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
}