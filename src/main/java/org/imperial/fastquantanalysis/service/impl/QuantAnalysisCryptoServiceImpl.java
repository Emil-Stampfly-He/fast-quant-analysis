package org.imperial.fastquantanalysis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.polygon.kotlin.sdk.HttpClientProvider;
import io.polygon.kotlin.sdk.rest.PolygonRestClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imperial.fastquantanalysis.annotation.AsyncTimed;
import org.imperial.fastquantanalysis.constant.KafkaConstant;
import org.imperial.fastquantanalysis.constant.Sort;
import org.imperial.fastquantanalysis.constant.Timespan;
import org.imperial.fastquantanalysis.dto.CryptoAggregatesDTO;
import org.imperial.fastquantanalysis.dto.CryptoAggregatesPairDTO;
import org.imperial.fastquantanalysis.entity.QuantStrategy;
import org.imperial.fastquantanalysis.mapper.QuantAnalysisCryptoMapper;
import org.imperial.fastquantanalysis.service.IQuantAnalysisCryptoService;
import org.imperial.fastquantanalysis.strategy.CryptoStrategy;
import org.imperial.fastquantanalysis.util.CryptoHttpClientUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Quant analysis service implementation class
 *
 * @author Emil S. He
 * @since 2025-03-20
 */
@Slf4j
@Service
public class QuantAnalysisCryptoServiceImpl extends ServiceImpl<QuantAnalysisCryptoMapper, QuantStrategy> implements IQuantAnalysisCryptoService {

    @Resource
    private CryptoStrategy cryptoStrategy;

