import React, { useState } from 'react';
import { apiClient } from '../api';

const CryptoAnalysisPage = () => {
    const [donchianForm, setDonchianForm] = useState({
        polygonApiKey: '',
        cryptoData: '',
        windowSize: 20
    });
    const [pairTradingForm, setPairTradingForm] = useState({
        polygonApiKey: '',
        cryptoPairData: '',
        windowSize: 20,
        zScoreThreshold: 1.5,
        x: 5
    });
    const [emaPercentageForm, setEmaPercentageForm] = useState({
        polygonApiKey: '',
        cryptoData: '',
        emaPeriod: 10,
        stopLossPercentage: 5.0
    });
    const [emaATRForm, setEmaATRForm] = useState({
        polygonApiKey: '',
        cryptoData: '',
        emaPeriod: 10,
        atrPeriod: 14,
        atrMultiplier: 3.0
    });
    const [message, setMessage] = useState('');

    const handleSubmit = async (endpoint, method, data, queryParams) => {
        try {
            let url = endpoint;
            if (queryParams) {
                const queryString = Object.entries(queryParams)
                    .map(([k, v]) => `${k}=${encodeURIComponent(v)}`)
                    .join('&');
                url += '?' + queryString;
            }
            const response = await apiClient({
                method,
                url,
                data
            });
            setMessage(`Call to ${endpoint} successful, response data: ${JSON.stringify(response.data)}`);
        } catch (error) {
            setMessage(`Call to ${endpoint} failed: ${error.message}`);
        }
    };

    return (
        <div className="container">
            <h2>Cryptocurrency Quantitative Analysis</h2>

            {/* Donchian Channel Strategy */}
            <div className="card">
                <h3>Donchian Channel Strategy</h3>
                <input
                    type="text"
                    placeholder="Polygon API Key"
                    value={donchianForm.polygonApiKey}
                    onChange={e => setDonchianForm({ ...donchianForm, polygonApiKey: e.target.value })}
                />
                <textarea
                    placeholder="Please enter CryptoAggregatesDTO JSON data including: tickerName, timespan, fromDate, toDate, sort, multiplier, unadjusted, limit"
                    value={donchianForm.cryptoData}
                    onChange={e => setDonchianForm({ ...donchianForm, cryptoData: e.target.value })}
                    rows={4}
                />
                <input
                    type="number"
                    placeholder="Window Size"
                    value={donchianForm.windowSize}
                    onChange={e =>
                        setDonchianForm({ ...donchianForm, windowSize: Number(e.target.value) })
                    }
                />
                <button onClick={() =>
                    handleSubmit(
                        'http://localhost:8080/quant/analysis/crypto/donchian',
                        'post',
                        JSON.parse(donchianForm.cryptoData || '{}'),
                        { polygon_api_key: donchianForm.polygonApiKey, windowSize: donchianForm.windowSize }
                    )
                }>
                    Call Donchian API
                </button>
            </div>

            {/* Pair Trading Strategy */}
            <div className="card">
                <h3>Pair Trading Strategy</h3>
                <input
                    type="text"
                    placeholder="Polygon API Key"
                    value={pairTradingForm.polygonApiKey}
                    onChange={e => setPairTradingForm({ ...pairTradingForm, polygonApiKey: e.target.value })}
                />
                <textarea
                    placeholder="Please enter CryptoAggregatesPairDTO JSON data including: tickerName1, tickerName2, timespan, fromDate, toDate, sort, multiplier, unadjusted, limit"
                    value={pairTradingForm.cryptoPairData}
                    onChange={e => setPairTradingForm({ ...pairTradingForm, cryptoPairData: e.target.value })}
                    rows={4}
                />
                <input
                    type="number"
                    placeholder="Window Size"
                    value={pairTradingForm.windowSize}
                    onChange={e =>
                        setPairTradingForm({ ...pairTradingForm, windowSize: Number(e.target.value) })
                    }
                />
                <input
                    type="number"
                    step="0.1"
                    placeholder="Z-Score Threshold"
                    value={pairTradingForm.zScoreThreshold}
                    onChange={e =>
                        setPairTradingForm({ ...pairTradingForm, zScoreThreshold: Number(e.target.value) })
                    }
                />
                <input
                    type="number"
                    placeholder="Previous x Days"
                    value={pairTradingForm.x}
                    onChange={e =>
                        setPairTradingForm({ ...pairTradingForm, x: Number(e.target.value) })
                    }
                />
                <button onClick={() =>
                    handleSubmit(
                        'http://localhost:8080/quant/analysis/crypto/pair/trading',
                        'post',
                        JSON.parse(pairTradingForm.cryptoPairData || '{}'),
                        {
                            polygon_api_key: pairTradingForm.polygonApiKey,
                            windowSize: pairTradingForm.windowSize,
                            zScoreThreshold: pairTradingForm.zScoreThreshold,
                            x: pairTradingForm.x
                        }
                    )
                }>
                    Call Pair Trading API
                </button>
            </div>

            {/* EMA Fixed Percentage Stop Loss Strategy */}
            <div className="card">
                <h3>EMA Fixed Percentage Stop Loss Strategy</h3>
                <input
                    type="text"
                    placeholder="Polygon API Key"
                    value={emaPercentageForm.polygonApiKey}
                    onChange={e => setEmaPercentageForm({ ...emaPercentageForm, polygonApiKey: e.target.value })}
                />
                <textarea
                    placeholder="Please enter CryptoAggregatesDTO JSON data including: tickerName, timespan, fromDate, toDate, sort, multiplier, unadjusted, limit"
                    value={emaPercentageForm.cryptoData}
                    onChange={e => setEmaPercentageForm({ ...emaPercentageForm, cryptoData: e.target.value })}
                    rows={4}
                />
                <input
                    type="number"
                    placeholder="EMA Period"
                    value={emaPercentageForm.emaPeriod}
                    onChange={e =>
                        setEmaPercentageForm({ ...emaPercentageForm, emaPeriod: Number(e.target.value) })
                    }
                />
                <input
                    type="number"
                    step="0.1"
                    placeholder="Stop Loss Percentage"
                    value={emaPercentageForm.stopLossPercentage}
                    onChange={e =>
                        setEmaPercentageForm({ ...emaPercentageForm, stopLossPercentage: Number(e.target.value) })
                    }
                />
                <button onClick={() =>
                    handleSubmit(
                        'http://localhost:8080/quant/analysis/crypto/ema/stop/loss/percentage',
                        'post',
                        JSON.parse(emaPercentageForm.cryptoData || '{}'),
                        {
                            polygon_api_key: emaPercentageForm.polygonApiKey,
                            emaPeriod: emaPercentageForm.emaPeriod,
                            stopLossPercentage: emaPercentageForm.stopLossPercentage
                        }
                    )
                }>
                    Call EMA Fixed Stop Loss API
                </button>
            </div>

            {/* EMA ATR Stop Loss Strategy */}
            <div className="card">
                <h3>EMA ATR Stop Loss Strategy</h3>
                <input
                    type="text"
                    placeholder="Polygon API Key"
                    value={emaATRForm.polygonApiKey}
                    onChange={e => setEmaATRForm({ ...emaATRForm, polygonApiKey: e.target.value })}
                />
                <textarea
                    placeholder="Please enter CryptoAggregatesDTO JSON data including: tickerName, timespan, fromDate, toDate, sort, multiplier, unadjusted, limit"
                    value={emaATRForm.cryptoData}
                    onChange={e => setEmaATRForm({ ...emaATRForm, cryptoData: e.target.value })}
                    rows={4}
                />
                <input
                    type="number"
                    placeholder="EMA Period"
                    value={emaATRForm.emaPeriod}
                    onChange={e =>
                        setEmaATRForm({ ...emaATRForm, emaPeriod: Number(e.target.value) })
                    }
                />
                <input
                    type="number"
                    placeholder="ATR Period"
                    value={emaATRForm.atrPeriod}
                    onChange={e =>
                        setEmaATRForm({ ...emaATRForm, atrPeriod: Number(e.target.value) })
                    }
                />
                <input
                    type="number"
                    step="0.1"
                    placeholder="ATR Multiplier"
                    value={emaATRForm.atrMultiplier}
                    onChange={e =>
                        setEmaATRForm({ ...emaATRForm, atrMultiplier: Number(e.target.value) })
                    }
                />
                <button onClick={() =>
                    handleSubmit(
                        'http://localhost:8080/quant/analysis/crypto/ema/stop/loss/atr',
                        'post',
                        JSON.parse(emaATRForm.cryptoData || '{}'),
                        {
                            polygon_api_key: emaATRForm.polygonApiKey,
                            emaPeriod: emaATRForm.emaPeriod,
                            atrPeriod: emaATRForm.atrPeriod,
                            atrMultiplier: emaATRForm.atrMultiplier
                        }
                    )
                }>
                    Call EMA ATR Stop Loss API
                </button>
            </div>

            {message && <div className="message">{message}</div>}
        </div>
    );
};

export default CryptoAnalysisPage;

