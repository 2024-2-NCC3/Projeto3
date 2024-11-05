package org.appoef.appappoef;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.appoef.appappoef.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    TextView campoUsuario, campoSenha, mensagem;
    Button btnEntrar, btnNaoTenhoConta;
    RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;
    String url = "https://2g9tc9-3000.csb.app/login";

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
            mensagem.setText("Os campos usuário e senha não podem estar vazios.");
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
                            Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, PrincipalActivity.class);
                            startActivity(intent);
                        } else {
                            // Falha no login
                            String mensagemErro = response.getString("message");
                            mensagem.setText(mensagemErro);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erro ao processar resposta do servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMessage = new String(error.networkResponse.data);
                        Log.e("Login", "Erro ao logar: " + errorMessage);
                        mensagem.setText("Erro ao logar: " + errorMessage);
                    } else {
                        Toast.makeText(this, "Erro de rede. Verifique sua conexão.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

}
