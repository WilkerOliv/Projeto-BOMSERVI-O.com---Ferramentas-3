import { requirePrestador } from "./auth-guard.js";
import { logout } from "./auth.js";
import AnuncioAPI from "./anuncios-api.js";

window.addEventListener("DOMContentLoaded", () => {
  const user = requirePrestador();

  document.getElementById("btnSair").onclick = logout;

  document.getElementById("pNome").innerText = user.nome;
  document.getElementById("pEmail").innerText = user.email;
  document.getElementById("pTel").innerText = user.telefone;

  carregarMeusAnuncios();
  carregarMensagens();
});

// MEUS ANÚNCIOS
async function carregarMeusAnuncios() {
  const box = document.getElementById("listaAn");
  const r = await AnuncioAPI.listarMeusAnuncios();

  if (!r.ok) {
    document.getElementById("msgAn").innerText = "Erro ao carregar anúncios.";
    return;
  }

  box.innerHTML = "";

  r.data.forEach(a => {
    const foto = a.fotoList?.length
      ? AnuncioAPI.fotoUrl(a.fotoList[0].nomeArq)
      : "https://via.placeholder.com/400x300.png?text=Anuncio";

    box.insertAdjacentHTML("beforeend", `
      <div class="col">
        <div class="card h-100 shadow-sm">
          <img src="${foto}" class="card-img-top">
          <div class="card-body">
            <h5 class="card-title">${a.titulo}</h5>
            <p class="card-text">${a.descricao ?? ""}</p>
            <button class="btn btn-primary btn-sm mt-1" onclick="editar(${a.id})">Editar</button>
            <button class="btn btn-danger btn-sm mt-1" onclick="apagar(${a.id})">Excluir</button>
          </div>
        </div>
      </div>
    `);
  });
}

function editar(id) {
  window.location.href = "anuncio-form.html?id=" + id;
}

async function apagar(id) {
  if (!confirm("Excluir anúncio permanentemente?")) return;
  await AnuncioAPI.excluirAnuncio(id);
  carregarMeusAnuncios();
}

// MENSAGENS
async function carregarMensagens() {
  const inboxBox = document.getElementById("listaInbox");
  const msgBox = document.getElementById("msgInbox");

  msgBox.innerText = "Carregando...";

  const user = JSON.parse(localStorage.getItem("bom_user"));

  const r = await fetch("http://localhost:8080/api/interesses/by-prestador/" + user.login);
  const txt = await r.text();

  let data;
  try { data = JSON.parse(txt); }
  catch {
    msgBox.innerText = "Erro ao carregar mensagens.";
    return;
  }

  msgBox.innerText = data.length ? "" : "Nenhuma mensagem recebida.";
  inboxBox.innerHTML = "";

  data.forEach(msg => {
  inboxBox.insertAdjacentHTML("beforeend", `
    <div class="border rounded p-2">
      <strong>${msg.nome}</strong><br>
      <small>${msg.email} · ${msg.fone ?? "—"}</small><br>
      <div class="mt-2">${msg.mensagem}</div>
      <button class="btn btn-sm btn-outline-danger mt-2" onclick="apagarMsg(${msg.id})">Apagar</button>
    </div>
  `);
});

}

async function apagarMsg(id) {
  await fetch("http://localhost:8080/api/interesses/" + id, { method: "DELETE" });
  carregarMensagens();
}



// EXPOSE FUNCTIONS TO HTML (MODULE FIX)
window.editar = editar;
window.apagar = apagar;
window.apagarMsg = apagarMsg;
