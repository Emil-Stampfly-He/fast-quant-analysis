package org.imperial.fastquantanalysis;

import io.polygon.kotlin.sdk.HttpClientProvider;
import io.polygon.kotlin.sdk.rest.PolygonRestClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imperial.fastquantanalysis.constant.Sort;
import org.imperial.fastquantanalysis.constant.Timespan;
import org.imperial.fastquantanalysis.model.ModelConfig;
import org.imperial.fastquantanalysis.model.ModelTraining;
import org.imperial.fastquantanalysis.util.CryptoHttpClientUtil;
import org.imperial.fastquantanalysis.vo.TrainingResultVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.imperial.fastquantanalysis.constant.PolygonConstant.POLYGON_API_KEY;

@SpringBootTest
@Slf4j
public class ModelTest {

    @Resource
    private ModelTraining modelTraining;

    @Resource
    private ModelConfig modelConfig;

    @Test
    void testLSTMModel() {
        HttpClientProvider okHttpClientProvider = CryptoHttpClientUtil.getOkHttpClientProvider();
        PolygonRestClient polygonClient = new PolygonRestClient(
                POLYGON_API_KEY,
                okHttpClientProvider
        );

        List<List<Double>> barPrices = CryptoHttpClientUtil.getBarPrices(
                "X:BTCUSD",
                null,
                Timespan.DAY,
                LocalDate.of(2024, 2, 3),
                LocalDate.of(2025, 2, 7),
                null,
                null,
                Sort.ASC,
                polygonClient
        );

        System.out.println(barPrices);

        TrainingResultVO trainingResultVO = modelTraining.train(
                22,
                barPrices,
                () -> modelConfig.getLSTMRNNDefaultModel(2, 1),
                5);

        System.out.println(trainingResultVO);
    }
}
