package org.imperial.fastquantanalysis.polygon.kotlin.crypto

import io.polygon.kotlin.sdk.rest.AggregateDTO
import io.polygon.kotlin.sdk.rest.AggregatesDTO
import io.polygon.kotlin.sdk.rest.AggregatesParameters
import io.polygon.kotlin.sdk.rest.PolygonRestClient
import org.imperial.fastquantanalysis.constant.PolygonConstant.POLYGON_API_KEY

fun main() {
    val polygonKey: String = POLYGON_API_KEY

    val polygonClient: PolygonRestClient = PolygonRestClient(
        polygonKey,
        httpClientProvider = okHttpClientProvider
    )

    val cryptoAggregatesBarsWithClosePrices = cryptoAggregatesBarsWithClosePrices(polygonClient)
    println(cryptoAggregatesBarsWithClosePrices)

    // cryptoExchanges(polygonClient)
}

fun cryptoAggregatesBarsWithClosePrices(polygonClient: PolygonRestClient): MutableList<Double?> {
    println("BTC-USD")
    val idxParams: AggregatesParameters = AggregatesParameters(
        ticker = "X:BTCUSD",
        timespan = "day",
        fromDate = "2025-02-03",
        toDate = "2025-02-07",
        sort = "asc",
        limit = 50_000,
    )

    val aggregatesBlocking: AggregatesDTO = polygonClient.getAggregatesBlocking(idxParams)
    val results: List<AggregateDTO> = aggregatesBlocking.results
    val closePrices: MutableList<Double?> = mutableListOf()

    for (aggregateDTO in results) {
        closePrices.add(aggregateDTO.close)
    }

    return closePrices
}

//fun cryptoAggregatesBars(polygonClient: PolygonRestClient) {
//    println("X:BTCUSD Aggs")
//    val idxParams = AggregatesParameters(
//        ticker = "X:BTCUSD",
//        timespan = "day",
//        fromDate = "2025-02-03",
//        toDate = "2025-02-07",
//        sort = "asc",
//        limit = 50_000,
//    )
//    polygonClient.getAggregatesBlocking(idxParams).pp()
//}