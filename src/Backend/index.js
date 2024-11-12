// Importações
const express = require("express");

const usuario = require("./usuario");
const calendario = require("./calendario");
const prova = require("./prova");

// Configurações
const app = express();
const port = process.env.PORT || 3000; // Porta do servidor

// Middleware
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Rotas
app.get("/", (req, res) => {
  res.send("Agora foi");
});

// Rotas de Usuário
app.post("/cadastrar", usuario.postCadastrarUsuario);
app.post("/login", usuario.postLogin);
app.get("/usuario", usuario.getverificarToken, usuario.getUsuarioCadastrado);

// Rotas de Calendário
app.post("/cadastrarEvento", calendario.postCadastrarEvento);
app.post("/deletarEvento", calendario.postDeletarEvento);
app.get("/eventosCadastrados", calendario.getEventosCadastrados);

// Rotas de Prova
app.post("/cadastrarPergunta", prova.postCadastrarPergunta);
app.post("/criarProva", prova.postCriarProva);
app.get("/perguntaCadastrada", prova.getPerguntaCadastrada);
app.get("/provaUsuario", prova.getProvaUsuario);
app.delete("/limparTabela", prova.deletarTodasAsProvas);

// Iniciar o servidor
app.listen(port, () => {
  console.log(`Servidor rodando na porta ${port}`);
});
