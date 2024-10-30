var sqlite3 = require("sqlite3").verbose();
var caminhoBanco = "BancoDadosAppoef.db"; //variável recebe o caminho
var banco = new sqlite3.Database(caminhoBanco);

const jwt = require("jsonwebtoken");
const tokenSecret = "T4#P90&876E454VDW##5678Pr$$#";

// Rota para login de um usuário existente
function postCriaLogin(req, res) {
  let usuario = req.body.usuario;
  let senha = req.body.senha;

  if (!usuario || !senha) {
    return res.status(400).json({
      success: false,
      message: "Usuário e senha são obrigatórios.",
    });
  }

  // verificando se ta tudo certo no usuario e senha
  banco.get(
    "SELECT * FROM Login WHERE usuario = ? AND senha = ?",
    [usuario, senha],
    function (err, row) {
      if (err) {
        return res.status(500).json({
          success: false,
          message: "Erro ao verificar usuário.",
        });
      }

      if (!row) {
        // Se o usuário não existe ou a senha está incorreta
        return res.status(400).json({
          success: false,
          message: "usuario e senha incorretos!",
        });
      }

      // Se o usuário existe e a senha está correta, cria o token
      const payload = { idLogin: row.idLogin, usuario: row.usuario };
      const token = jwt.sign(payload, tokenSecret, { expiresIn: "1h" });

      // Retorna sucesso com token
      return res.status(200).json({
        success: true,
        message: "Login bem-sucedido",
        token: token,
        idLogin: row.idLogin,
      });
    }
  );
}
// Rota para cadastrar um novo usuário
function postCadastrarUsuario(req, res) {
  let usuario = req.body.usuario;
  let senha = req.body.senha;

  if (!usuario || !senha) {
    return res.status(400).json({
      success: false,
      message: "erro",
    });
  }

  // Verifica se o usuário já existe no banco de dados
  banco.get(
    "SELECT * FROM Login WHERE usuario = ?",
    [usuario],
    function (err, row) {
      if (err) {
        return res.status(500).json({
          success: false,
          message: "erro na verificação",
        });
      }

      if (row) {
        // Se o usuário já existe, retorna um erro
        return res.status(400).json({
          success: false,
          message: "Usuário já existe.",
        });
      }

      // Se o usuário não existe, cria um novo
      banco.run(
        `INSERT INTO Login (usuario, senha) VALUES (?, ?)`,
        [usuario, senha],
        function (err) {
          if (err) {
            return res.status(500).json({
              success: false,
              message: "Erro ao criar usuário.",
            });
          }

          // Pega o ID do usuário recém inserido
          const idLogin = this.lastID;

          // Cria o token com o ID do usuário
          const payload = { idLogin: idLogin, usuario: usuario };
          const token = jwt.sign(payload, tokenSecret, { expiresIn: "1h" });

          // Retorna sucesso com token
          return res.status(200).json({
            success: true,
            message: "Usuário criado com sucesso",
            token: token,
            idLogin: idLogin,
          });
        }
      );
    }
  );
}
function getUsuarioCadastrado(req, res) {
  banco.all(`SELECT * FROM Login`, [], (err, rows) => {
    if (err) {
      res.send(err);
    }
    res.send(rows);
  });
}

// Exportar a função
module.exports = {
  postCriaLogin,
  postCadastrarUsuario,
  getUsuarioCadastrado,
};
