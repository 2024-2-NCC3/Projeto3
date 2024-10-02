package com.example.appappoef;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void Entrar(View view){
        // Instânciamento dos elementos Views do meu Arquivo XML;
        TextInputEditText campoUsuario = findViewById(R.id.textInputEditTextUsuario);
        TextInputEditText campoSenha = findViewById(R.id.textInputEditTextSenha);

        TextView mensagem = findViewById(R.id.textMensagemErro);

        // extrai dos Objetos, recuperando a String que pompões:
        String usuario = campoUsuario.getText().toString();
        String senha = campoSenha.getText().toString();

        // VALIDAÇÃO ENTRADA ZERADA
        if(TextUtils.isEmpty(usuario) || TextUtils.isEmpty(senha)){
            mensagem.setText("Os campos usuário ou senha não pode estar vazios.");
            return;
        }else {
            mensagem.setText("Acesso permitido");
        }
    }

    public void NaoTenhoConta(View view){
        // Instânciamento dos elementos Views do meu Arquivo XML;
        TextInputEditText campoUsuario = findViewById(R.id.textInputEditTextUsuario);
        TextInputEditText campoSenha = findViewById(R.id.textInputEditTextSenha);

        TextView mensagem = findViewById(R.id.textMensagemErro);

        // extrai dos Objetos, recuperando a String que pompões:
        String usuario = campoUsuario.getText().toString();
        String senha = campoSenha.getText().toString();

        Intent intencao = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intencao);
        campoUsuario.setText("");
        campoSenha.setText("");
        mensagem.setText("");
    }
}