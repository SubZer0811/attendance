package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        Intent intent = new Intent(RegisterActivity.this, FaceRecogActivity.class);
//        intent.putExtra("mode", "train");
//        startActivity(intent);
//        finish();
        Button regBtn = findViewById(R.id.regBtn);
        regBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    register(v);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void register(View v) throws JSONException, InterruptedException {
        EditText usernameView = findViewById(R.id.idInpText);
        EditText fullnameView = findViewById(R.id.fullnameInpText);
        EditText passwordView = findViewById(R.id.pwdInpText);
        EditText passwordRepView = findViewById(R.id.pwdRepInpText);

//        RadioButton studBtn = findViewById(R.id.studBtn);
//        RadioButton facBtn = findViewById(R.id.facBtn);

        String username = usernameView.getText().toString().trim();
        String fullname = fullnameView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();
        String passwordRep = passwordRepView.getText().toString().trim();
        int role = 2;

        TextView responseText = findViewById(R.id.responseTextRegister);
        responseText.setText("Connecting to server...");

        if (fullname.length() == 0 || username.length() == 0 || password.length() == 0 || passwordRep.length() == 0) {
            Toast.makeText(getApplicationContext(), "All fields need to be filled.", Toast.LENGTH_LONG).show();
        } else {
            String keys[] = {"username", "fullname", "password", "password_rep", "role"};
            String values[] = {username, fullname, password, passwordRep, ""+role};

            JSONObject response = JSONRequest.sendRequest(keys, values, "register");
            Log.d("JSON response", ""+response);
            String msg = response.getString("error_message");
            responseText.setText(msg);

            if(msg.equals("success")) {
                Intent intent = new Intent(RegisterActivity.this, FaceRecogActivity.class);
                intent.putExtra("mode", "train");
                startActivity(intent);
                finish();
            }
        }
    }
}