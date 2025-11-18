import { getUser, logout } from "./auth.js";
import AnuncioAPI from "./anuncios-api.js";

// MENU
function safe(id) {
  return document.getElementById(id);
}

function show(id) {
  const el = safe(id);
  if (el) el.style.display = "block";
}

function hide(id) {
  const el = safe(id);
  if (el) el.style.display = "none";
}

function atualizarMenu() {
  const user = getUser();

  // Referências seguras
  const loginLink = safe("loginLink");
  const logoutLink = safe("logoutLink");
  const dashboardLink = safe("dashboardLink");
  const btnEntrarR = safe("btnEntrarR");
  const btnCriarR = safe("btnCriarR");
  const atalhoDashboard = safe("atalhoDashboard");

  if (!user) {
    show("loginLink");
    show("btnEntrarR");
    show("btnCriarR");

    hide("logoutLink");
    hide("dashboardLink");
    hide("atalhoDashboard");

    return;
  }

  // Logado
  hide("loginLink");
  hide("btnEntrarR");
  hide("btnCriarR");

  if (logoutLink) {
    logoutLink.style.display = "block";
    logoutLink.onclick = e => { e.preventDefault(); logout(); };
  }

  const isAdmin = Number(user.nivel) === 1;

  if (dashboardLink) {
    dashboardLink.style.display = "block";
    dashboardLink.href = isAdmin ? "admin-dashboard.html" : "dashboard-prestador.html";
  }

  if (atalhoDashboard) {
    atalhoDashboard.style.display = "block";
    atalhoDashboard.href = dashboardLink ? dashboardLink.href : "#";
  }
}


// CATEGORIAS
async function carregarCategorias() {
  const sel = document.querySelector('select[name="cat"]');
  try {
    const r = await fetch("http://localhost:8080/api/categorias");
    const categorias = await r.json();

    categorias.forEach(c => {
      const op = document.createElement("option");
      op.value = c.id;
      op.textContent = c.nome;
      sel.appendChild(op);
    });
  } catch (e) {
    console.warn("Erro ao carregar categorias...");
  }
}

// BUSCA
function configurarBusca() {
  const form = document.getElementById("frmBusca");
  const lista = document.getElementById("listaAnuncios");
  const msg = document.getElementById("msgBusca");

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    lista.innerHTML = "";
    msg.textContent = "Buscando...";

    const q = form.q.value.trim();
    const cat = form.cat.value.trim();

    const r = await AnuncioAPI.buscarAnuncios(q, cat);

    if (!r.ok) {
      msg.textContent = "Erro ao buscar.";
      return;
    }

    msg.textContent = r.data.length ? "" : "Nenhum anúncio encontrado";

    r.data.forEach(a => {
      const foto = a.fotoList?.length ?
        AnuncioAPI.fotoUrl(a.fotoList[0].nomeArq) :
        "https://via.placeholder.com/400x300.png?text=Anuncio";

      lista.insertAdjacentHTML("beforeend", `
        <div class="col">
          <div class="card h-100 shadow-sm anuncio-card" onclick="abrirAnuncio(${a.id})" style="cursor:pointer;">
            <img src="${foto}" class="card-img-top">
            <div class="card-body">
              <h5 class="card-title">${a.titulo}</h5>
              <p class="card-text">${a.descricao ?? ""}</p>
              <div class="small text-secondary">${a.categorias.map(c => c.nome).join(", ")}</div>
            </div>
          </div>
        </div>
      `);
    });

  });
}

window.addEventListener("DOMContentLoaded", () => {
  atualizarMenu();
  carregarCategorias();
  configurarBusca();
  // Botão superior deve acionar a busca real
document.querySelector('a[href="#busca"]').addEventListener("click", (e) => {
  e.preventDefault();

  // rola a página para a área de busca
  document.getElementById("busca").scrollIntoView({ behavior: "smooth" });

  // dispara o submit automaticamente
  setTimeout(() => {
    document.getElementById("frmBusca").dispatchEvent(new Event("submit"));
  }, 350); // espera um pouco pra rolar e só então busca
});

});
