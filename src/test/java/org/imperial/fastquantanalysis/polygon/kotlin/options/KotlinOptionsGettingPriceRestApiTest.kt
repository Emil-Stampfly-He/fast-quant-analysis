package org.imperial.fastquantanalysis.polygon.kotlin.options

import io.polygon.kotlin.sdk.rest.PolygonRestClient
import org.imperial.fastquantanalysis.constant.PolygonConstant.POLYGON_API_KEY
import org.imperial.fastquantanalysis.polygon.kotlin.crypto.okHttpClientProvider

/**
 * Options data annot be fetched if not upgrading the plan
 */
fun main() {
    val polygonKey: String = POLYGON_API_KEY

    val polygonClient: PolygonRestClient = PolygonRestClient(
        polygonKey,
        httpClientProvider = okHttpClientProvider
    )

    optionsAggregatesBars(polygonClient)
}