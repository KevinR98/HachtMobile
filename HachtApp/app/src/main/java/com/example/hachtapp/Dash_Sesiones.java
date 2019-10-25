package com.example.hachtapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
    private String[] estados = {"Seguro", "Moderado", "Riesgoso"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacientes);

        ListView resultsListView = findViewById(R.id.list_view);

        JSONObject json;
        try {
            json = new JSONObject(getIntent().getStringExtra("Data"));
            sesiones = json.getJSONArray("sesiones");
            System.out.println(sesiones);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    private void sesionClick(int position){
        try{
            String id = sesiones.getJSONObject(position).getString("id");

            Controller controller = Controller.get_instance();
            controller.setContext(this);

            controller.get_muestras_sesion(id, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response.toString());
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
}
