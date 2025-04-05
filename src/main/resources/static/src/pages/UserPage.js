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
            const response = await apiClient.post(`/user/code?email_id=${encodeURIComponent(email)}`);
            setMessage('Verification code sent');
        } catch (error) {
            setMessage('Failed to send verification code');
        }
    };

    const login = async () => {
        try {
            const response = await apiClient.post('/user/login', loginData);
            setMessage('Logged in successfully');
        } catch (error) {
            setMessage('Failed to log in');
        }
    };

    const updateUser = async () => {
        try {
            const response = await apiClient.put('/user/update', updateData);
            setMessage('Update successfully');
        } catch (error) {
            setMessage('Failed to update user');
        }
    };

    const fetchUserInfo = async () => {
        try {
            const response = await apiClient.get('/user/me');
            setUserInfo(response.data);
        } catch (error) {
            setMessage('Failed to fetch user info');
        }
    };

    return (
        <div>
            <h2>User Management</h2>
            <div style={{ border: '1px solid #ccc', padding: '10px', marginBottom: '20px' }}>
                <h3>Sending verification code</h3>
                <input
                    type="email"
                    placeholder="Please input your email address"
                    value={email}
                    onChange={e => setEmail(e.target.value)}
                />
                <button onClick={sendCode}>发送验证码</button>
            </div>

            <div style={{ border: '1px solid #ccc', padding: '10px', marginBottom: '20px' }}>
                <h3>Log in</h3>
                <input
                    type="email"
                    placeholder="email"
                    value={loginData.email}
                    onChange={e => setLoginData({ ...loginData, email: e.target.value })}
                /><br/>
                <input
                    type="text"
                    placeholder="verification code"
                    value={loginData.code}
                    onChange={e => setLoginData({ ...loginData, code: e.target.value })}
                /><br/>
                <input
                    type="password"
                    placeholder="password"
                    value={loginData.password}
                    onChange={e => setLoginData({ ...loginData, password: e.target.value })}
                /><br/>
                <button onClick={login}>Log in</button>
            </div>

            <div style={{ border: '1px solid #ccc', padding: '10px', marginBottom: '20px' }}>
                <h3>Update user info</h3>
                <input
                    type="text"
                    placeholder="name"
                    value={updateData.name}
                    onChange={e => setUpdateData({ ...updateData, name: e.target.value })}
                /><br/>
                <input
                    type="date"
                    placeholder="birthday"
                    value={updateData.birthday}
                    onChange={e => setUpdateData({ ...updateData, birthday: e.target.value })}
                /><br/>
                <button onClick={updateUser}>更新信息</button>
            </div>

            <div style={{ border: '1px solid #ccc', padding: '10px' }}>
                <h3>Fetching user info</h3>
                <button onClick={fetchUserInfo}>Fetching info</button>
                {userInfo && (
                    <pre>{JSON.stringify(userInfo, null, 2)}</pre>
                )}
            </div>
            {message && <p>{message}</p>}
        </div>
    );
};

export default UserPage;
