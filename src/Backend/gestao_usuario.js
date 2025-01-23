function adicionarUsuario() {
    const nomeUsuario = prompt("Digite o nome do novo usuário:");

    if (!nomeUsuario) {
        alert("O nome do usuário não pode estar vazio!");
        return;
    }

    const tabela = document.getElementById("tabelaUsuarios").querySelector("tbody");
    const linha = tabela.insertRow();

    const celulaNome = linha.insertCell(0);
    const celulaCategoria = linha.insertCell(1);
    const celulaAcoes = linha.insertCell(2);

    celulaNome.textContent = nomeUsuario;

    celulaCategoria.innerHTML = `
        <select>
            <option value="Administrador">Administrador</option>
            <option value="Associado">Associado</option>
            <option value="Usuario">Usuário</option>
        </select>
    `;

    celulaAcoes.innerHTML = `
        <button onclick="salvarCategoria(this)">Salvar</button>
        <button onclick="removerUsuario(this)">Remover</button>
    `;
}

function salvarCategoria(botao) {
    const linha = botao.closest("tr");
    const selectCategoria = linha.querySelector("select");
    const categoria = selectCategoria.value;
    alert(`Categoria do usuário foi alterada para: ${categoria}`);
}

function removerUsuario(botao) {
    const linha = botao.closest("tr");
    linha.remove();
    alert("Usuário removido com sucesso!");
}

// Botão para adicionar usuários
const botaoAdicionar = document.createElement("button");
botaoAdicionar.textContent = "Adicionar Usuário";
botaoAdicionar.style.marginTop = "10px";
botaoAdicionar.onclick = adicionarUsuario;

document.querySelector(".container").appendChild(botaoAdicionar);

