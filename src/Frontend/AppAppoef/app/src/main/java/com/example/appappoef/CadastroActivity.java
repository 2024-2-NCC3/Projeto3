package com.example.appappoef;


import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class CadastroActivity extends AppCompatActivity {
    // Variáveis de instância
    private TextInputEditText campoUsuario, campoSenha, campoConfirmarSenha;
    private TextView validQtdCarac, validCaracEspecial, validLetraMaius, validLetraMinusc, validNum, mensagem;
    private RequestQueue requestQueue;
    private String emojierro;
    private String url = "https://7nzcxx-3000.csb.app/criarLogin";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        // Inicialização dos campos
        campoUsuario = findViewById(R.id.textInputEditTextUsuario);
        campoSenha = findViewById(R.id.textInputEditTextSenha);
        campoConfirmarSenha = findViewById(R.id.textInputEditTextConfirmarSenha);
        validQtdCarac = findViewById(R.id.textValidQtdCarac);
        validCaracEspecial = findViewById(R.id.textValidCaracEspecial);
        validLetraMaius = findViewById(R.id.textValidLetraMaius);
        validLetraMinusc = findViewById(R.id.textValidLetraMinusc);
        validNum = findViewById(R.id.textValidNum);
        mensagem = findViewById(R.id.textMensagemErro);
        emojierro = getString(R.string.emojierror);

        // Inicialização do RequestQueue para requisições
        requestQueue = Volley.newRequestQueue(this);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void Entrar(View view) {
        // Extraindo valores dos campos
        String usuario = campoUsuario.getText().toString();
        String senha = campoSenha.getText().toString();
        String confirmarsenha = campoConfirmarSenha.getText().toString();

        // Validações dos campos
        if (TextUtils.isEmpty(usuario) || TextUtils.isEmpty(senha)) {
            mensagem.setText("Os campos de usuário e senha não podem estar vazios.");
            return;
        }
        if (!isValidEmail(usuario)) {
            mensagem.setText("E-mail fornecido é inválido.");
            return;
        }
        if (senha.length() < 6) {
            validQtdCarac.setText(emojierro + " É necessário que a senha contenha 6 dígitos.");
            return;
        }
        if (!senha.matches(".*[!@#&*$/;~^+_-].*")) {
            validCaracEspecial.setText(emojierro + " É necessário que a senha contenha um caractere especial.");
            return;
        }
        if (!senha.matches(".*[A-Z].*")) {
            validLetraMaius.setText(emojierro + " É necessário que a senha contenha uma letra maiúscula.");
            return;
        }
        if (!senha.matches(".*[a-z].*")) {
            validLetraMinusc.setText(emojierro + " É necessário que a senha contenha uma letra minúscula.");
            return;
        }
        if (!senha.matches(".*[0-9].*")) {
            validNum.setText(emojierro + " É necessário que a senha contenha um número.");
            return;
        }
        if (!senha.equals(confirmarsenha)) {
            mensagem.setText("As senhas se diferem, tente novamente.");
            return;
        }

        CriarLogin(usuario, senha);

        // volta para login depois que o usuario é cadastrado
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    //  criar o login
    public void CriarLogin(String usuario, String senha) {

        JSONObject obj = new JSONObject();
        try {
            obj.put("usuario",usuario);
            obj.put("senha", senha);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao criar o JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                response -> {
                    // Processar resposta de sucesso
                    Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Processar erro
                    Toast.makeText(this, "Erro ao cadastrar. Tente novamente.", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }

    // validar o e-mail
    private boolean isValidEmail(String usuario) {
        return usuario != null && android.util.Patterns.EMAIL_ADDRESS.matcher(usuario).matches();
    }
}
