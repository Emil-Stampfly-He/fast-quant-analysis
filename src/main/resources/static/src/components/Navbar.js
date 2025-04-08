import React from 'react';
import { NavLink } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => (
    <header className="navbar">
            <div className="navbar-container">
                    <div className="logo">
                            <NavLink exact="true" to="/">FastQuant</NavLink>
                    </div>
                    <nav className="navbar-links">
                            <NavLink exact="true" to="/" className="nav-link" activeclassname="active">
                                    Main
                            </NavLink>
                            <NavLink to="/user" className="nav-link" activeclassname="active">
                                    User Management
                            </NavLink>
                            <NavLink to="/crypto" className="nav-link" activeclassname="active">
                                    Quant Analysis
                            </NavLink>
                            <NavLink to="/models" className="nav-link" activeclassname="active">
                                    Model Training
                            </NavLink>
                            <NavLink to="/code" className="nav-link" activeclassname="active">
                                    Customized Strategy
                            </NavLink>
                    </nav>
            </div>
    </header>
);

export default Navbar;
