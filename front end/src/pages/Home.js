import React from 'react';
import { Link } from 'react-router-dom';
import { FiShield, FiLock, FiBarChart2, FiUsers } from 'react-icons/fi';

const Home = () => {
  return (
    <div className="home">
      <section className="hero">
        <h1>🔐 SecureVault</h1>
        <p>Votre gestionnaire de mots de passe sécurisé et intelligent</p>
        <div className="cta-buttons">
          <Link to="/register" className="btn btn-primary" style={{ marginRight: '10px' }}>
            Commencer gratuitement
          </Link>
          <Link to="/login" className="btn btn-secondary">
            Se connecter
          </Link>
        </div>
      </section>

      <section className="features">
        <div className="feature-card">
          <div className="feature-icon">
            <FiLock size={40} />
          </div>
          <h3>Stockage sécurisé</h3>
          <p>Tous vos mots de passe sont chiffrés avec AES-256 et stockés en sécurité.</p>
        </div>

        <div className="feature-card">
          <div className="feature-icon">
            <FiShield size={40} />
          </div>
          <h3>Analyse de sécurité</h3>
          <p>Vérifiez la force de vos mots de passe et détectez les fuites de données.</p>
        </div>

        <div className="feature-card">
          <div className="feature-icon">
            <FiBarChart2 size={40} />
          </div>
          <h3>Tableau de bord</h3>
          <p>Visualisez vos statistiques de sécurité et obtenez des recommandations.</p>
        </div>

        <div className="feature-card">
          <div className="feature-icon">
            <FiUsers size={40} />
          </div>
          <h3>Multi-plateforme</h3>
          <p>Accédez à vos mots de passe depuis n'importe quel appareil.</p>
        </div>
      </section>

      <section className="how-it-works">
        <h2>Comment ça marche</h2>
        <div className="steps">
          <div className="step">
            <div className="step-number">1</div>
            <h3>Créez votre compte</h3>
            <p>Inscrivez-vous gratuitement en quelques secondes.</p>
          </div>
          <div className="step">
            <div className="step-number">2</div>
            <h3>Ajoutez vos mots de passe</h3>
            <p>Enregistrez vos identifiants en toute sécurité.</p>
          </div>
          <div className="step">
            <div className="step-number">3</div>
            <h3>Gérez et analysez</h3>
            <p>Visualisez et améliorez votre sécurité numérique.</p>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Home;