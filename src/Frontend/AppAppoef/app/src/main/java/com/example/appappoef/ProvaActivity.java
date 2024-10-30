package com.example.appappoef;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
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

    // Declaração de constantes
    private static final long tempoTotal = 3000000;
    private static final long tempoQuestao = 90000;
    private final String URL_BUSCAR_DADOS = "https://h4592k-3000.csb.app/provaUsuario";

    // Delcaração de objetos
    private RequestQueue requestQueue;
    private TextView campoIdProva,campoQuestao, campoIdRespA, campoIdRespB, campoIdRespC, campoRespCorreta;
    private TextView textNumTimeTotal, textNumTempoQ, textResultado;
    private RadioButton campoTextRespA, campoTextRespB, campoTextRespC;
    private Button btnProxima, btnFinalizarProva;
    private SeekBar campoSeekBar;

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

        // chama metodo instanciando Ojetos e listeners
        instanciandoObjetos();
        listeners();

        // Contagem regressiva
        contagemRegressivaTotal(tempoTotal);
        contagemRegressivaQ(tempoQuestao);

        // mostar primira pergunta
        dadosServidor();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    // Metodo para instancias de objetos
    private void instanciandoObjetos(){
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
        campoSeekBar = findViewById(R.id.seekBar);

        textNumTempoQ = findViewById(R.id.textNumTempoQ);
        textNumTimeTotal = findViewById(R.id.textNumTimeTotal);
        textResultado = findViewById(R.id.textResultado);

        btnProxima = findViewById(R.id.btnProxima);
        btnFinalizarProva = findViewById(R.id.btnFinalizarProva);
    }
    // Metodo para Listeners
    private void listeners(){
        btnProxima.setOnClickListener(view -> {
            avancarQuestoes();

        });

        btnFinalizarProva.setOnClickListener(view -> {
            Intent intent = new Intent(ProvaActivity.this, PrincipalActivity.class);
            startActivity(intent);
        });
    }
    // metodo ocultar objetos
    private void ocultarObjetos(){
        campoIdProva.setVisibility(View.GONE);
        campoIdRespA.setVisibility(View.GONE);
        campoTextRespA.setVisibility(View.GONE);
        campoIdRespB.setVisibility(View.GONE);
        campoTextRespB.setVisibility(View.GONE);
        campoIdRespC.setVisibility(View.GONE);
        campoTextRespC.setVisibility(View.GONE);
        campoRespCorreta.setVisibility(View.GONE);
    }
    // Metodo para mostar as perguntas na activity
    private void mostrarPergunta(int indice){
        // Verifica se o indice está dentro do intervalo
        if(questoes != null && indice >= 0 && indice < questoes.length()){
            try {
                JSONObject obj = questoes.getJSONObject(indice);
                // Pegar dados
                campoIdProva.setText(obj.getString("idProva"));
                campoQuestao.setText(obj.getString("Questao"));
                campoIdRespA.setText(obj.getString("idRespA"));
                campoTextRespA.setText(obj.getString("textRespA"));
                campoIdRespB.setText(obj.getString("idRespB"));
                campoTextRespB.setText(obj.getString("textRespB"));
                campoIdRespC.setText(obj.getString("idRespC"));
                campoTextRespC.setText(obj.getString("textRespC"));
                campoRespCorreta.setText(obj.getString("RespCorreta"));
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(ProvaActivity.this, "Erro ao processar o JSON", Toast.LENGTH_SHORT).show();
                }
        } else {
            // Se indice fora do intervalo
            Toast.makeText(ProvaActivity.this, "Indice fora do Intervalor", Toast.LENGTH_SHORT).show();
        }
    }
    // Metodo de contagem regressiva
    private void contagemRegressivaTotal(long tempoInicialTotal){
        campoSeekBar.setMax((int) (tempoInicialTotal / 1000));
        campoSeekBar.setProgress((int) (tempoInicialTotal / 1000));
        new CountDownTimer(tempoInicialTotal, 1000){
            @Override
            public void onTick(long millisUntilFinished){
                textNumTimeTotal.setText(formataTempo(millisUntilFinished));
                campoSeekBar.setProgress((int) (millisUntilFinished / 1000));
            }
            @Override
            public void onFinish(){
                textNumTimeTotal.setText("Fim!");
                campoSeekBar.setProgress(0);
            }
        }.start();
    }
    private void contagemRegressivaQ(long tempoQuestao){
        // Cancela o timer existente
        if (timer != null){
            timer.cancel();
        }
        new CountDownTimer(tempoQuestao, 1000){
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
    //Metodo finalizar prova
    private void finalizarProva(){
        // Se ultimo item
        textResultado.setVisibility(View.VISIBLE);
        textResultado.setText("Prova finalizada! \n Aguarde e-mail da APPOEF com seu resultado.");
        btnFinalizarProva.setVisibility(View.VISIBLE);
        btnProxima.setVisibility(View.GONE);
        ocultarObjetos();
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
    // Metodo para mudar de questão
    public void avancarQuestoes(){
        // Incrementa o indice para exibir a próxima pergunta
        if (questoes != null && indiceAtual < questoes.length() - 1){
            indiceAtual++;
            mostrarPergunta(indiceAtual); // mostar a próxima pergunta
            contagemRegressivaQ(90000);
        }else {
            finalizarProva();
        }
    }
    // Metodo para formatar o tempo em mm ss
    private String formataTempo(long tempo){
        long min = (tempo / 1000) / 60;
        long sec = (tempo/ 1000) % 60;
        return String.format("%02d:%02d", min, sec);
    }
}