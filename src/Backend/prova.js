// identificando caminho do database
var sqlite3 = require("sqlite3").verbose();
var caminhoBanco = "BancoDadosAppoef.db";
var banco = new sqlite3.Database(caminhoBanco);

// metodos da Prova
function postCadastrarPergunta(req, res) {
  let Questao = req.body.Questao;
  let idRespA = req.body.idRespA;
  let textRespA = req.body.textRespA;
  let idRespB = req.body.idRespB;
  let textRespB = req.body.textRespB;
  let idRespC = req.body.idRespC;
  let textRespC = req.body.textRespC;
  let RespCorreta = req.body.RespCorreta;

  banco.run(
    `INSERT INTO Prova (Questao, idRespA, textRespA, idRespB, textRespB, idRespC, textRespC, RespCorreta)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
    [
      Questao,
      idRespA,
      textRespA,
      idRespB,
      textRespB,
      idRespC,
      textRespC,
      RespCorreta,
    ],
    function (err) {
      if (err) {
        return res.status(500).send(err.message);
      }
      return res.send("Pergunta cadastrada");
    }
  );
}
function getPerguntasCadastradas(req, res) {
  banco.all(`SELECT * FROM Prova`, [], (err, rows) => {
    if (err) {
      return res.send(err);
    }
    return res.json(rows);
  });
}
function postCriarProva(req, res) {
  let idLogin = req.body.idLogin;
  if (!idLogin) {
    return res.status(400).send({ error: "Login é obrigatório" });
  }
  let sql = `SELECT * FROM Prova ORDER BY random() LIMIT 5;`;
  banco.all(sql, [], (err, rows) => {
    if (err) {
      return res.status(500).send({ error: "Erro ao buscar perguntas" });
    }
    let provas = rows.map((prova) => ({
      idProva: prova.idProva,
      idLogin: idLogin,
    }));

    let insertSql = `INSERT INTO ProvaUsuario (idProva, idLogin)
      VALUES (?, ?);`;

    let insertPromises = provas.map((provaU) => {
      return new Promise((resolve, reject) => {
        banco.run(insertSql, [provaU.idProva, provaU.idLogin], (err) => {
          if (err) {
            reject(err);
          } else {
            resolve();
          }
        });
      });
    });

    // Aqui, aguarde todas as promessas de inserção serem concluídas
    Promise.all(insertPromises)
      .then(() => {
        return res.send({ message: "Prova criada com sucesso" });
      })
      .catch((insertErr) => {
        return res.status(500).send({ error: "Erro ao inserir provas" });
      });
  });
}
function getProvaUsuario(req, res) {
  let idLogin = req.query.idLogin; // Recebe o idLogin da query

  idLogin = 3;

  if (!idLogin) {
    return res.status(400).send({ error: "Login é obrigatório" });
  }
  console.log("Prova id:!" + idLogin);
  // SQL para buscar as provas do usuário
  let sql = `
      SELECT P.idProva, P.Questao, P.idRespA, P.textRespA, P.idRespB, P.textRespB, P.idRespC, P.textRespC, P.RespCorreta
      FROM Prova P
      JOIN ProvaUsuario PU ON P.idProva = PU.idProva
      WHERE PU.idLogin = ?`;

  banco.all(sql, [idLogin], (err, rows) => {
    1;
    if (err) {
      return res.status(500).send({ error: "Erro ao buscar provas" });
    }

    if (rows.length === 0) {
      return res.status(404).send({ message: "Nenhuma prova encontrada" });
    }

    return res.json(rows);
  });
}
// Exportar a função
module.exports = {
  postCadastrarPergunta,
  postCriarProva,
  getPerguntasCadastradas,
  getProvaUsuario,
};
