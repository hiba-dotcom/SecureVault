import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FiLock, FiUser, FiLogOut, FiHome } from 'react-icons/fi';

const Header = ({ isAuthenticated, user, onLogout }) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    onLogout();
    navigate('/');
  };

  return (
    <header className="header">
      <div className="logo">
        <FiLock size={24} />
        <span>SecureVault</span>
      </div>
      
      <nav className="nav">
        <Link to="/" className="nav-link">
          <FiHome /> Accueil
        </Link>
        
        {isAuthenticated ? (
          <>
            <Link to="/dashboard" className="nav-link">
              Tableau de bord
            </Link>
            <div className="user-info">
              <FiUser />
              <span>{user?.username}</span>
              <button onClick={handleLogout} className="btn btn-secondary">
                <FiLogOut /> Déconnexion
              </button>
            </div>
          </>
        ) : (
          <>
            <Link to="/login" className="nav-link">
              Connexion
            </Link>
            <Link to="/register" className="btn btn-primary">
              S'inscrire
            </Link>
          </>
        )}
      </nav>
    </header>
  );
};

export default Header;