package org.appoef.appappoef;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import android.widget.SimpleAdapter;


public class CalendarioActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private EditText editTextTitulo;
    private EditText editTextDescricao;
    private Button btnCadastrar;
    private Button btnDeletar;
    private ListView listEventos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendario);

        CalendarView calendarView = findViewById(R.id.calendarView);
        EditText editTextTitulo = findViewById(R.id.editTextTitulo);
        EditText editTextDescricao = findViewById(R.id.editTextDescricao);
        Button btnCadastrar = findViewById(R.id.btnCadastrar);
        Button btnDeletar = findViewById(R.id.btnDeletar);
        ListView listEventos = findViewById(R.id.listEventos);

        final String[] dataSelecionada = new String[1];

        // Cadastra evento

        btnCadastrar.setOnClickListener(view -> {
            String URL_EVENTO = "https://qyyjfz-3000.csb.app/cadastrarEvento";

            String titulo = editTextTitulo.getText().toString();
            String descricao = editTextDescricao.getText().toString();
            String data = dataSelecionada[0];

            // JSON do evento
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("tituloCalendario", titulo);
                jsonBody.put("descricaoCalendario", descricao);
                jsonBody.put("dataCalendario", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    URL_EVENTO,
                    jsonBody,
                    response -> {
                        /*if (titulo.isEmpty() || descricao.isEmpty() || data == null) {
                            Toast.makeText(CalendarioActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                            return;
                        }*/
                        Toast.makeText(CalendarioActivity.this, "Evento cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        String errorMessage = "Erro ao cadastrar evento: ";
                        if (error.networkResponse != null) {
                            errorMessage += " Código: " + error.networkResponse.statusCode;
                        }
                        errorMessage += " " + error.getMessage();
                        Toast.makeText(CalendarioActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                    //error -> Toast.makeText(CalendarioActivity.this, "Erro ao cadastrar evento", Toast.LENGTH_SHORT).show()
            );

            Volley.newRequestQueue(this).add(request);
        });

        // DELETAR EVENTO

        btnDeletar.setOnClickListener(view -> {
            String url = "https://qyyjfz-3000.csb.app/deletarEvento";

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("dataCalendario", dataSelecionada[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> Toast.makeText(CalendarioActivity.this, "Evento deletado com sucesso", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(CalendarioActivity.this, "Erro ao deletar evento", Toast.LENGTH_SHORT).show()
            );

            Volley.newRequestQueue(this).add(request);
        });

        // EVENTOS CADASTRADOS

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            dataSelecionada[0] = year + "-" + (month + 1) + "-" + dayOfMonth;
            String url = "https://qyyjfz-3000.csb.app/eventosCadastrados?dataCalendario=" + dataSelecionada[0];

            JsonArrayRequest request = new JsonArrayRequest(
                    Request.Method.GET,
                    url,
                    null,
                    response -> {
                        List<Evento> eventos = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonEvento = response.getJSONObject(i);
                                String titulo = jsonEvento.getString("tituloCalendario");
                                String descricao = jsonEvento.getString("descricaoCalendario");
                                String data = jsonEvento.getString("dataCalendario");
                                eventos.add(new Evento(titulo, descricao, data));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // Exibir eventos no ListView

                        List<Map<String, String>> data = new ArrayList<>();
                        for (Evento evento : eventos) {
                            Map<String, String> item = new HashMap<>();
                            item.put("linha1", "Data: " + evento.getData());
                            item.put("linha2", "Título: " + evento.getTitulo());
                            item.put("linha3", "Descrição: " + evento.getDescricao());
                            data.add(item);
                        }

                        SimpleAdapter adapter = new SimpleAdapter(
                                CalendarioActivity.this,
                                data,
                                android.R.layout.simple_list_item_1,
                                new String[]{"linha1", "linha2", "linha3"},
                                new int[]{android.R.id.text1, android.R.id.text2, android.R.id.text2}
                        );

                        listEventos.setAdapter(adapter);

                        //ArrayAdapter<Evento> adapter = new ArrayAdapter<>(CalendarioActivity.this, android.R.layout.simple_list_item_1, eventos);
                        //listEventos.setAdapter(adapter);
                    },
                    error -> Toast.makeText(CalendarioActivity.this, "Erro ao carregar eventos", Toast.LENGTH_SHORT).show()
            );

            Volley.newRequestQueue(this).add(request);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public class Evento {
        private String tituloCalendario;
        private String descricaoCalendario;
        private String dataCalendario;

        public Evento(String titulo, String descricao, String data) {
            this.tituloCalendario = titulo;
            this.descricaoCalendario = descricao;
            this.dataCalendario = data;
        }

        // Getters
        public String getTitulo() { return tituloCalendario; }
        public String getDescricao() { return descricaoCalendario; }
        public String getData() { return dataCalendario; }
    }


}
