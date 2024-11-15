package org.appoef.appappoef;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    TextView campoUsuario, campoSenha, mensagem;
    Button btnEntrar, btnNaoTenhoConta;
    RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;
    String url = "https://3756jq-3000.csb.app/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Requisição acesso ao servidor
        requestQueue = Volley.newRequestQueue(this);

        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);

        // Exemplo de como salvar no SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", "");
        editor.apply();

        // Instanciar dados
        campoUsuario = findViewById(R.id.textInputEditEmailL);
        campoSenha = findViewById(R.id.textInputEditSenhaL);
        mensagem = findViewById(R.id.textMensagemErro);
        btnEntrar = findViewById(R.id.btnEntrar);
        btnNaoTenhoConta = findViewById(R.id.btnNaoTenhoConta);

        btnNaoTenhoConta.setOnClickListener(v -> {
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

    public void Entrar(View view) {
        String usuario = campoUsuario.getText().toString().trim();
        String senha = campoSenha.getText().toString().trim();

        if (usuario.isEmpty() || senha.isEmpty()) {
            mensagem.setText("Por favor, preencha os campos de usuário e senha.");
            return;
        }
        loginUsuario(usuario, senha);
    }

    public void loginUsuario(String usuario, String senha) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("usuario", usuario);
            obj.put("senha", senha); // Enviando a senha não criptografada para o servidor
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            // Sucesso no login
                            String token = response.getString("token");

                            // Salvar o token noSharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("token", token);
                            editor.apply();

                            // Chamar a rota protegida
                            acessarRotaProtegida(token);

                            // Continue para a próxima atividade
                            Toast.makeText(this, "Bem-vindo! Você está logado.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, PrincipalActivity.class);
                            startActivity(intent);
                        } else {
                            // Falha no login
                            String mensagemErro = response.getString("message");
                            mensagem.setText("Erro: " + mensagemErro);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Ocorreu um erro. Tente novamente.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        String errorMessage = new String(error.networkResponse.data);
                        Log.e("Erro", "Erro ao acessar a rota protegida: " + errorMessage);
                        mensagem.setText("Não foi possível completar o login. Verifique sua conexão com a internet.");
                    } else {
                        Log.e("Erro", "Erro de rede. Verifique sua conexão: " + error.getMessage());
                        mensagem.setText("Não conseguimos conectar ao servidor. Tente novamente mais tarde.");
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    // Metodo para acessar uma rota protegida
    public void acessarRotaProtegida(String token) {
        String urlProtegido = "https://3756jq-3000.csb.app/usuario";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlProtegido, null,
                response -> {
                    // Tratar resposta da rota protegida
                    Log.d("Resposta", response.toString());
                },
                error -> {
                    // Tratar erro
                    Log.e("Erro", "Erro ao acessar a rota protegida: " + error.getMessage());
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }
}
