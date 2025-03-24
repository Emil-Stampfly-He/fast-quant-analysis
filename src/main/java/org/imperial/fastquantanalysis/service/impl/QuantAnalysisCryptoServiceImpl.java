package org.imperial.fastquantanalysis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.polygon.kotlin.sdk.HttpClientProvider;
import io.polygon.kotlin.sdk.rest.PolygonRestClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

    /**
     * Donchian channel strategy for crypto
     * @param polygonApiKey user's Polygon.io API key
     * @param cryptoAggregatesDTO cryptoAggregatesDTO
     * @param windowSize lookback
     * @return OK or fail message
     */
    @Override
    public ResponseEntity<?> donchian(String polygonApiKey, CryptoAggregatesDTO cryptoAggregatesDTO, Integer windowSize) {
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
        save(quantStrategy);

        return ResponseEntity.ok(quantStrategy);
    }

    /**
     * Pair trading for 2 cryptos
     * @param polygonApiKey User's polygon API key
     * @param cryptoAggregatesPairDTO DTO for carrying necessary information
     * @param windowSize Window size
     * @return OK or fail message
     */
    @Override
    public ResponseEntity<?> pairTrading(String polygonApiKey, CryptoAggregatesPairDTO cryptoAggregatesPairDTO, Integer windowSize) {
        String tickerName1 = cryptoAggregatesPairDTO.getTickerName1();
        String tickerName2 = cryptoAggregatesPairDTO.getTickerName2();
        Timespan timespan = cryptoAggregatesPairDTO.getTimespan();
        LocalDate fromDate = cryptoAggregatesPairDTO.getFromDate();
        LocalDate toDate = cryptoAggregatesPairDTO.getToDate();
        Sort sort = cryptoAggregatesPairDTO.getSort();

        Long multiplier = cryptoAggregatesPairDTO.getMultiplier();
        Boolean unadjusted = cryptoAggregatesPairDTO.getUnadjusted();
        Long limit = cryptoAggregatesPairDTO.getLimit();

        HttpClientProvider okHttpClientProvider = CryptoHttpClientUtil.getOkHttpClientProvider();
        PolygonRestClient polygonRestClient = new PolygonRestClient(
                polygonApiKey,
                okHttpClientProvider
        );

        List<List<Double>> barPrices1 = CryptoHttpClientUtil.getBarPrices(
                tickerName1, multiplier,
                timespan, fromDate,
                toDate, unadjusted,
                limit, sort, polygonRestClient
        );
        List<List<Double>> barPrices2 = CryptoHttpClientUtil.getBarPrices(
                tickerName2, multiplier,
                timespan, fromDate,
                toDate, unadjusted,
                limit, sort, polygonRestClient
        );

        List<Double> avgBarPrice1 = IntStream.range(0, barPrices1.get(0).size())
                .mapToDouble(i -> barPrices1.stream()
                        .mapToDouble(list ->
                                list.get(i)).average().orElse(0))
                .boxed()
                .toList();
        List<Double> avgBarPrice2 = IntStream.range(0, barPrices1.get(0).size())
                .mapToDouble(i -> barPrices2.stream()
                        .mapToDouble(list ->
                                list.get(i)).average().orElse(0))
                .boxed()
                .toList();



        return null;
    }


}
