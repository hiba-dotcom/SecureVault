import React, { useState } from 'react';
import PasswordForm from './PasswordForm';
import { passwordService } from '../services/api';
import { FiTrash2, FiEdit, FiEye, FiEyeOff, FiCopy, FiSearch } from 'react-icons/fi';

const PasswordList = ({ passwords, onRefresh }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingPassword, setEditingPassword] = useState(null);
  const [revealedPasswords, setRevealedPasswords] = useState({});

  const handleDelete = async (id) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer ce mot de passe ?')) {
      try {
        await passwordService.delete(id);
        onRefresh();
      } catch (err) {
        alert('Erreur lors de la suppression');
      }
    }
  };

  const handleEdit = (password) => {
    setEditingPassword(password);
    setShowForm(true);
  };

  const toggleReveal = (id) => {
    setRevealedPasswords(prev => ({
      ...prev,
      [id]: !prev[id]
    }));
  };

  const handleCopy = (text) => {
    navigator.clipboard.writeText(text);
    alert('Copié dans le presse-papier !');
  };

  const getStrengthColor = (score) => {
    if (score >= 80) return '#10b981';
    if (score >= 60) return '#3b82f6';
    if (score >= 40) return '#f59e0b';
    return '#ef4444';
  };

  const filteredPasswords = passwords.filter(password =>
    password.website.toLowerCase().includes(searchTerm.toLowerCase()) ||
    password.username.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="password-list-container">
      <div className="password-list-header">
        <h2>Mes mots de passe</h2>
        <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
          <div className="search-box">
            <FiSearch />
            <input
              type="text"
              placeholder="Rechercher..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          <button 
            className="btn btn-primary"
            onClick={() => {
              setEditingPassword(null);
              setShowForm(true);
            }}
          >
            + Ajouter
          </button>
        </div>
      </div>

      {showForm && (
        <PasswordForm
          password={editingPassword}
          onClose={() => {
            setShowForm(false);
            setEditingPassword(null);
          }}
          onSuccess={() => {
            onRefresh();
            setShowForm(false);
            setEditingPassword(null);
          }}
        />
      )}

      <div className="password-grid">
        {filteredPasswords.length === 0 ? (
          <div style={{ 
            gridColumn: '1 / -1', 
            textAlign: 'center', 
            padding: '3rem',
            background: '#f8f9fa',
            borderRadius: '10px'
          }}>
            <p style={{ marginBottom: '1rem', color: '#666' }}>
              {searchTerm ? 'Aucun résultat trouvé' : 'Aucun mot de passe enregistré'}
            </p>
            <button 
              className="btn btn-primary"
              onClick={() => setShowForm(true)}
            >
              Ajouter votre premier mot de passe
            </button>
          </div>
        ) : (
          filteredPasswords.map(password => (
            <div key={password.id} className="password-card">
              <div className="password-card-header">
                <h3 style={{ margin: 0 }}>{password.website}</h3>
                <span 
                  className="strength-badge"
                  style={{ backgroundColor: getStrengthColor(password.strengthScore) }}
                >
                  {password.strengthScore}/100
                </span>
              </div>
              
              <div style={{ marginTop: '1rem' }}>
                <div style={{ marginBottom: '0.5rem' }}>
                  <small style={{ color: '#666', fontSize: '0.875rem' }}>Utilisateur:</small>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span>{password.username}</span>
                    <button 
                      className="icon-btn"
                      onClick={() => handleCopy(password.username)}
                      title="Copier le nom d'utilisateur"
                    >
                      <FiCopy />
                    </button>
                  </div>
                </div>
                
                <div style={{ marginBottom: '0.5rem' }}>
                  <small style={{ color: '#666', fontSize: '0.875rem' }}>Mot de passe:</small>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span>{revealedPasswords[password.id] ? '••••••••' : '••••••••'}</span>
                    <div style={{ display: 'flex', gap: '5px' }}>
                      <button 
                        className="icon-btn"
                        onClick={() => toggleReveal(password.id)}
                        title={revealedPasswords[password.id] ? "Cacher" : "Afficher"}
                      >
                        {revealedPasswords[password.id] ? <FiEyeOff /> : <FiEye />}
                      </button>
                      <button 
                        className="icon-btn"
                        onClick={() => handleCopy('••••••••')}
                        title="Copier"
                      >
                        <FiCopy />
                      </button>
                    </div>
                  </div>
                </div>
                
                {password.notes && (
                  <div style={{ marginTop: '1rem' }}>
                    <small style={{ color: '#666', fontSize: '0.875rem' }}>Notes:</small>
                    <p style={{ marginTop: '0.25rem', fontSize: '0.9rem' }}>{password.notes}</p>
                  </div>
                )}
                
                {password.isCompromised && (
                  <div style={{
                    backgroundColor: '#fef3c7',
                    color: '#92400e',
                    padding: '8px',
                    borderRadius: '5px',
                    marginTop: '1rem',
                    fontSize: '0.875rem'
                  }}>
                    ⚠️ Mot de passe compromis
                  </div>
                )}
              </div>
              
              <div className="password-actions" style={{ marginTop: '1rem' }}>
                <button 
                  className="icon-btn"
                  onClick={() => handleEdit(password)}
                  title="Modifier"
                  style={{ color: '#3b82f6' }}
                >
                  <FiEdit />
                </button>
                <button 
                  className="icon-btn"
                  onClick={() => handleDelete(password.id)}
                  title="Supprimer"
                  style={{ color: '#ef4444' }}
                >
                  <FiTrash2 />
                </button>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default PasswordList;