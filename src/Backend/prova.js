require("dotenv").config();

// Caminho do database
var sqlite3 = require("sqlite3").verbose();
var caminhoBanco = "BancoDadosAppoef.db";
var banco = new sqlite3.Database(caminhoBanco);

// Chave secreta para o JWT
const jwt = require("jsonwebtoken");
const tokenSecret = process.env.JWT_SECRET;

const cripto = require("./criptografar");

// Função Cadastro de perguntas
function postCadastrarPergunta(req, res) {
  let questao = req.body.Questao;
  let respCorreta = req.body.RespCorreta;
  let textRespA = req.body.textRespA;
  let textRespB = req.body.textRespB;
  let textRespC = req.body.textRespC;

  // Verifica a informação da questão
  if (!questao || !respCorreta || !textRespA || !textRespB || !textRespC) {
    return res.status(400).send("Todos os campos são obrigatórios");
  }

  // Chave secreta para criptografia (16 bytes para AES-128)
  const segredo = process.env.SEGREDO;

  try {
    // Criptografa os dados
    const questaoCriptografada = cripto.criptoGrafar(questao, segredo);
    const respCorretaCriptografada = cripto.criptoGrafar(respCorreta, segredo);
    const textRespACriptografado = cripto.criptoGrafar(textRespA, segredo);
    const textRespBCriptografado = cripto.criptoGrafar(textRespB, segredo);
    const textRespCCriptografado = cripto.criptoGrafar(textRespC, segredo);

    banco.run(
      // Insere as  informações na tabela Prova no banco de dados
      `INSERT INTO Prova (questao, respCorreta, textRespA, textRespB, textRespC)
          VALUES (?, ?, ?, ?, ?)`,
      [
        questaoCriptografada,
        respCorretaCriptografada,
        textRespACriptografado,
        textRespBCriptografado,
        textRespCCriptografado,
      ],
      function (err) {
        if (err) {
          // Retorno se erro
          return res.status(500).send(err.message);
        }
        // retorno se tudo funcionar
        return res.send("Pergunta cadastrada");
      }
    );
  } catch (err) {
    // Retorno de erro se o Try não funcionar
    return res
      .status(500)
      .send("Erro ao criptografar os dados: " + err.message);
  }
}
function postCriarProva(req, res) {
  let idLogin = req.body.idLogin;

  // Verifica se o idLogin foi enviado
  if (!idLogin) {
    return res.status(400).send({ error: "Login é obrigatório" });
  }

  // Verifica quantas provas já foram criadas para esse idLogin
  let countSql = `SELECT COUNT(*) as total FROM ProvaUsuario WHERE idLogin = ?`;
  banco.get(countSql, [idLogin], (countErr, countRow) => {
    if (countErr) {
      return res.status(500).send({ error: "Erro ao contar provas" });
    }

    // Se já existem 1, retorna erro
    if (countRow.total > 1) {
      return res.status(400).send({ error: "Limite de 1 provas atingido" });
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

      // Prepara a consulta para inserir as provas no banco
      let insertSql = `INSERT INTO ProvaUsuario (idQuestao, idLogin) VALUES (?, ?);`;

      // Cria um array de promessas de inserção
      let insertPromises = provas.map((provaU) => {
        return new Promise((resolve, reject) => {
          banco.run(insertSql, [provaU.idProva, provaU.idLogin], (err) => {
            if (err) {
              reject(err); // Caso algum erro ocorra durante a inserção
            } else {
              resolve(); // Resolve a promessa quando a inserção for bem-sucedida
            }
          });
        });
      });

      // Aguarda todas as promessas de inserção serem resolvidas
      Promise.all(insertPromises)
        .then(() => {
          // Se todas as inserções foram bem-sucedidas, envia a resposta de sucesso
          return res.send({ message: "Provas criadas com sucesso!" });
        })
        .catch((insertErr) => {
          // Se alguma das promessas falhar, envia o erro
          return res.status(500).send({ error: "Erro ao criar provas" });
        });
    });
  });
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
  // Obtém o token do cabeçalho da requisição
  const token = req.headers.authorization?.split(" ")[1];

  if (!token) {
    return res
      .status(401)
      .json({ error: "Token de autenticação é obrigatório" });
  }

  // Verifica se o token é válido
  jwt.verify(token, tokenSecret, (err, decoded) => {
    if (err) {
      return res.status(401).json({ error: "Token inválido ou expirado" });
    }

    // O token foi validado, podemos acessar o idLogin do payload
    const idLogin = decoded.idLogin;

    // Lógica para verificar se o usuário já tem provas criadas
    let countSql = `SELECT COUNT(*) as total FROM ProvaUsuario WHERE idLogin = ?`;
    banco.get(countSql, [idLogin], (countErr, countRow) => {
      if (countErr) {
        return res.status(500).json({ error: "Erro ao contar provas" });
      }

      // Se já existem 1 ou mais provas, retorna erro
      if (countRow.total >= 2) {
        return res.status(400).json({ error: "Limite de 2 prova atingido" });
      }

      // Seleciona 5 questões aleatórias para a prova
      let sql = `SELECT * FROM Prova ORDER BY random() LIMIT 5;`;
      banco.all(sql, [], (err, rows) => {
        if (err) {
          return res.status(500).json({ error: "Erro ao buscar perguntas" });
        }

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

        // Aguardando todas as promessas de inserção
        Promise.all(insertPromises)
          .then(() => {
            // Após a inserção, consultar as provas criadas pelo usuário
            let selectSql = `SELECT P.idQuestao, P.questao, P.respCorreta, P.textRespA, P.textRespB, P.textRespC
                             FROM Prova P
                             JOIN ProvaUsuario PU ON P.idQuestao = PU.idQuestao
                             WHERE PU.idLogin = ?`;

            banco.all(selectSql, [idLogin], (selectErr, provasCriadas) => {
              if (selectErr) {
                return res
                  .status(500)
                  .json({ error: "Erro ao buscar provas criadas" });
              }

              return res.json({
                message: "Prova criada com sucesso",
                provas: provasCriadas,
              });
            });
          })
          .catch((insertErr) => {
            return res.status(500).json({ error: "Erro ao inserir provas" });
          });
      });
    });
  });
}
function getProvaUsuario(req, res) {
  const token = req.headers.authorization?.split(" ")[1]; // Obtém o token do cabeçalho

  if (!token) {
    return res
      .status(401)
      .send({ error: "Token de autenticação é obrigatório" });
  }

  // Verifica o token e obtém o idLogin do payload
  jwt.verify(token, tokenSecret, (err, decoded) => {
    if (err) {
      return res.status(401).send({ error: "Token inválido ou expirado" });
    }

    const idLogin = decoded.idLogin; // Extrai o idLogin do payload do token

    // Verifique se o idLogin foi corretamente extraído
    if (!idLogin) {
      return res.status(400).send({ error: "idLogin não encontrado no token" });
    }

    console.log("Questao id:" + idLogin);

    // SQL para buscar as provas do usuário
    let sql = `
      SELECT P.idQuestao, P.questao, P.respCorreta, P.textRespA, P.textRespB, P.textRespC
      FROM Prova P
      JOIN ProvaUsuario PU ON P.idQuestao = PU.idQuestao
      WHERE PU.idLogin = ?`; // Use '?' para parametrizar a consulta

    banco.all(sql, [idLogin], (err, rows) => {
      if (err) {
        console.error("Erro ao buscar provas:", err); // Log do erro
        return res.status(500).send({ error: "Erro ao buscar provas" });
      }

      if (rows.length === 0) {
        return res.status(404).send({ message: "Nenhuma prova encontrada" });
      }

      return res.json(rows); // Retorna as provas do usuário
    });
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
// Exporta as funções
module.exports = {
  postCadastrarPergunta,
  postCriarProva,
  getPerguntaCadastrada,
  getProvaUsuario,
  deletarTodasAsProvas,
};
