// identificando caminho do database
var sqlite3 = require("sqlite3").verbose();
var caminhoBanco = "BancoDadosAppoef.db";
var banco = new sqlite3.Database(caminhoBanco);

// metodos da Prova
function postCadastrarPergunta(req, res) {
  let questao = req.body.Questao;
  let respCorreta = req.body.RespCorreta;
  let textRespA = req.body.textRespA;
  let textRespB = req.body.textRespB;
  let textRespC = req.body.textRespC;

  banco.run(
    `INSERT INTO Prova (questao, respCorreta, textRespA, textRespB, textRespC)
       VALUES (?, ?, ?, ?, ?)`,
    [questao, respCorreta, textRespA, textRespB, textRespC],
    function (err) {
      if (err) {
        return res.status(500).send(err.message);
      }
      return res.send("Pergunta cadastrada");
    }
  );
}
function getPerguntaCadastrada(req, res) {
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

  // Verifica quantas provas já foram criadas para esse idLogin
  let countSql = `SELECT COUNT(*) as total FROM ProvaUsuario WHERE idLogin = ?`;
  banco.get(countSql, [idLogin], (countErr, countRow) => {
    if (countErr) {
      return res.status(500).send({ error: "Erro ao contar provas" });
    }

    // Se já existem 5 ou mais provas, retorna erro
    if (countRow.total >= 5) {
      return res.status(400).send({ error: "Limite de 5 provas atingido" });
    }

    // Seleciona 5 questões aleatórias
    let sql = `SELECT * FROM Prova ORDER BY random() LIMIT 5;`;
    banco.all(sql, [], (err, rows) => {
      if (err) {
        return res.status(500).send({ error: "Erro ao buscar perguntas" });
      }

      // Mapeia as provas selecionadas
      let provas = rows.map((prova) => ({
        idProva: prova.idQuestao,
        idLogin: idLogin,
      }));

      let insertSql = `INSERT INTO ProvaUsuario (idQuestao, idLogin) VALUES (?, ?);`;

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

      // aguardando todas as promessas de inserção serem concluídas
      Promise.all(insertPromises)
        .then(() => {
          // Após a inserção, consultar as provas do usuário
          let selectSql = `SELECT P.idQuestao, P.questao, P.respCorreta, P.textRespA, P.textRespB, P.textRespC
                           FROM Prova P
                           JOIN ProvaUsuario PU ON P.idQuestao = PU.idQuestao
                           WHERE PU.idLogin = ?`;

          banco.all(selectSql, [idLogin], (selectErr, provasCriadas) => {
            if (selectErr) {
              return res
                .status(500)
                .send({ error: "Erro ao buscar provas criadas" });
            }
            return res.json({
              message: "Prova criada com sucesso",
              provas: provasCriadas,
            });
          });
        })
        .catch((insertErr) => {
          return res.status(500).send({ error: "Erro ao inserir provas" });
        });
    });
  });
}
function getProvaUsuario(req, res) {
  let idLogin = 1; // Recebe o idLogin da query

  if (!idLogin) {
    return res.status(400).send({ error: "Login é obrigatório" });
  }
  console.log("Questao id:" + idLogin);

  // SQL para buscar as provas do usuário
  let sql = `
            SELECT P.idQuestao, P.questao, P.respCorreta, P.textRespA, P.textRespB, P.textRespC
            FROM Prova P
            JOIN ProvaUsuario PU ON P.idQuestao = PU.idQuestao
            WHERE PU.idLogin = 1`;

  banco.all(sql, [idLogin], (err, rows) => {
    if (err) {
      console.error("Erro ao buscar provas:", err); // Log do erro
      return res.status(500).send({ error: "Erro ao buscar provas" });
    }

    if (rows.length === 0) {
      return res.status(404).send({ message: "Nenhuma prova encontrada" });
    }

    return res.json(rows);
  });
}
function deletarTodasAsProvas(req, res) {
  const sql = `DELETE FROM Prova`;
  const sqlz = `VACUUM`;

  banco.run(sql, function (err) {
    if (err) {
      return res.status(500).send("Erro ao deletar dados");
    }

    const linhasDeletadas = this.changes;

    banco.run(sqlz, function (err) {
      if (err) {
        return res.status(500).send("Erro ao executar VACUUM");
      }

      res.send({
        message: "Tabela limpa e chave primária reiniciada",
        linhasDeletadas: linhasDeletadas,
      });
    });
  });
}
// Exportar a função
module.exports = {
  postCadastrarPergunta,
  postCriarProva,
  getPerguntaCadastrada,
  getProvaUsuario,
  deletarTodasAsProvas,
};
