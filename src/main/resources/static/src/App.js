import React from 'react';
import './App.css';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import UserPage from './pages/UserPage';
import CryptoAnalysisPage from './pages/CryptoAnalysisPage';
import ModelTrainingPage from './pages/ModelTrainingPage';
import CodeController from './pages/CodeController';

function App() {
    return (
        <Router>
            <Navbar />
            <div style={{ padding: '20px' }}>
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/user" element={<UserPage />} />
                    <Route path="/crypto" element={<CryptoAnalysisPage />} />
                    <Route path="/models" element={<ModelTrainingPage />} />
                    <Route path="/code" element={<CodeController />} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;

