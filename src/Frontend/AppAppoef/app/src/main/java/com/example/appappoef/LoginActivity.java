package com.example.appappoef;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.content.Intent;
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
import com.example.appappoef.databinding.ActivityCadastroBinding;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    private TextInputEditText campoUsuario, campoSenha;
    private TextView mensagem;
    private RequestQueue requestQueue;
    private String url = "https://7nzcxx-3000.csb.app/usuariosCadastrados";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // requisição acesso ao servidor
        requestQueue = Volley.newRequestQueue(this);

        // Instanciar dados
        campoUsuario = findViewById(R.id.textInputEditTextUsuario);
        campoSenha = findViewById(R.id.textInputEditTextSenha);
        mensagem = findViewById(R.id.textMensagemErro);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void Login(View view){

        // extrai dos Objetos
        String usuario = campoUsuario.getText().toString();
        String senha = campoSenha.getText().toString();


        // Validação de senha e usuario
        if(TextUtils.isEmpty(usuario) || TextUtils.isEmpty(senha)){
            mensagem.setText("Os campos de usuário e senha não podem estar vazios.");
            return;

        }
        autenticarLogin(usuario, senha);

    }
    public void NaoTenhoConta(View view) {
        // Mudar para Tela de Cadastro
        Intent intencao = new Intent(this, CadastroActivity.class);
        startActivity(intencao);
        LimparCampos();
    }

    //mudar para get
    public void autenticarLogin( String usuario,String senha ){


        // extrair dos Objetos
        usuario = campoUsuario.getText().toString();
        senha = campoSenha.getText().toString();

        // criando Json para enviar dados
        JSONObject obj = new JSONObject();
        try{
            obj.put("usuario", usuario);
            obj.put("senha", senha);

        } catch(JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao criar O JSON ", Toast.LENGTH_SHORT).show();
            return;
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, obj, response -> {

            try{
              String token = response.getString("token");
              // armazenar os tokens
                SharedPreferences sharedPreferences = getSharedPreferences("armazeArq", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token_teste", token); // Salvar o token
                editor.apply(); // Aplicar mudanças
                Toast.makeText(LoginActivity.this, "Login cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                // Navegar para a tela principal após o login bem-sucedido
                Intent intent = new Intent(LoginActivity.this,PrincipalActivity.class);
                startActivity(intent);
                LimparCampos();

            }catch (JSONException e){
                e.printStackTrace();
                Toast.makeText(this, "usuario ou senha invalidos! Tente novamente", Toast.LENGTH_SHORT).show();


            }

        }, error -> {
                    if(error.networkResponse != null){
                        Log.e("Volley", "Erro na requisição: " + new String(error.networkResponse.data));
                    }
                }
        );
        // adicionar requisição a fila
        requestQueue.add(jsonObjectRequest);
    }
    public void LimparCampos(){
        campoSenha.setText("");
        mensagem.setText("");
    }

}