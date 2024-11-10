package org.appoef.appappoef;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.MessageDigest;
import java.util.Base64;

import android.util.Log;

public class DesCriptoGrafar {
    // Função para descriptografar o texto
    public static String desCriptoGrafar(String dadosCriptografadosBase64, String segredo) {
        try {
            // Decodificar usando android.util.Base64
            byte[] dadosCriptografados = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                dadosCriptografados = Base64.getDecoder().decode(dadosCriptografadosBase64);
            }
            // O IV é os primeiros 16 bytes
            byte[] iv = new byte[16];
            System.arraycopy(dadosCriptografados, 0, iv, 0, iv.length);

            // Os dados criptografados começam após o IV
            byte[] dados = new byte[dadosCriptografados.length - iv.length];
            System.arraycopy(dadosCriptografados, iv.length, dados, 0, dados.length);

            // Deriva a chave do segredo usando SHA-256 e pega os primeiros 16 bytes
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] chaveBytes = digest.digest(segredo.getBytes("UTF-8"));
            byte[] chave = new byte[16];
            System.arraycopy(chaveBytes, 0, chave, 0, chave.length);

            // Configura o AES CBC com a chave e o IV
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(chave, "AES"), ivSpec);

            // Descriptografa os dados
            byte[] resultado = cipher.doFinal(dados);

            // Retorna o texto original
            String textoDescriptografado = new String(resultado, "UTF-8");

            // Imprimir o texto final para depuração
            Log.d("CriptografiaUtil", "Texto descriptografado: " + textoDescriptografado);

            return textoDescriptografado;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("CriptografiaUtil", "Erro ao descriptografar: " + e.getMessage());
            return null;  // Garantir que o método sempre retorne algo
        }
    }
}

