import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import Register from './components/Register';
import Dashboard from './pages/Dashboard';
import Home from './pages/Home';
import Header from './components/Header';
import './App.css';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);

  useEffect(() => {
    // Vérifier si l'utilisateur est connecté au chargement
    const token = localStorage.getItem('token');
    const storedUser = localStorage.getItem('user');
    
    if (token && storedUser) {
      setIsAuthenticated(true);
      setUser(JSON.parse(storedUser));
    }
  }, []);

  const handleLogin = (userData, token) => {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(userData));
    setUser(userData);
    setIsAuthenticated(true);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
    setIsAuthenticated(false);
  };

  return (
    <Router>
      <div className="App">
        <Header 
          isAuthenticated={isAuthenticated} 
          user={user} 
          onLogout={handleLogout} 
        />
        
        <main className="main-content">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={
              !isAuthenticated ? (
                <Login onLogin={handleLogin} />
              ) : (
                <Navigate to="/dashboard" />
              )
            } />
            <Route path="/register" element={
              !isAuthenticated ? (
                <Register onRegister={handleLogin} />
              ) : (
                <Navigate to="/dashboard" />
              )
            } />
            <Route path="/dashboard" element={
              isAuthenticated ? (
                <Dashboard />
              ) : (
                <Navigate to="/login" />
              )
            } />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;