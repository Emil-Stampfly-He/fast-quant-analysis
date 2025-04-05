package org.imperial.fastquantanalysis.model;

import lombok.extern.slf4j.Slf4j;
import org.datavec.api.records.reader.impl.inmemory.InMemorySequenceRecordReader;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.Writable;
import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.imperial.fastquantanalysis.vo.TrainingResultVO;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Train various models
 *
 * @author Emil S. He
 * @since 2025-04-02
 */
@Slf4j
@Component
public class ModelTraining {

    private static final double SPLIT_RATION = 0.8;
    private static final int BATCH_SIZE = 32;
    private static final int SCORE_ITERATIONS = 100;

    /**
     * Train the model (and save to database)
     * @param windowSize window size
     * @param dataList bar prices
     * @param modelProvider model
     * @param epochs epochs
     * @return Training result
     * TODO Add function of saving the model to NoSQL database
     */
    public TrainingResultVO train(int windowSize, List<List<Double>> dataList,
                              Supplier<MultiLayerNetwork> modelProvider, int epochs) {
        // Split the data
        List<List<List<Writable>>> allData = buildSequenceData(windowSize, dataList);
        if (allData.size() <= windowSize) {
            log.info("All data's size {} should be larger than the indicated window size.", allData.size());
            return new TrainingResultVO(Collections.emptyList(), Double.NaN);
        }

        long splitIndex = Math.round(allData.size() * SPLIT_RATION);
        List<List<List<Writable>>> trainData = allData.stream().limit(splitIndex).toList();
        List<List<List<Writable>>> testData = allData.stream().skip(splitIndex).toList();

        SequenceRecordReaderDataSetIterator trainIterator =
                new SequenceRecordReaderDataSetIterator(
                        new InMemorySequenceRecordReader(trainData),
                        BATCH_SIZE,
                        -1,
                        2,
                        true);
        SequenceRecordReaderDataSetIterator testIterator =
                new SequenceRecordReaderDataSetIterator(
                        new InMemorySequenceRecordReader(testData),
                        1,
                        -1,
                        2,
                        true);
        SequenceRecordReaderDataSetIterator allDataIterator =
                new SequenceRecordReaderDataSetIterator(
                        new InMemorySequenceRecordReader(allData),
                        1,
                        -1,
                        2,
                        true);

        // Normalize
        NormalizerMinMaxScaler minMaxScaler = new NormalizerMinMaxScaler(0, 1);
        minMaxScaler.fitLabel(true);
        minMaxScaler.fit(allDataIterator);
        trainIterator.setPreProcessor(minMaxScaler);
        testIterator.setPreProcessor(minMaxScaler);

        // Load the model
        // TODO Add the model of uploading the pre-trained model from NoSQL database
        // TODO 1. Load the model (from NoSQL database)
        MultiLayerNetwork multiLayerNetwork = modelProvider.get();
        multiLayerNetwork.setListeners(new ScoreIterationListener(SCORE_ITERATIONS));
        // TODO 2. Save the model's information

        // Train the model
        long startTime = System.currentTimeMillis();
        multiLayerNetwork.fit(trainIterator, epochs);
        long endTime = System.currentTimeMillis();
        log.info("Training took {} ms", endTime - startTime);
        // TODO 3. Save the model to NoSQL database

        // Test the model
        List<List<Double>> testPredictData = new ArrayList<>();
        double sumSquaredError = 0.0;
        int totalCount = 0;

        while (testIterator.hasNext()){
            DataSet ds = testIterator.next();
            INDArray features = ds.getFeatures();
            // Output shape: [batchSize, outputSize, timeSteps]
            INDArray predicted = multiLayerNetwork.output(features, false);
            minMaxScaler.revertLabels(predicted);

            INDArray labels = ds.getLabels();
            minMaxScaler.revertLabels(labels);

            for (int sample = 0; sample < predicted.size(0); sample++) {
                long timeSteps = predicted.size(2);
                INDArray lastTimeStep = predicted.get(
                        NDArrayIndex.point(sample),
                        NDArrayIndex.all(),
                        NDArrayIndex.point(timeSteps - 1)
                );

                INDArray labelLast = labels.get(
                        NDArrayIndex.point(sample),
                        NDArrayIndex.all(),
                        NDArrayIndex.point(timeSteps - 1)
                );

                List<Double> predictionList = new ArrayList<>();
                for (int i = 0; i < lastTimeStep.length(); i++){
                    double predValue = lastTimeStep.getDouble(i);
                    double trueValue = labelLast.getDouble(i);
                    predictionList.add(predValue);

                    double error = predValue - trueValue;
                    sumSquaredError += error * error;
                    totalCount++;
                }
                testPredictData.add(predictionList);
            }
        }

        double mse = totalCount > 0 ? sumSquaredError / totalCount : Double.NaN;
        log.info("Evaluation MSE: {}", mse);

        List<Double> finalPredictionData = testPredictData.stream()
                .map(price -> price.get(0))
                .toList();

        return new TrainingResultVO(finalPredictionData, mse);
    }

    private List<List<List<Writable>>> buildSequenceData(int windowSize, List<List<Double>> barPrices) {
        List<List<List<Writable>>> data = new ArrayList<>();

        for (int i = 0; i < barPrices.get(0).size() - windowSize; i++) {
            List<List<Writable>> sequence = new ArrayList<>();
            int endIndex = i + windowSize;

            for (int j = i; j < endIndex; j++) {
                List<Writable> features = new ArrayList<>();

                // Open & close prices are used in features to predict the open price the next day
                features.add(new DoubleWritable(barPrices.get(0).get(j))); // open
                features.add(new DoubleWritable(barPrices.get(3).get(j))); // close

                // Labels
                features.add(new DoubleWritable(barPrices.get(0).get(j + 1))); // open
                sequence.add(features);
            }

            data.add(sequence);
        }

        return data;
    }

}
