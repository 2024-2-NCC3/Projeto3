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

public class CadastroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void Entrar(View view) {
        // Instânciamento dos elementos Views do meu Arquivo XML;
        TextInputEditText campoUsuario = findViewById(R.id.textInputEditTextUsuario);
        TextInputEditText campoSenha = findViewById(R.id.textInputEditTextSenha);
        TextInputEditText campoConfirmarSenha = findViewById(R.id.textInputEditTextConfirmarSenha);

        TextView mensagem = findViewById(R.id.textMensagemErro);

        // extrai dos Objetos, recuperando a String que pompões:
        String usuario = campoUsuario.getText().toString();
        String senha = campoSenha.getText().toString();
        String confirmarsenha = campoConfirmarSenha.getText().toString();

        // VALIDAÇÃO ENTRADA ZERADA
        if (TextUtils.isEmpty(usuario) || TextUtils.isEmpty(senha) || TextUtils.isEmpty(confirmarsenha)) {
            mensagem.setText("Os campos usuário ou senha não pode estar vazios.");
            return;
        }
        if (senha.equals(confirmarsenha)) {
            mensagem.setText("Acesso permitido");
        } else {
            mensagem.setText("As senhas se diferem, tente novamente.");
        }
    }

    public void Associar(View view){
        // Instânciamento dos elementos Views do meu Arquivo XML;
        TextInputEditText campoUsuario = findViewById(R.id.textInputEditTextUsuario);
        TextInputEditText campoSenha = findViewById(R.id.textInputEditTextSenha);
        TextInputEditText campoConfirmarSenha = findViewById(R.id.textInputEditTextConfirmarSenha);

        TextView mensagem = findViewById(R.id.textMensagemErro);

        // extrai dos Objetos, recuperando a String que pompões:
        String usuario = campoUsuario.getText().toString();
        String senha = campoSenha.getText().toString();
        String confirmarsenha = campoConfirmarSenha.getText().toString();

        // Validação entrada zerada
        if(TextUtils.isEmpty(usuario) || TextUtils.isEmpty(senha) || TextUtils.isEmpty(confirmarsenha)){
            mensagem.setText("Os campos usuário ou senha não pode estar vazios.");
            return;
        }
        // Validação cadastro senha
        if(senha.equals(confirmarsenha)){
            Intent intent = new Intent(this, ProvaActivity.class);
            startActivity(intent);
        }
        else{
            mensagem.setText("As senhas se diferem, tente novamente.");
        }
    }

}