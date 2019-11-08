package com.example.hachtapp.controller;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// Singleton Controller
// The idea is to have one and only one controller for all the views.
public class Controller {

    // Dominio para conexión a python anywhere
    private final String domain = "http://martinvc96.pythonanywhere.com/";

    // Dominio para conexión local
    //private final String domain = "http://192.168.0.15:8000/";

    private static Controller instance;
    private Context ctx;
    private String cookies;
    private String session_id;

    public static Controller get_instance(){
        return (instance != null) ? instance : new Controller();
    }

    private Controller(){
        ctx = null;
        instance = this;
    }

    private Controller(Context ctx, Response.Listener listener){
        this.ctx = ctx;
        request_get_cookies(domain, listener, null);
        instance = this;
    }

    public void initialize(Context ctx, Response.Listener listener){
        this.ctx = ctx;
        request_get_cookies(domain, listener, null);
    }

    public Context getContext(){ return ctx; }

    public void setContext(Context ctx){
        this.ctx = ctx;
    }

    private void request_get_cookies(String main_url, Response.Listener listener,
                             Response.ErrorListener errorListener){

        String url = main_url + "?android=1";

        RequestQueue queue = Volley.newRequestQueue(ctx);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                new JSONObject(), listener, errorListener)
        {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                // since we don't know which of the two underlying network vehicles
                // will Volley use, we have to handle and store session cookies manually
                Map<String, String> responseHeaders = response.headers;
                cookies = responseHeaders.get("Set-Cookie");
                return super.parseNetworkResponse(response);
            }
        };

        queue.add(request);

    }

    private void request_get(String main_url, Response.Listener listener,
                             Response.ErrorListener errorListener){

        String url = main_url + "?android=1";

        RequestQueue queue = Volley.newRequestQueue(ctx);
        //final String token = cookies.substring(cookies.indexOf('=')+1, cookies.indexOf(';'));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                new JSONObject(), listener, errorListener){

            /**
             * Passing some request headers*
             */
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                //headers.put("X-CSRFToken", token);
                headers.put("Cookie", add_session_cookies());
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                // since we don't know which of the two underlying network vehicles
                // will Volley use, we have to handle and store session cookies manually
                Map<String, String> responseHeaders = response.headers;
                String rawCookies = responseHeaders.get("Set-Cookie");
                cookies = rawCookies;
                return super.parseNetworkResponse(response);
            }
        };

        queue.add(request);

    }

    private void request_get(String main_url, Map<String, String> params,
                                   Response.Listener listener, Response.ErrorListener errorListener){

        String url = main_url + "?android=1";

        for(String key : params.keySet()){
            url += '&' + key + "=" + params.get(key);
        }

        RequestQueue queue = Volley.newRequestQueue(ctx);
        //final String token = cookies.substring(cookies.indexOf('=')+1, cookies.indexOf(';'));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                new JSONObject(), listener, errorListener){

            /**
             * Passing some request headers*
             */
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                //headers.put("X-CSRFToken", token);
                headers.put("Cookie", add_session_cookies());
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                // since we don't know which of the two underlying network vehicles
                // will Volley use, we have to handle and store session cookies manually
                Map<String, String> responseHeaders = response.headers;
                String rawCookies = responseHeaders.get("Set-Cookie");
                cookies = rawCookies;
                return super.parseNetworkResponse(response);
            }
        };

        queue.add(request);

    }

    private void request_post(String url, final Map<String, String> params,
                              Response.Listener listener,
                              Response.ErrorListener errorListener){

        RequestQueue queue = Volley.newRequestQueue(ctx);

        final String token = cookies.substring(cookies.indexOf('=')+1, cookies.indexOf(';'));
        params.put("csrfmiddlewaretoken", token);

        StringRequest request = new StringRequest(Request.Method.POST, url, listener, errorListener)
        {
            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("X-CSRFToken", token);
                headers.put("Cookie", cookies);
                return headers;
            }

             @Override
             protected Response<String> parseNetworkResponse(NetworkResponse response) {
                 // since we don't know which of the two underlying network vehicles
                 // will Volley use, we have to handle and store session cookies manually
                 Map<String, String> responseHeaders = response.headers;
                 String rawCookies = responseHeaders.get("Set-Cookie");
                 cookies = rawCookies;
                 return super.parseNetworkResponse(response);
             }

             @Override
             protected Map<String, String> getParams(){
                return params;
             }
         };


        queue.add(request);

    }


    public void login(String nombre, String pass, Response.Listener listener,
                      Response.ErrorListener errorListener){

        Map<String, String>  params = new HashMap<String, String>();
        params.put("username", nombre);
        params.put("password", pass);
        request_post(domain + "login_app/", params, listener, errorListener);
    }

    public void get_pacientes(Response.Listener listener,
                              Response.ErrorListener errorListener){

        request_get(domain + "dashboard_pacientes/",
                listener,
                errorListener);

    }

    public void get_sesiones(String id_paciente,
                             Response.Listener listener,
                             Response.ErrorListener errorListener){

        HashMap<String, String> params = new HashMap<>();
        params.put("id_paciente", id_paciente);

        request_get(domain + "dashboard_sesiones/",
                params,
                listener,
                errorListener);

    }

    public void get_muestras_sesion(String id_sesion,
                             Response.Listener listener,
                             Response.ErrorListener errorListener){

        HashMap<String, String> params = new HashMap<>();
        params.put("id_sesion", id_sesion);

        request_get(domain + "dashboard_sesiones/components/muestras_sesion/",
                params,
                listener,
                errorListener);

    }

    public void test_sample(String url,
                                    Response.Listener listener,
                                    Response.ErrorListener errorListener){

        HashMap<String, String> params = new HashMap<>();
        params.put("url", url);

        request_get(domain + "demo_app/",
                params,
                listener,
                errorListener);

    }

    public void ver_graficos_sesion(
            String id_sesion,
            Response.Listener listener,
            Response.ErrorListener errorListener){

        HashMap<String, String> params = new HashMap<>();
        params.put("id_sesion", id_sesion);

        request_get(domain + "dashboard_sesiones/components/analytics_sesion/", params, listener, errorListener);

    }


    private String add_session_cookies(){
        if (cookies != null) {
            if (!cookies.contains("sessionid")) {
                return "sessionid=" + session_id + "; " + cookies;
            } else {
                String session = cookies.substring(cookies.indexOf('=') + 1, cookies.indexOf(';'));
                session_id = session;
                return cookies;
            }
        }else{
            return "";
        }
    }


}
