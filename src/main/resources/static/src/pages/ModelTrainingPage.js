import React, { useState } from 'react';
import { apiClient } from '../api';

const ModelTrainingPage = () => {
    const [defaultForm, setDefaultForm] = useState({
        polygonApiKey: '',
        cryptoData: '',
        modelKind: '', // Available options: LSTM_RNN, LSTM_DENSE_RNN, CNN_RNN_HYBRID
        windowSize: 20,
        epochs: 10,
        inputSize: 10,
        outPutSize: 1
    });

    const [customizedForm, setCustomizedForm] = useState({
        polygonApiKey: '',
        cryptoData: '',
        modelKind: '', // Only supports LSTM_RNN or LSTM_DENSE_RNN
        seed: 12345,
        learningRate: 0.001,
        momentum: 0.9,
        dropoutRate: 0.5,
        windowSize: 20,
        epochs: 10,
        inputSize: 10,
        outPutSize: 1
    });

    const [cnnRnnForm, setCnnRnnForm] = useState({
        polygonApiKey: '',
        cryptoData: '',
        modelKind: '', // Only supports CNN_RNN_HYBRID
        seed: 12345,
        learningRate: 0.001,
        numFeatures: 10,
        numFilters: 32,
        kernelWidth: 3,
        poolWidth: 2,
        lstmHiddenSize: 50,
        momentum: 0.9,
        dropoutRate: 0.5,
        timeSteps: 20,
        epochs: 10,
        outPutSize: 1,
        windowSize: 20
    });

    const [message, setMessage] = useState('');

    const handleSubmit = async (endpoint, formData, queryParams) => {
        try {
            let url = endpoint;
            if (queryParams) {
                const queryString = Object.entries(queryParams)
                    .map(([k, v]) => `${k}=${encodeURIComponent(v)}`)
                    .join('&');
                url += '?' + queryString;
            }
            const response = await apiClient.post(url, JSON.parse(formData));
            setMessage(`Call to ${endpoint} successful, response data: ${JSON.stringify(response.data)}`);
        } catch (error) {
            setMessage(`Call to ${endpoint} failed: ${error.message}`);
        }
    };

    return (
        <div className="container">
            <h2>Model Training</h2>

            {/* Default Model Training */}
            <div className="card">
                <h3>Train Default Model</h3>
                <input
                    type="text"
                    placeholder="Polygon API Key"
                    value={defaultForm.polygonApiKey}
                    onChange={e => setDefaultForm({ ...defaultForm, polygonApiKey: e.target.value })}
                />
                <textarea
                    placeholder="Please enter CryptoAggregatesDTO JSON data including: tickerName, timespan, fromDate, toDate, sort, multiplier, unadjusted, limit"
                    value={defaultForm.cryptoData}
                    onChange={e => setDefaultForm({ ...defaultForm, cryptoData: e.target.value })}
                    rows={4}
                />
                <input
                    type="text"
                    placeholder="Model Kind (LSTM_RNN, LSTM_DENSE_RNN, CNN_RNN_HYBRID)"
                    value={defaultForm.modelKind}
                    onChange={e => setDefaultForm({ ...defaultForm, modelKind: e.target.value })}
                />
                <input
                    type="number"
                    placeholder="Window Size"
                    value={defaultForm.windowSize}
                    onChange={e => setDefaultForm({ ...defaultForm, windowSize: Number(e.target.value) })}
                />
                <input
                    type="number"
                    placeholder="Epochs"
                    value={defaultForm.epochs}
                    onChange={e => setDefaultForm({ ...defaultForm, epochs: Number(e.target.value) })}
                />
                <input
                    type="number"
                    placeholder="Input Size"
                    value={defaultForm.inputSize}
                    onChange={e => setDefaultForm({ ...defaultForm, inputSize: Number(e.target.value) })}
                />
                <input
                    type="number"
                    placeholder="Output Size"
                    value={defaultForm.outPutSize}
                    onChange={e => setDefaultForm({ ...defaultForm, outPutSize: Number(e.target.value) })}
                />
                <button onClick={() =>
                    handleSubmit(
                        'http://localhost:8080/models/default',
                        defaultForm.cryptoData,
                        {
                            polygon_api_key: defaultForm.polygonApiKey,
                            modelKind: defaultForm.modelKind,
                            windowSize: defaultForm.windowSize,
                            epochs: defaultForm.epochs,
                            inputSize: defaultForm.inputSize,
                            outPutSize: defaultForm.outPutSize
                        }
                    )
                }>
                    Train Default Model
                </button>
            </div>

            {/* Customized Model Training */}
            <div className="card">
                <h3>Train Customized LSTM-RNN or LSTM-Dense-RNN Model</h3>
                <input
                    type="text"
                    placeholder="Polygon API Key"
                    value={customizedForm.polygonApiKey}
                    onChange={e => setCustomizedForm({ ...customizedForm, polygonApiKey: e.target.value })}
                />
                <textarea
                    placeholder="Please enter CryptoAggregatesDTO JSON data including: tickerName, timespan, fromDate, toDate, sort, multiplier, unadjusted, limit"
                    value={customizedForm.cryptoData}
                    onChange={e => setCustomizedForm({ ...customizedForm, cryptoData: e.target.value })}
                    rows={4}
                />
                <input
                    type="text"
                    placeholder="Model Kind (Only supports LSTM_RNN or LSTM_DENSE_RNN)"
                    value={customizedForm.modelKind}
                    onChange={e => setCustomizedForm({ ...customizedForm, modelKind: e.target.value })}
                />
                <input
                    type="number"
                    placeholder="Seed"
                    value={customizedForm.seed}
                    onChange={e => setCustomizedForm({ ...customizedForm, seed: Number(e.target.value) })}
                />
                <input
                    type="number"
                    step="0.001"
                    placeholder="Learning Rate"
                    value={customizedForm.learningRate}
                    onChange={e => setCustomizedForm({ ...customizedForm, learningRate: Number(e.target.value) })}
                />
                <input
                    type="number"
                    step="0.1"
                    placeholder="Momentum"
                    value={customizedForm.momentum}
                    onChange={e => setCustomizedForm({ ...customizedForm, momentum: Number(e.target.value) })}
                />
                <input
                    type="number"
                    step="0.1"
                    placeholder="Dropout Rate"
                    value={customizedForm.dropoutRate}
                    onChange={e => setCustomizedForm({ ...customizedForm, dropoutRate: Number(e.target.value) })}
                />
                <input
                    type="number"
                    placeholder="Window Size"
                    value={customizedForm.windowSize}
                    onChange={e => setCustomizedForm({ ...customizedForm, windowSize: Number(e.target.value) })}
                />
                <input
                    type="number"
                    placeholder="Epochs"
                    value={customizedForm.epochs}
                    onChange={e => setCustomizedForm({ ...customizedForm, epochs: Number(e.target.value) })}
                />
                <input
                    type="number"
                    placeholder="Input Size"
                    value={customizedForm.inputSize}
                    onChange={e => setCustomizedForm({ ...customizedForm, inputSize: Number(e.target.value) })}
                />
                <input
                    type="number"
                    placeholder="Output Size"
                    value={customizedForm.outPutSize}
                    onChange={e => setCustomizedForm({ ...customizedForm, outPutSize: Number(e.target.value) })}
                />
                <button onClick={() =>
                    handleSubmit(
                        'http://localhost:8080/models/customized',
                        customizedForm.cryptoData,
                        {
                            polygon_api_key: customizedForm.polygonApiKey,
                            modelKind: customizedForm.modelKind,
                            seed: customizedForm.seed,
                            learningRate: customizedForm.learningRate,
                            momentum: customizedForm.momentum,
                            dropoutRate: customizedForm.dropoutRate,
                            windowSize: customizedForm.windowSize,
                            epochs: customizedForm.epochs,
                            inputSize: customizedForm.inputSize,
                            outPutSize: customizedForm.outPutSize
                        }
                    )
                }>
                    Train Customized Model
                </button>
            </div>

            {/* CNN-RNN Hybrid Model Training */}
            <div className="card">
                <h3>Train CNN-RNN Hybrid Model</h3>
                <input
                    type="text"
                    placeholder="Polygon API Key"
                    value={cnnRnnForm.polygonApiKey}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, polygonApiKey: e.target.value })}
                />
                <textarea
                    placeholder="Please enter CryptoAggregatesDTO JSON data including: tickerName, timespan, fromDate, toDate, sort, multiplier, unadjusted, limit"
                    value={cnnRnnForm.cryptoData}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, cryptoData: e.target.value })}
                    rows={4}
                />
                <input
                    type="text"
                    placeholder="Model Kind (Only supports CNN_RNN_HYBRID)"
                    value={cnnRnnForm.modelKind}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, modelKind: e.target.value })}
                />
                <input
                    type="number"
                    placeholder="Seed"
                    value={cnnRnnForm.seed}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, seed: Number(e.target.value) })}
                />
                <input
                    type="number"
                    step="0.001"
                    placeholder="Learning Rate"
                    value={cnnRnnForm.learningRate}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, learningRate: Number(e.target.value) })}
                />
                <input
                    type="number"
                    placeholder="Number of Features"
                    value={cnnRnnForm.numFeatures}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, numFeatures: Number(e.target.value) })}
                />
                <input
                    type="number"
                    placeholder="Number of Filters"
                    value={cnnRnnForm.numFilters}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, numFilters: Number(e.target.value) })}
                />
                <input
                    type="number"
                    placeholder="Kernel Width"
                    value={cnnRnnForm.kernelWidth}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, kernelWidth: Number(e.target.value) })}
                />
                <input
                    type="number"
                    placeholder="Pool Width"
                    value={cnnRnnForm.poolWidth}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, poolWidth: Number(e.target.value) })}
                />
                <input
                    type="number"
                    placeholder="LSTM Hidden Size"
                    value={cnnRnnForm.lstmHiddenSize}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, lstmHiddenSize: Number(e.target.value) })}
                />
                <input
                    type="number"
                    step="0.1"
                    placeholder="Momentum"
                    value={cnnRnnForm.momentum}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, momentum: Number(e.target.value) })}
                />
                <input
                    type="number"
                    step="0.1"
                    placeholder="Dropout Rate"
                    value={cnnRnnForm.dropoutRate}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, dropoutRate: Number(e.target.value) })}
                />
                <input
                    type="number"
                    placeholder="Time Steps"
                    value={cnnRnnForm.timeSteps}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, timeSteps: Number(e.target.value) })}
                />
                <input
                    type="number"
                    placeholder="Epochs"
                    value={cnnRnnForm.epochs}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, epochs: Number(e.target.value) })}
                />
                <input
                    type="number"
                    placeholder="Output Size"
                    value={cnnRnnForm.outPutSize}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, outPutSize: Number(e.target.value) })}
                />
                <input
                    type="number"
                    placeholder="Window Size"
                    value={cnnRnnForm.windowSize}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, windowSize: Number(e.target.value) })}
                />
                <button onClick={() =>
                    handleSubmit(
                        'http://localhost:8080/models/customized/cnn-rnn-hybrid',
                        cnnRnnForm.cryptoData,
                        {
                            polygon_api_key: cnnRnnForm.polygonApiKey,
                            modelKind: cnnRnnForm.modelKind,
                            seed: cnnRnnForm.seed,
                            learningRate: cnnRnnForm.learningRate,
                            numFeatures: cnnRnnForm.numFeatures,
                            numFilters: cnnRnnForm.numFilters,
                            kernelWidth: cnnRnnForm.kernelWidth,
                            poolWidth: cnnRnnForm.poolWidth,
                            lstmHiddenSize: cnnRnnForm.lstmHiddenSize,
                            momentum: cnnRnnForm.momentum,
                            dropoutRate: cnnRnnForm.dropoutRate,
                            timeSteps: cnnRnnForm.timeSteps,
                            epochs: cnnRnnForm.epochs,
                            outPutSize: cnnRnnForm.outPutSize,
                            windowSize: cnnRnnForm.windowSize
                        }
                    )
                }>
                    Train CNN-RNN Hybrid Model
                </button>
            </div>

            {message && <div className="message">{message}</div>}
        </div>
    );
};

export default ModelTrainingPage;
