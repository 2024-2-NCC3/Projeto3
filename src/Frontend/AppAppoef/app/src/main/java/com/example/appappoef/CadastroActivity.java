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

public class CadastroActivity extends AppCompatActivity implements LoginActivity.ILoginActivity {
    private String emojierro;
    private TextView campoUsuario, campoSenha, mensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);

        emojierro = getString(R.string.emojierror);
        campoUsuario = findViewById(R.id.textInputEditTextUsuario);
        campoSenha = findViewById(R.id.textInputEditTextSenha);
        mensagem = findViewById(R.id.textMensagemErro);

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

        if(!isValidEmail(usuario)) {
            mensagem.setText("E-mail fornecido é inválido.");
            return;
        }
        if(senha.length() < 6  ){

            validQtdCarac.setText(emojierro + " É necessário que a senha contenha 6 dígitos.");
            LimparCampos();
            return;
        }
        if(!senha.matches(".*[!@#&*$/;~^+_-].*")){
            validCaracEspecial.setText(emojierro + " É necessário que a senha contenha um caractere especial.");
            LimparCampos();
            return;
        }
        if(!senha.matches(".*[A-Z].*")){
            validLetraMaius.setText(emojierro +" É necessário que a senha contenha uma letra maiúscula.");
            LimparCampos();
            return;
        }
        if(!senha.matches(".*[a-z].*")){
            validLetraMinusc.setText(emojierro + " É necessário que a senha contenha uma letra minúscula.");
            return;
        }
        if(!senha.matches(".*[0-9].*")){
            validNum.setText(emojierro +" É necessário que a senha contenha um número.");
            return;
        }
        CriarLogin();
        Intent intent = new Intent(this, PrincipalActivity.class);
        startActivity(intent);
        LimparCampos();
    }
    // criaçaõ de elementos no layout para acompnhar em tempo real a verificação do usuario e senha
    private boolean isValidEmail(String usuario) {
        return usuario != null && Patterns.EMAIL_ADDRESS.matcher(usuario).matches();
    };

        if (!senha.equals(confirmarsenha)) {
            mensagem.setText("As senhas não são iguais! Tente novamente.");
            return;

        } CriarLogin(usuario, senha);
        Intent intencao = new Intent(CadastroActivity.this, PrincipalActivity.class);
        startActivity(intencao);
    }

    @Override
    public void CriarLogin(String u, String s) {

    }


}