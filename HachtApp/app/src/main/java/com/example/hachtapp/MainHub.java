package com.example.hachtapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.hachtapp.controller.Controller;

import org.json.JSONObject;

public class MainHub extends AppCompatActivity {

    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_hub);
        final Button demo_btn = findViewById(R.id.button1);
        final Button dash_pacientes_btn = findViewById(R.id.button2);

        data = getIntent().getStringExtra("Data");


        demo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GotoDemo();
            }
        });


        dash_pacientes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GotoDasboardPacientes();
            }
        });



    }


    private void GotoDemo(){
        Intent intent = new Intent(this, Demo.class);
        startActivity(intent);
    }

    private void GotoDasboardPacientes(){
        Intent intent = new Intent(this, Dash_Pacientes.class);
        intent.putExtra("Data", data);
        startActivity(intent);
    }


}
