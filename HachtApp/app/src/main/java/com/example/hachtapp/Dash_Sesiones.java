package com.example.hachtapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Dash_Sesiones extends AppCompatActivity {

    JSONArray sesiones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacientes);

        JSONObject json;
        try {
            json = new JSONObject(getIntent().getStringExtra("Data"));
            sesiones = json.getJSONArray("sesiones");
            System.out.println(sesiones);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
