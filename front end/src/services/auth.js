class Auth {
  constructor() {
    this.user = JSON.parse(localStorage.getItem('user')) || null;
    this.token = localStorage.getItem('token') || null;
  }

  setAuth(user, token) {
    this.user = user;
    this.token = token;
    localStorage.setItem('user', JSON.stringify(user));
    localStorage.setItem('token', token);
  }

  clearAuth() {
    this.user = null;
    this.token = null;
    localStorage.removeItem('user');
    localStorage.removeItem('token');
  }

  isAuthenticated() {
    return !!this.token;
  }

  getToken() {
    return this.token;
  }

  getUser() {
    return this.user;
  }
}

export default new Auth();