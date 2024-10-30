package com.example.appappoef;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    private final String url = "https://h4592k-3000.csb.app/cadastrarUsuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);

        emojierro = getString(R.string.emojierror);
        campoNomeUsuario = findViewById(R.id.textInputEditNomeUsuario);
        campoUsuario = findViewById(R.id.textInputEditEmail);
        campoSenha = findViewById(R.id.textInputEditCriarSenha);
        campoConfirmarSenha = findViewById(R.id.textInputEditConfirmarSenha);
        mensagem = findViewById(R.id.textMensagemErro);
        requestQueue = Volley.newRequestQueue(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void Entrar(View view) {
        // extrai dos Objetos, recuperando a String que pompões:
        String usuario = campoUsuario.getText().toString();
        String senha = campoSenha.getText().toString();
        String confirmarsenha = campoConfirmarSenha.getText().toString();
        // VALIDAÇÃO ENTRADA ZERADA
        if (TextUtils.isEmpty(usuario) || TextUtils.isEmpty(senha) || TextUtils.isEmpty(confirmarsenha)) {
            mensagem.setText("Os campos usuário ou senha não pode estar vazios.");
            return;
        }
        if(!isValidEmail(usuario)) {
            mensagem.setText("E-mail fornecido é inválido.");
            return;
        }
        if(senha.length() < 6  ){
           mensagem.setText(emojierro + " É necessário que a senha contenha 6 dígitos.");
            return;
        }
        if(!senha.matches(".*[!@#&*$/;~^+_-].*")){
            mensagem.setText(emojierro + " É necessário que a senha contenha um caractere especial.");
            return;
        }
        if(!senha.matches(".*[A-Z].*")){
            mensagem.setText(emojierro +" É necessário que a senha contenha uma letra maiúscula.");
            return;
        }
        if(!senha.matches(".*[a-z].*")){
           mensagem.setText(emojierro + " É necessário que a senha contenha uma letra minúscula.");
            return;
        }
        if(!senha.matches(".*[0-9].*")){
           mensagem.setText(emojierro +" É necessário que a senha contenha um número.");
            return;
        }
        if (!senha.equals(confirmarsenha)) {
            mensagem.setText("As senhas não são iguais! Tente novamente.");
            return;
        }
        CriarLogin();
        Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    // criaçaõ de elementos no layout para acompnhar em tempo real a verificação do usuario e senha
    private boolean isValidEmail(String usuario) {
        return usuario != null && Patterns.EMAIL_ADDRESS.matcher(usuario).matches();
    }
    public void CriarLogin() {
        // extrai dos Objetos, recuperando a String que pompões:
        String usuario = campoUsuario.getText().toString();
        String senha = campoSenha.getText().toString();
        // criando Json para enviar dados
        JSONObject obj = new JSONObject();
        try{
            obj.put("usuario", usuario);
            obj.put("senha", senha);
        } catch(JSONException e){
            e.printStackTrace();
            Toast.makeText(this, "Erro ao criar O JSON " , Toast.LENGTH_SHORT).show();
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, obj, response -> {
            Toast.makeText(CadastroActivity.this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
        },
                error -> {
                    if(error.networkResponse != null){
                        Log.e("Volley", "Erro na requisição: " + new String(error.networkResponse.data));
                    }
                }
        );
        // adicionar requisição a fila
        requestQueue.add(jsonObjectRequest);
    }
}