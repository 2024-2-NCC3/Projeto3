package com.example.appappoef;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProvaActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private final String URL_BUSCAR_DADOS = "https://xm4tg7-3000.csb.app/randomPerguntas";
    private TextView campoIdProva,campoQuestao, campoIdRespA, campoIdRespB, campoIdRespC, campoRespCorreta;
    private TextView texTime, textNumTime;
    private RadioButton campoTextRespA, campoTextRespB, campoTextRespC;
    private Button btnProxima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_prova);
// Requisição de rede
        requestQueue = Volley.newRequestQueue(this);

        // Instanciando objetos
        campoIdProva = findViewById(R.id.textIdProva);
        campoQuestao = findViewById(R.id.textQuestao);
        campoIdRespA = findViewById(R.id.textRespA);
        campoTextRespA = findViewById(R.id.radioTextRespA);
        campoIdRespB = findViewById(R.id.textRespB);
        campoTextRespB = findViewById(R.id.radioTextRespB);
        campoIdRespC = findViewById(R.id.textRespC);
        campoTextRespC = findViewById(R.id.radioTextRespC);
        campoRespCorreta = findViewById(R.id.RespCorreta);

        texTime = findViewById(R.id.texTime);
        textNumTime = findViewById(R.id.textNumTime);

        btnProxima = findViewById(R.id.btnProxima);

        btnProxima.setOnClickListener(view -> {
            buscarDados();
        });

        //chamar método
        buscarDados();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void buscarDados(){
        //Buscar dados do servidor
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_BUSCAR_DADOS,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Dados recebidos
                        ArrayList<String> dados = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                // Pegar dados
                                String idProva = obj.getString("idProva");
                                String Questao = obj.getString("Questao");
                                String idRespA = obj.getString("idRespA");
                                String textRespA = obj.getString("textRespA");
                                String idRespB = obj.getString("idRespB");
                                String textRespB = obj.getString("textRespB");
                                String idRespC = obj.getString("idRespC");
                                String textRespC = obj.getString("textRespC");
                                String RespCorreta = obj.getString("RespCorreta");

                                // Exibis dados
                                campoIdProva.setText(idProva);
                                campoQuestao.setText(Questao);
                                campoIdRespA.setText(idRespA);
                                campoTextRespA.setText(textRespA);
                                campoIdRespB.setText(idRespB);
                                campoTextRespB.setText(textRespB);
                                campoIdRespC.setText(idRespC);
                                campoTextRespC.setText(textRespC);
                                campoRespCorreta.setText(RespCorreta);

                                // Comparar ids
                                campoTextRespA.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                    if(isChecked && RespCorreta.equals(idRespA)){
                                        campoIdRespA.setVisibility(View.VISIBLE);
                                    } else{
                                        campoIdRespA.setVisibility(View.INVISIBLE);
                                    }
                                });
                                campoTextRespB.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                    if(isChecked && RespCorreta.equals(idRespB)){
                                        campoIdRespB.setVisibility(View.VISIBLE);
                                    } else{
                                        campoIdRespB.setVisibility(View.INVISIBLE);
                                    }
                                });
                                campoTextRespC.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                    if(isChecked && RespCorreta.equals(idRespC)){
                                        campoIdRespC.setVisibility(View.VISIBLE);
                                    } else{
                                        campoIdRespC.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ProvaActivity.this, "Erro ao processar o JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Erro na requisição
                        Log.e("Volley", "Erro ao buscar dados: " + error.getMessage());
                        Toast.makeText(ProvaActivity.this, "Erro ao buscar dados: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        // Requisição à fila
        requestQueue.add(jsonArrayRequest);
    }
}