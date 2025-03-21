package org.imperial.fastquantanalysis.util;

import io.polygon.kotlin.sdk.DefaultOkHttpClientProvider;
import io.polygon.kotlin.sdk.HttpClientProvider;
import io.polygon.kotlin.sdk.rest.AggregatesDTO;
import io.polygon.kotlin.sdk.rest.AggregatesParameters;
import io.polygon.kotlin.sdk.rest.PolygonRestClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.imperial.fastquantanalysis.constant.Sort;
import org.imperial.fastquantanalysis.constant.Timespan;
import org.imperial.fastquantanalysis.factory.AggregatesParametersFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Various methods about getting crypto prices
 *
 * @author Emil S. He
 * @since 2025-03-21
 */
@Slf4j
public class CryptoHttpClientUtil {

    /**
     * Get the link object to Polygon.io
     * @return HTTP client provider (the link object)
     */
    public static HttpClientProvider getOkHttpClientProvider() {
        return new DefaultOkHttpClientProvider(
                List.of(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        log.info("Intercepting application level");
                        log.info("request: {}", chain.request().url());
                        return chain.proceed(chain.request());
                    }
                }),

                List.of(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        log.info("Intercepting network level");
                        return chain.proceed(chain.request());
                    }
                })
        );
    }

    /**
     * Get crypto close prices
     * @param tickerName ticker name
     * @param multiplier multiplier, default value: 1
     * @param timespan timespan
     * @param fromDate from date
     * @param toDate to date
     * @param unadjusted unadjusted, default value: false
     * @param limit number of queries, default value: 50000
     * @param sort sort, asc means from oldest to newest
     * @param polygonClient polygon client
     * @return list of crypto close prices
     */
    public static List<Double> getClosePrices(String tickerName, Long multiplier,
                                              Timespan timespan, LocalDate fromDate,
                                              LocalDate toDate, Boolean unadjusted,
                                              Long limit, Sort sort,
                                              PolygonRestClient polygonClient) {
        log.info("{} Close", tickerName);
        AggregatesParameters idxParams = AggregatesParametersFactory.create(
                tickerName,
                multiplier,
                timespan.getValue(),
                fromDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                toDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                unadjusted,
                limit,
                sort.getValue()
        );

        AggregatesDTO aggregatesBlocking = polygonClient.getAggregatesBlocking(idxParams);
        List<Double> closePrices = new ArrayList<>();
        aggregatesBlocking.getResults().forEach(aggregateDTO -> {
            closePrices.add(aggregateDTO.getClose());
        });

        replaceNullWithPrevious(closePrices);

        return closePrices;
    }

    // TODO Add methods to get secondly, minutely, hourly, monthly, quarterly and yearly prices

    // TODO This method should be made more robust
    private static void replaceNullWithPrevious(List<Double> list) {
        if (list.isEmpty() || list.size() == 1) {
            return;
        }

        if (list.get(0) == null) {
            list.set(0, list.get(1));
        }

        for (int i = 1; i < list.size(); i++) {
            if (list.get(i) == null) {
                list.set(i, list.get(i - 1));
            }
        }
    }
}
