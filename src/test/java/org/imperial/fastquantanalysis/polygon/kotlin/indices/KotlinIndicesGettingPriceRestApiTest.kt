package org.imperial.fastquantanalysis.polygon.kotlin.indices

import io.polygon.kotlin.sdk.DefaultOkHttpClientProvider
import io.polygon.kotlin.sdk.HttpClientProvider
import io.polygon.kotlin.sdk.rest.PolygonRestClient
import okhttp3.Interceptor
import okhttp3.Response
import org.imperial.fastquantanalysis.constant.PolygonConstant.POLYGON_API_KEY

/**
 * Indices data annot be fetched if not upgrading the plan
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
        httpClientProvider = org.imperial.fastquantanalysis.polygon.kotlin.crypto.okHttpClientProvider
    )

    // indicesAggregatesBars(polygonClient) // Unauthorized, need to upgrade the plan


}

//fun indicesAggregatesBars(polygonClient: PolygonRestClient) {
//    println("I:SPX Aggs")
//    val idxParams = AggregatesParameters(
//        ticker = "I:SPX",
//        timespan = "day",
//        fromDate = "2023-07-03",
//        toDate = "2023-07-07",
//        sort = "asc",
//        limit = 50_000,
//    )
//    polygonClient.getAggregatesBlocking(idxParams).pp()
//}
