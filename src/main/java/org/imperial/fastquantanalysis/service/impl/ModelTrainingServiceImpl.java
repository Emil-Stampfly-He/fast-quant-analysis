package org.imperial.fastquantanalysis.service.impl;

import io.polygon.kotlin.sdk.HttpClientProvider;
import io.polygon.kotlin.sdk.rest.PolygonRestClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.imperial.fastquantanalysis.constant.ModelKind;
import org.imperial.fastquantanalysis.dto.CryptoAggregatesDTO;
import org.imperial.fastquantanalysis.model.ModelConfig;
import org.imperial.fastquantanalysis.model.ModelTraining;
import org.imperial.fastquantanalysis.service.IModelTrainingService;
import org.imperial.fastquantanalysis.util.CryptoHttpClientUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Model training service
 *
 * @author Emil S. He
 * @since 2025-04-03
 */
@Slf4j
@Service
public class ModelTrainingServiceImpl implements IModelTrainingService {

    @Resource
    private ModelTraining modelTraining;

    @Resource
    private ModelConfig modelConfig;

    /**
     * Train default deep learning models, most parameters have been fixed
     * @param polygonApiKey User's polygon API key
     * @param cryptoAggregatesDTO DTO for carrying necessary information
     * @param modelKind Model kind
     * @param windowSize Window size
     * @param epochs Training epochs
     * @param inputSize Input size of the model
     * @param outPutSize Output size of the model
     * @return Final predicted data list
     */
    @Override
    @Transactional // Avoid saving amputated models
    public ResponseEntity<List<Double>> trainModel(String polygonApiKey, CryptoAggregatesDTO cryptoAggregatesDTO,
                                        ModelKind modelKind, int windowSize, int epochs,
                                        int inputSize, int outPutSize) {
        HttpClientProvider okHttpClientProvider = CryptoHttpClientUtil.getOkHttpClientProvider();
        PolygonRestClient polygonRestClient = new PolygonRestClient(
                polygonApiKey,
                okHttpClientProvider
        );

        List<List<Double>> barPrices = CryptoHttpClientUtil.getBarPrices(
                cryptoAggregatesDTO.getTickerName(),
                cryptoAggregatesDTO.getMultiplier(),
                cryptoAggregatesDTO.getTimespan(),
                cryptoAggregatesDTO.getFromDate(),
                cryptoAggregatesDTO.getToDate(),
                cryptoAggregatesDTO.getUnadjusted(),
                cryptoAggregatesDTO.getLimit(),
                cryptoAggregatesDTO.getSort(),
                polygonRestClient
        );

        Supplier<MultiLayerNetwork> modelProvider = switch (modelKind) {
            case LSTM_RNN -> () -> modelConfig.getLSTMRNNDefaultModel(inputSize, outPutSize);
            case LSTM_DENSE_RNN -> () -> modelConfig.getLSTMDenseRNNDefaultModel(inputSize, outPutSize);
            case CNN_RNN_HYBRID -> () -> modelConfig.getCNNRNNHybridDefaultModel(inputSize);
        };
        List<Double> finalPredictData = modelTraining.train(windowSize, barPrices, modelProvider, epochs);

        return ResponseEntity.ok(finalPredictData);
    }

    @Override
    @Transactional
    public ResponseEntity<?> trainCustomizedModel(String polygonApiKey,
                                                             CryptoAggregatesDTO cryptoAggregatesDTO,
                                                             ModelKind modelKind, long seed, double learningRate,
                                                             double momentum, double dropoutRate, int windowSize,
                                                             int epochs, int inputSize, int outPutSize) {
        HttpClientProvider okHttpClientProvider = CryptoHttpClientUtil.getOkHttpClientProvider();
        PolygonRestClient polygonRestClient = new PolygonRestClient(
                polygonApiKey,
                okHttpClientProvider
        );

        List<List<Double>> barPrices = CryptoHttpClientUtil.getBarPrices(
                cryptoAggregatesDTO.getTickerName(),
                cryptoAggregatesDTO.getMultiplier(),
                cryptoAggregatesDTO.getTimespan(),
                cryptoAggregatesDTO.getFromDate(),
                cryptoAggregatesDTO.getToDate(),
                cryptoAggregatesDTO.getUnadjusted(),
                cryptoAggregatesDTO.getLimit(),
                cryptoAggregatesDTO.getSort(),
                polygonRestClient
        );

        Optional<Supplier<MultiLayerNetwork>> safeModelProvider = switch (modelKind) {
            case LSTM_RNN -> Optional.of(() -> modelConfig.getLSTMRNNModel(seed, learningRate, momentum, dropoutRate,
                    inputSize, outPutSize));
            case LSTM_DENSE_RNN -> Optional.of(() -> modelConfig.getLSTMDenseRNNModel(seed, learningRate, momentum,
                    dropoutRate, inputSize, outPutSize));
            case CNN_RNN_HYBRID -> Optional.empty();
        };

        if (safeModelProvider.isEmpty()) {
            return ResponseEntity.badRequest().body("CNN-RNN hybrid model should not be chosen here.");
        }

        Supplier<MultiLayerNetwork> modelProvider = safeModelProvider.get();
        List<Double> finalPredictData = modelTraining.train(windowSize, barPrices, modelProvider, epochs);
        return ResponseEntity.ok(finalPredictData);
    }

    @Override
    @Transactional
    public ResponseEntity<List<Double>> trainCNNRNNHybridCustomizedModel(String polygonApiKey,
                                                                         CryptoAggregatesDTO cryptoAggregatesDTO,
                                                                         ModelKind modelKind, long seed,
                                                                         double learningRate, int numFeatures,
                                                                         int numFilters, int kernelWidth,
                                                                         int poolWidth, int lstmHiddenSize,
                                                                         double momentum, double dropoutRate,
                                                                         int timeSteps, int epochs, int outPutSize) {
        return null;
    }


}