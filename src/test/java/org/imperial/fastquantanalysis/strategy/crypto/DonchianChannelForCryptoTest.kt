package org.imperial.fastquantanalysis.strategy.crypto

import io.polygon.kotlin.sdk.DefaultOkHttpClientProvider
import io.polygon.kotlin.sdk.HttpClientProvider
import io.polygon.kotlin.sdk.rest.PolygonRestClient
import jakarta.annotation.Resource
import okhttp3.Interceptor
import okhttp3.Response
import org.imperial.fastquantanalysis.constant.PolygonConstant.POLYGON_API_KEY
import org.imperial.fastquantanalysis.constant.StrategyName
import org.imperial.fastquantanalysis.entity.QuantStrategy
import org.imperial.fastquantanalysis.polygon.kotlin.crypto.cryptoAggregatesBarsWithClosePrices
import org.imperial.fastquantanalysis.util.RedisIdUtil
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.math.pow
import kotlin.math.sqrt

val okHttpClientProvider: HttpClientProvider
    get() = DefaultOkHttpClientProvider(
        applicationInterceptors = listOf(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                println("Intercepting application level")
                println("request: ${chain.request().url}")
                return chain.proceed(chain.request())
            }
        }),
        networkInterceptors = listOf(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                println("Intercepting network level")
                return chain.proceed(chain.request())
            }
        })
    )

fun main() {
    val polygonKey: String = POLYGON_API_KEY

    val polygonClient: PolygonRestClient = PolygonRestClient(
        polygonKey,
        httpClientProvider = org.imperial.fastquantanalysis.polygon.kotlin.crypto.okHttpClientProvider
    )

    val prices = replaceNullsWithPreviousElement(cryptoAggregatesBarsWithClosePrices(polygonClient))

    // cryptoExchanges(polygonClient).pp()

    // cryptoAggregatesBars(polygonClient).pp()
}

fun replaceNullsWithPreviousElement(list: MutableList<Double?>, default: Double = 0.0): MutableList<Double> {
    val result: MutableList<Double?> = list.toMutableList()

    if (result.isNotEmpty() && result[0] == null) {
        result[0] = default
    }

    for (i in 1 until result.size) {
        if (result[i] == null) {
            result[i] = result[i - 1]
        }
    }

    return result.filterNotNull().toMutableList()
}

class DonchianChannelForCryptoTest (
    @Resource
    private val redisIdUtil: RedisIdUtil
) {


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
        val meanReturn = if (returns.isNotEmpty()) returns.average() else 0.0
        val variance: Double = returns.fold(0.0) {acc, r -> acc + (r - meanReturn) * (r - meanReturn)} / returns.size
        val volatility: Double = sqrt(variance)

        // Sharpe ratio
        val sharpeRatio: Double = if (volatility != 0.0) meanReturn / volatility else 0.0

        // Annualized return
        val tradingDays = (prices.size - lookback).toDouble()
        val annualizedReturn = cumulativeReturn.pow(365.0 / tradingDays) - 1

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
}
