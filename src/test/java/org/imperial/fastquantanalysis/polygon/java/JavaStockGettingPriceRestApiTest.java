package org.imperial.fastquantanalysis.polygon.java;

import io.polygon.kotlin.sdk.HttpClientProvider;
import io.polygon.kotlin.sdk.rest.AggregateDTO;
import io.polygon.kotlin.sdk.rest.AggregatesDTO;
import io.polygon.kotlin.sdk.rest.AggregatesParameters;
import io.polygon.kotlin.sdk.rest.PolygonRestClient;
import org.imperial.fastquantanalysis.constant.PolygonConstant;
import org.imperial.fastquantanalysis.constant.Sort;
import org.imperial.fastquantanalysis.constant.Timespan;
import org.imperial.fastquantanalysis.util.PricesHttpClientUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JavaStockGettingPriceRestApiTest {
    public static void main(String[] args) {
        String polygonApiKey = PolygonConstant.POLYGON_API_KEY;
        HttpClientProvider okHttpClientProvider = PricesHttpClientUtil.getOkHttpClientProvider();

        PolygonRestClient polygonClient = new PolygonRestClient(
                polygonApiKey,
                okHttpClientProvider);


        List<List<Double>> barPrices = PricesHttpClientUtil.getBarPrices(
                "RDFN",
                null,
                Timespan.DAY,
                LocalDate.of(2025, 2, 3),
                LocalDate.of(2025, 2, 7),
                null,
                null,
                Sort.ASC,
                polygonClient
        );
        System.out.println("Bar Prices:");
        System.out.println(barPrices); // 5 entries?

        List<Double> closePrices = PricesHttpClientUtil.getClosePrices("RDFN",
                null,
                Timespan.DAY,
                LocalDate.of(2025, 2, 3),
                LocalDate.of(2025, 2, 7),
                null,
                null,
                Sort.ASC,
                polygonClient);
        System.out.println("Close Prices:");
        System.out.println(closePrices);

        System.out.println("Aggregate Prices in DTO:");
        List<AggregateDTO> aggregateDTOS = stockAggregateBarsClosingPrice(polygonClient);
        System.out.println(aggregateDTOS);

    }

    public static List<AggregateDTO> stockAggregateBarsClosingPrice(PolygonRestClient polygonRestClient) {
        System.out.println("RDFN Aggs");
        AggregatesParameters aggregatesParameters = new AggregatesParameters(
                "RDFN",
                1,
                "day",
                "2025-02-03",
                "2025-02-07",
                false,
                50000,
                "asc"

        );

        AggregatesDTO aggregatesBlocking = polygonRestClient.getAggregatesBlocking(aggregatesParameters);
        System.out.println(aggregatesBlocking);
        return new ArrayList<>(aggregatesBlocking.getResults());
    }
}
