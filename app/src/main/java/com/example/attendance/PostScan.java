package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class PostScan extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_scan);
        Log.d("PostScan", getIntent().getStringExtra("QRValue"));
        TextView responseText = (TextView)findViewById(R.id.responseText);
        responseText.setText("Scanned: " + getIntent().getStringExtra("QRValue"));
    }
}