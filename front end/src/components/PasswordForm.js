import React, { useState, useEffect } from 'react';
import { passwordService, securityService } from '../services/api';

const PasswordForm = ({ password, onClose, onSuccess }) => {
  const [formData, setFormData] = useState({
    website: '',
    username: '',
    password: '',
    notes: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [analysis, setAnalysis] = useState(null);

  useEffect(() => {
    if (password) {
      setFormData({
        website: password.website || '',
        username: password.username || '',
        password: '', // On ne pré-remplit pas le mot de passe pour la sécurité
        notes: password.notes || ''
      });
    }
  }, [password]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Analyser le mot de passe en temps réel
    if (name === 'password' && value.length > 0) {
      analyzePassword(value);
    } else if (name === 'password' && value.length === 0) {
      setAnalysis(null);
    }
  };

  const analyzePassword = async (pwd) => {
    try {
      const result = await securityService.analyzePassword(pwd);
      setAnalysis(result);
    } catch (err) {
      console.error('Erreur d\'analyse:', err);
    }
  };

  const generatePassword = async () => {
    try {
      const generated = await securityService.generatePassword();
      setFormData(prev => ({ ...prev, password: generated }));
      analyzePassword(generated);
    } catch (err) {
      setError('Erreur lors de la génération');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.website || !formData.username || !formData.password) {
      setError('Tous les champs obligatoires doivent être remplis');
      return;
    }

    setLoading(true);
    setError('');

    try {
      if (password) {
        // Mise à jour
        await passwordService.update(password.id, formData);
      } else {
        // Création
        await passwordService.create(formData);
      }
      
      onSuccess();
      
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de l\'enregistrement');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="modal-header">
          <h3 style={{ margin: 0 }}>{password ? 'Modifier' : 'Ajouter'} un mot de passe</h3>
          <button className="close-btn" onClick={onClose}>×</button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Site web *</label>
            <input
              type="text"
              name="website"
              className="form-control"
              value={formData.website}
              onChange={handleChange}
              placeholder="ex: google.com"
              required
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label>Nom d'utilisateur/Email *</label>
            <input
              type="text"
              name="username"
              className="form-control"
              value={formData.username}
              onChange={handleChange}
              placeholder="ex: utilisateur@gmail.com"
              required
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label>Mot de passe *</label>
            <div style={{ display: 'flex', gap: '10px' }}>
              <input
                type="password"
                name="password"
                className="form-control"
                value={formData.password}
                onChange={handleChange}
                placeholder="Entrez ou générez un mot de passe"
                required
                disabled={loading}
              />
              <button 
                type="button" 
                onClick={generatePassword}
                disabled={loading}
              >
               🔐Générer
              </button>
            </div>
          </div>

          <div className="form-group">
            <label>Notes</label>
            <textarea
              name="notes"
              className="form-control"
              value={formData.notes}
              onChange={handleChange}
              placeholder="Notes optionnelles..."
              rows="3"
              disabled={loading}
            />
          </div>

          {analysis && (
            <div style={{
              backgroundColor: '#f8f9fa',
              padding: '15px',
              borderRadius: '8px',
              margin: '20px 0'
            }}>
              <h4 style={{ marginBottom: '10px' }}>Analyse de sécurité:</h4>
              <div className="strength-meter">
                <div 
                  className="strength-bar"
                  style={{ 
                    width: `${analysis.strengthScore}%`,
                    backgroundColor: analysis.strengthScore >= 60 ? '#10b981' : 
                                   analysis.strengthScore >= 40 ? '#f59e0b' : '#ef4444'
                  }}
                ></div>
              </div>
              <div style={{ 
                display: 'flex', 
                justifyContent: 'space-between',
                marginTop: '10px',
                fontSize: '0.9rem'
              }}>
                <span>Force: <strong>{analysis.strengthLevel}</strong></span>
                <span>Score: {analysis.strengthScore}/100</span>
              </div>
              {analysis.compromised && (
                <div style={{
                  backgroundColor: '#fef3c7',
                  color: '#92400e',
                  padding: '10px',
                  borderRadius: '5px',
                  marginTop: '10px',
                  fontSize: '0.875rem'
                }}>
                  ⚠️ Ce mot de passe a été compromis dans {analysis.breachCount} fuites
                </div>
              )}
            </div>
          )}

          {error && <div className="error-message">{error}</div>}

          <div style={{ 
            display: 'flex', 
            justifyContent: 'flex-end', 
            gap: '10px',
            marginTop: '20px'
          }}>
            <button 
              type="button" 
              className="btn btn-secondary"
              onClick={onClose}
              disabled={loading}
            >
              Annuler
            </button>
            <button 
              type="submit" 
              className="btn btn-primary"
              disabled={loading}
            >
              {loading ? 'Enregistrement...' : (password ? 'Mettre à jour' : 'Enregistrer')}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default PasswordForm;
