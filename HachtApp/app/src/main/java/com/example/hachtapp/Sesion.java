package com.example.hachtapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class Sesion extends AppCompatActivity {

    ScrollView scrollView;
    private JSONArray muestras;
    private JSONObject sesion;
    private Button button_graficos_sesion;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sesion);

        button_graficos_sesion = findViewById(R.id.button_graficos_sesion);

        JSONObject json;
        try {
            json = new JSONObject(getIntent().getStringExtra("Data"));
            muestras = json.getJSONArray("muestras");
            sesion = json.getJSONObject("sesion");

            button_graficos_sesion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try{

                        // Paciente
                        String id_sesion = sesion.getString("id");
                        String url = "http://martinvc96.pythonanywhere.com/dashboard_sesiones/components/analytics_sesion/?android=1&id_sesion=";
                        url += id_sesion;

                        // Lleva a actividad webview
                        GotoGraficos(url);

                    }catch (Exception e){
                        System.out.println("Ha habido un error extrayendo la información");
                    }

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        scrollView = findViewById(R.id.scroll);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);



        LinearLayout.LayoutParams params_names = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params_names.setMargins(100,300,100,100);


        LinearLayout.LayoutParams params_images = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params_images.setMargins(10,0,10,0);


        for (int i = 0; i < muestras.length(); i++)
        {
            try {
                String pred = muestras.getJSONObject(i).getString("pred");
                String url = muestras.getJSONObject(i).getString("url_img");
                String validated = muestras.getJSONObject(i).getString("pred_true");

                if(validated.equals("null")){
                    System.out.println("entra");
                    validated = "Estimación no validada";}






                //clean url

                url.replace("\\","" );



                TextView name = new TextView(this);
                name.setText("Estimación obtenida:" + '\n' + pred + '\n' + '\n' + "Estimación corecta:" + '\n' + validated);
                name.setLayoutParams(params_names);
                name.setTextSize(20);
                name.setGravity(Gravity.CENTER);
                linearLayout.addView(name);

                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(params_images);
                new GetImageFromURL(imageView).execute(url);
                linearLayout.addView(imageView);






            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //dummy text added so scrollview can get to the bottom
        TextView name = new TextView(this);
        name.setText("");
        linearLayout.addView(name);
        name.setLayoutParams(params_names);
        scrollView.addView(linearLayout);

    }

    //Class for download IMAGE
    public class GetImageFromURL extends AsyncTask<String, Void, Bitmap> {
        ImageView imgV;

        public GetImageFromURL(ImageView imgV){
            this.imgV = imgV;
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            String urldisplay = url[0];
            bitmap = null;
            try {
                InputStream srt = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(srt);
            } catch (Exception e){
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imgV.setImageBitmap(bitmap);
        }
    }

    //Move to the next activity
    private void GotoGraficos(String data){
        Intent intent = new Intent(this, Graficos_Sesion.class);
        intent.putExtra("url", data);
        startActivity(intent);
    }



}
