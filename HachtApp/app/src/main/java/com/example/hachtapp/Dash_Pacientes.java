package com.example.hachtapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.hachtapp.controller.Controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Dash_Pacientes extends AppCompatActivity {

    JSONArray pacientes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacientes);

        ListView resultsListView = findViewById(R.id.list_view);

        JSONObject json;
        try {
            json = new JSONObject(getIntent().getStringExtra("Data"));
            pacientes = json.getJSONArray("pacientes");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(pacientes == null){
            GotoDashSesiones(getIntent().getStringExtra("Data"));
        }

        List<HashMap<String, String>> listItems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.list_item,
                new String[]{"First Line", "Second Line", "Third Line"},
                new int[]{R.id.text1, R.id.text2, R.id.text3});

        for (int i = 0; i < pacientes.length(); i++)
        {
            HashMap<String, String> resultsMap = new HashMap<>();
            try {
                resultsMap.put("First Line", "Id: " + pacientes.getJSONObject(i).getString("ced"));
                resultsMap.put("Second Line", "Nombre: " + pacientes.getJSONObject(i).getString("nombre"));
                resultsMap.put("Third Line", "Edad: " + pacientes.getJSONObject(i).getString("edad"));
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
                pacienteClick(position);
            }
        });

    }

    private void pacienteClick(int position){
        try{
            String id = pacientes.getJSONObject(position).getString("id");

            Controller controller = Controller.get_instance();
            controller.setContext(this);

            controller.get_sesiones(id, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    GotoDashSesiones(response.toString());
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

    //Move to the next activity
    private void GotoDashSesiones(String data){
        Intent intent = new Intent(this, Dash_Sesiones.class);
        intent.putExtra("Data", data);
        startActivity(intent);
    }
}