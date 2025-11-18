import AnuncioAPI from "./anuncios-api.js";
import { getUser } from "./auth.js";

let modal;
let anuncioAtual = null;

window.addEventListener("DOMContentLoaded", () => {
  modal = new bootstrap.Modal(document.getElementById("modalAnuncio"));
});

window.abrirAnuncio = async function (id) {
  const r = await AnuncioAPI.obterAnuncio(id);

  if (!r.ok || !r.data) return alert("Erro ao abrir anúncio.");

  const a = r.data;
  anuncioAtual = a;

  document.getElementById("mTitulo").innerText = a.titulo;
  document.getElementById("mDescricao").innerText = a.descricao ?? "";
  document.getElementById("mCategorias").innerText = a.categorias.map(c => c.nome).join(", ");
  document.getElementById("mHorario").innerText = `${a.horarioInicio ?? ""} - ${a.horarioFim ?? ""}`;
  document.getElementById("mDias").innerText = a.diasTrabalho ?? "";

  document.getElementById("mEmail").innerText = a.usuario?.email ?? "—";
  document.getElementById("mTelefone").innerText = a.usuario?.telefone ?? "—";

  montarCarrossel(a.fotoList ?? []);

  configurarEnvioMensagem();

  modal.show();
};


function montarCarrossel(fotos) {
  const box = document.getElementById("carouselFotos");

  if (!fotos.length) {
    box.innerHTML = `
      <div class="carousel-inner">
        <div class="carousel-item active">
          <img src="https://via.placeholder.com/800x500?text=Sem+Foto" class="d-block w-100 rounded">
        </div>
      </div>
    `;
    return;
  }

  const indicators = fotos.map((f, i) =>
    `<button type="button" data-bs-target="#carouselFotos" data-bs-slide-to="${i}" ${i === 0 ? "class='active'" : ""}></button>`
  ).join("");

  const items = fotos.map((f, i) =>
    `<div class="carousel-item ${i === 0 ? "active" : ""}">
        <img src="${AnuncioAPI.fotoUrl(f.nomeArq)}" class="d-block w-100 rounded">
     </div>`
  ).join("");

  box.innerHTML = `
    <div class="carousel-indicators">${indicators}</div>
    <div class="carousel-inner">${items}</div>

    <button class="carousel-control-prev" type="button" data-bs-target="#carouselFotos" data-bs-slide="prev">
      <span class="carousel-control-prev-icon"></span>
    </button>

    <button class="carousel-control-next" type="button" data-bs-target="#carouselFotos" data-bs-slide="next">
      <span class="carousel-control-next-icon"></span>
    </button>
  `;
}



function configurarEnvioMensagem() {
  const user = getUser();

  const areaLogado = document.getElementById("mAreaMensagemLogado");
  const areaNao = document.getElementById("mAreaMensagemNaoLogado");

  if (!user) {
    areaLogado.style.display = "none";
    areaNao.style.display = "block";
    return;
  }

  areaLogado.style.display = "block";
  areaNao.style.display = "none";

  document.getElementById("btnEnviarMensagem").onclick = enviarMensagem;
}

async function enviarMensagem() {
  const msgBox = document.getElementById("msgEnvio");
  msgBox.innerText = "";

  const texto = document.getElementById("mMensagem").value.trim();
  if (!texto) {
    msgBox.className = "text-danger";
    msgBox.innerText = "Digite sua mensagem.";
    return;
  }

  const user = getUser();

  const payload = {
    nome: user.nome,
    fone: user.telefone,       // <<< CORREÇÃO AQUI
    email: user.email,
    mensagem: texto,
    anuncio: { id: anuncioAtual.id }   // <<< CONTINUA IGUAL
  };

  const r = await fetch("http://localhost:8080/api/interesses", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });

  if (!r.ok) {
    msgBox.className = "text-danger";
    msgBox.innerText = "Erro ao enviar mensagem.";
    return;
  }

  msgBox.className = "text-success";
  msgBox.innerText = "Mensagem enviada com sucesso!";
  document.getElementById("mMensagem").value = "";
}
