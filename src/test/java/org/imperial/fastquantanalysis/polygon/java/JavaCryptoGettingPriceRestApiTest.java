package org.imperial.fastquantanalysis.polygon.java;

import io.polygon.kotlin.sdk.DefaultOkHttpClientProvider;
import io.polygon.kotlin.sdk.HttpClientProvider;
import io.polygon.kotlin.sdk.rest.AggregatesParameters;
import io.polygon.kotlin.sdk.rest.PolygonRestClient;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.imperial.fastquantanalysis.factory.AggregatesParametersFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.imperial.fastquantanalysis.constant.PolygonConstant.POLYGON_API_KEY;

public class JavaCryptoGettingPriceRestApiTest {
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

    public static void main(String[] args) {
        PolygonRestClient polygonClient = new PolygonRestClient(
                POLYGON_API_KEY,
                getOkHttpClientProvider()
        );

        cryptoAggregatesBars(polygonClient);

        // cryptoExchanges(polygonClient);
    }

    public static void cryptoAggregatesBars(PolygonRestClient polygonClient) {
        System.out.println("X:BTCUSD Aggs");
        AggregatesParameters idxParams = AggregatesParametersFactory.create(
                "X:BTCUSD",   // ticker
                "day",        // timespan
                "2025-02-03", // fromDate
                "2025-02-07", // toDate
                "asc"         // sort
        );
        System.out.println(polygonClient.getAggregatesBlocking(idxParams));
    }
}
