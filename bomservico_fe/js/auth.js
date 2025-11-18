// js/auth.js

export function saveAuth(token, user) {
  localStorage.setItem('bom_token', token);
  localStorage.setItem('bom_user', JSON.stringify(user));
  if (typeof window !== 'undefined') {
    window.bom_token = token;
    window.bom_user = user;
  }
}

export function getUser() {
  try {
    const raw = localStorage.getItem('bom_user');
    return raw ? JSON.parse(raw) : null;
  } catch (e) {
    return null;
  }
}

export function logout() {
  localStorage.removeItem('bom_token');
  localStorage.removeItem('bom_user');
  if (typeof window !== 'undefined') {
    delete window.bom_token;
    delete window.bom_user;
    window.location = 'index.html';
  }
}

if (typeof window !== 'undefined') {
  window.auth = { saveAuth, getUser, logout };
}
