package org.appoef.appappoef;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
public class PerfilActivity extends AppCompatActivity {
    TextView usuarioNome, usuarioEmail;
    Button btnSair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        usuarioNome = findViewById(R.id.textNomeCompleto);
        usuarioEmail = findViewById(R.id.textEmail);
        btnSair = findViewById(R.id.btnSair);

        // Recuperar os dados do SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        String nome = sharedPreferences.getString("nome", "Usuário não encontrado");
        String email = sharedPreferences.getString("email", "Email não encontrado");

        // Atualizar os TextViews com as informações recuperadas
        usuarioNome.setText("Nome: " + nome);
        usuarioEmail.setText("Email: " + email);

        // Configurar o botão Sair
        btnSair.setOnClickListener(view -> {

            // Redirecionar para a página de login
            Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Finalizar a activity atual
        });
    }
}
