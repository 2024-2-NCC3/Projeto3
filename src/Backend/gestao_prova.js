function adicionarPergunta() {
    const pergunta = document.getElementById("pergunta").value;
    const resposta1 = document.getElementById("resposta1").value;
    const resposta2 = document.getElementById("resposta2").value;
    const resposta3 = document.getElementById("resposta3").value;
    const resposta4 = document.getElementById("resposta4").value;
    const correta = document.getElementById("correta").value;

    if (!pergunta || !resposta1 || !resposta2 || !resposta3 || !resposta4) {
        alert("Preencha todos os campos!");
        return;
    }

    const tabela = document.getElementById("tabelaProvas").querySelector("tbody");
    const linha = tabela.insertRow();

    const celulaPergunta = linha.insertCell(0);
    const celulaRespostas = linha.insertCell(1);
    const celulaCorreta = linha.insertCell(2);
    const celulaAcoes = linha.insertCell(3);

    celulaPergunta.textContent = pergunta;
    celulaRespostas.innerHTML = `
        1. ${resposta1}<br>
        2. ${resposta2}<br>
        3. ${resposta3}<br>
        4. ${resposta4}`;
    celulaCorreta.textContent = `Resposta ${correta}`;
    celulaAcoes.innerHTML = `<button onclick="removerLinha(this)">Remover</button>`;

    document.getElementById("formProva").reset();
}

function removerLinha(botao) {
    const linha = botao.closest("tr");
    linha.remove();
}
