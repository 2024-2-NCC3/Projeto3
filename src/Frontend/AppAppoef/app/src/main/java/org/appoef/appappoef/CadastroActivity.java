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
    private final String url = "https://3756jq-3000.csb.app/cadastrar";

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
        String nome = campoNomeUsuario.getText().toString().trim();
        String usuario = campoUsuario.getText().toString().trim();
        String senha = campoSenha.getText().toString().trim();
        String confirmarsenha = campoConfirmarSenha.getText().toString().trim();

        String erroMensagem = validarEntradas(nome, usuario, senha, confirmarsenha);
        if (erroMensagem != null) {
            mensagem.setText(erroMensagem);
            return;
        }

        cadastrarUsuario(nome, usuario, senha);
    }

    private String validarEntradas(String nome, String usuario, String senha, String confirmarsenha) {
        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(usuario) || TextUtils.isEmpty(senha) || TextUtils.isEmpty(confirmarsenha)) {
            return "Por favor, preencha todos os campos.";
        }
        if (!isValidEmail(usuario)) {
            return "O e-mail fornecido não é válido.";
        }
        if (senha.length() < 6) {
            return emojierro + " A senha deve ter pelo menos 6 caracteres.";
        }
        if (!senha.matches(".*[!@#&*$/;~^+_-].*")) {
            return emojierro + " A senha deve conter pelo menos um caractere especial.";
        }
        if (!senha.matches(".*[A-Z].*")) {
            return emojierro + " A senha deve conter pelo menos uma letra maiúscula.";
        }
        if (!senha.matches(".*[a-z].*")) {
            return emojierro + " A senha deve conter pelo menos uma letra minúscula.";
        }
        if (!senha.matches(".*[0-9].*")) {
            return emojierro + " A senha deve conter pelo menos um número.";
        }
        if (!senha.equals(confirmarsenha)) {
            return "As senhas não coincidem. Tente novamente.";
        }
        return null; // sem erros
    }

    private boolean isValidEmail(String usuario) {
        return usuario != null && Patterns.EMAIL_ADDRESS.matcher(usuario).matches();
    }

    public void cadastrarUsuario(String nome, String usuario, String senha) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("nome", nome); // Enviar o nome do usuário
            obj.put("usuario", usuario);
            obj.put("senha", senha); // Enviar a senha

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                    response -> {
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Não foi possível realizar o Cadastro! Tente novamente", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Mensagem amigável para o usuário
                            Toast.makeText(this, "Ocorreu um erro inesperado. Por favor, tente novamente.", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        // Mensagem amigável para erro de rede
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String errorMessage = new String(error.networkResponse.data);
                            Log.e("Cadastro", "Erro ao cadastrar");
                            mensagem.setText("Não foi possível completar o cadastro. Verifique sua conexão com a internet.");
                        } else {
                            Toast.makeText(this, "Erro de rede. Por favor, verifique sua conexão e tente novamente.", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ocorreu um erro ao processar seus dados. Por favor, tente novamente.", Toast.LENGTH_SHORT).show();
        }
    }
}