package org.appoef.appappoef;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ConfiguracoesActivity extends AppCompatActivity {
    TextView usuarioNome, usuarioEmail, usuarioTelefone, usuarioSenha;
    private Button btnSair;
    private TextView txtEsqueciSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_configuracoes);

        usuarioNome = findViewById(R.id.textInputEditNome);
        usuarioEmail = findViewById(R.id.textInputEditEmail);
        usuarioTelefone = findViewById(R.id.textInputEditTelefone);
        usuarioSenha = findViewById(R.id.textInputEditSenha);
        btnSair = findViewById(R.id.btnSair);
        txtEsqueciSenha = findViewById(R.id.txtEsqueciSenha);

        btnSair.setOnClickListener(v -> {
            Intent intent = new Intent(ConfiguracoesActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);

        String nome = sharedPreferences.getString("nome", "Usuário não encontrado");
        String email = sharedPreferences.getString("email", "Email não encontrado");
        String telefone = sharedPreferences.getString("telefone", "Telefone não encontrado");
        String senha = sharedPreferences.getString("senha", "Senha não encontrada");

        usuarioNome.setText(nome);
        usuarioEmail.setText(email);
        usuarioTelefone.setText(telefone);
        usuarioSenha.setText(senha);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}