import React, { useState } from 'react';
import { apiClient } from '../api';

const UserPage = () => {
    const [email, setEmail] = useState('');
    const [loginData, setLoginData] = useState({ email: '', code: '', password: '' });
    const [updateData, setUpdateData] = useState({ name: '', birthday: '' });
    const [userInfo, setUserInfo] = useState(null);
    const [message, setMessage] = useState('');

    const sendCode = async () => {
        try {
            await apiClient.post(`http://localhost:8080/user/code?email_id=${encodeURIComponent(email)}`);
            setMessage('Verification code sent successfully.');
        } catch (error) {
            setMessage('Failed to send verification code.');
        }
    };

    const login = async () => {
        try {
            await apiClient.post('http://localhost:8080/user/login', loginData);
            setMessage('Logged in successfully.');
        } catch (error) {
            setMessage('Failed to log in.');
        }
    };

    const updateUser = async () => {
        try {
            await apiClient.put('http://localhost:8080/user/update', updateData);
            setMessage('User information updated successfully.');
        } catch (error) {
            setMessage('Failed to update user information.');
        }
    };

    const fetchUserInfo = async () => {
        try {
            const response = await apiClient.get('http://localhost:8080/user/me');
            setUserInfo(response.data);
            setMessage('User information retrieved successfully.');
        } catch (error) {
            setMessage('Failed to fetch user information.');
        }
    };

    return (
        <div className="container">
            <h2>User Management</h2>

            <div className="card">
                <h3>Send Verification Code</h3>
                <input
                    type="email"
                    placeholder="Enter your email address"
                    value={email}
                    onChange={e => setEmail(e.target.value)}
                />
                <button onClick={sendCode}>Send Code</button>
            </div>

            <div className="card">
                <h3>Log In</h3>
                <input
                    type="email"
                    placeholder="Email"
                    value={loginData.email}
                    onChange={e => setLoginData({ ...loginData, email: e.target.value })}
                />
                <input
                    type="text"
                    placeholder="Verification Code"
                    value={loginData.code}
                    onChange={e => setLoginData({ ...loginData, code: e.target.value })}
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={loginData.password}
                    onChange={e => setLoginData({ ...loginData, password: e.target.value })}
                />
                <button onClick={login}>Log In</button>
            </div>

            <div className="card">
                <h3>Update User Information</h3>
                <input
                    type="text"
                    placeholder="Name"
                    value={updateData.name}
                    onChange={e => setUpdateData({ ...updateData, name: e.target.value })}
                />
                <input
                    type="date"
                    placeholder="Birthday"
                    value={updateData.birthday}
                    onChange={e => setUpdateData({ ...updateData, birthday: e.target.value })}
                />
                <button onClick={updateUser}>Update Info</button>
            </div>

            <div className="card">
                <h3>Fetch User Information</h3>
                <button onClick={fetchUserInfo}>Get Info</button>
                {userInfo && <pre>{JSON.stringify(userInfo, null, 2)}</pre>}
            </div>

            {message && <div className="message">{message}</div>}
        </div>
    );
};

export default UserPage;

