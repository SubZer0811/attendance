package com.example.attendance;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.LongSummaryStatistics;
import java.util.Random;

import okhttp3.Response;

public class PostScan extends AppCompatActivity {

    private GpsTracker gpsTracker;
    static int CHILD_ACTIVITY_CODE = 1;

    TextView responseText;
    JSONRequest req;
    Intent intent;
    TextView questionTV;
    int answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_scan);
        Log.d("PostScan", "" + getIntent().getStringExtra("QRValue"));

        int num1 = new Random().nextInt(40) - 20;
        int num2 = new Random().nextInt(40) - 20;
        int num3 = new Random().nextInt(40) - 20;

        responseText = (TextView) findViewById(R.id.responseText);
        req = new JSONRequest();
        intent = getIntent();

        questionTV = (TextView) findViewById(R.id.questionTV);
        questionTV.setText(""+num1+"-"+num2+"+"+num3);
        answer = num1 - num2 + num3;

        Button attdBtn = findViewById(R.id.punchAttendance);
        attdBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText answerET = (EditText) findViewById(R.id.answerET);
                String usrAns = answerET.getText().toString();

                if(!(String.valueOf(answer).equals(usrAns))) {
                    responseText.setText("Wrong Answer! Try Again!");
                    return;
                }

                Log.d("FACERECOG", "Starting!");
                Intent intent = new Intent(PostScan.this, FaceRecogActivity.class);
                intent.putExtra("mode", "test");
                startActivityForResult(intent, CHILD_ACTIVITY_CODE);

                Log.d("FACERECOG", "Done!");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            Log.d("DONE", "DONE");

            String keys[] = {"qr_text", "username", "timestamp", "latitude", "longitude"};
            Log.d("TEST", "WHAT IS HAPPENING");

            double latlong[] = new double[2];
            getLocation();
            getLocation();
            latlong = getLocation();

            Log.d("LATLONG", ""+latlong);

            long timestamp = System.currentTimeMillis() / 1000;

            String values[] = {getIntent().getStringExtra("QRValue"),
                    intent.getStringExtra("username"),
                    Long.toString(timestamp),
                    String.valueOf(latlong[0]),
                    String.valueOf(latlong[1])
            };

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

        super.onActivityResult(requestCode, resultCode, data);
    }

    public double[] getLocation(){
        gpsTracker = new GpsTracker(PostScan.this);
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            return new double[]{latitude, longitude};
        } else {
            gpsTracker.showSettingsAlert();
            return new double[]{-200, -200};
        }
    }
}