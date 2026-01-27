import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authService } from '../services/api';

const Login = ({ onLogin }) => {
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await authService.login(formData.username, formData.password);

      console.log("LOGIN RESPONSE:", response); // 👈 OBLIGATOIRE pour vérifier

      const token = response.token; // 👈 PAS response.token

      if (!token) {
        console.error("❌ TOKEN ABSENT DANS LA RÉPONSE");
        return;
      }

      onLogin(
        {
          username: response.username,
          email: response.email
        },
        token
      );

      navigate('/dashboard');


    } catch (err) {
      setError(err.response?.data?.message || 'Nom d\'utilisateur ou mot de passe incorrect');
    } finally {
      setLoading(false);
    }

   

  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>Connexion</h2>
        <p>Accédez à votre coffre-fort sécurisé</p>

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
              placeholder="Entrez votre nom d'utilisateur"
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
              placeholder="Entrez votre mot de passe"
              required
              disabled={loading}
            />
          </div>

          {error && <div className="error-message">{error}</div>}

          <button
            type="submit"
            className="btn btn-primary"
            style={{ width: '100%', marginTop: '1rem' }}
            disabled={loading}
          >
            {loading ? 'Connexion...' : 'Se connecter'}
          </button>
        </form>

        <div className="auth-footer">
          <p>Pas encore de compte ? <Link to="/register" className="auth-link">S'inscrire</Link></p>
        </div>
      </div>
    </div>
  );
};

export default Login;