    @Resource
    private KafkaTemplate<String, QuantStrategy> kafkaTemplate;

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down executor service...");
            executorService.shutdown();
        }));
    }

    /**
     * Donchian channel strategy for crypto
     * @param polygonApiKey user's Polygon.io API key
     * @param cryptoAggregatesDTO cryptoAggregatesDTO
     * @param windowSize lookback
     * @return OK or fail message
     */
    @Override
    public ResponseEntity<QuantStrategy> donchian(String polygonApiKey, CryptoAggregatesDTO cryptoAggregatesDTO, Integer windowSize) {
        String tickerName = cryptoAggregatesDTO.getTickerName();
        Timespan timespan = cryptoAggregatesDTO.getTimespan();
        LocalDate fromDate = cryptoAggregatesDTO.getFromDate();
        LocalDate toDate = cryptoAggregatesDTO.getToDate();
        Sort sort = cryptoAggregatesDTO.getSort();
        Long multiplier = cryptoAggregatesDTO.getMultiplier();
        Boolean unadjusted = cryptoAggregatesDTO.getUnadjusted();
        Long limit = cryptoAggregatesDTO.getLimit();

        HttpClientProvider okHttpClientProvider = CryptoHttpClientUtil.getOkHttpClientProvider();
        PolygonRestClient polygonRestClient = new PolygonRestClient(
                polygonApiKey,
                okHttpClientProvider
        );

        List<Double> closePrices = CryptoHttpClientUtil.getClosePrices(
                tickerName, multiplier,
                timespan, fromDate, toDate,
                unadjusted, limit, sort,
                polygonRestClient
        );

        QuantStrategy quantStrategy = cryptoStrategy.donchianChannel(closePrices, windowSize);
        quantStrategy.setStartDate(fromDate.atStartOfDay());
        quantStrategy.setEndDate(toDate.atStartOfDay());

        kafkaTemplate.send(KafkaConstant.TOPIC_NAME, quantStrategy);

        return ResponseEntity.ok(quantStrategy);
    }

    /**
     * Pair trading for 2 cryptos
     * @param polygonApiKey User's polygon API key
     * @param cryptoAggregatesPairDTO DTO for carrying necessary information, but in pairs
     * @param windowSize Window size
     * @param zScoreThreshold Threshold of z-score
     * @param x Previous x days
     * @return OK or fail message
     */
    @AsyncTimed
    @Override
    public CompletableFuture<ResponseEntity<QuantStrategy>> pairTrading(String polygonApiKey, CryptoAggregatesPairDTO cryptoAggregatesPairDTO,
                                                            Integer windowSize, Double zScoreThreshold, Integer x) {
        PolygonRestClient polygonRestClient = new PolygonRestClient(polygonApiKey, CryptoHttpClientUtil.getOkHttpClientProvider());

        CompletableFuture<List<List<Double>>> future1 = CompletableFuture.supplyAsync(() ->
                CryptoHttpClientUtil.getBarPrices(
                        cryptoAggregatesPairDTO.getTickerName1(),
                        cryptoAggregatesPairDTO.getMultiplier(),
                        cryptoAggregatesPairDTO.getTimespan(),
                        cryptoAggregatesPairDTO.getFromDate(),
                        cryptoAggregatesPairDTO.getToDate(),
                        cryptoAggregatesPairDTO.getUnadjusted(),
                        cryptoAggregatesPairDTO.getLimit(),
                        cryptoAggregatesPairDTO.getSort(),
                        polygonRestClient),
                executorService
        );

        CompletableFuture<List<List<Double>>> future2 = CompletableFuture.supplyAsync(() ->
                CryptoHttpClientUtil.getBarPrices(
                        cryptoAggregatesPairDTO.getTickerName2(),
                        cryptoAggregatesPairDTO.getMultiplier(),
                        cryptoAggregatesPairDTO.getTimespan(),
                        cryptoAggregatesPairDTO.getFromDate(),
                        cryptoAggregatesPairDTO.getToDate(),
                        cryptoAggregatesPairDTO.getUnadjusted(),
                        cryptoAggregatesPairDTO.getLimit(),
                        cryptoAggregatesPairDTO.getSort(),
                        polygonRestClient),
                executorService
        );

        return future1.thenCombineAsync(future2, (barPrices1, barPrices2) -> {
            List<Double> avgBarPrice1 = IntStream.range(0, barPrices1.get(0).size())
                    .mapToDouble(i -> barPrices1.stream().mapToDouble(list -> list.get(i)).average().orElse(0))
                    .boxed().toList();

            List<Double> avgBarPrice2 = IntStream.range(0, barPrices2.get(0).size())
                    .mapToDouble(i -> barPrices2.stream().mapToDouble(list -> list.get(i)).average().orElse(0))
                    .boxed().toList();

            QuantStrategy quantStrategy = cryptoStrategy.pairTrading(avgBarPrice1, avgBarPrice2, windowSize, zScoreThreshold, x);
            quantStrategy.setStartDate(cryptoAggregatesPairDTO.getFromDate().atStartOfDay());
            quantStrategy.setEndDate(cryptoAggregatesPairDTO.getToDate().atStartOfDay());

            kafkaTemplate.send(KafkaConstant.TOPIC_NAME, quantStrategy);

            return ResponseEntity.ok(quantStrategy);
        }, executorService);
    }

    /**
     * EMA with fixed percentage stop loss
     * @param polygonApiKey User's polygon API key
     * @param cryptoAggregatesDTO DTO for carrying necessary information
     * @param emaPeriod EMA window size
     * @param stopLossPercentage Fixed percentage of stop loss
     * @return OK or fail message
     */
    @Override
    public ResponseEntity<QuantStrategy> emaWithStopLossPercentage(String polygonApiKey, CryptoAggregatesDTO cryptoAggregatesDTO,
                                                       Integer emaPeriod, Double stopLossPercentage) {
        String tickerName = cryptoAggregatesDTO.getTickerName();
        Timespan timespan = cryptoAggregatesDTO.getTimespan();
        LocalDate fromDate = cryptoAggregatesDTO.getFromDate();
        LocalDate toDate = cryptoAggregatesDTO.getToDate();
        Sort sort = cryptoAggregatesDTO.getSort();
        Long multiplier = cryptoAggregatesDTO.getMultiplier();
        Boolean unadjusted = cryptoAggregatesDTO.getUnadjusted();
        Long limit = cryptoAggregatesDTO.getLimit();

        HttpClientProvider okHttpClientProvider = CryptoHttpClientUtil.getOkHttpClientProvider();
        PolygonRestClient polygonRestClient = new PolygonRestClient(
                polygonApiKey,
                okHttpClientProvider
        );

        List<List<Double>> barPrices = CryptoHttpClientUtil.getBarPrices(
                tickerName, multiplier,
                timespan, fromDate, toDate,
                unadjusted, limit, sort,
                polygonRestClient
        );
        List<Double> closePrices = barPrices.get(3);
        List<Double> avgBarPrice = IntStream.range(0, barPrices.get(0).size())
                .mapToDouble(i -> barPrices.stream()
                        .mapToDouble(list ->
                                list.get(i)).average().orElse(0))
                .boxed()
                .toList();

        QuantStrategy quantStrategy = cryptoStrategy.emaWithStopLossPercentage(closePrices, avgBarPrice, emaPeriod, stopLossPercentage);
        quantStrategy.setStartDate(fromDate.atStartOfDay());
        quantStrategy.setEndDate(toDate.atStartOfDay());

        kafkaTemplate.send(KafkaConstant.TOPIC_NAME, quantStrategy);

        return ResponseEntity.ok(quantStrategy);
    }

    @Override
    public ResponseEntity<QuantStrategy> emaWithATRStopLoss(String polygonApiKey, CryptoAggregatesDTO cryptoAggregatesDTO,
                                                Integer emaPeriod, Integer atrPeriod,
                                                Double atrMultiplier) {
        String tickerName = cryptoAggregatesDTO.getTickerName();
        Timespan timespan = cryptoAggregatesDTO.getTimespan();
        LocalDate fromDate = cryptoAggregatesDTO.getFromDate();
        LocalDate toDate = cryptoAggregatesDTO.getToDate();
        Sort sort = cryptoAggregatesDTO.getSort();
        Long multiplier = cryptoAggregatesDTO.getMultiplier();
        Boolean unadjusted = cryptoAggregatesDTO.getUnadjusted();
        Long limit = cryptoAggregatesDTO.getLimit();

        HttpClientProvider okHttpClientProvider = CryptoHttpClientUtil.getOkHttpClientProvider();
        PolygonRestClient polygonRestClient = new PolygonRestClient(
                polygonApiKey,
                okHttpClientProvider
        );

        List<List<Double>> barPrices = CryptoHttpClientUtil.getBarPrices(
                tickerName, multiplier,
                timespan, fromDate, toDate,
                unadjusted, limit, sort,
                polygonRestClient
        );

        QuantStrategy quantStrategy = cryptoStrategy.emaWithATRStopLoss(barPrices, emaPeriod, atrPeriod, atrMultiplier);
        quantStrategy.setStartDate(fromDate.atStartOfDay());
        quantStrategy.setEndDate(toDate.atStartOfDay());

        kafkaTemplate.send(KafkaConstant.TOPIC_NAME, quantStrategy);

        return ResponseEntity.ok(quantStrategy);
    }
}
