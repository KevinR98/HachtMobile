package com.example.hachtapp.ui.login;

import org.json.JSONObject;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String displayName;
    private JSONObject response;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName, JSONObject response) {
        this.displayName = displayName;
        this.response = response;
    }

    String getDisplayName() {
        return displayName;
    }

    JSONObject getResponse() { return response; }
}
