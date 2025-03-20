package org.imperial.fastquantanalysis.polygon.kotlin.forex

import io.polygon.kotlin.sdk.rest.PolygonRestClient
import org.imperial.fastquantanalysis.constant.PolygonConstant.POLYGON_API_KEY

/**
 * Forex data cannot be fetched if not upgrading the plan
 */
fun main() {
    val polygonKey: String = POLYGON_API_KEY

    val polygonClient: PolygonRestClient = PolygonRestClient(
        polygonKey,
        httpClientProvider = org.imperial.fastquantanalysis.polygon.kotlin.crypto.okHttpClientProvider
    )

    forexAggregatesBars(polygonClient)


}