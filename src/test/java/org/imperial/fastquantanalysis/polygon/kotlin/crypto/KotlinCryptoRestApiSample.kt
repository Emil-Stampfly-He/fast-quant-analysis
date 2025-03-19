package org.imperial.fastquantanalysis.polygon.kotlin.crypto

import io.polygon.kotlin.sdk.rest.reference.SupportedTickersParameters
import com.tylerthrailkill.helpers.prettyprint.pp
import io.polygon.kotlin.sdk.DefaultOkHttpClientProvider
import io.polygon.kotlin.sdk.HttpClientProvider
import io.polygon.kotlin.sdk.rest.*
import io.polygon.kotlin.sdk.rest.crypto.CryptoDailyOpenCloseParameters
import io.polygon.kotlin.sdk.rest.reference.ConditionsParameters
import io.polygon.kotlin.sdk.rest.reference.ExchangesParameters
import io.polygon.kotlin.sdk.rest.stocks.GainersOrLosersDirection
import okhttp3.Interceptor
import okhttp3.Response
import org.imperial.fastquantanalysis.constant.PolygonConstant.POLYGON_API_KEY

/**
 * Credit to <a href="https://github.com/polygon-io/client-jvm">https://github.com/polygon-io/client-jvm</a>
 */
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
        httpClientProvider = okHttpClientProvider
    )

    cryptoAggregatesBars(polygonClient)

    // cryptoExchanges(polygonClient)
}


// Crypto Aggregates Bars
// https://polygon.io/docs/crypto/get_v2_aggs_ticker__cryptoticker__range__multiplier___timespan___from___to
fun cryptoAggregatesBars(polygonClient: PolygonRestClient) {
    println("X:BTCUSD Aggs")
    val idxParams = AggregatesParameters(
        ticker = "X:BTCUSD",
        timespan = "day",
        fromDate = "2025-02-03",
        toDate = "2025-02-07",
        sort = "asc",
        limit = 50_000,
    )
    polygonClient.getAggregatesBlocking(idxParams).pp()
}

// Crypto Conditions
// https://polygon.io/docs/crypto/get_v3_reference_conditions
fun cryptoConditions(polygonClient: PolygonRestClient) {
    println("Conditions:")
    polygonClient.referenceClient.getConditionsBlocking(ConditionsParameters(assetClass = "crypto")).pp()
    polygonClient.referenceClient.listConditions(ConditionsParameters(assetClass = "crypto", limit = 5))
        .asSequence().take(15).forEach { println("got condition #${it.id}") }
}

// Crypto Daily Open Close
// https://polygon.io/docs/crypto/get_v1_open-close_crypto__from___to___date
fun cryptoDailyOpenClose(polygonClient: PolygonRestClient) {
    println("BTC open/close on 2023-01-09")
    val params = CryptoDailyOpenCloseParameters(
        from = "BTC", to = "USD", date = "2023-01-09"
    )

    polygonClient.cryptoClient.getDailyOpenCloseBlocking(params).pp()
}

// Crypto Exchanges
// https://polygon.io/docs/crypto/get_v3_reference_exchanges
fun cryptoExchanges(polygonClient: PolygonRestClient) {
    println("Supported stock exchanges: ")
    polygonClient.referenceClient.getExchangesBlocking(ExchangesParameters(assetClass = "crypto")).pp()
}

// Crypto Grouped Daily Bars
// https://polygon.io/docs/crypto/get_v2_aggs_grouped_locale_global_market_crypto__date
fun cryptoGroupedDailyBars(polygonClient: PolygonRestClient) {
    println("Grouped dailies for 2023-01-09")
    val params = GroupedDailyParameters(
        locale = "global",
        market = "crypto",
        date = "2023-01-09"
    )

    polygonClient.getGroupedDailyAggregatesBlocking(params).pp()
}

// Crypto Last Trade For A Crypto Pair
// https://polygon.io/docs/crypto/get_v1_last_crypto__from___to
fun cryptoLastTradeForCryptoPair(polygonClient: PolygonRestClient) {
    println("Last trade for BTC/USD crypto pair")
    polygonClient.cryptoClient.getLastTradeBlocking("BTC", "USD").pp()
}

// Crypto Market Holidays
// https://polygon.io/docs/crypto/get_v1_marketstatus_upcoming
fun cryptoMarketHolidays(polygonClient: PolygonRestClient) {
    println("Market holidays:")
    polygonClient.referenceClient.getMarketHolidaysBlocking().pp()
}

// Crypto Market Status
// https://polygon.io/docs/crypto/get_v1_marketstatus_now
fun cryptoMarketStatus(polygonClient: PolygonRestClient) {
    println("Market status:")
    polygonClient.referenceClient.getMarketStatusBlocking().pp()
}

