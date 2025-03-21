package org.imperial.fastquantanalysis.factory

import io.polygon.kotlin.sdk.rest.AggregatesParameters

/**
 * Aggregates parameters factory
 *
 * @author Emil S. He
 * @since 2025-03-21
 */
object AggregatesParametersFactory {
    @JvmStatic
    @JvmOverloads
    fun create(
        ticker: String,
        multiplier: Long? = null,
        timespan: String,
        fromDate: String,
        toDate: String,
        unadjusted: Boolean? = null,
        limit: Long? = null,
        sort: String
    ): AggregatesParameters {
        return AggregatesParameters(
            ticker = ticker,
            multiplier = multiplier ?: 1, // default value
            timespan = timespan,
            fromDate = fromDate,
            toDate = toDate,
            unadjusted = unadjusted ?: false, // default value
            limit = limit ?: 50000, // default value
            sort = sort
        )
    }
}
