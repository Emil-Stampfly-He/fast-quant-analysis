package org.imperial.fastquantanalysis.polygon.java;

import io.polygon.kotlin.sdk.DefaultOkHttpClientProvider;
import io.polygon.kotlin.sdk.HttpClientProvider;
import io.polygon.kotlin.sdk.rest.AggregatesDTO;
import io.polygon.kotlin.sdk.rest.AggregatesParameters;
import io.polygon.kotlin.sdk.rest.PolygonRestClient;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.imperial.fastquantanalysis.constant.Sort;
import org.imperial.fastquantanalysis.constant.Timespan;
import org.imperial.fastquantanalysis.factory.AggregatesParametersFactory;
import org.imperial.fastquantanalysis.util.CryptoHttpClientUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.imperial.fastquantanalysis.constant.PolygonConstant.POLYGON_API_KEY;

public class JavaCryptoGettingPriceRestApiTest {
    public static void main(String[] args) {
        HttpClientProvider okHttpClientProvider = CryptoHttpClientUtil.getOkHttpClientProvider();
        PolygonRestClient polygonClient = new PolygonRestClient(
                POLYGON_API_KEY, // To be changed to user's
                okHttpClientProvider
        );

//        List<Double> closePrices = CryptoHttpClientUtil.getClosePrices(
//                "X:BTCUSD",
//                null,
//                Timespan.DAY,
//                LocalDate.of(2025, 2, 3),
//                LocalDate.of(2025,2,7),
//                null,
//                null,
//                Sort.ASC,
//                polygonClient
//        );

        List<List<Double>> barPrices = CryptoHttpClientUtil.getBarPrices(
                "X:BTCUSD",
                null,
                Timespan.DAY,
                LocalDate.of(2025, 2, 3),
                LocalDate.of(2025, 2, 7),
                null,
                null,
                Sort.ASC,
                polygonClient
        );

        barPrices.forEach(System.out::println);
    }

    public static List<Double> getClosePrices(PolygonRestClient polygonClient) {
        System.out.println("X:BTCUSD Close");
        AggregatesParameters idxParams = AggregatesParametersFactory.create(
                "X:BTCUSD",   // ticker
                Timespan.DAY.getValue(),        // timespan
                "2024-02-03", // fromDate
                "2025-02-07", // toDate
                "asc"
        );

        AggregatesDTO aggregatesBlocking = polygonClient.getAggregatesBlocking(idxParams);
        List<Double> closePrices = new ArrayList<>();
        aggregatesBlocking.getResults().forEach(aggregateDTO -> {
            closePrices.add(aggregateDTO.getClose());
        });

        replaceNullWithPrevious(closePrices);

        return closePrices;
    }

    private static HttpClientProvider getOkHttpClientProvider() {
        return new DefaultOkHttpClientProvider(
                List.of(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        System.out.println("Intercepting application level");
                        System.out.println("request: " + chain.request().url());
                        return chain.proceed(chain.request());
                    }
                }),

                List.of(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        System.out.println("Intercepting network level");
                        return chain.proceed(chain.request());
                    }
                })
        );
    }

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

    @Test
    void testReplaceNullWithPrevious() {
        List<Double> testList = new ArrayList<>();
        testList.add(null);
        testList.add(1.2);
        testList.add(-1.2);
        testList.add(null);
        testList.add(1.2);
        testList.add(-1.2);

        replaceNullWithPrevious(testList);

        System.out.println(testList);
    }
}
