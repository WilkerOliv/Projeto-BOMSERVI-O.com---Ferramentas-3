import { requireAdmin } from "./auth-guard.js";
import { logout } from "./auth.js";

window.addEventListener("DOMContentLoaded", () => {
  requireAdmin();
  document.getElementById("btnSair").onclick = logout;

  carregarCategorias();
  configurarCategoriaForm();
});

// LISTAR
async function carregarCategorias() {
  const lista = document.getElementById("listaCat");
  lista.innerHTML = "";

  const r = await fetch("http://localhost:8080/api/categorias");
  const cats = await r.json();

  lista.innerHTML = cats.map(c => `
    <li class="list-group-item d-flex justify-content-between align-items-center">
      <span id="catNome_${c.id}">${c.nome}</span>

      <div class="d-flex gap-1">
        <button class="btn btn-sm btn-warning" onclick="editarCat(${c.id})">Editar</button>
        <button class="btn btn-sm btn-danger" onclick="excluirCat(${c.id})">Excluir</button>
      </div>
    </li>
  `).join("");
}

window.editarCat = function (id) {
  const span = document.getElementById(`catNome_${id}`);
  const nomeAtual = span.innerText;

  span.outerHTML = `
    <input id="catEdit_${id}" class="form-control form-control-sm" value="${nomeAtual}">
    <button class="btn btn-success btn-sm mt-1" onclick="salvarCat(${id})">Salvar</button>
  `;
};

window.salvarCat = async function (id) {
  const novoNome = document.getElementById(`catEdit_${id}`).value.trim();

  if (!novoNome) return;

  await fetch(`http://localhost:8080/api/categorias/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      "Authorization": "Bearer " + localStorage.getItem("bom_token")
    },
    body: JSON.stringify({ nome: novoNome })
  });

  carregarCategorias();
};

window.excluirCat = async function (id) {
  if (!confirm("Excluir categoria?")) return;

  await fetch(`http://localhost:8080/api/categorias/${id}`, {
    method: "DELETE",
    headers: { "Authorization": "Bearer " + localStorage.getItem("bom_token") }
  });

  carregarCategorias();
};

// ADICIONAR
function configurarCategoriaForm() {
  const form = document.getElementById("frmCat");

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const nome = form.nome.value.trim();
    if (!nome) return;

    await fetch("http://localhost:8080/api/categorias", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + localStorage.getItem("bom_token")
      },
      body: JSON.stringify({ nome })
    });

    form.reset();
    carregarCategorias();
  });
}



// ==============================
//     LISTAR ANÚNCIOS (ADMIN)
// ==============================

window.addEventListener("DOMContentLoaded", () => {
  requireAdmin();
  carregarCategorias();
  configurarCategoriaForm();
  carregarAnuncios(); // <-- AQUI FAZ APARECER
});

// Carregar todos anúncios
async function carregarAnuncios() {
  const box = document.getElementById("listaAn");
  const msg = document.getElementById("msgAn");

  box.innerHTML = "";
  msg.innerText = "Carregando...";

  const r = await fetch("http://localhost:8080/api/anuncios", {
    headers: {
      "Authorization": "Bearer " + localStorage.getItem("bom_token")
    }
  });

  if (!r.ok) {
    msg.innerText = "Erro ao carregar anúncios.";
    return;
  }

  const anuncios = await r.json();

  msg.innerText = anuncios.length
    ? ""
    : "Nenhum anúncio encontrado.";

  anuncios.forEach(a => {
    const foto = (a.fotoList?.length)
      ? `http://localhost:8080/api/fotos/raw/${a.fotoList[0].nomeArq}`
      : "https://via.placeholder.com/400x300?text=Sem+Imagem";

    box.insertAdjacentHTML("beforeend", `
      <div class="card shadow-sm p-2">
        <div class="row g-2">
          <div class="col-4">
            <img src="${foto}" class="img-fluid rounded" style="height:100px; object-fit:cover;">
          </div>
          <div class="col-8">
            <h6 class="mb-1">${a.titulo}</h6>
            <p class="small text-muted mb-1">${a.descricao ?? ""}</p>
            <p class="small mb-1"><strong>Prestador:</strong> ${a.usuario?.nome ?? "--"} (${a.usuario?.login})</p>
            <button class="btn btn-danger btn-sm" onclick="removerAnuncio(${a.id})">
              Excluir anúncio
            </button>
          </div>
        </div>
      </div>
    `);
  });
}

// Remover anúncio
window.removerAnuncio = async function(id) {
  if (!confirm("Excluir este anúncio permanentemente?")) return;

  await fetch(`http://localhost:8080/api/anuncios/${id}`, {
    method: "DELETE",
    headers: {
      "Authorization": "Bearer " + localStorage.getItem("bom_token")
    }
  });

  carregarAnuncios(); // atualiza lista
};
