require("dotenv").config();

var sqlite3 = require("sqlite3").verbose();
var caminhoBanco = "BancoDadosAppoef.db"; //variável recebe o caminho
var banco = new sqlite3.Database(caminhoBanco);

const bcrypt = require("bcryptjs");

const jwt = require("jsonwebtoken");

// Definindo uma chave secreta para o JWT
const tokenSecret = process.env.JWT_SECRET;

function postLogin(req, res) {
  const usuario = req.body.usuario;
  const senha = req.body.senha;

  console.log("Dados recebidos");

  if (!usuario || !senha) {
    return res.status(400).json({
      success: false,
      message: "Usuário e senha são obrigatórios.",
    });
  }

  console.log("Tentando logar com:", usuario);

  banco.get(
    `SELECT * FROM Login WHERE usuario = ?`,
    [usuario],
    function (err, row) {
      if (err) {
        console.error("Erro ao buscar usuário:", err);
        return res.status(500).json({
          success: false,
          message: "Erro ao verificar usuário.",
        });
      }

      if (!row) {
        return res.status(401).json({
          success: false,
          message: "Credenciais inválidas.",
        });
      }

      if (!bcrypt.compareSync(senha, row.senha)) {
        return res.status(401).json({
          success: false,
          message: "Credenciais inválidas.",
        });
      }

      const payload = { idLogin: row.idLogin, usuario: row.usuario };
      const token = jwt.sign(payload, tokenSecret, { expiresIn: "24h" });

      
      return res.status(200).json({
        success: true,
        message: "Login realizado com sucesso",
        token: token,
        idLogin: row.idLogin,
        nome: row.nome, // Adiciona o nome do usuário
        usuario: row.usuario // Adiciona o email do usuário
      });
    }
  );
}
function getverificarToken(req, res, next) {
  const token = req.headers["authorization"];

  if (!token) {
    return res
      .status(401)
      .json({ success: false, message: "Token não fornecido." });
  }

  jwt.verify(token, tokenSecret, (err, decoded) => {
    if (err) {
      return res
        .status(401)
        .json({ success: false, message: "Token inválido." });
    }
    // Salva os dados decodificados do token na requisição
    req.user = decoded;
    next();
  });
}
// Função para cadastrar um usuário
function postCadastrarUsuario(req, res) {
  const nome = req.body.nome; // Captura o nome enviado no cadastro
  const usuario = req.body.usuario;
  const senha = req.body.senha;

  if (!nome || !usuario || !senha) {
    return res.status(400).json({
      success: false,
      message: "Nome, usuário e senha são obrigatórios.",
    });
  }

  banco.get(
    `SELECT * FROM Login WHERE usuario = ?`,
    [usuario],
    function (err, row) {
      if (err) {
        console.log("Erro ao verificar usuário:", err);
        return res.status(500).json({
          success: false,
          message: "Erro na verificação do usuário.",
        });
      }

      if (row) {
        return res.status(400).json({
          success: false,
          message: "Usuário já existe.",
        });
      }

      const hashedPassword = bcrypt.hashSync(senha, 10);
      console.log("Senha criptografada:", hashedPassword);

      banco.run(
        `INSERT INTO Login (nome, usuario, senha) VALUES (?, ?, ?)`,
        [nome, usuario, hashedPassword],
        function (err) {
          if (err) {
            console.log("Erro ao inserir usuário no banco de dados:", err);
            return res.status(500).json({
              success: false,
              message: "Erro ao criar usuário.",
            });
          } else {
            console.log("Usuário cadastrado com sucesso! ID:", this.lastID);
            res.status(201).json({
              success: true,
              message: "Usuário cadastrado com sucesso!",
              idLogin: this.lastID
            });
          }
        }
      );
    }
  );
}


function getUsuarioCadastrado(req, res) {
  banco.all(`SELECT * FROM Login`, [], (err, rows) => {
    if (err) {
      console.error("Erro ao buscar usuárinos:", err);
      return res
        .status(500)
        .json({ success: false, message: "Erro ao buscar usuários." });
    }
    return res.status(200).json({ success: true, users: rows });
  });
}

// Exportar a função
module.exports = {
  postLogin,
  postCadastrarUsuario,
  getUsuarioCadastrado,
  getverificarToken,
};
