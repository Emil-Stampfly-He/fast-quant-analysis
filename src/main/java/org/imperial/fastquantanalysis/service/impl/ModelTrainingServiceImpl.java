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
import org.imperial.fastquantanalysis.vo.TrainingResultVO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
     * @return Training result
     */
    @Override
    @Transactional // Avoid saving amputated models
    public ResponseEntity<TrainingResultVO> trainModel(String polygonApiKey,
                                                       CryptoAggregatesDTO cryptoAggregatesDTO,
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

        return ResponseEntity.ok(modelTraining.train(windowSize, barPrices, modelProvider, epochs));
    }

    /**
     * Train customized deep learning models
     * @param polygonApiKey User's polygon API key
     * @param cryptoAggregatesDTO DTO for carrying necessary information
     * @param modelKind Model kind
     * @param seed Seed
     * @param learningRate Learning rate
     * @param momentum Momentum
     * @param dropoutRate Dropout rate
     * @param windowSize Window size
     * @param epochs Training epochs
     * @param inputSize Input size
     * @param outPutSize Output size
     * @return Training result
     */
    @Override
    @Transactional
    public ResponseEntity<TrainingResultVO> trainCustomizedModel(String polygonApiKey,
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
            log.error("CNN-RNN hybrid model should not be chosen here.");
            return ResponseEntity.badRequest().body(new TrainingResultVO(Collections.emptyList(), Double.NaN));
        }

        return ResponseEntity.ok(modelTraining.train(windowSize, barPrices, safeModelProvider.get(), epochs));
    }

    /**
     * Train customized CNN-RNN hybrid models
     * @param polygonApiKey User's polygon API key
     * @param cryptoAggregatesDTO DTO for carrying necessary information
     * @param modelKind Model kind
     * @param seed Seed
     * @param learningRate Learning rate
     * @param numFeatures Number of features per time step
     * @param numFilters Number of CNN convolution kernels
     * @param kernelWidth Convolution kernel width (height fixed to 1 to achieve 1D convolution)
     * @param poolWidth Pooling window width (height of 1)
     * @param lstmHiddenSize Number of LSTM hidden layer cells
     * @param momentum Momentum
     * @param dropoutRate Drop out rate
     * @param timeSteps Input sequence length
     * @param epochs Training epochs
     * @param outPutSize Output size
     * @param windowSize Window size
     * @return Training result
     */
    @Override
    @Transactional
    public ResponseEntity<TrainingResultVO> trainCNNRNNHybridCustomizedModel(String polygonApiKey,
                                                              CryptoAggregatesDTO cryptoAggregatesDTO,
                                                              ModelKind modelKind, long seed,
                                                              double learningRate, int numFeatures,
                                                              int numFilters, int kernelWidth,
                                                              int poolWidth, int lstmHiddenSize,
                                                              double momentum, double dropoutRate,
                                                              int timeSteps, int epochs, int outPutSize,
                                                              int windowSize) {
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
            case CNN_RNN_HYBRID -> Optional.of(() -> modelConfig.getCNNRNNHybridModel(seed, learningRate, momentum, numFeatures,
                    numFilters, kernelWidth, poolWidth, lstmHiddenSize, dropoutRate, timeSteps, outPutSize));
            case LSTM_RNN, LSTM_DENSE_RNN -> Optional.empty();
        };

        if (safeModelProvider.isEmpty()) {
            log.error("LSTM-RNN or LSTM-Dense-RNN model should not be chosen here.");
            return ResponseEntity.badRequest().body(new TrainingResultVO(Collections.emptyList(), Double.NaN));
        }

        return ResponseEntity.ok(modelTraining.train(windowSize, barPrices, safeModelProvider.get(), epochs));
    }
}