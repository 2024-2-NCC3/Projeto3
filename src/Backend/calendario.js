// identificando caminho do database
var sqlite3 = require("sqlite3").verbose();
var caminhoBanco = "BancoDadosAppoef.db";
var banco = new sqlite3.Database(caminhoBanco);

// Cadastrar evento
function postCadastrarEvento(req, res) {
  let tituloCalendario = req.body.tituloCalendario;
  let descricaoCalendario = req.body.descricaoCalendario;
  let dataCalendario = req.body.dataCalendario;

  banco.run(
    `INSERT INTO Calendario (tituloCalendario, descricaoCalendario, dataCalendario)
     VALUES (?, ?, ?)`,
    [tituloCalendario, descricaoCalendario, dataCalendario],
    function (err) {
      if (err) {
        return res.status(500).send(err.message);
      }
      return res.send("Evento Cadastrado");
    }
  );
}

// Deletar evento por data
function postDeletarEvento(req, res) {
  let dataCalendario = req.body.dataCalendario;

  banco.run(
    `DELETE FROM Calendario WHERE dataCalendario = ?`,
    [dataCalendario],
    function (err) {
      if (err) {
        return res.status(500).send(err.message);
      }
      return res.send("Evento deletado");
    }
  );
}

// Visualizar eventos cadastrados por data ou todos os eventos se data não fornecida
function getEventosCadastrados(req, res) {
  let dataCalendario = req.query.dataCalendario;
  let query = `SELECT * FROM Calendario`;
  let params = [];

  // Ajusta a consulta caso uma data específica tenha sido fornecida
  if (dataCalendario) {
    query += ` WHERE dataCalendario = ?`;
    params.push(dataCalendario);
  }

  banco.all(query, params, (err, rows) => {
    if (err) {
      return res.status(500).send("Erro ao buscar eventos");
    }
    return res.json(rows); // Enviar resposta como JSON
  });
}

// Exportar a função
module.exports = {
  postCadastrarEvento,
  postDeletarEvento,
  getEventosCadastrados,
};