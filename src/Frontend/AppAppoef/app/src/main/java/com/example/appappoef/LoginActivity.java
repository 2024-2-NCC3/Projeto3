package com.example.appappoef;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private TextView campoUsuario, campoSenha, mensagem;

    private RequestQueue requestQueue;
    private final String url = "https://z8vpqp-3000.csb.app/criarLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // requisição acesso ao servidor
        requestQueue = Volley.newRequestQueue(this);

        // Instanciar dados
        campoUsuario = findViewById(R.id.textInputEditTextUsuario);
        campoSenha = findViewById(R.id.textInputEditTextSenha);
        mensagem = findViewById(R.id.textMensagemErro);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void Entrar(View view){
        // extrai dos Objetos, recuperando a String que pompões:
        String usuario = campoUsuario.getText().toString();
        String senha = campoSenha.getText().toString();
        // Validar dados
        if (TextUtils.isEmpty(usuario) || TextUtils.isEmpty(senha)) {
            mensagem.setText("Os campos usuário ou senha não podem estar vazios.");

        }}

}