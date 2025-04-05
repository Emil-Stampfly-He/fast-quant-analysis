import React, { useState } from 'react';
import { apiClient } from '../api';

const ModelTrainingPage = () => {
    const [defaultForm, setDefaultForm] = useState({
        polygonApiKey: '',
        // JSON格式数据需包含：tickerName, timespan, fromDate, toDate, sort, multiplier, unadjusted, limit
        cryptoData: '',
        modelKind: '', // 可选值：LSTM_RNN, LSTM_DENSE_RNN, CNN_RNN_HYBRID
        windowSize: 20,
        epochs: 10,
        inputSize: 10,
        outPutSize: 1
    });

    const [customizedForm, setCustomizedForm] = useState({
        polygonApiKey: '',
        // JSON格式数据需包含：tickerName, timespan, fromDate, toDate, sort, multiplier, unadjusted, limit
        cryptoData: '',
        // 仅支持 LSTM_RNN 或 LSTM_DENSE_RNN
        modelKind: '',
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
        // JSON格式数据需包含：tickerName, timespan, fromDate, toDate, sort, multiplier, unadjusted, limit
        cryptoData: '',
        // 仅支持 CNN_RNN_HYBRID
        modelKind: '',
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
            setMessage(`调用 ${endpoint} 成功，返回数据：` + JSON.stringify(response.data));
        } catch (error) {
            setMessage(`调用 ${endpoint} 失败：` + error.message);
        }
    };

    return (
        <div>
            <h2>模型训练</h2>

            {/* 1. 训练默认模型 */}
            <div style={{ border: '1px solid #ccc', padding: '10px', marginBottom: '20px' }}>
                <h3>训练默认模型</h3>
                <input
                    type="text"
                    placeholder="Polygon API Key"
                    value={defaultForm.polygonApiKey}
                    onChange={e => setDefaultForm({ ...defaultForm, polygonApiKey: e.target.value })}
                /><br/>
                <textarea
                    placeholder="请输入 CryptoAggregatesDTO JSON 数据..."
                    value={defaultForm.cryptoData}
                    onChange={e => setDefaultForm({ ...defaultForm, cryptoData: e.target.value })}
                    rows={4}
                    cols={50}
                /><br/>
                <input
                    type="text"
                    placeholder="Model Kind (LSTM_RNN, LSTM_DENSE_RNN, CNN_RNN_HYBRID)"
                    value={defaultForm.modelKind}
                    onChange={e => setDefaultForm({ ...defaultForm, modelKind: e.target.value })}
                /><br/>
                <input
                    type="number"
                    placeholder="Window Size"
                    value={defaultForm.windowSize}
                    onChange={e => setDefaultForm({ ...defaultForm, windowSize: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    placeholder="Epochs"
                    value={defaultForm.epochs}
                    onChange={e => setDefaultForm({ ...defaultForm, epochs: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    placeholder="Input Size"
                    value={defaultForm.inputSize}
                    onChange={e => setDefaultForm({ ...defaultForm, inputSize: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    placeholder="Output Size"
                    value={defaultForm.outPutSize}
                    onChange={e => setDefaultForm({ ...defaultForm, outPutSize: Number(e.target.value) })}
                /><br/>
                <button onClick={() =>
                    handleSubmit(
                        '/models/default',
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
                    训练默认模型
                </button>
            </div>

            {/* 2. 训练定制模型 */}
            <div style={{ border: '1px solid #ccc', padding: '10px', marginBottom: '20px' }}>
                <h3>训练定制模型</h3>
                <input
                    type="text"
                    placeholder="Polygon API Key"
                    value={customizedForm.polygonApiKey}
                    onChange={e => setCustomizedForm({ ...customizedForm, polygonApiKey: e.target.value })}
                /><br/>
                <textarea
                    placeholder="请输入 CryptoAggregatesDTO JSON 数据..."
                    value={customizedForm.cryptoData}
                    onChange={e => setCustomizedForm({ ...customizedForm, cryptoData: e.target.value })}
                    rows={4}
                    cols={50}
                /><br/>
                <input
                    type="text"
                    placeholder="Model Kind (仅支持 LSTM_RNN 或 LSTM_DENSE_RNN)"
                    value={customizedForm.modelKind}
                    onChange={e => setCustomizedForm({ ...customizedForm, modelKind: e.target.value })}
                /><br/>
                <input
                    type="number"
                    placeholder="Seed"
                    value={customizedForm.seed}
                    onChange={e => setCustomizedForm({ ...customizedForm, seed: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    step="0.001"
                    placeholder="Learning Rate"
                    value={customizedForm.learningRate}
                    onChange={e => setCustomizedForm({ ...customizedForm, learningRate: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    step="0.1"
                    placeholder="Momentum"
                    value={customizedForm.momentum}
                    onChange={e => setCustomizedForm({ ...customizedForm, momentum: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    step="0.1"
                    placeholder="Dropout Rate"
                    value={customizedForm.dropoutRate}
                    onChange={e => setCustomizedForm({ ...customizedForm, dropoutRate: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    placeholder="Window Size"
                    value={customizedForm.windowSize}
                    onChange={e => setCustomizedForm({ ...customizedForm, windowSize: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    placeholder="Epochs"
                    value={customizedForm.epochs}
                    onChange={e => setCustomizedForm({ ...customizedForm, epochs: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    placeholder="Input Size"
                    value={customizedForm.inputSize}
                    onChange={e => setCustomizedForm({ ...customizedForm, inputSize: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    placeholder="Output Size"
                    value={customizedForm.outPutSize}
                    onChange={e => setCustomizedForm({ ...customizedForm, outPutSize: Number(e.target.value) })}
                /><br/>
                <button onClick={() =>
                    handleSubmit(
                        '/models/customized',
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
                    训练定制模型
                </button>
            </div>

            {/* 3. 训练 CNN-RNN Hybrid 定制模型 */}
            <div style={{ border: '1px solid #ccc', padding: '10px' }}>
                <h3>训练 CNN-RNN Hybrid 定制模型</h3>
                <input
                    type="text"
                    placeholder="Polygon API Key"
                    value={cnnRnnForm.polygonApiKey}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, polygonApiKey: e.target.value })}
                /><br/>
                <textarea
                    placeholder="请输入 CryptoAggregatesDTO JSON 数据..."
                    value={cnnRnnForm.cryptoData}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, cryptoData: e.target.value })}
                    rows={4}
                    cols={50}
                /><br/>
                <input
                    type="text"
                    placeholder="Model Kind (仅支持 CNN_RNN_HYBRID)"
                    value={cnnRnnForm.modelKind}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, modelKind: e.target.value })}
                /><br/>
                <input
                    type="number"
                    placeholder="Seed"
                    value={cnnRnnForm.seed}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, seed: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    step="0.001"
                    placeholder="Learning Rate"
                    value={cnnRnnForm.learningRate}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, learningRate: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    placeholder="Number of Features"
                    value={cnnRnnForm.numFeatures}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, numFeatures: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    placeholder="Number of Filters"
                    value={cnnRnnForm.numFilters}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, numFilters: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    placeholder="Kernel Width"
                    value={cnnRnnForm.kernelWidth}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, kernelWidth: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    placeholder="Pool Width"
                    value={cnnRnnForm.poolWidth}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, poolWidth: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    placeholder="LSTM Hidden Size"
                    value={cnnRnnForm.lstmHiddenSize}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, lstmHiddenSize: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    step="0.1"
                    placeholder="Momentum"
                    value={cnnRnnForm.momentum}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, momentum: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    step="0.1"
                    placeholder="Dropout Rate"
                    value={cnnRnnForm.dropoutRate}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, dropoutRate: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    placeholder="Time Steps"
                    value={cnnRnnForm.timeSteps}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, timeSteps: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    placeholder="Epochs"
                    value={cnnRnnForm.epochs}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, epochs: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    placeholder="Output Size"
                    value={cnnRnnForm.outPutSize}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, outPutSize: Number(e.target.value) })}
                /><br/>
                <input
                    type="number"
                    placeholder="Window Size"
                    value={cnnRnnForm.windowSize}
                    onChange={e => setCnnRnnForm({ ...cnnRnnForm, windowSize: Number(e.target.value) })}
                /><br/>
                <button onClick={() =>
                    handleSubmit(
                        '/models/customized/cnn-rnn-hybrid',
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
                    训练 CNN-RNN Hybrid 模型
                </button>
            </div>

            {message && <p>{message}</p>}
        </div>
    );
};

export default ModelTrainingPage;

