package com.example.hachtapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.hachtapp.controller.Controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Dash_Sesiones extends AppCompatActivity {

    private JSONArray sesiones;
    private JSONObject paciente;
    private String[] estados = {"Seguro", "Moderado", "Riesgoso"};
    ListView resultsListView;
    TextView txt_sesiones;
    Button button_graficos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash__sesiones);

        resultsListView = findViewById(R.id.list_view);
        txt_sesiones = findViewById(R.id.textView3);
        button_graficos = findViewById(R.id.button_graficos);

        JSONObject json;
        try {
            json = new JSONObject(getIntent().getStringExtra("Data"));
            paciente = json.getJSONObject("paciente");
            sesiones = json.getJSONArray("sesiones");
            System.out.println(sesiones);

            txt_sesiones.setVisibility(View.GONE);
            button_graficos.setVisibility(View.VISIBLE);

            button_graficos.setOnClickListener(
                    new View.OnClickListener(){

                        public void onClick(View view){

                            try{

                                // Paciente
                                String id_paciente = paciente.getString("id");
                                String url = "http://martinvc96.pythonanywhere.com/dashboard_pacientes/components/analytics_paciente/?android=1&id_paciente=";
                                url += id_paciente;

                                // Lleva a actividad webview
                                GotoGraficos("http://martinvc96.pythonanywhere.com/dashboard_sesiones/components/analytics_sesion/?android=1&id_sesion=2");

                            }catch (Exception e){
                                System.out.println("Ha habido un error extrayendo la información");
                            }
                        }

                    }
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        FillListView();


    }

    private void sesionClick(int position){
        try{
            String id = sesiones.getJSONObject(position).getString("id");

            Controller controller = Controller.get_instance();
            controller.setContext(this);

            controller.get_muestras_sesion(id, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    GotoSesion(response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("No se pudo obtener las sesiones del paciente, dentro del request");
                }
            });

        }catch (Exception e){
            System.out.println("No se pudo obtener las sesiones del paciente");
        }
    }

    private void GotoSesion(String data){
        Intent intent = new Intent(Dash_Sesiones.this, Sesion.class );
        intent.putExtra("Data", data);
        startActivity(intent);

    }

    private void FillListView(){
        List<HashMap<String, String>> listItems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.list_item,
                new String[]{"First Line", "Second Line"},
                new int[]{R.id.text1, R.id.text2});

        for (int i = 0; i < sesiones.length(); i++)
        {
            HashMap<String, String> resultsMap = new HashMap<>();
            try {
                String fecha = sesiones.getJSONObject(i).getString("created_at");
                int index_estado = Integer.parseInt(sesiones.getJSONObject(i).getString("estado"));

                resultsMap.put("First Line", "Fecha: " + fecha.substring(0, 10));
                resultsMap.put("Second Line", "Estado: " + estados[index_estado]);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            listItems.add(resultsMap);
        }

        resultsListView.setAdapter(adapter);
        resultsListView.setClickable(true);
        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                System.out.println(position);
                sesionClick(position);
            }
        });

    }

    //Move to the next activity
    private void GotoGraficos(String data){
        Intent intent = new Intent(this, Graficos_Sesion.class);
        intent.putExtra("url", data);
        startActivity(intent);
    }
}
