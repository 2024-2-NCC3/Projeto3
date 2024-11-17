package org.appoef.appappoef;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.appoef.appappoef.R;

public class PrincipalActivity extends AppCompatActivity {

    private ImageButton btnPerfil, btnAssociar, btnBuscar, btnProva, btnCalendario, btnConfiguracoes;
    private TextView txtSobre;

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
        btnConfiguracoes = findViewById(R.id.btnConfiguracoes);
        txtSobre = findViewById(R.id.txtSobre);

        btnAssociar.setOnClickListener(v -> {
            Intent intent = new Intent(PrincipalActivity.this, InformacoesAssociacaoActivity.class);
            startActivity(intent);
        });

        btnProva.setOnClickListener(v -> {
            Intent intent = new Intent(PrincipalActivity.this, OrientacaoProvaActivity.class);
            startActivity(intent);
        });

        btnCalendario.setOnClickListener(v -> {
            Intent intent = new Intent(PrincipalActivity.this, CalendarioActivity.class);
            startActivity(intent);
        });

        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(PrincipalActivity.this, PerfilActivity.class);
            startActivity(intent);
        });

        btnBuscar.setOnClickListener(v -> {
            Intent intent = new Intent(PrincipalActivity.this, BuscarActivity.class);
            startActivity(intent);
        });

        txtSobre.setOnClickListener(v -> {
            Intent intent = new Intent(PrincipalActivity.this, SobreActivity.class);
            startActivity(intent);
        });
    }
}



