package com.example.hachtapp.ui.login;

import android.app.Activity;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import android.content.Intent;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hachtapp.MainHub;
import com.example.hachtapp.R;
import com.example.hachtapp.controller.Controller;
import com.example.hachtapp.ui.login.LoginViewModel;
import com.example.hachtapp.ui.login.LoginViewModelFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Controller controller = Controller.get_instance();
        final LifecycleOwner lifecycleOwner = this;
        final Context ctx = this;

        /*
        * Renderiza la app solo si tiene respuesta del servidor
        * Es necesario obtener información de éste.
        * */
        controller.initialize(this,

                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {

                        setContentView(R.layout.activity_login);
                        loginViewModel = ViewModelProviders.of((FragmentActivity) ctx, new LoginViewModelFactory())
                                .get(LoginViewModel.class);

                        final EditText usernameEditText = findViewById(R.id.username);
                        final EditText passwordEditText = findViewById(R.id.password);
                        final Button loginButton = findViewById(R.id.login);
                        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

                        loginViewModel.getLoginFormState().observe(lifecycleOwner, new Observer<LoginFormState>() {
                            @Override
                            public void onChanged(@Nullable LoginFormState loginFormState) {
                                if (loginFormState == null) {
                                    return;
                                }
                                loginButton.setEnabled(loginFormState.isDataValid());
                                if (loginFormState.getUsernameError() != null) {
                                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                                }
                                if (loginFormState.getPasswordError() != null) {
                                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                                }
                            }
                        });

                        loginViewModel.getLoginResult().observe(lifecycleOwner, new Observer<LoginResult>() {
                            @Override
                            public void onChanged(@Nullable LoginResult loginResult) {
                                if (loginResult == null) {
                                    return;
                                }
                                loadingProgressBar.setVisibility(View.GONE);

                                if (loginResult.getError() != null) {
                                    showLoginFailed(loginResult.getError());
                                }
                                if (loginResult.getSuccess() != null) {
                                    updateUiWithUser(loginResult.getSuccess());
                                    GotoMainHub(loginResult.getSuccess().getResponse().toString());
                                }

                                setResult(Activity.RESULT_OK);

                                /*
                                Debería hacer el nuevo activity o view acá!!!
                                 */
                                //finish();
                            }
                        });

                        TextWatcher afterTextChangedListener = new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                // ignore
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                // ignore
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                                        passwordEditText.getText().toString());
                            }
                        };
                        usernameEditText.addTextChangedListener(afterTextChangedListener);
                        passwordEditText.addTextChangedListener(afterTextChangedListener);
                        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                    loginViewModel.login(usernameEditText.getText().toString(),
                                            passwordEditText.getText().toString());
                                }
                                return false;
                            }
                        });

                        loginButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadingProgressBar.setVisibility(View.VISIBLE);

                                loginViewModel.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                            }
                        });

                    }
                });


    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    //Move to the next activity
    private void GotoMainHub(String data){
        Intent intent = new Intent(this, MainHub.class);
        intent.putExtra("Data", data);
        startActivity(intent);
    }

}
