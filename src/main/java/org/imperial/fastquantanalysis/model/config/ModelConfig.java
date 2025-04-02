package org.imperial.fastquantanalysis.model.config;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.conf.preprocessor.CnnToRnnPreProcessor;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.stereotype.Component;

/**
 * Configuration of deep learning models
 *
 * @author Emil S. He
 * @since 2025-04-02
 */
@Component
public class ModelConfig {

    /**
     * LSTM + RNN in default parameters
     * @param inputSize input size
     * @param outputSize output size
     * @return the default model
     */
    public MultiLayerNetwork getLSTMRNNDefaultModel(int inputSize, int outputSize) {
        return this.getLSTMRNNModel(42, 0.001, 0.9, 0.2,
                inputSize, outputSize);
    }

    /**
     * LSTM + Dense layer + RNN in default parameters
     * @param inputSize input size
     * @param outputSize output size
     * @return the default model
     */
    public MultiLayerNetwork getLSTMDenseRNNDefaultModel(int inputSize, int outputSize) {
        return this.getLSTMDenseRNNModel(42, 0.001, 0.9, 0.2,
                inputSize, outputSize);
    }

    /**
     * CNN + Subsampling + LSTM + RNN (RNN hybrid) in default parameters
     * @param outputSize output size
     * @return the default model
     */
    public MultiLayerNetwork getCNNRNNHybridDefaultModel(int outputSize) {
        return this.getCNNRNNHybridModel(42, 0.001, 0.9,
                5, 16, 3, 2, 128,
                0.2, 30, outputSize);
    }

    /**
     * LSTM + RNN
     * LSTM: good at dealing with time-series data, use tanh as activation function
     * <p>
     * RNN: output layer
     * @param seed seed number
     * @param learningRate learning rate
     * @param momentum momentum
     * @param dropout drop out rate
     * @param inputSize input size
     * @param outputSize output size
     * @return the model
     */
    public MultiLayerNetwork getLSTMRNNModel(long seed, double learningRate,
                                             double momentum, double dropout,
                                             int inputSize, int outputSize) {
        MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(learningRate, momentum))
                .list()
                .layer(0, new LSTM.Builder()
                        .activation(Activation.TANH)
                        .nIn(inputSize)
                        .nOut(128)
                        .dropOut(dropout)
                        .build())
                .layer(1, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nOut(outputSize)
                        .build())
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(config);
        model.init();
        return model;
    }

    /**
     * LSTM + Dense layer + RNN
     * <p>
     * LSTM: good at dealing with time-series data, use tanh as activation function
     * <p>
     * Dense layer: increasing the generalizing capability
     * <p>
     * RNN: output layer
     * @param seed seed number
     * @param learningRate learning rate
     * @param momentum momentum
     * @param dropout drop out rate
     * @param inputSize input size
     * @param outputSize output size
     * @return the model
     */
    public MultiLayerNetwork getLSTMDenseRNNModel(long seed, double learningRate,
                                                  double momentum, double dropout,
                                                  int inputSize, int outputSize) {
        MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(learningRate, momentum))
                .list()
                .layer(0, new LSTM.Builder()
                        .activation(Activation.TANH)
                        .nIn(inputSize)
                        .nOut(128)
                        .dropOut(dropout)
                        .build())
                .layer(1, new LSTM.Builder()
                        .activation(Activation.TANH)
                        .nOut(128)
                        .dropOut(dropout)
                        .build())
                .layer(2, new DenseLayer.Builder()
                        .activation(Activation.RELU)
                        .nOut(64)
                        .dropOut(dropout)
                        .build())
                .layer(3, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nOut(outputSize)
                        .build())
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(config);
        model.init();
        return model;
    }

    /**
     * CNN + Subsampling + LSTM + RNN (RNN hybrid)
     * <p>
     * CNN: extracting local features
     * <p>
     * Subsampling: downsampling with pooled layers
     * <p>
     * Preprocessor: converting data form
     * <p>
     * LSTM & RNN: modeling timing dependencies and outputting prediction results
     * @param seed seed
     * @param learningRate learning rate
     * @param momentum momentum
     * @param numFeatures number of features per time step
     * @param numFilters number of CNN convolution kernels
     * @param kernelWidth convolution kernel width (height fixed to 1 to achieve 1D convolution)
     * @param poolWidth pooling window width (height of 1)
     * @param lstmHiddenSize number of LSTM hidden layer cells
     * @param dropout dropout rate
     * @param timeSteps input sequence length
     * @return the model
     */
    public MultiLayerNetwork getCNNRNNHybridModel(long seed, double learningRate,
                                                  double momentum, int numFeatures,
                                                  int numFilters, int kernelWidth,
                                                  int poolWidth, int lstmHiddenSize,
                                                  double dropout, int timeSteps,
                                                  int outputSize) {
        int convOutWidth = timeSteps - kernelWidth + 1;
        int newTimeSteps = convOutWidth / poolWidth;

        MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(learningRate, momentum))
                .list()
                .layer(new ConvolutionLayer.Builder(new int[]{1, kernelWidth})
                        .nIn(numFeatures)
                        .nOut(numFilters)
                        .stride(new int[]{1, 1})
                        .activation(Activation.RELU)
                        .build())
                .layer(new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX,
                        new int[]{1, poolWidth})
                        .stride(new int[]{1, poolWidth})
                        .build())
                .layer(2, new LSTM.Builder()
                        .nOut(lstmHiddenSize)
                        .activation(Activation.TANH)
                        .dropOut(dropout)
                        .build())
                .layer(3, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nOut(outputSize)
                        .build())
                .setInputType(InputType.convolutional(1, timeSteps, numFeatures))
                .inputPreProcessor(2, new CnnToRnnPreProcessor(1, newTimeSteps, numFilters))
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(config);
        model.init();
        return model;
    }
}
