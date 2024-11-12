package org.appoef.appappoef;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
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
    private static final long tempoTotal = 50000;
    private static final long tempoQuestao = 10000;
    private final String URL_PROVA_DADOS = "https://qyyjfz-3000.csb.app/provaUsuario";
    private final String CHAVE = "SenhaAppoefSec";

    // Declaração de objetos
    private RequestQueue requestQueue;
    private TextView campoIdQuestao, campoQuestao;
    private TextView texTimeTotal, textNumTimeTotal, textTempoQuestao, textNumTempoQ, textResultado;
    private RadioButton campoTextRespA, campoTextRespB, campoTextRespC, campoTextRespD;
    private RadioGroup radioGroupRespostas;
    private Button btnProxima, btnFinalizarProva;

    // Declaração de variáveis auxiliares
    private int indiceAtual = 0;
    private int respostaCorreta;
    private int selectedIndex;
    private int selectedId;

    private JSONArray questoes;
    private CountDownTimer timer;
    private int resultadoAcertos;
    private SharedPreferences sharedPreferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova);

        // Inicializa a fila de requisições
        requestQueue = Volley.newRequestQueue(this);

        // Instancia os objetos da UI
        instanciandoObjetosProva();

        // Configura os listeners de botões
        listenersProva();

        // Inicia a contagem regressiva total e da questão
        contagemRegressivaTotal(tempoTotal);
        contagemRegressivaQ(tempoQuestao);

        // Recupera o token do SharedPreferences
        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Token não encontrado!", Toast.LENGTH_SHORT).show();
            return;  // Não prosseguir se o token não for encontrado
        }
        // Chama a função para buscar os dados no servidor
        dadosServidor();
    }

    // Método para instanciar os objetos da UI
    private void instanciandoObjetosProva() {
        campoIdQuestao = findViewById(R.id.textIdQuestao);
        campoQuestao = findViewById(R.id.textQuestao);
        radioGroupRespostas = findViewById(R.id.radioGroupRespostas);
        campoTextRespA = findViewById(R.id.radioTextRespA);
        campoTextRespB = findViewById(R.id.radioTextRespB);
        campoTextRespC = findViewById(R.id.radioTextRespC);
        campoTextRespD = findViewById(R.id.radioTextRespD);

        textNumTempoQ = findViewById(R.id.textNumTempoQ);
        textNumTimeTotal = findViewById(R.id.textNumTimeTotal);
        textTempoQuestao = findViewById(R.id.textTempoQuestao);
        texTimeTotal = findViewById(R.id.texTimeTotal);
        textResultado = findViewById(R.id.textResultado);

        btnProxima = findViewById(R.id.btnProxima);
        btnFinalizarProva = findViewById(R.id.btnFinalizarProva);
    }

    // Configura os listeners dos botões
    private void listenersProva() {
        btnProxima.setOnClickListener(view -> avancarQuestoes());
        btnFinalizarProva.setOnClickListener(view -> {
            Intent intent = new Intent(ProvaActivity.this, PrincipalActivity.class);
            startActivity(intent);
        });
    }

    // Método para buscar as informações do servidor
    public void dadosServidor() {
        // Cria a requisição JSON com o cabeçalho Authorization
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_PROVA_DADOS,
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
                        String errorMessage = "Erro ao buscar dados: " + error.getMessage();
                        if (error.networkResponse != null) {
                            errorMessage += " - Status code: " + error.networkResponse.statusCode;
                        }
                        Log.e("VolleyError", errorMessage);
                        Toast.makeText(ProvaActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Adiciona o cabeçalho Authorization com o token
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        // Adiciona a requisição à fila de requisições
        requestQueue.add(jsonArrayRequest);
    }

    // Método para processar os dados retornados da API
    public void pegarDados(JSONArray response) {
        if (response != null && response.length() > 0) {
            questoes = response;
            mostrarPergunta(indiceAtual); // Exibe a primeira questão
        } else {
            Toast.makeText(ProvaActivity.this, "Nenhuma questão encontrada.", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para mostrar a pergunta
    public void mostrarPergunta(int indice) {
        String segredo = CHAVE;
        if (questoes != null && indice >= 0 && indice < questoes.length()) {
            try {
                // Recupera a questão do JSON
                JSONObject obj = questoes.getJSONObject(indice);

                // Verifica se a questão está criptografada e faz a descriptografia
                String dadosCriptografadosBase64 = obj.optString("Questao", null);
                String questao = "";

                if (dadosCriptografadosBase64 != null && !dadosCriptografadosBase64.isEmpty()) {
                    // Se a questão estiver criptografada, tenta descriptografar
                     // A chave secreta usada para criptografar
                    questao = DesCriptoGrafar.desCriptoGrafar(dadosCriptografadosBase64, segredo);
                } else {
                    // Caso contrário, pega o campo "Questao" diretamente
                    questao = obj.optString("Questao", "Questão não disponível");
                }

                // Recupera as respostas criptografadas
                String respCorretaCriptografada = obj.optString("RespCorreta", null);
                String textRespACriptografada = obj.optString("textRespA", null);
                String textRespBCriptografada = obj.optString("textRespB", null);
                String textRespCCriptografada = obj.optString("textRespC", null);

                // Descriptografa as respostas, se estiverem criptografadas
                String respCorreta = (respCorretaCriptografada != null && !respCorretaCriptografada.isEmpty()) ?
                        DesCriptoGrafar.desCriptoGrafar(respCorretaCriptografada, segredo) : obj.optString("RespCorreta", "Resposta correta não disponível");

                String textRespA = (textRespACriptografada != null && !textRespACriptografada.isEmpty()) ?
                        DesCriptoGrafar.desCriptoGrafar(textRespACriptografada, segredo) : obj.optString("textRespA", "Resposta A não disponível");

                String textRespB = (textRespBCriptografada != null && !textRespBCriptografada.isEmpty()) ?
                        DesCriptoGrafar.desCriptoGrafar(textRespBCriptografada, segredo) : obj.optString("textRespB", "Resposta B não disponível");

                String textRespC = (textRespCCriptografada != null && !textRespCCriptografada.isEmpty()) ?
                        DesCriptoGrafar.desCriptoGrafar(textRespCCriptografada, segredo) : obj.optString("textRespC", "Resposta C não disponível");

                // Lista de respostas
                List<String> respostas = new ArrayList<>();
                respostas.add(respCorreta);  // Adiciona a resposta correta
                respostas.add(textRespA);    // Adiciona a resposta A
                respostas.add(textRespB);    // Adiciona a resposta B
                respostas.add(textRespC);    // Adiciona a resposta C

                // Embaralha as respostas
                Collections.shuffle(respostas);

                // Localiza a posição da resposta correta após o embaralhamento
                respostaCorreta = respostas.indexOf(respCorreta);

                // Atualiza os campos na UI com os dados da questão
                campoIdQuestao.setText(obj.optString("idQuestao", "ID não disponível"));
                campoQuestao.setText(questao);  // Aqui você está usando a questão (descriptografada ou não)

                // Limpa a seleção anterior dos RadioButtons
                radioGroupRespostas.clearCheck();

                // Define as respostas nos campos de UI
                if (respostas.size() > 0) campoTextRespA.setText(respostas.get(0));
                if (respostas.size() > 1) campoTextRespB.setText(respostas.get(1));
                if (respostas.size() > 2) campoTextRespC.setText(respostas.get(2));
                if (respostas.size() > 3) campoTextRespD.setText(respostas.get(3));

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(ProvaActivity.this, "Erro ao processar o JSON", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("DescriptografarErro", "Erro ao descriptografar: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(ProvaActivity.this, "Erro ao descriptografar " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ProvaActivity.this, "Índice fora do intervalo", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para avançar as questões
    public void avancarQuestoes(){
        selectedId = radioGroupRespostas.getCheckedRadioButtonId();
        if (selectedId != -1) {
            // Se alguma resposta foi selecionada
            RadioButton selectedRadioButton = findViewById(selectedId);
            selectedIndex = radioGroupRespostas.indexOfChild(selectedRadioButton);
            if (selectedIndex == respostaCorreta) {
                resultadoAcertos++;
            }
        }

        // Incrementa o índice para exibir a próxima pergunta
        if (questoes != null && indiceAtual < questoes.length() - 1) {
            indiceAtual++;
            mostrarPergunta(indiceAtual); // Mostra a próxima pergunta
            contagemRegressivaQ(tempoQuestao); // Reinicia a contagem regressiva
        } else {
            finalizarProva(); // Se não houver mais questões, finalizar a prova
        }
    }

    // Método para finalizar a prova
    private void finalizarProva() {
        textResultado.setVisibility(View.VISIBLE);
        textResultado.setText("Prova finalizada! " + resultadoAcertos + " acertos.\nAguarde o resultado no seu e-mail.");
        btnFinalizarProva.setVisibility(View.VISIBLE);
        btnProxima.setVisibility(View.GONE);
        ocultarObjetos();
    }

    // Método para ocultar objetos após finalizar a prova
    private void ocultarObjetos() {
        textNumTimeTotal.setVisibility(View.GONE);
        texTimeTotal.setVisibility(View.GONE);
        textTempoQuestao.setVisibility((View.GONE));
        textNumTempoQ.setVisibility(View.GONE);
        campoIdQuestao.setVisibility(View.GONE);
        campoQuestao.setVisibility(View.GONE);
        campoTextRespA.setVisibility(View.GONE);
        campoTextRespB.setVisibility(View.GONE);
        campoTextRespC.setVisibility(View.GONE);
        campoTextRespD.setVisibility(View.GONE);
    }

    // Método para formatar o tempo em minutos e segundos
    private String formataTempo(long tempo) {
        long min = (tempo / 1000) / 60;
        long sec = (tempo / 1000) % 60;
        return String.format("%02d:%02d", min, sec);
    }

    // Contagem regressiva do tempo total da prova
    private void contagemRegressivaTotal(long tempoInicialTotal) {
        new CountDownTimer(tempoInicialTotal, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textNumTimeTotal.setText(formataTempo(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                textNumTimeTotal.setText("Fim!");
            }
        }.start();
    }

    // Contagem regressiva do tempo de cada questão
    private void contagemRegressivaQ(long tempoQuestao) {
        if (timer != null) {
            timer.cancel();  // Cancela o timer anterior
        }
        timer = new CountDownTimer(tempoQuestao, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textNumTempoQ.setText(formataTempo(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                avancarQuestoes();  // Avança para a próxima questão
            }
        }.start();
    }
}
