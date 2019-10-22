package com.example.hachtapp.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.hachtapp.controller.Controller;
import com.example.hachtapp.data.LoginRepository;
import com.example.hachtapp.data.Result;
import com.example.hachtapp.data.model.LoggedInUser;
import com.example.hachtapp.R;

import org.json.JSONObject;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(final String username, String password) {

        // can be launched in a separate asynchronous job
        //Result<LoggedInUser> result = loginRepository.login(username, password);

        final Controller controller = Controller.get_instance();

        controller.login(username, password,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        controller.get_pacientes(new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                loginResult.setValue(new LoginResult(new LoggedInUserView(username, response)));
                            }}, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                loginResult.setValue(new LoginResult(R.string.login_failed));
                            }
                        });
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loginResult.setValue(new LoginResult(R.string.login_failed));
                    }
                }
        );
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && !password.isEmpty();
    }
}
