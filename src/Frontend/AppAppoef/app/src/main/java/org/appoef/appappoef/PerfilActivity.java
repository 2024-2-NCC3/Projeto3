package org.appoef.appappoef;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
public class PerfilActivity extends AppCompatActivity {
    TextView usuarioNome, usuarioEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        usuarioNome = findViewById(R.id.textNomeCompleto);
       usuarioEmail = findViewById(R.id.textEmail);

        SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        String nome = sharedPreferences.getString("nome", "Usuário não encontrado");
        String email = sharedPreferences.getString("email", "Email não encontrado");

        usuarioNome.setText("Nome: " + nome);
       usuarioEmail.setText("Email: " + email);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
