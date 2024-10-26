var express = require("express"); // carrega o servidor em uma variável
var app = express(); // biblioteca express - variável com a função express
var port = process.env.PORT || 3000; // Porta alterada para 3001
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

// chama metodos de prova.js
let prova = require("./prova");
app.post("/cadastrarPergunta", prova.postCadastrarPergunta);
app.post("/criarProva", prova.postCriarProva);
app.get("/perguntaCadastrada", prova.getPerguntasCadastradas);
app.get("/provaUsuario", prova.getProvaUsuario);

// Criar login
app.post("/criarLogin", function (req, res) {
  let usuario = req.body.usuario;
  let senha = req.body.senha;

  banco.all(
    `INSERT INTO Login (usuario, senha)
     VALUES (?, ?)`,
    [usuario, senha],
    function (err) {
      if (err) {
        res.send(err);
      }
      res.send("Usuario cadastrado");
    }
  );
});
// Visualizar cadastros
app.get("/usuariosCadastrados", function (req, res) {
  banco.all(`SELECT * FROM Login`, [], (err, rows) => {
    if (err) {
      res.send(err);
    }
    res.send(rows);
  });
});
// deletar informações
app.delete("/deletar", function (req, res) {
  banco.all(`DELETE FROM Login`, [], (err, rows) => {
    if (err) {
      res.send(err);
    }
    res.send(rows);
  });
});
