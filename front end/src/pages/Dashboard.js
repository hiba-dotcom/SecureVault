import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import PasswordList from '../components/PasswordList';
import PasswordAnalyzer from '../components/PasswordAnalyzer';
import { passwordService } from '../services/api';
import { FiLock, FiShield, FiAlertTriangle, FiBarChart2 } from 'react-icons/fi';

const Dashboard = () => {
  const [passwords, setPasswords] = useState([]);
  const [stats, setStats] = useState({
    total: 0,
    strong: 0,
    weak: 0,
    compromised: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchPasswords();
  }, []);

  const fetchPasswords = async () => {
    try {
      const data = await passwordService.getAll();
      setPasswords(data);
      
      // Calculer les statistiques
      const strong = data.filter(p => p.strengthScore >= 60).length;
      const weak = data.filter(p => p.strengthScore < 40).length;
      const compromised = data.filter(p => p.isCompromised).length;
      
      setStats({
        total: data.length,
        strong,
        weak,
        compromised
      });
    } catch (err) {
      console.error('Error fetching passwords:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="dashboard-container">
        <div className="loading">Chargement...</div>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h1>Tableau de bord</h1>
        <p>Vue d'ensemble de votre sécurité</p>
      </div>

      {/* Statistiques */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#667eea' }}>
            <FiLock />
          </div>
          <div className="stat-info">
            <h3>{stats.total}</h3>
            <p>Mots de passe</p>
          </div>
        </div>
        
        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#10b981' }}>
            <FiShield />
          </div>
          <div className="stat-info">
            <h3>{stats.strong}</h3>
            <p>Mots de passe forts</p>
          </div>
        </div>
        
        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#f59e0b' }}>
            <FiAlertTriangle />
          </div>
          <div className="stat-info">
            <h3>{stats.weak}</h3>
            <p>Mots de passe faibles</p>
          </div>
        </div>
        
        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#ef4444' }}>
            <FiAlertTriangle />
          </div>
          <div className="stat-info">
            <h3>{stats.compromised}</h3>
            <p>Mots de passe compromis</p>
          </div>
        </div>
      </div>

      {/* Actions rapides */}
      <div className="quick-actions">
        <h2>Actions rapides</h2>
        <div className="actions-grid">
          <Link to="/dashboard" className="action-card">
            <div className="action-icon" style={{ background: '#667eea', color: 'white' }}>
              <FiLock />
            </div>
            <h3>Ajouter un mot de passe</h3>
            <p>Enregistrez un nouveau mot de passe sécurisé</p>
          </Link>
          
          <div className="action-card">
            <div className="action-icon" style={{ background: '#10b981', color: 'white' }}>
              <FiShield />
            </div>
            <h3>Analyser la sécurité</h3>
            <p>Vérifiez la force de vos mots de passe</p>
          </div>
        </div>
      </div>

      {/* Liste des mots de passe */}
      <div style={{ marginTop: '3rem' }}>
        <PasswordList passwords={passwords} onRefresh={fetchPasswords} />
      </div>

      {/* Analyseur de mots de passe */}
      <div style={{ marginTop: '3rem' }}>
        <PasswordAnalyzer />
      </div>
    </div>
  );
};

export default Dashboard;