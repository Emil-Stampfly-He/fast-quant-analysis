package org.imperial.fastquantanalysis.polygon.kotlin.stocks

import io.polygon.kotlin.sdk.rest.AggregatesParameters
import io.polygon.kotlin.sdk.rest.PolygonRestClient
import org.imperial.fastquantanalysis.constant.PolygonConstant.POLYGON_API_KEY
import org.imperial.fastquantanalysis.polygon.kotlin.crypto.okHttpClientProvider

fun main() {
    val polygonKey: String = POLYGON_API_KEY

    val polygonClient: PolygonRestClient = PolygonRestClient(
        polygonKey,
        httpClientProvider = okHttpClientProvider
    )

    println(stocksAggregatesBarsClosingPrice(polygonClient))
    // stocksAggregatesBars(polygonClient)
}

fun stocksAggregatesBarsClosingPrice(polygonClient: PolygonRestClient): MutableList<Double?> {
    println("RDFN Aggs")
    val params = AggregatesParameters(
        ticker = "RDFN",
        timespan = "day",
        fromDate = "2023-07-03",
        toDate = "2023-07-07",
        sort = "asc",
        limit = 50_000,
    )

    val closePrices: MutableList<Double?> = mutableListOf()
    polygonClient.getAggregatesBlocking(params).results.forEach {
        closePrices.add(it.close)
    }

    return closePrices
}

