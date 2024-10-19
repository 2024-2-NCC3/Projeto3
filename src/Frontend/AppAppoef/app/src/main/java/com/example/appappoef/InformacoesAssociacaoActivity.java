package com.example.appappoef;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InformacoesAssociacaoActivity extends AppCompatActivity {

    private Button btnRealizarProva;
    private Button btnInfEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_informacoes_associacao);

        btnInfEntrar = findViewById(R.id.btnInfEntrar);
        btnRealizarProva = findViewById(R.id.btnRealizarProva);

        btnRealizarProva.setOnClickListener(view -> {
            Intent intent = new Intent(this, OrientacaoProvaActivity.class);
            startActivity(intent);
        });

        btnInfEntrar.setOnClickListener(view -> {
            Intent intent = new Intent(this, PrincipalActivity.class);
            startActivity(intent);
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}