package org.imperial.fastquantanalysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imperial.fastquantanalysis.constant.ModelKind;
import org.imperial.fastquantanalysis.dto.CryptoAggregatesDTO;
import org.imperial.fastquantanalysis.service.IModelTrainingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Model training controller
 *
 * @author Emil S. He
 * @since 2025-04-02
 */
@Slf4j
@RestController
@RequestMapping("/models")
@Tag(name = "Model Training Interface")
public class ModelTrainingController {

    @Resource
    private IModelTrainingService modelTrainingService;

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
     * @postmantest untested
     */
    @PostMapping("/default")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Train default models")
    public ResponseEntity<List<Double>> trainDefaultModel(
            @Parameter(name = "User's polygon API key") @RequestParam("polygon_api_key") String polygonApiKey,
            @Parameter(name = "DTO for carrying necessary information") @RequestBody CryptoAggregatesDTO cryptoAggregatesDTO,
            @Parameter(name = "Model kind") @RequestParam ModelKind modelKind,
            @Parameter(name = "Window size") @RequestParam int windowSize,
            @Parameter(name = "Training epochs") @RequestParam int epochs,
            @Parameter(name = "Input size, not used in CNN-RNN hybrid default model") int inputSize,
            @Parameter(name = "Output size") @RequestParam int outPutSize) {
        return modelTrainingService.trainModel(polygonApiKey, cryptoAggregatesDTO, modelKind,
                windowSize, epochs, inputSize, outPutSize);
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
     * @return Final predicted data list
     * @postmantest untested
     * TODO Unfinished
     */
    @PostMapping("/customized")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Train customized models")
    public ResponseEntity<?> trainCustomizedModel(
            @Parameter(name = "User's polygon API key") @RequestParam("polygon_api_key") String polygonApiKey,
            @Parameter(name = "DTO for carrying necessary information") @RequestBody CryptoAggregatesDTO cryptoAggregatesDTO,
            @Parameter(name = "Model kind") @RequestParam ModelKind modelKind,
            @Parameter(name = "Seed") @RequestParam long seed,
            @Parameter(name = "Learning rate") @RequestParam double learningRate,
            @Parameter(name = "Momentum") @RequestParam double momentum,
            @Parameter(name = "Dropout rate") @RequestParam double dropoutRate,
            @Parameter(name = "Window size") @RequestParam int windowSize,
            @Parameter(name = "Training epochs") @RequestParam int epochs,
            @Parameter(name = "Input size") @RequestParam int inputSize,
            @Parameter(name = "Output size") @RequestParam int outPutSize) {
        return modelTrainingService.trainCustomizedModel(polygonApiKey, cryptoAggregatesDTO,
                modelKind, seed, learningRate, momentum, dropoutRate, windowSize, epochs,
                inputSize, outPutSize);
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
     * @return the model
     * @postmantest untested
     * TODO Unfinished
     */
    @PostMapping("/customized/cnn-rnn-hybrid")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Train customized CNN-RNN hybrid models")
    public ResponseEntity<List<Double>> trainCNNRNNHybridCustomizedModel(
            @Parameter(name = "User's polygon API key") @RequestParam("polygon_api_key") String polygonApiKey,
            @Parameter(name = "DTO for carrying necessary information") @RequestBody CryptoAggregatesDTO cryptoAggregatesDTO,
            @Parameter(name = "Model kind") @RequestParam ModelKind modelKind,
            @Parameter(name = "Seed") @RequestParam long seed,
            @Parameter(name = "Learning rate") @RequestParam double learningRate,
            @Parameter(name = "Number of features per time step") @RequestParam int numFeatures,
            @Parameter(name = "Number of CNN convolution kernels") @RequestParam int numFilters,
            @Parameter(name = "Convolution kernel width") @RequestParam int kernelWidth,
            @Parameter(name = "Pooling window width") @RequestParam int poolWidth,
            @Parameter(name = "Number of LSTM hidden layer cells") @RequestParam int lstmHiddenSize,
            @Parameter(name = "Momentum") @RequestParam double momentum,
            @Parameter(name = "Dropout rate") @RequestParam double dropoutRate,
            @Parameter(name = "Input sequence length") @RequestParam int timeSteps,
            @Parameter(name = "Training epochs") @RequestParam int epochs,
            @Parameter(name = "Output size") @RequestParam int outPutSize) {
        return modelTrainingService.trainCNNRNNHybridCustomizedModel(polygonApiKey, cryptoAggregatesDTO,
                modelKind, seed, learningRate, numFeatures, numFilters, kernelWidth, poolWidth, lstmHiddenSize,
                momentum, dropoutRate, timeSteps, epochs, outPutSize);
    }
}
