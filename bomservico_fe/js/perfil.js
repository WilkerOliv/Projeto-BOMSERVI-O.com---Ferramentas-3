import { requireLogin } from "./auth-guard.js";

// ------------------------------------------------
// PEGAR USUÁRIO LOGADO (sem alterar seu guards)
// ------------------------------------------------
function getUsuarioLogado() {
  try {
    return JSON.parse(localStorage.getItem("bom_user"));
  } catch {
    return null;
  }
}

// ------------------------------------------------
// apiFetch LOCAL (não depende de auth-guard.js)
// ------------------------------------------------
async function apiFetch(url, options = {}) {
  const token = localStorage.getItem("bom_token");

  const headers = {
    "Content-Type": "application/json",
    ...options.headers
  };

  if (token) {
    headers["Authorization"] = "Bearer " + token;
  }

  const response = await fetch("http://localhost:8080" + url, {
    ...options,
    headers
  });

  let data = null;
  try { data = await response.json(); } catch {}

  return { ok: response.ok, data };
}

// ------------------------------------------------
// MÁSCARAS
// ------------------------------------------------
function maskTelefone(v) {
  v = v.replace(/\D/g, "");
  if (v.length <= 10) {
    return v.replace(/(\d{2})(\d)/, "($1) $2").replace(/(\d{4})(\d)/, "$1-$2");
  }
  return v.replace(/(\d{2})(\d)/, "($1) $2").replace(/(\d{5})(\d)/, "$1-$2");
}

function maskData(v) {
  v = v.replace(/\D/g, "");
  v = v.replace(/(\d{2})(\d)/, "$1/$2");
  v = v.replace(/(\d{2})(\d)/, "$1/$2");
  return v.substring(0, 10);
}

// Aplicar máscara enquanto digita
document.addEventListener("input", e => {
  if (e.target.id === "telefone") e.target.value = maskTelefone(e.target.value);
  if (e.target.id === "dtNasc") e.target.value = maskData(e.target.value);
});

// ------------------------------------------------
// DATA: conversões BR <-> ISO
// ------------------------------------------------
function brToIso(d) {
  const [dia, mes, ano] = d.split("/");
  return `${ano}-${mes}-${dia}`;
}

function isoToBr(d) {
  const [ano, mes, dia] = d.split("-");
  return `${dia}/${mes}/${ano}`;
}

// ------------------------------------------------
// IDADE >= 18
// ------------------------------------------------
function calcularIdade(dataBr) {
  const [d, m, a] = dataBr.split("/");
  const nascimento = new Date(`${a}-${m}-${d}`);
  const hoje = new Date();

  let idade = hoje.getFullYear() - nascimento.getFullYear();
  const diffMes = hoje.getMonth() - nascimento.getMonth();

  if (diffMes < 0 || (diffMes === 0 && hoje.getDate() < nascimento.getDate())) idade--;

  return idade;
}

// ------------------------------------------------
// CARREGAR PERFIL
// ------------------------------------------------
async function carregarPerfil() {
  requireLogin(); // não altera seu projeto

  const usuario = getUsuarioLogado();

  if (!usuario) {
    showToast("Faça login primeiro.", "info");
    window.location.href = "login.html";
    return;
  }

  const r = await apiFetch(`/api/usuarios/${usuario.login}`);
  if (!r.ok) {
    showToast("Erro ao carregar perfil.", "error");
    return;
  }

  const u = r.data;

  document.getElementById("login").value = u.login || "";
  document.getElementById("nome").value = u.nome || "";
  document.getElementById("email").value = u.email || "";
  document.getElementById("endereco").value = u.endereco || "";

  if (u.telefone) document.getElementById("telefone").value = maskTelefone(u.telefone);

  if (u.cpf) {
    const cpfFormatado = u.cpf
      .replace(/\D/g, "")
      .replace(/(\d{3})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d{1,2})$/, "$1-$2");

    document.getElementById("cpf").value = cpfFormatado;
  }

  if (u.dtNasc) document.getElementById("dtNasc").value = isoToBr(u.dtNasc);
}

// -------- SALVAR ALTERAÇÕES --------
document.getElementById("frmPerfil").addEventListener("submit", async e => {
  e.preventDefault();

  const usuario = getUsuarioLogado();
  const f = e.target;

  const dataBr = f.dtNasc.value;
  const idade = calcularIdade(dataBr);

  if (idade < 18) {
    showToast("Você deve ter pelo menos 18 anos.", "error");
    return;
  }

  const body = {
    nome: f.nome.value,
    email: f.email.value,
    telefone: f.telefone.value,
    endereco: f.endereco.value,
    dtNasc: brToIso(dataBr)
  };

  if (f.senha.value.trim() !== "") {
    body.senha = f.senha.value;
  }

  const r = await apiFetch(`/api/usuarios/${usuario.login}`, {
    method: "PUT",
    body: JSON.stringify(body)
  });

  if (!r.ok) {
    showToast("Erro ao salvar alterações.", "error");
    return;
  }

  const userAtualizado = r.data;

  // ATUALIZAR LOCALSTORAGE COM OS NOVOS DADOS
  localStorage.setItem("bom_user", JSON.stringify(userAtualizado));

  showToast("Perfil atualizado com sucesso!", "success");

  // REDIRECIONAR PARA DASHBOARD
  setTimeout(() => {
    window.location.href = "dashboard-prestador.html";
  }, 800);
});


// iniciar
carregarPerfil();
