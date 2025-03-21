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
    fun create(
        ticker: String,
        timespan: String,
        fromDate: String,
        toDate: String,
        sort: String
    ): AggregatesParameters {
        return AggregatesParameters(
            ticker = ticker,
            multiplier = 1, // default value
            timespan = timespan,
            fromDate = fromDate,
            toDate = toDate,
            unadjusted = false, // default value
            limit = 50000, // default value
            sort = sort
        )
    }
}
