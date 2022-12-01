package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_login);
        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    login(v);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void login(View v) throws InterruptedException, JSONException {
        EditText usernameView = findViewById(R.id.idInpText);
        EditText passwordView = findViewById(R.id.pwdInpText);

        String username = usernameView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();

        TextView responseText = findViewById(R.id.responseTextLogin);

        if (username.length() == 0 || password.length() == 0) {
            Toast.makeText(getApplicationContext(), "All fields are mandatory!", Toast.LENGTH_LONG).show();
            return;
        }
        String keys[] = {"username", "password"};
        String values[] = {username, password};

        JSONObject response = JSONRequest.sendRequest(keys, values, "login");
        Log.d("JSON response", ""+response);
        String msg = response.getString("error_message");
        Log.d("msg", msg);
        responseText.setText(msg);

        if(msg.equals("success")) {
            if(username.toString().equals("admin")) {
                Intent intent = new Intent(LoginActivity.this, CreateRoomActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            } else {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        }
    }
}