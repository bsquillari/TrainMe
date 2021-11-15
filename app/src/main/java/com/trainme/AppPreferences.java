package com.trainme;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AppPreferences {
    private final String AUTH_TOKEN = "auth_token";

    private final SharedPreferences sharedPreferences;

    public AppPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    public void setAuthToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.d("token", "setAuthToken: " + token);
        editor.putString(AUTH_TOKEN, token);
        editor.apply();
    }

    public String getAuthToken() {
        return sharedPreferences.getString(AUTH_TOKEN, null);
    }
}