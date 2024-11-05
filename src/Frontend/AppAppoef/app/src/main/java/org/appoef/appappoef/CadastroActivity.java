package org.appoef.appappoef;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class CadastroActivity extends AppCompatActivity {

    private String emojierro;
    private TextView campoNomeUsuario, campoUsuario, campoSenha, campoConfirmarSenha, mensagem;
    private RequestQueue requestQueue;
    private final String url = "https://h4592k-3000.csb.app/cadastrar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        emojierro = getString(R.string.emojierror);
        campoNomeUsuario = findViewById(R.id.textInputEditNomeUsuario);
        campoUsuario = findViewById(R.id.textInputEditEmail);
        campoSenha = findViewById(R.id.textInputEditCriarSenha);
        campoConfirmarSenha = findViewById(R.id.textInputEditConfirmarSenha);
        mensagem = findViewById(R.id.textMensagemErro);
        requestQueue = Volley.newRequestQueue(this);
    }

    public void Entrar(View view) {
        String usuario = campoUsuario.getText().toString().trim();
        String senha = campoSenha.getText().toString().trim();
        String confirmarsenha = campoConfirmarSenha.getText().toString().trim();

        String erroMensagem = validarEntradas(usuario, senha, confirmarsenha);
        if (erroMensagem != null) {
            mensagem.setText(erroMensagem);
            return;
        }

        cadastrarUsuario(usuario, senha);
    }

    private String validarEntradas(String usuario, String senha, String confirmarsenha) {
        if (TextUtils.isEmpty(usuario) || TextUtils.isEmpty(senha) || TextUtils.isEmpty(confirmarsenha)) {
            return "Os campos usuário ou senha não podem estar vazios.";
        }
        if (!isValidEmail(usuario)) {
            return "E-mail fornecido é inválido.";
        }
        if (senha.length() < 6) {
            return emojierro + " É necessário que a senha contenha pelo menos 6 caracteres.";
        }
        if (!senha.matches(".*[!@#&*$/;~^+_-].*")) {
            return emojierro + " É necessário que a senha contenha um caractere especial.";
        }
        if (!senha.matches(".*[A-Z].*")) {
            return emojierro + " É necessário que a senha contenha uma letra maiúscula.";
        }
        if (!senha.matches(".*[a-z].*")) {
            return emojierro + " É necessário que a senha contenha uma letra minúscula.";
        }
        if (!senha.matches(".*[0-9].*")) {
            return emojierro + " É necessário que a senha contenha um número.";
        }
        if (!senha.equals(confirmarsenha)) {
            return "As senhas não são iguais! Tente novamente.";
        }
        return null; // sem erros
    }

    private boolean isValidEmail(String usuario) {
        return usuario != null && Patterns.EMAIL_ADDRESS.matcher(usuario).matches();
    }

    public void cadastrarUsuario(String usuario, String senha) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("usuario", usuario);
            obj.put("senha", senha); // Enviando a senha criptografada

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                    response -> {
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, LoginActivity.class));
                                finish();
                            } else {
                                String mensagemErro = response.getString("message");
                                mensagem.setText(mensagemErro);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Erro ao processar resposta do servidor", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String errorMessage = new String(error.networkResponse.data);
                            Log.e("Cadastro", "Erro ao cadastrar: " + errorMessage);
                            mensagem.setText("Erro ao cadastrar: " + errorMessage);
                        } else {
                            Toast.makeText(this, "Erro de rede. Verifique sua conexão.", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao criptografar a senha", Toast.LENGTH_SHORT).show();
        }
    }
}
