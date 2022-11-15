package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;

import okhttp3.Response;

public class PostScan extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_scan);
        Log.d("PostScan", "" + getIntent().getStringExtra("QRValue"));
        TextView responseText = (TextView) findViewById(R.id.responseText);
        JSONRequest req = new JSONRequest();
        Intent intent = getIntent();

        Button attdBtn = findViewById(R.id.punchAttendance);
        attdBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String keys[] = {"qr_text", "username", "timestamp"};
                Log.d("TEST", "WHAT IS HAPPENING");

                long timestamp = System.currentTimeMillis() / 1000;

//                String timestamp = (String) android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", new java.util.Date());
                Log.d("TIMESTAMP", "" + timestamp);
                String values[] = {getIntent().getStringExtra("QRValue"), intent.getStringExtra("username"), Long.toString(timestamp)};
                JSONObject response = null;
                try {
                    response = JSONRequest.sendRequest(keys, values, "punch_attendance");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    responseText.setText(response.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


//        Response response = JSONRequest.sendRequest(keys, values, "test");

    }
}