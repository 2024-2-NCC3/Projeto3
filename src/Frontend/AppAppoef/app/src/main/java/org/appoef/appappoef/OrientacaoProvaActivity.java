package org.appoef.appappoef;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OrientacaoProvaActivity extends AppCompatActivity {

    // Declaração de constantes
    private final String URL_CRIAR_PROVA = "https://qyyjfz-3000.csb.app/criarProva";

    // Declaração de objetos
    private RequestQueue requestQueue;
    private Button btnIniciarProva;
    private Button btnOrientVoltar;

    // Declaração de variáveis auxiliares
    private SharedPreferences sharedPreferences;
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_orientacao_prova);

        // Inicializa a fila de requisições
        requestQueue = Volley.newRequestQueue(this);

        // Instanciando objetos
        instanciandoObjetosOrientacao();

        // Listeners
        listenersOrientacao();

        // Recupera o token do SharedPreferences
        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Você precisa estar logado para realizar a prova!", Toast.LENGTH_SHORT).show();
            return;  // Não prosseguir se o token não for encontrado
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void instanciandoObjetosOrientacao() {
        btnOrientVoltar = findViewById(R.id.btnOrientVoltar);
        btnIniciarProva = findViewById(R.id.btnIniciarProva);
    }
    private void listenersOrientacao() {
        btnIniciarProva.setOnClickListener(view -> {
            criarProva();
            Intent intent = new Intent(this, ProvaActivity.class);
            startActivity(intent);
        });

        btnOrientVoltar.setOnClickListener(view -> {
            Intent intent = new Intent(this, PrincipalActivity.class);
            startActivity(intent);
        });
    }
    private void criarProva() {
        // Configuração da requisição
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL_CRIAR_PROVA, new JSONObject() {{
        }},
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Processar resposta do servidor
                        if (response.has("message")) {
                            // Exibir mensagem de sucesso
                            String message = response.optString("message");
                            Log.d("Prova", message);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Lidar com erros de requisição
                        Log.e("Erro", "Erro na requisição: " + error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token); // Inclui o token no cabeçalho
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

// Adiciona a requisição à fila de execução (supondo que você tenha uma instância de Volley)
        requestQueue.add(request);
    }

}