package com.example.appappoef;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OrientacaoProvaActivity extends AppCompatActivity {

    private Button btnIniciarProva;
    private Button btnOrientVoltar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_orientacao_prova);

        btnOrientVoltar = findViewById(R.id.btnOrientVoltar);
        btnIniciarProva = findViewById(R.id.btnIniciarProva);

        btnIniciarProva.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProvaActivity.class);
            startActivity(intent);

        });

        btnOrientVoltar.setOnClickListener(view -> {
            Intent intent = new Intent(this, InformacoesAssociacaoActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

}