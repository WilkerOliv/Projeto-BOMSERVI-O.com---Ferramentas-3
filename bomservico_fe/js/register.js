const API = "http://localhost:8080";

window.addEventListener("DOMContentLoaded", () => {
  document.getElementById("frm").addEventListener("submit", registrar);
});

// -------- CONVERTER dd/mm/yyyy → yyyy-mm-dd --------
function paraISO(dataBr) {
  const [d, m, a] = dataBr.split("/");
  return `${a}-${m}-${d}`;
}

// -------- CALCULAR IDADE --------
function calcularIdade(dateStringBr) {
  const [d, m, a] = dateStringBr.split("/");
  const nascimento = new Date(`${a}-${m}-${d}`);
  const hoje = new Date();

  let idade = hoje.getFullYear() - nascimento.getFullYear();
  const diffMes = hoje.getMonth() - nascimento.getMonth();

  if (diffMes < 0 || (diffMes === 0 && hoje.getDate() < nascimento.getDate())) {
    idade--;
  }
  return idade;
}

async function registrar(e) {
  e.preventDefault();
  const f = e.target;

  // VALIDAR DATA
  const dataBr = f.dataNasc.value;
  const idade = calcularIdade(dataBr);

  if (idade < 18) {
    showToast("Você deve ter pelo menos 18 anos.", "error");
    return;
  }

  // converter formato antes de enviar ao backend
  const dataISO = paraISO(dataBr);

  const data = {
    login: f.login.value,
    senha: f.senha.value,
    nome: f.nome.value,
    cpf: f.cpf.value,
    telefone: f.telefone.value,
    email: f.email.value,
    endereco: f.endereco.value,
    dtNasc: dataISO, // <- AQUI AGORA ESTÁ CERTO
    nivel: 0
  };

  const r = await fetch(API + "/api/auth/registro", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  });

  const txt = await r.text();
  let response;

  try { response = JSON.parse(txt); }
  catch { showToast("Erro inesperado.", "error"); return; }

  if (!r.ok || response.error) {
    showToast(response.error || "Erro ao registrar.", "error");
    return;
  }

  showToast("Cadastro realizado com sucesso!", "success");

  setTimeout(() => window.location.href = "login.html", 800);
}
