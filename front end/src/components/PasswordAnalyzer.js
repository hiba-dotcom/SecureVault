import React, { useState } from 'react';
import { securityService } from '../services/api';

const PasswordAnalyzer = () => {
  const [password, setPassword] = useState('');
  const [analysis, setAnalysis] = useState(null);
  const [loading, setLoading] = useState(false);
  const [generatedPassword, setGeneratedPassword] = useState('');

  const handleAnalyze = async () => {
    if (!password) {
      alert('Veuillez entrer un mot de passe à analyser');
      return;
    }
    
    setLoading(true);
    try {
      const result = await securityService.analyzePassword(password);
      setAnalysis(result);
    } catch (err) {
      console.error('Erreur d\'analyse:', err);
      alert('Erreur lors de l\'analyse du mot de passe');
    } finally {
      setLoading(false);
    }
  };

  const handleGenerate = async () => {
    try {
      const generated = await securityService.generatePassword();
      setGeneratedPassword(generated);
      setPassword(generated);
      
      // Analyser automatiquement le mot de passe généré
      const result = await securityService.analyzePassword(generated);
      setAnalysis(result);
    } catch (err) {
      console.error('Erreur de génération:', err);
      alert('Erreur lors de la génération du mot de passe');
    }
  };

  const getStrengthColor = (score) => {
    if (score >= 80) return '#10b981';
    if (score >= 60) return '#3b82f6';
    if (score >= 40) return '#f59e0b';
    return '#ef4444';
  };

  return (
    <div className="analyzer-container">
      <h3>🔍 Analyseur de mots de passe</h3>
      
      <div className="analyzer-input">
        <input
          type="text"
          className="form-control"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Entrez un mot de passe à analyser"
          disabled={loading}
        />
        <div className="analyzer-buttons">
          <button 
            onClick={handleAnalyze} 
            disabled={!password || loading}
            className="btn btn-primary"
          >
            {loading ? 'Analyse...' : 'Analyser'}
          </button>
          <button 
            onClick={handleGenerate}
            className="btn btn-secondary"
            disabled={loading}
          >
            Générer
          </button>
        </div>
      </div>

      {generatedPassword && (
        <div style={{
          backgroundColor: '#f8f9fa',
          padding: '15px',
          borderRadius: '8px',
          margin: '15px 0'
        }}>
          <p style={{ marginBottom: '10px', fontWeight: '500' }}>Mot de passe généré:</p>
          <div style={{ 
            display: 'flex', 
            justifyContent: 'space-between',
            alignItems: 'center'
          }}>
            <code style={{ 
              fontFamily: 'monospace', 
              backgroundColor: 'white',
              padding: '8px 12px',
              borderRadius: '5px',
              flex: 1,
              marginRight: '10px'
            }}>
              {generatedPassword}
            </code>
            <button 
              onClick={() => {
                navigator.clipboard.writeText(generatedPassword);
                alert('Copié dans le presse-papier !');
              }}
              className="btn btn-secondary"
            >
              Copier
            </button>
          </div>
        </div>
      )}

      {analysis && (
        <div style={{ marginTop: '20px' }}>
          <div style={{ marginBottom: '20px' }}>
            <div style={{ 
              display: 'flex', 
              justifyContent: 'space-between',
              alignItems: 'center',
              marginBottom: '10px'
            }}>
              <h4 style={{ margin: 0 }}>Force: {analysis.strengthLevel}</h4>
              <span style={{ 
                backgroundColor: getStrengthColor(analysis.strengthScore),
                color: 'white',
                padding: '4px 12px',
                borderRadius: '20px',
                fontSize: '0.875rem',
                fontWeight: '500'
              }}>
                {analysis.strengthScore}/100
              </span>
            </div>
            <div className="strength-meter">
              <div 
                className="strength-bar"
                style={{ 
                  width: `${analysis.strengthScore}%`,
                  backgroundColor: getStrengthColor(analysis.strengthScore)
                }}
              ></div>
            </div>
          </div>

          {analysis.compromised && (
            <div style={{
              backgroundColor: '#fef3c7',
              border: '1px solid #f59e0b',
              padding: '15px',
              borderRadius: '8px',
              marginBottom: '15px',
              display: 'flex',
              alignItems: 'flex-start',
              gap: '10px'
            }}>
              <span style={{ fontSize: '1.2rem' }}>⚠️</span>
              <div>
                <strong>ATTENTION:</strong> Ce mot de passe a été compromis dans {analysis.breachCount} fuites de données.
                <p style={{ marginTop: '5px', marginBottom: 0 }}>Ne l'utilisez pas !</p>
              </div>
            </div>
          )}

          {analysis.suggestions && analysis.suggestions.length > 0 && (
            <div>
              <h5>Suggestions d'amélioration:</h5>
              <ul style={{ 
                paddingLeft: '20px',
                marginTop: '10px'
              }}>
                {analysis.suggestions.map((suggestion, index) => (
                  <li key={index} style={{ marginBottom: '5px' }}>{suggestion}</li>
                ))}
              </ul>
            </div>
          )}

          <div style={{ 
            marginTop: '15px',
            fontSize: '0.875rem',
            color: '#666',
            textAlign: 'right'
          }}>
            <p>Temps d'analyse: {analysis.calculationTimeMs}ms</p>
          </div>
        </div>
      )}
    </div>
  );
};

export default PasswordAnalyzer;