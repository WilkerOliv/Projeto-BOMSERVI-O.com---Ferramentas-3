import { getUser } from "./auth.js";

export function requireLogin() {
  const user = getUser();
  if (!user) {
    alert("É necessário estar logado.");
    window.location.href = "login.html";
  }
  return user;
}

export function requirePrestador() {
  const user = requireLogin();
  if (Number(user.nivel) !== 0) {
    alert("Acesso permitido somente para prestadores.");
    window.location.href = "index.html";
  }
  return user;
}

export function requireAdmin() {
  const user = requireLogin();
  if (Number(user.nivel) !== 1) {
    alert("Acesso restrito ao administrador.");
    window.location.href = "index.html";
  }
  return user;
}
