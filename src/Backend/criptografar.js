require("dotenv").config(); // Para usar variáveis de ambiente

// Importar o módulo 'crypto' do Node.js
const crypto = require("crypto");

// Função para criptografar dados com AES CBC
function criptoGrafar(dados, segredo) {
  // Gera um IV aleatório de 16 bytes
  const iv = crypto.randomBytes(16);

  // Cria um hash SHA-256 da chave e pega os primeiros 16 bytes
  let chave = crypto.createHash("sha256").update(segredo, "utf-8").digest();
  chave = chave.slice(0, 16); // Pega os primeiros 16 bytes do hash

  // Cria o cipher AES com CBC e PKCS5Padding
  const cifra = crypto.createCipheriv("aes-128-cbc", chave, iv);

  // Criptografa os dados
  let dadosCriptografados = cifra.update(dados, "utf-8");
  dadosCriptografados = Buffer.concat([dadosCriptografados, cifra.final()]);

  // Concatena o IV com os dados criptografados (base64)
  const resultadoFinal = Buffer.concat([iv, dadosCriptografados]);

  // Retorna o resultado concatenado como uma string base64
  return resultadoFinal.toString("base64");
}

// Exporta a função
module.exports = { criptoGrafar };
