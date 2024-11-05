package org.appoef.appappoef;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import android.util.Base64;

public class Criptografia {

    // Método para gerar uma chave secreta
    public static SecretKey gerarChave() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // Tamanho da chave
        return keyGenerator.generateKey();
    }

    // Método para criptografar o texto
    public static String criptografar(String texto, SecretKey chave) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, chave);
        byte[] textoCriptografado = cipher.doFinal(texto.getBytes());
        return Base64.encodeToString(textoCriptografado, Base64.DEFAULT);
    }
}
