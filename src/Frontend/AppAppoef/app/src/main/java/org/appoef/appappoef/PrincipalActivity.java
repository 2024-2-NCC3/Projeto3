package org.appoef.appappoef;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.appoef.appappoef.R;

public class PrincipalActivity extends AppCompatActivity {

    private ImageButton btnPerfil, btnAssociar, btnBuscar, btnProva, btnCalendario, btnServicos, btnMembros, btnSobre, btnConfiguracoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);

        btnAssociar = findViewById(R.id.btnAssociar);
        btnPerfil = findViewById(R.id.btnPerfil);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnProva = findViewById(R.id.btnProva);
        btnCalendario = findViewById(R.id.btnCalendario);
        btnServicos = findViewById(R.id.btnServicos);
        btnMembros = findViewById(R.id.btnMembros);
        btnSobre = findViewById(R.id.btnSobre);
        btnConfiguracoes = findViewById(R.id.btnConfiguracoes);

        btnAssociar.setOnClickListener(v ->{
            Intent intent = new Intent(PrincipalActivity.this, InformacoesAssociacaoActivity.class);
            startActivity(intent);
        });
        btnPerfil.setOnClickListener(v ->{
            Intent intent = new Intent(PrincipalActivity.this, PerfilActivity.class);
            startActivity(intent);
        });
        btnBuscar.setOnClickListener(v ->{

        });
        btnProva.setOnClickListener(v ->{
            Intent intent = new Intent(PrincipalActivity.this, OrientacaoProvaActivity.class);
            startActivity(intent);
        });

        /* btnCalendario.setOnClickListener(v ->{
            Intent intent = new Intent(PrincipalActivity.this, CalendarioActivity.class);
            startActivity(intent);
        }); */
        btnServicos.setOnClickListener(v ->{
            Intent intent = new Intent(PrincipalActivity.this, ServicosActivity.class);
            startActivity(intent);
        });
        btnMembros.setOnClickListener(v ->{
            Intent intent = new Intent(PrincipalActivity.this, AssociadosActivity.class);
            startActivity(intent);
        });
        btnSobre.setOnClickListener(v -> {
            // Define a URL que você quer abrir
            String url = "https://www.appoef.org";

            // Cria um Intent para abrir o navegador
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

            // Verifica se há algum aplicativo que possa abrir a URL
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        });
        btnConfiguracoes.setOnClickListener(v ->{
            Intent intent = new Intent(PrincipalActivity.this, ConfiguracoesActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}