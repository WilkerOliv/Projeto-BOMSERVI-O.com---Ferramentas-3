// ============================================
// API ÚNICA DE ANÚNCIOS
// ============================================
import { getUser } from "./auth.js";

const API = "http://localhost:8080";

// --------------------------
// TOKEN
// --------------------------
function getToken() {
  return localStorage.getItem("bom_token");
}

// --------------------------
// FETCH UNIVERSAL
// --------------------------
async function apiFetch(path, opts = {}) {
  opts.headers = opts.headers || {};
  const t = getToken();

  if (t) opts.headers["Authorization"] = "Bearer " + t;

  if (opts.body && !(opts.body instanceof FormData)) {
    opts.headers["Content-Type"] = "application/json";
    opts.body = JSON.stringify(opts.body);
  }

  const res = await fetch(API + path, opts);
  const txt = await res.text();

  try {
    return { ok: res.ok, data: txt ? JSON.parse(txt) : null };
  } catch {
    return { ok: res.ok, data: txt };
  }
}

// --------------------------
// FUNÇÕES DE ANÚNCIOS
// --------------------------
function listarAnuncios() {
  return apiFetch("/api/anuncios");
}

function buscarAnuncios(q = "", cat = "") {
  const url = `/api/anuncios/search?q=${encodeURIComponent(q)}&cat=${encodeURIComponent(cat)}`;
  return apiFetch(url);
}

function obterAnuncio(id) {
  return apiFetch("/api/anuncios/" + id);
}

function listarMeusAnuncios() {
  const user = getUser();
  if (!user) return { ok: false, data: null };
  return apiFetch("/api/anuncios/by-user/" + user.login);
}

function criarAnuncio(a) {
  return apiFetch("/api/anuncios", { method: "POST", body: a });
}

function atualizarAnuncio(id, a) {
  return apiFetch("/api/anuncios/" + id, { method: "PUT", body: a });
}

function excluirAnuncio(id) {
  return apiFetch("/api/anuncios/" + id, { method: "DELETE" });
}

// --------------------------
// FOTOS
// --------------------------
function fotoUrl(filename) {
  return `${API}/api/fotos/raw/${filename}`;
}

async function uploadFoto(anuncioId, file) {
  const fd = new FormData();
  fd.append("file", file);

  const token = getToken();
  const headers = token ? { "Authorization": "Bearer " + token } : {};

  const res = await fetch(API + "/api/fotos/upload/" + anuncioId, {
    method: "POST",
    headers,
    body: fd
  });

  const txt = await res.text();
  try {
    return { ok: res.ok, data: txt ? JSON.parse(txt) : null };
  } catch {
    return { ok: res.ok, data: txt };
  }
}

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// AQUI ESTÁ A FUNÇÃO CORRETA
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
async function excluirFoto(idFoto) {
  return apiFetch(`/api/fotos/${idFoto}`, { method: "DELETE" });
}

// --------------------------
// EXPORT FINAL
// --------------------------
const AnuncioAPI = {
  listarAnuncios,
  buscarAnuncios,
  obterAnuncio,
  listarMeusAnuncios,
  criarAnuncio,
  atualizarAnuncio,
  excluirAnuncio,

  // Fotos
  fotoUrl,
  uploadFoto,
  excluirFoto
};

export default AnuncioAPI;
