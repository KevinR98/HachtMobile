package com.example.hachtapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.toolbox.JsonArrayRequest;

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
            }
        });

    }
}