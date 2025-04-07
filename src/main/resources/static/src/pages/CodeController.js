import React, { useState } from 'react';
import './App.css';

const CodeController = () => {
    const [language, setLanguage] = useState("Java");
    const [code, setCode] = useState("");
    const [output, setOutput] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    // 根据选择的语言确定请求的后端 API 地址
    const getEndpoint = (lang) => {
        switch(lang) {
            case "Java":
                return "/api/upload-strategy";
            case "Kotlin":
                return "/api/upload-strategy-kotlin";
            case "Python":
                return "/api/upload-strategy-python";
            default:
                return "/api/upload-strategy";
        }
    };

    const handleRunCode = async () => {
        setIsLoading(true);
        setOutput("");
        try {
            const response = await fetch(getEndpoint(language), {
                method: 'POST',
                headers: {
                    'Content-Type': 'text/plain'
                },
                body: code
            });
            const data = await response.text();
            setOutput(data);
        } catch (err) {
            console.error(err);
            setOutput("Error: " + err.message);
        }
        setIsLoading(false);
    };

    return (
        <div className="container">
            <div className="card">
                <h3>在线代码运行</h3>
                <div>
                    <label htmlFor="language-select">选择语言:</label>
                    <select
                        id="language-select"
                        value={language}
                        onChange={(e) => setLanguage(e.target.value)}
                    >
                        <option value="Java">Java</option>
                        <option value="Kotlin">Kotlin</option>
                        <option value="Python">Python</option>
                    </select>
                </div>
                <textarea
                    placeholder="请输入你的代码..."
                    rows="15"
                    value={code}
                    onChange={(e) => setCode(e.target.value)}
                ></textarea>
                <button onClick={handleRunCode} disabled={isLoading}>
                    {isLoading ? "运行中..." : "运行代码"}
                </button>
                {output && (
                    <div className="message">
                        <h4>执行结果:</h4>
                        <pre>{output}</pre>
                    </div>
                )}
            </div>
        </div>
    );
};

export default CodeController;
