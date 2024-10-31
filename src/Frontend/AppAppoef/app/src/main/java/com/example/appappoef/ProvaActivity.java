package com.example.appappoef;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProvaActivity extends AppCompatActivity {

    // Declaração de constantes
    private static final long tempoTotal = 3000000;
    private static final long tempoQuestao = 90000;
    private final String URL_BUSCAR_DADOS = "https://h4592k-3000.csb.app/provaUsuario";

    // Delcaração de objetos
    private RequestQueue requestQueue;
    private TextView campoIdQuestao,campoQuestao;
    private TextView texTimeTotal, textNumTimeTotal, textNumTempoQ, textResultado;
    private RadioButton campoTextRespA, campoTextRespB, campoTextRespC, campoTextRespD;
    private RadioGroup radioGroupRespostas;
    private Button btnProxima, btnFinalizarProva;

    //Declaração de variáeis auxiliares
    private int indiceAtual = 0;
    private int respostaCorreta;
    private int selectedIndex;
    private int selectedId;
    private JSONArray questoes;
    private CountDownTimer timer;
    private int resultadoAcertos;

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
        campoIdQuestao = findViewById(R.id.textIdQuestao);
        campoQuestao = findViewById(R.id.textQuestao);
        radioGroupRespostas = findViewById(R.id.radioGroupRespostas);
        campoTextRespA = findViewById(R.id.radioTextRespA);
        campoTextRespB = findViewById(R.id.radioTextRespB);
        campoTextRespC = findViewById(R.id.radioTextRespC);
        campoTextRespD = findViewById(R.id.radioTextRespD);

        textNumTempoQ = findViewById(R.id.textNumTempoQ);
        textNumTimeTotal = findViewById(R.id.textNumTimeTotal);
        texTimeTotal = findViewById(R.id.texTimeTotal);
        textResultado = findViewById(R.id.textResultado);

        btnProxima = findViewById(R.id.btnProxima);
        btnFinalizarProva = findViewById(R.id.btnFinalizarProva);
    }
    // Metodo para Listeners
    private void listeners(){
        btnProxima.setOnClickListener(view -> {
            avancarQuestoes();
            radioGroupRespostas.clearCheck();
        });
        btnFinalizarProva.setOnClickListener(view -> {
            Intent intent = new Intent(ProvaActivity.this, PrincipalActivity.class);
            startActivity(intent);
        });

    }
    //Metodo para buscar infromações do servidor, pegar os dados da prova, mostar as perguntas na activity
    // Metodo para avançar a questão,finalizar prova, ocultar objetos
    public void dadosServidor(){
        //Buscar dados do servidor
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://h4592k-3000.csb.app/perguntaCadastrada",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        pegarDados(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Erro na requisição
                        Toast.makeText(ProvaActivity.this, "Erro ao buscar dados: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        // Requisição à fila
        requestQueue.add(jsonArrayRequest);
    }
    public void mostrarPergunta(int indice){
        // Verifica se o indice está dentro do intervalo
        if(questoes != null && indice >= 0 && indice < questoes.length()){
            try {
                JSONObject obj = questoes.getJSONObject(indice);
                String idQuestao = obj.has("idQuestao") ? obj.getString("idQuestao") : "ID não disponível";
                String questao = obj.has("Questao") ? obj.getString("Questao") : "Questão não disponível";
                String respCorreta = obj.has("RespCorreta") ? obj.getString("RespCorreta") : "Resposta não disponível";
                String textRespA = obj.has("textRespA") ? obj.getString("textRespA") : "Resposta A não disponível";
                String textRespB = obj.has("textRespB") ? obj.getString("textRespB") : "Resposta B não disponível";
                String textRespC = obj.has("textRespC") ? obj.getString("textRespC") : "Resposta C não disponível";
                // Lista de respostas
                List<String> respostas = new ArrayList<>();
                respostas.add(respCorreta);
                respostas.add(textRespA);
                respostas.add(textRespB);
                respostas.add(textRespC);

                // Embaralhando as respostas
                Collections.shuffle(respostas);

                // Resposta correta após embaralhar
                respostaCorreta = respostas.indexOf(respCorreta);

                // Pegar dados
                campoIdQuestao.setText(idQuestao);
                campoQuestao.setText(questao);
                campoTextRespA.setText(respostas.get(0));
                campoTextRespB.setText(respostas.get(1));
                campoTextRespC.setText(respostas.get(2));
                campoTextRespD.setText(respostas.get(3));
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(ProvaActivity.this, "Erro ao processar o JSON", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Se indice fora do intervalo
            Toast.makeText(ProvaActivity.this, "Indice fora do Intervalor", Toast.LENGTH_SHORT).show();
        }
    }
    public void pegarDados(JSONArray response){
        questoes = response;
        mostrarPergunta(indiceAtual);
    }
    public void avancarQuestoes(){
        selectedId = radioGroupRespostas.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            selectedIndex = radioGroupRespostas.indexOfChild(selectedRadioButton);
            if (selectedIndex == respostaCorreta) {
                resultadoAcertos++;
            }
            // Incrementa o indice para exibir a próxima pergunta
            if (questoes != null && indiceAtual < questoes.length() - 1) {
                indiceAtual++;
                mostrarPergunta(indiceAtual); // mostar a próxima pergunta
                contagemRegressivaQ(90000);
            } else {
                finalizarProva();
            }
        }else {
            Toast.makeText(this, "Por favor, selecione uma resposta.", Toast.LENGTH_SHORT).show();
        }
    }
    private void finalizarProva(){
        // Se ultimo item
        textResultado.setVisibility(View.VISIBLE);
        textResultado.setText("Prova finalizada! "+ resultadoAcertos +"\n Aguarde e-mail da APPOEF com seu resultado.");
        btnFinalizarProva.setVisibility(View.VISIBLE);
        btnProxima.setVisibility(View.GONE);
        ocultarObjetos();
    }
    private void ocultarObjetos(){
        textNumTimeTotal.setVisibility(View.GONE);
        texTimeTotal.setVisibility(View.GONE);
        campoIdQuestao.setVisibility(View.GONE);
        campoQuestao.setVisibility(View.GONE);
        campoTextRespA.setVisibility(View.GONE);
        campoTextRespB.setVisibility(View.GONE);
        campoTextRespC.setVisibility(View.GONE);
        campoTextRespD.setVisibility(View.GONE);
    }
    // Metodo para formatar o tempo em mm ss e contagem regressiva
    private String formataTempo(long tempo){
        long min = (tempo / 1000) / 60;
        long sec = (tempo/ 1000) % 60;
        return String.format("%02d:%02d", min, sec);
    }
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
}