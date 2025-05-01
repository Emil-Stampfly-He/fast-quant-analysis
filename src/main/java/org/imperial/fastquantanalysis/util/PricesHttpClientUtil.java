package org.imperial.fastquantanalysis.util;

import io.polygon.kotlin.sdk.DefaultOkHttpClientProvider;
import io.polygon.kotlin.sdk.HttpClientProvider;
import io.polygon.kotlin.sdk.rest.AggregateDTO;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Various methods about getting crypto prices
 *
 * @author Emil S. He
 * @since 2025-03-21
 */
@Slf4j
public class PricesHttpClientUtil {

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
        log.info("Close prices: {}", closePrices);

        return closePrices;
    }

    /**
     * Get crypto bar prices and volume, which contains open, close, high, low prices
     * and transaction volume
     * @param tickerName ticker name
     * @param multiplier multiplier, default value: 1
     * @param timespan timespan
     * @param fromDate from date
     * @param toDate to date
     * @param unadjusted unadjusted, default value: false
     * @param limit number of queries, default value: 50000
     * @param sort sort, asc means from oldest to newest
     * @param polygonClient polygon client
     * @return The list of 4 price lists and volume list
     */
    public static List<List<Double>> getBarPricesAndVolume(String tickerName, Long multiplier,
                                         Timespan timespan, LocalDate fromDate,
                                         LocalDate toDate, Boolean unadjusted,
                                         Long limit, Sort sort,
                                         PolygonRestClient polygonClient) {
        log.info("{} Bar Prices & Volume", tickerName);
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
        List<AggregateDTO> results = aggregatesBlocking.getResults();

        List<List<Double>> barPricesWithVolume = Arrays.asList(
                results.stream().map(AggregateDTO::getOpen).collect(Collectors.toList()), // 0: open
                results.stream().map(AggregateDTO::getHigh).collect(Collectors.toList()), // 1: high
                results.stream().map(AggregateDTO::getLow).collect(Collectors.toList()), // 2: low
                results.stream().map(AggregateDTO::getClose).collect(Collectors.toList()), // 3: close
                results.stream().map(AggregateDTO::getVolume).collect(Collectors.toList()) // 4: volume

        );

        barPricesWithVolume.forEach(PricesHttpClientUtil::replaceNullWithPrevious);

        return barPricesWithVolume;
    }

    /**
     * Get crypto bar prices without volume, which contains open, close, high, and low prices
     * @param tickerName ticker name
     * @param multiplier multiplier, default value: 1
     * @param timespan timespan
     * @param fromDate from date
     * @param toDate to date
     * @param unadjusted unadjusted, default value: false
     * @param limit number of queries, default value: 50000
     * @param sort sort, asc means from oldest to newest
     * @param polygonClient polygon client
     * @return The list of 4 price lists
     */
    public static List<List<Double>> getBarPrices(String tickerName, Long multiplier,
                                                  Timespan timespan, LocalDate fromDate,
                                                  LocalDate toDate, Boolean unadjusted,
                                                  Long limit, Sort sort,
                                                  PolygonRestClient polygonClient) {
        log.info("{} Bar", tickerName);
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
        List<AggregateDTO> results = aggregatesBlocking.getResults();

        List<List<Double>> barPrices = Arrays.asList(
                results.stream().map(AggregateDTO::getOpen).collect(Collectors.toList()), // 0: open prices
                results.stream().map(AggregateDTO::getHigh).collect(Collectors.toList()), // 1: high prices
                results.stream().map(AggregateDTO::getLow).collect(Collectors.toList()), // 2: low prices
                results.stream().map(AggregateDTO::getClose).collect(Collectors.toList()) // 3: close prices
        );

        barPrices.forEach(PricesHttpClientUtil::replaceNullWithPrevious);

        return barPrices;
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
