import axios from 'axios';

// Configuration des services backend
const API_CONFIG = {
  AUTH: 'http://localhost:8081',
  SECURITY: 'http://localhost:8083',
  PASSWORD: 'http://localhost:8082'
};

// Créer une instance axios avec timeout
const createApiInstance = (baseURL) => {
  const instance = axios.create({
    baseURL,
    headers: {
      'Content-Type': 'application/json'
    },
    timeout: 10000 // 10 secondes
  });
  
  return instance;
};

// Création des instances
const authApi = createApiInstance(API_CONFIG.AUTH);
const securityApi = createApiInstance(API_CONFIG.SECURITY);
const passwordApi = createApiInstance(API_CONFIG.PASSWORD);

// Intercepteur pour ajouter automatiquement le token JWT
passwordApi.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log('Token ajouté à la requête:', token.substring(0, 20) + '...');
    } else {
      console.warn('Aucun token trouvé pour la requête');
    }
    return config;
  },
  (error) => {
    console.error('Erreur dans l\'intercepteur request:', error);
    return Promise.reject(error);
  }
);

// Intercepteur pour logger les réponses et erreurs


// Fonctions d'authentification
export const authService = {
  login: async (username, password) => {
    try {
      console.log('Tentative de connexion pour:', username);
      const response = await authApi.post('/auth/login', { username, password });
      console.log('Connexion réussie:', response.data.username);
      return response.data;
    } catch (error) {
      console.error('Erreur de connexion:', error.response?.data || error.message);
      throw error;
    }
  },
  
  register: async (username, email, password) => {
    try {
      console.log('Tentative d\'inscription pour:', username);
      const response = await authApi.post('/auth/register', { 
        username, 
        email, 
        password 
      });
      console.log('Inscription réussie:', response.data.username);
      return response.data;
    } catch (error) {
      console.error('Erreur d\'inscription:', error.response?.data || error.message);
      throw error;
    }
  }
};

// Fonctions de sécurité
export const securityService = {
  analyzePassword: async (password) => {
    try {
      const response = await securityApi.post('/security/analyze', { password });
      return response.data;
    } catch (error) {
      console.error('Erreur d\'analyse de mot de passe:', error);
      return {
        strengthScore: 0,
        strengthLevel: 'UNKNOWN',
        compromised: false,
        breachCount: 0,
        suggestions: [],
        calculationTimeMs: 0
      };
    }
  },
  
  generatePassword: async () => {
    try {
      const response = await securityApi.get('/security/generate/quick');
      return response.data.password;
    } catch (error) {
      console.error('Erreur de génération de mot de passe:', error);
      return 'ErrorGeneratingPassword';
    }
  }
};

// Fonctions de gestion des mots de passe
export const passwordService = {
  getAll: async () => {
    try {
      console.log('Récupération de tous les mots de passe...');
      const response = await passwordApi.get('/passwords');
      console.log(`${response.data.length} mots de passe récupérés`);
      return response.data;
    } catch (error) {
      console.error('Erreur lors de la récupération des mots de passe:', error);
      return [];
    }
  },
  
  create: async (passwordData) => {
    try {
      console.log('Création d\'un mot de passe:', passwordData.website);
      const response = await passwordApi.post('/passwords', passwordData);
      console.log('Mot de passe créé avec succès:', response.data);
      return response.data;
    } catch (error) {
      console.error('Erreur lors de la création du mot de passe:', {
        error: error.response?.data || error.message,
        data: passwordData
      });
      throw error;
    }
  },
  
  update: async (id, passwordData) => {
    try {
      const response = await passwordApi.put(`/passwords/${id}`, passwordData);
      return response.data;
    } catch (error) {
      console.error('Erreur lors de la mise à jour du mot de passe:', error);
      throw error;
    }
  },
  
  delete: async (id) => {
    try {
      await passwordApi.delete(`/passwords/${id}`);
    } catch (error) {
      console.error('Erreur lors de la suppression du mot de passe:', error);
      throw error;
    }
  }
};