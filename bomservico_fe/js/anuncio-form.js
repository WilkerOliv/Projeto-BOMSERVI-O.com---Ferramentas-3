import { requirePrestador } from "./auth-guard.js";
import AnuncioAPI from "./anuncios-api.js";


// =============================
// MÁSCARA DE HORA AUTOMÁTICA
// =============================
function maskHora(v) {
  v = v.replace(/\D/g, "");
  if (v.length >= 3) {
    v = v.substring(0, 4);
    return v.replace(/(\d{2})(\d{2})/, "$1:$2");
  }
  return v;
}

document.addEventListener("input", (e) => {
  if (e.target.name === "horarioInicio" || e.target.name === "horarioFim") {
    e.target.value = maskHora(e.target.value);
  }
});


requirePrestador();

// =============================
// CONFIGURAÇÃO GERAL
// =============================
const MAX_MB = 5;
const MAX_BYTES = MAX_MB * 1024 * 1024;

// =============================
// PREVIEW DAS NOVAS FOTOS
// =============================
document.getElementById("files").addEventListener("change", e => {
  const box = document.getElementById("preview");
  box.innerHTML = "";

  Array.from(e.target.files).slice(0, 3).forEach(file => {

    if (file.size > MAX_BYTES) {
      showToast(`Arquivo muito grande: ${file.name}`, "error");
      return;
    }

    const reader = new FileReader();
    reader.onload = ev => {
      box.insertAdjacentHTML("beforeend",
        `<img src="${ev.target.result}" class="foto-preview">`
      );
    };
    reader.readAsDataURL(file);
  });
});

// =============================
// CARREGAR CATEGORIAS
// =============================
async function loadCategories() {
  const sel = document.getElementById('cats');
  const resp = await fetch("http://localhost:8080/api/categorias");

  if (!resp.ok) {
    sel.innerHTML = "<option>Erro ao carregar...</option>";
    return;
  }

  const cats = await resp.json();

  sel.innerHTML = cats
    .map(c => `<option value="${c.id}">${c.nome}</option>`)
    .join('');
}

// =============================
// LISTAR FOTOS ANTIGAS
// =============================
function renderFotosAntigas(fotoList = []) {
  const box = document.getElementById("fotosAntigasBox");
  box.innerHTML = "";

  fotoList.forEach(f => {
    box.insertAdjacentHTML("beforeend", `
      <div class="foto-antiga-card border p-2 rounded shadow-sm">
        <img src="${AnuncioAPI.fotoUrl(f.nomeArq)}">
        <button type="button" class="btn btn-sm btn-danger w-100 mt-2"
                onclick="excluirFoto(${f.id})">
          Excluir
        </button>
      </div>
    `);
  });
}

// =============================
// EXCLUIR FOTO ANTIGA
// =============================
window.excluirFoto = async function(idFoto) {
  if (!confirm("Excluir esta foto?")) return;

  const res = await fetch(`http://localhost:8080/api/fotos/${idFoto}`, {
    method: "DELETE",
    headers: {
      "Authorization": "Bearer " + localStorage.getItem("bom_token")
    }
  });

  if (!res.ok) {
    showToast("Erro ao excluir a foto.", "error");
    return;
  }

  showToast("Foto excluída!", "success");
  await loadForEdit(qsParam("id"));
};

// =============================
// CAPTURAR PARAMETRO URL
// =============================
function qsParam(name) {
  return new URLSearchParams(location.search).get(name);
}

// =============================
// CARREGAR ANÚNCIO PARA EDIÇÃO
// =============================
async function loadForEdit(id) {
  if (!id) return;

  const r = await AnuncioAPI.obterAnuncio(id);
  if (!r.ok || !r.data) return;

  const a = r.data;
  const f = document.getElementById("frm");

  f.id.value = a.id;
  f.titulo.value = a.titulo;
  f.descricao.value = a.descricao || "";
  f.diasTrabalho.value = a.diasTrabalho || "";
  f.horarioInicio.value = a.horarioInicio || "";
  f.horarioFim.value = a.horarioFim || "";

  // Marcar categorias
  const sel = document.getElementById("cats");
  Array.from(sel.options).forEach(opt => {
    if (a.categorias?.some(c => c.id == opt.value)) {
      opt.selected = true;
    }
  });

  // Carregar fotos antigas
  renderFotosAntigas(a.fotoList);
}

// =============================
// SUBMIT — SALVAR ANÚNCIO
// =============================
document.getElementById("frm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const f = e.target;
  const id = f.id.value;

  // VALIDAR DIAS DE TRABALHO
  if (!f.diasTrabalho.value) {
    showToast("Selecione um intervalo de dias da semana.", "error");
    return;
  }

  // VALIDAR HORÁRIO
  if (f.horarioInicio.value >= f.horarioFim.value) {
    showToast("Hora inicial deve ser menor que hora final.", "error");
    return;
  }

  const selectedIds = Array.from(
    document.getElementById("cats").selectedOptions
  ).map(opt => Number(opt.value));

  function normalizarHora(h) {
  if (!h) return "";
  h = h.replace(/\D/g, "");
  if (h.length === 4) {
    return h.replace(/(\d{2})(\d{2})/, "$1:$2");
  }
  return h;
}

const payload = {
  titulo: f.titulo.value,
  descricao: f.descricao.value,
  diasTrabalho: f.diasTrabalho.value,
  horarioInicio: normalizarHora(f.horarioInicio.value),
  horarioFim: normalizarHora(f.horarioFim.value),
  categorias: selectedIds
};

  // Criar ou atualizar
  let res;
  if (id)
    res = await AnuncioAPI.atualizarAnuncio(id, payload);
  else
    res = await AnuncioAPI.criarAnuncio(payload);

  if (!res.ok) {
    showToast("Erro ao salvar anúncio.", "error");
    return;
  }

  const anuncio = res.data;

  // =========================
  // Upload novas fotos
  // =========================
  const files = document.getElementById("files").files;

  if (files.length > 0) {
    const toUpload = Array.from(files).slice(0, 3);

    for (const file of toUpload) {
      if (file.size > MAX_BYTES) {
        showToast(`A imagem '${file.name}' é maior que ${MAX_MB}MB.`, "error");
        return;
      }

      await AnuncioAPI.uploadFoto(anuncio.id, file);
    }
  }

  showToast("Anúncio salvo com sucesso!", "success");

  setTimeout(() => {
    location.href = "dashboard-prestador.html";
  }, 800);
});

// =============================
// INICIAR
// =============================
(async () => {
  await loadCategories();
  await loadForEdit(qsParam("id"));
})();
