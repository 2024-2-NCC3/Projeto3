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

        // CADASTRAR EVENTO

        btnCadastrar.setOnClickListener(view -> {
            String url = "https://qyyjfz-3000.csb.app/cadastrarEvento";

            String titulo = editTextTitulo.getText().toString();
            String descricao = editTextDescricao.getText().toString();
            String data = dataSelecionada[0];

            // Cria JSON do evento
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
                    url,
                    jsonBody,
                    response -> Toast.makeText(CalendarioActivity.this, "Evento cadastrado com sucesso", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(CalendarioActivity.this, "Erro ao cadastrar evento", Toast.LENGTH_SHORT).show()
            );

            Volley.newRequestQueue(this).add(request);
        });

        // DELETAR EVENTO

        btnDeletar.setOnClickListener(view -> {
            String url = "https://qyyjfz-3000.csb.app/deletarEvento";

            // Cria JSON com a data do evento a ser deletado
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
            String url = "https://qyyjfz-3000.csb.app/eventosCadastrados" + dataSelecionada[0];

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
                        ArrayAdapter<Evento> adapter = new ArrayAdapter<>(CalendarioActivity.this, android.R.layout.simple_list_item_1, eventos);
                        listEventos.setAdapter(adapter);
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