package com.example.hachtapp.controller;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// Singleton Controller
// The idea is to have one and only one controller for all the views.
public class Controller {

    private static Controller instance;
    private Context ctx;
    private String cookies;

    public static Controller get_instance(){
        return (instance != null) ? instance : new Controller();
    }

    private Controller(){
        ctx = null;
        instance = this;
    }

    private Controller(Context ctx, Response.Listener listener){
        this.ctx = ctx;
        request_get_cookies("http://martinvc96.pythonanywhere.com/", listener, null);
        instance = this;
    }

    public void initialize(Context ctx, Response.Listener listener){
        this.ctx = ctx;
        request_get_cookies("http://martinvc96.pythonanywhere.com/", listener, null);
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
        final String token = cookies.substring(cookies.indexOf('=')+1, cookies.indexOf(';'));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                new JSONObject(), listener, errorListener){

            /**
             * Passing some request headers*
             */
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-CSRFToken", token);
                headers.put("Cookie", cookies);
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
            char delimiter = '&';
            url += delimiter + key + params.get(key);
        }

        RequestQueue queue = Volley.newRequestQueue(ctx);
        final String token = cookies.substring(cookies.indexOf('=')+1, cookies.indexOf(';'));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                new JSONObject(), listener, errorListener){

            /**
             * Passing some request headers*
             */
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-CSRFToken", token);
                headers.put("Cookie", cookies);
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
                              Response.ErrorListener errorListener) throws JSONException {

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
                      Response.ErrorListener errorListener) throws JSONException{

        Map<String, String>  params = new HashMap<String, String>();
        params.put("username", "martin@algo.com");
        params.put("password", "1234");
        request_post("http://martinvc96.pythonanywhere.com/login_app/", params, listener, errorListener);

    }

    public void get_pacientes(Response.Listener listener,
                              Response.ErrorListener errorListener){

        request_get("http://martinvc96.pythonanywhere.com/dashboard_pacientes/",
                listener,
                errorListener);

    }


}