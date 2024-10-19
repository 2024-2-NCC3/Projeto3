var express = require("express"); // carrega o servidor em uma variável
var app = express(); // biblioteca express - variável com a função express

var port = process.env.PORT || 3000; // Porta alterada para 3001
// conexão web

app.get("/", function (req, res) {
  res.send("Agora foi");
});

var bodyParser = require("body-parser");

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.json());

// Banco de dados
//verbose comandos na biblioteca sqlite
var sqlite3 = require("sqlite3").verbose();
var caminhoBanco = "PiProva.db"; //variável recebe o caminho
var banco = new sqlite3.Database(caminhoBanco);

// site para abrir postman.com
app.post("/criarQuestionario", function (req, res) {
  let pergunta = req.body.pergunta;
  let opcao_a = req.body.opcao_a;
  let opcao_b = req.body.opcao_b;
  let opcao_c = req.body.opcao_c;
  let opcao_d = req.body.opcao_d;
  let resp_correta = req.body.resp_correta;

  banco.run(
    `INSERT INTO Questionario (pergunta, opcao_a, opcao_b, opcao_c, opcao_d, Resp_correta) VALUES (?, ?, ?, ?, ?, ?)`,
    [pergunta, opcao_a, opcao_b, opcao_c, opcao_d, resp_correta],
    function (err) {
      if (err) {
        res.send(err);
        return;
      }
      res.send("Pergunta cadastrada");
    }
  );
});

// (err, rows) => função temporária
app.get("/tudo", function (req, res) {
  banco.all(`SELECT * FROM Questionario`, [], (err, rows) => {
    if (err) {
      res.send(err);
      return;
    }
    res.send(rows);
  });
});

app.listen(port, () => {
  //comando que permite o node escutar os dados que estão sendo enviados
  console.log("Servidor rodando na porta " + port);
});
