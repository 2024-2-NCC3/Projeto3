function adicionarEvento() {
    const titulo = document.getElementById("tituloEvento").value;
    const data = document.getElementById("dataEvento").value;
    const descricao = document.getElementById("descricaoEvento").value;

    if (!titulo || !data || !descricao) {
        alert("Preencha todos os campos!");
        return;
    }

    const tabela = document.getElementById("tabelaEventos").querySelector("tbody");
    const linha = tabela.insertRow();

    const celulaTitulo = linha.insertCell(0);
    const celulaData = linha.insertCell(1);
    const celulaDescricao = linha.insertCell(2);

    celulaTitulo.textContent = titulo;
    celulaData.textContent = data;
    celulaDescricao.textContent = descricao;

    document.getElementById("formEvento").reset();
}
