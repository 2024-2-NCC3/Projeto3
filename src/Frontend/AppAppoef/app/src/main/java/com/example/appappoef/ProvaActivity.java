package com.example.appappoef;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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

public class ProvaActivity extends AppCompatActivity {

    // Delcaração de objetos
    private RequestQueue requestQueue;
    private final String URL_BUSCAR_DADOS = "https://xm4tg7-3000.csb.app/provaUsuario";
    private TextView campoIdProva,campoQuestao, campoIdRespA, campoIdRespB, campoIdRespC, campoRespCorreta;
    private TextView textNumTimeTotal, textNumTempoQ;
    private RadioButton campoTextRespA, campoTextRespB, campoTextRespC;
    private Button btnProxima, btnFinalizarProva;

    //Declaração de variáeis auxiliares
    private int indiceAtual = 0;
    private JSONArray questoes;
    private CountDownTimer timer;



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

        textNumTempoQ = findViewById(R.id.textNumTempoQ);
        textNumTimeTotal = findViewById(R.id.textNumTimeTotal);

        btnProxima = findViewById(R.id.btnProxima);
        btnFinalizarProva = findViewById(R.id.btnFinalizarProva);

        // Contagem regressiva
        contagemRegressivaTotal(3000000);
        contagemRegressivaQ(90000);


        // mostar primira pergunta
        dadosServidor();

        btnProxima.setOnClickListener(view -> {
            avancarQuestoes();

        });

        btnFinalizarProva.setOnClickListener(view -> {
            Intent intent = new Intent(this, PrincipalActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    //Metodo para buscar infromações do servidor
    public void dadosServidor(){
        //Buscar dados do servidor
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_BUSCAR_DADOS,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        buscarDados(response);
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
    // Metodo para pegar os dados da prova
    public void buscarDados(JSONArray response){
        questoes = response;
        mostrarPergunta(indiceAtual);
    }
    // Metodo para mostar as perguntas na activity
    public void mostrarPergunta(int indice){
        // Verifica se o indice está dentro do intervalo
        if(questoes != null && indice >= 0 && indice < questoes.length()){
            try {
                JSONObject obj = questoes.getJSONObject(indice);
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
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(ProvaActivity.this, "Erro ao processar o JSON", Toast.LENGTH_SHORT).show();
                }
        } else {
            // Se indice fora do intervalo
            Toast.makeText(ProvaActivity.this, "Indice fora do Intervalor", Toast.LENGTH_SHORT).show();
        }

    }
    // Metodo para mudar de questão
    public void avancarQuestoes(){
        // Incrementa o indice para exibir a próxima pergunta
        if (questoes != null && indiceAtual < questoes.length() - 1){
            indiceAtual++;
            mostrarPergunta(indiceAtual); // mostar a próxima pergunta
            contagemRegressivaQ(90000);
        }else {
            // Se ultimo item
            campoQuestao.setTextSize(30);
            campoQuestao.setText("Prova finalizada! \n Aguarde e-mail da APPOEF com seu resultado.");
            btnFinalizarProva.setVisibility(View.VISIBLE);
            btnProxima.setVisibility(View.GONE);
            campoIdProva.setVisibility(View.GONE);
            campoIdRespA.setVisibility(View.GONE);
            campoTextRespA.setVisibility(View.GONE);
            campoIdRespB.setVisibility(View.GONE);
            campoTextRespB.setVisibility(View.GONE);
            campoIdRespC.setVisibility(View.GONE);
            campoTextRespC.setVisibility(View.GONE);
            campoRespCorreta.setVisibility(View.GONE);
        }
    }
    // Metodo para formatar o tempo em mm ss
    private String formataTempo(long tempo){
        long min = (tempo / 1000) / 60;
        long sec = (tempo/ 1000) % 60;
        return String.format("%02d:%02d", min, sec);
    }
    // Metodo de contagem regressiva
    private void contagemRegressivaTotal(long tempoInicialTotal){
        new CountDownTimer(tempoInicialTotal, 1000){
            @Override
            public void onTick(long millisUntilFinished){
                textNumTimeTotal.setText(formataTempo(millisUntilFinished));
            }
            @Override
            public void onFinish(){
                textNumTimeTotal.setText("Fim!");
            }
        }.start();
    }
    private void contagemRegressivaQ(long tempoInicialTotal){
        // Cancela o timer existente
        if (timer != null){
            timer.cancel();
        }
        new CountDownTimer(tempoInicialTotal, 1000){
            @Override
            public void onTick(long millisUntilFinished){
                textNumTempoQ.setText(formataTempo(millisUntilFinished));
            }
            @Override
            public void onFinish(){
                textNumTempoQ.setText("Fim!");
                avancarQuestoes();
            }
        }.start();
    }

}