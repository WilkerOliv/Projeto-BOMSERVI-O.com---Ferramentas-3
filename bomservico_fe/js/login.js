import { saveAuth } from "./auth.js";

const API = "http://localhost:8080";

window.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("frm");
  form.addEventListener("submit", entrar);
});

async function entrar(e) {
  e.preventDefault();

  const form = document.getElementById("frm");
  const login = form.login.value;
  const senha = form.senha.value;

  const r = await fetch(API + "/api/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ login, senha })
  });

  const txt = await r.text();
  let data;

  try { data = JSON.parse(txt); } catch {
    document.getElementById("msg").innerText = "Erro inesperado.";
    return;
  }

  if (!data.token || !data.user) {
    document.getElementById("msg").innerText = data.error || "Credenciais inválidas.";
    return;
  }

  // salvar
  saveAuth(data.token, data.user);

  // redirecionar
  if (Number(data.user.nivel) === 1)
    window.location.href = "admin-dashboard.html";
  else
    window.location.href = "dashboard-prestador.html";
}
