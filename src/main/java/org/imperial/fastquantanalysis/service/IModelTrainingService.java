package org.imperial.fastquantanalysis.service;

import org.imperial.fastquantanalysis.constant.ModelKind;
import org.imperial.fastquantanalysis.dto.CryptoAggregatesDTO;
import org.imperial.fastquantanalysis.vo.TrainingResultVO;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Model training service interface
 *
 * @author Emil S. He
 * @since 2025-04-02
 */
public interface IModelTrainingService {

    ResponseEntity<TrainingResultVO> trainModel(String polygonApiKey, CryptoAggregatesDTO cryptoAggregatesDTO,
                                                ModelKind modelKind, int windowSize, int epochs,
                                                int inputSize, int outPutSize);

    ResponseEntity<TrainingResultVO> trainCustomizedModel(String polygonApiKey, CryptoAggregatesDTO cryptoAggregatesDTO,
                                           ModelKind modelKind, long seed, double learningRate,
                                           double momentum, double dropoutRate, int windowSize,
                                           int epochs, int inputSize, int outPutSize);

    ResponseEntity<TrainingResultVO> trainCNNRNNHybridCustomizedModel(String polygonApiKey,
                                                       CryptoAggregatesDTO cryptoAggregatesDTO,
                                                       ModelKind modelKind, long seed, double learningRate,
                                                       int numFeatures, int numFilters, int kernelWidth,
                                                       int poolWidth, int lstmHiddenSize, double momentum,
                                                       double dropoutRate, int timeSteps, int epochs,
                                                       int outPutSize, int windowSize);
}
