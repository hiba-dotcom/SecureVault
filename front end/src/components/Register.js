import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authService } from '../services/api';

const Register = ({ onRegister }) => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError('');
    setSuccess('');
  };

  const validateForm = () => {
    if (formData.password !== formData.confirmPassword) {
      setError('Les mots de passe ne correspondent pas');
      return false;
    }
    
    if (formData.password.length < 8) {
      setError('Le mot de passe doit contenir au moins 8 caractères');
      return false;
    }
    
    if (!formData.email.includes('@')) {
      setError('Veuillez entrer une adresse email valide');
      return false;
    }
    
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;
    
    setLoading(true);
    setError('');

    try {
      const response = await authService.register(
        formData.username,
        formData.email,
        formData.password
      );
      
      setSuccess('Compte créé avec succès ! Redirection...');
      
      // Auto-login après inscription
      setTimeout(() => {
        onRegister({
          username: response.username,
          email: response.email
        }, response.token);
        
        navigate('/dashboard');
      }, 1500);
      
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la création du compte');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>Créer un compte</h2>
        <p>Rejoignez SecureVault</p>
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="username">Nom d'utilisateur</label>
            <input
              type="text"
              id="username"
              name="username"
              className="form-control"
              value={formData.username}
              onChange={handleChange}
              placeholder="Choisissez un nom d'utilisateur"
              required
              disabled={loading}
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              className="form-control"
              value={formData.email}
              onChange={handleChange}
              placeholder="Entrez votre email"
              required
              disabled={loading}
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="password">Mot de passe</label>
            <input
              type="password"
              id="password"
              name="password"
              className="form-control"
              value={formData.password}
              onChange={handleChange}
              placeholder="Créez un mot de passe fort"
              required
              disabled={loading}
            />
            <small style={{ color: '#666', fontSize: '0.875rem' }}>Minimum 8 caractères</small>
          </div>
          
          <div className="form-group">
            <label htmlFor="confirmPassword">Confirmer le mot de passe</label>
            <input
              type="password"
              id="confirmPassword"
              name="confirmPassword"
              className="form-control"
              value={formData.confirmPassword}
              onChange={handleChange}
              placeholder="Retapez votre mot de passe"
              required
              disabled={loading}
            />
          </div>
          
          {error && <div className="error-message">{error}</div>}
          {success && <div className="success-message">{success}</div>}
          
          <button 
            type="submit" 
            className="btn btn-primary"
            style={{ width: '100%', marginTop: '1rem' }}
            disabled={loading}
          >
            {loading ? 'Création...' : 'Créer mon compte'}
          </button>
        </form>
        
        <div className="auth-footer">
          <p>Déjà un compte ? <Link to="/login" className="auth-link">Se connecter</Link></p>
        </div>
      </div>
    </div>
  );
};

export default Register;