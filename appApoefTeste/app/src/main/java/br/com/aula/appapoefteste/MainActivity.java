package br.com.aula.appapoefteste;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Localizar o Botão "Não tenho Conta" no Layout
        Button naoTenhoConta = findViewById(R.id.btnNaoTenhoConta);

        // Definir a ação do clique do botão
        naoTenhoConta.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                // Iniciar a Tela de Cadastro
                Intent intencao = new Intent(MainActivity.this,telaCadastro.class);
                startActivity(intencao);
            }
        });
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
            mensagem.setText("Usuário ou senha incorretos! Digite novamente");
            return;
        }
        mensagem.setText("Acesso permitido");
    }

}