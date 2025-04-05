import React from 'react';
import { Link } from 'react-router-dom';

const Navbar = () => (
    <nav style={{ padding: '10px', background: '#eee' }}>
        <Link to="/" style={{ marginRight: '10px' }}>首页</Link>
        <Link to="/user" style={{ marginRight: '10px' }}>用户管理</Link>
        <Link to="/crypto" style={{ marginRight: '10px' }}>量化分析</Link>
        <Link to="/models">模型训练</Link>
    </nav>
);

export default Navbar;