// Crypto Previous Close
// https://polygon.io/docs/crypto/get_v2_aggs_ticker__cryptoticker__prev
fun cryptoPreviousClose(polygonClient: PolygonRestClient) {
    println("X:BTCUSD Previous Close:")
    polygonClient.stocksClient.getPreviousCloseBlocking("X:BTCUSD", true).pp()
}

// Crypto Snapshots All Tickers
// https://polygon.io/docs/crypto/get_v2_snapshot_locale_global_markets_crypto_tickers
fun cryptoSnapshotsAllTickers(polygonClient: PolygonRestClient) {
    println("Crypto Snapshots All Tickers:")
    polygonClient.cryptoClient.getSnapshotAllTickersBlocking().pp()
}

// Crypto Snapshots Gainers Losers
// https://polygon.io/docs/crypto/get_v2_snapshot_locale_global_markets_crypto__direction
fun cryptoSnapshotsGainersLosers(polygonClient: PolygonRestClient) {
    println("Today's crypto gainers: ")
    polygonClient.cryptoClient.getSnapshotGainersOrLosersBlocking(GainersOrLosersDirection.GAINERS).pp()

    println("Today's crypto losers: ")
    polygonClient.cryptoClient.getSnapshotGainersOrLosersBlocking(GainersOrLosersDirection.LOSERS).pp()
}

// Crypto Snapshots Ticker
// https://polygon.io/docs/crypto/get_v2_snapshot_locale_global_markets_crypto_tickers__ticker
fun cryptoSnapshotsTicker(polygonClient: PolygonRestClient) {
    println("Snapshot for X:BTCUSD")
    polygonClient.cryptoClient.getSnapshotSingleTickerBlocking("X:BTCUSD").pp()
}

// Crypto Snapshots Ticker Full Book L2
// https://polygon.io/docs/crypto/get_v2_snapshot_locale_global_markets_crypto_tickers__ticker__book
fun cryptoSnapshotsTickerFullBookL2(polygonClient: PolygonRestClient) {
    println("X:BTCUSD L2 data:")
    polygonClient.cryptoClient.getL2SnapshotSingleTickerBlocking("X:BTCUSD").pp()
}

// Crypto Technical Indicators EMA
// https://polygon.io/docs/crypto/get_v1_indicators_ema__cryptoticker
fun cryptoTechnicalIndicatorsEMA(polygonClient: PolygonRestClient) {
    println("X:BTCUSD EMA:")
    polygonClient.getTechnicalIndicatorEMABlocking("X:BTCUSD", EMAParameters()).pp()
}

// Crypto Technical Indicators MACD
// https://polygon.io/docs/crypto/get_v1_indicators_macd__cryptoticker
fun cryptoTechnicalIndicatorsMACD(polygonClient: PolygonRestClient) {
    println("X:BTCUSD MACD:")
    polygonClient.getTechnicalIndicatorMACDBlocking("X:BTCUSD", MACDParameters()).pp()
}

// Crypto Technical Indicators RSI
// https://polygon.io/docs/crypto/get_v1_indicators_rsi__cryptoticker
fun cryptoTechnicalIndicatorsRSI(polygonClient: PolygonRestClient) {
    println("X:BTCUSD RSI:")
    polygonClient.getTechnicalIndicatorRSIBlocking("X:BTCUSD", RSIParameters()).pp()
}

// Crypto Technical Indicators SMA
// https://polygon.io/docs/crypto/get_v1_indicators_sma__cryptoticker
fun cryptoTechnicalIndicatorsSMA(polygonClient: PolygonRestClient) {
    println("X:BTCUSD SMA:")
    polygonClient.getTechnicalIndicatorSMABlocking("X:BTCUSD", SMAParameters()).pp()
}

// Crypto Tickers
// https://polygon.io/docs/crypto/get_v3_reference_tickers
fun cryptoTickers(polygonClient: PolygonRestClient) {
    println("Tickers:")
    val params = SupportedTickersParameters(
        sortBy = "ticker",
        sortDescending = false,
        market = "crypto",
        limit = 100
    )

    polygonClient.referenceClient.getSupportedTickersBlocking(params).pp()
}

// Crypto Trades
// https://polygon.io/docs/crypto/get_v3_trades__cryptoticker
fun cryptoTrades(polygonClient: PolygonRestClient) {
    println("X:BTC-USD Trades:")
    val params = TradesParameters(timestamp = ComparisonQueryFilterParameters.equal("2023-02-01"), limit = 2)
    polygonClient.getTradesBlocking("X:BTC-USD", params).pp()
}