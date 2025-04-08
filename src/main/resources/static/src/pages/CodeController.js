import React, { useState } from 'react';

const CodeController = () => {
    const [language, setLanguage] = useState("Java");
    const [code, setCode] = useState("");
    const [output, setOutput] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    // Determine backend API endpoint based on selected language
    const getEndpoint = (lang) => {
        switch(lang) {
            case "Java":
                return "http://localhost:8080/code/java";
            case "Kotlin":
                return "http://localhost:8080/code/kotlin";
            case "Python":
                return "http://localhost:8080/code/python";
            default:
                return "http://localhost:8080/code/java";
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
                <h3>Online Code Execution</h3>
                <div>
                    <label htmlFor="language-select">Select Language:</label>
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
                    placeholder="Please enter your code here..."
                    rows="15"
                    value={code}
                    onChange={(e) => setCode(e.target.value)}
                ></textarea>
                <button onClick={handleRunCode} disabled={isLoading}>
                    {isLoading ? "Running..." : "Run Code"}
                </button>
                {output && (
                    <div className="message">
                        <h4>Execution Result:</h4>
                        <pre>{output}</pre>
                    </div>
                )}
            </div>
        </div>
    );
};

export default CodeController;