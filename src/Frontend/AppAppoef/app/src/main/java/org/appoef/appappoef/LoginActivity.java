package org.appoef.appappoef;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.appoef.appappoef.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private TextView campoUsuario, campoSenha, mensagem;
    private Button btnEntrar, btnNaoTenhoConta;
    private RequestQueue requestQueue;
    private final String url = "https://h4592k-3000.csb.app/criarLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // requisição acesso ao servidor
        requestQueue = Volley.newRequestQueue(this);

        // Instanciar dados
        campoUsuario = findViewById(R.id.textInputEditEmailL);
        campoSenha = findViewById(R.id.textInputEditSenhaL);
        mensagem = findViewById(R.id.textMensagemErro);
        btnEntrar = findViewById(R.id.btnEntrar);
        btnNaoTenhoConta = findViewById(R.id.btnNaoTenhoConta);

        btnNaoTenhoConta.setOnClickListener(v ->{
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void Entrar(View view){
        btnEntrar.setEnabled(false);
        // extrai dos Objetos, recuperando a String que pompões:
        String usuario = campoUsuario.getText().toString();
        String senha = campoSenha.getText().toString();
        // Validar dados
        if (TextUtils.isEmpty(usuario) || TextUtils.isEmpty(senha)) {
            mensagem.setText("Os campos usuário ou senha não podem estar vazios.");
        }
        try {
            JSONObject dadosLogin = new JSONObject();
            dadosLogin.put("usuario", usuario);
            dadosLogin.put("senha", senha);
            JsonObjectRequest requisicaoLogin = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    dadosLogin,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean success = response.getBoolean("success");
                                String message = response.optString("message", "");

                                if (success) {
                                    // Salva o token e idLogin localmente
                                    String token = response.getString("token");
                                    int idLogin = response.getInt("idLogin");

                                    SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("token", token);
                                    editor.putInt("idLogin", idLogin);
                                    editor.apply();

                                    // Navega para a próxima tela
                                    Intent intent = new Intent(LoginActivity.this, PrincipalActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    mensagem.setText(message);
                                    btnEntrar.setEnabled(true);
                                }
                            } catch (JSONException e) {
                                mensagem.setText("Erro ao processar resposta do servidor");
                                btnEntrar.setEnabled(true);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String errorMessage = "Erro na conexão com servidor";

                            // Tenta extrair mensagem de erro do servidor
                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                try {
                                    String errorData = new String(error.networkResponse.data, "UTF-8");
                                    JSONObject errorJson = new JSONObject(errorData);
                                    errorMessage = errorJson.optString("message", errorMessage);
                                } catch (Exception e) {
                                    // Mantém a mensagem padrão se não conseguir extrair
                                }
                            }
                            mensagem.setText(errorMessage);
                            btnEntrar.setEnabled(true);
                        }
                    }
            );
            requisicaoLogin.setRetryPolicy(new DefaultRetryPolicy(
                    15000,    // 15 segundos timeout
                    1,        // 1 retry
                    1.0f      // backoffMultiplier
            ));
            requestQueue.add(requisicaoLogin);
        } catch (JSONException e) {
            mensagem.setText("Erro ao preparar dados de login");
            btnEntrar.setEnabled(true);
        }
    }
}