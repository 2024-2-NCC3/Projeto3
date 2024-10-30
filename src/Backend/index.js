var express = require("express"); // carrega o servidor em uma variável
var app = express(); // biblioteca express - variável com a função express
var port = process.env.PORT || 3000; // Porta alterada para 3001

const jwt = require("jsonwebtoken");
const tokenSecret = "T4#P90&876E454VDW##5678Pr$$#";

// conexão web
var bodyParser = require("body-parser");
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.json());

// Banco de dados
//verbose comandos na biblioteca sqlite
var sqlite3 = require("sqlite3").verbose();
var caminhoBanco = "BancoDadosAppoef.db"; //variável recebe o caminho
var banco = new sqlite3.Database(caminhoBanco);

app.get("/", function (req, res) {
  res.send("Agora foi");
});

app.listen(port, () => {
  //comando que permite o node escutar os dados que estão sendo enviados
  console.log("Servidor rodando!" + port);
});

// chama metodos de Calendario
let calendario = require("./calendario");
app.post("/cadastrarEvento", calendario.postCadastrarEvento);
app.post("/deletarEvento", calendario.postDeletarEvento);
app.get("/eventosCadastrados", calendario.getEventosCadastrados);

// chama metodos de prova.js
let prova = require("./prova");
app.post("/cadastrarPergunta", prova.postCadastrarPergunta);
app.post("/criarProva", prova.postCriarProva);
app.get("/perguntaCadastrada", prova.getPerguntasCadastradas);
app.get("/provaUsuario", prova.getProvaUsuario);

// Chamar funcoes de usuario.json
let usuario = require("./usuario");
app.post("/criarLogin", usuario.postCriaLogin);
app.post("/cadastrarUsuario", usuario.postCadastrarUsuario);
app.get("/usuariosCadastrados", usuario.getUsuarioCadastrado);
