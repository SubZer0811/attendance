package com.example.attendance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateRoomActivity extends Activity {

    TextView locsTV;
    TextView submitTV;
    EditText hallnumber;

    private GpsTracker gpsTracker;

    double lats[] = new double[4];
    double longs[] = new double[4];
    String latlong[] = new String[4];

    int curCorner = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        locsTV = findViewById(R.id.locsTV);
        submitTV = findViewById(R.id.submitFeedback);
        hallnumber = findViewById(R.id.hallNumber);
        gpsTracker = new GpsTracker(CreateRoomActivity.this);

    }

    public void scanCorner(View v) {
        if(gpsTracker.canGetLocation()) {
            lats[curCorner] = gpsTracker.getLatitude();
            longs[curCorner] = gpsTracker.getLongitude();
            StringBuilder latlongstr = new StringBuilder((100));
            latlongstr.append(lats[curCorner]);
            latlongstr.append(",");
            latlongstr.append(longs[curCorner]);

            latlong[curCorner] = String.valueOf(latlongstr);
            Log.d("GPS", "here");
            StringBuilder text = new StringBuilder(1000);
            for(int i=0; i<=curCorner; i+=1) {
//                text.append(lats[i]);
//                text.append(" ");
//                text.append(longs[i]);
//                text.append("\n");
                text.append(latlong[i]);
                text.append("\n");

            }
            Log.d("GPS", text.toString());
            locsTV.setText(text.toString());
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    public void nextCorner(View v) {
        if(curCorner < 3) {
            curCorner = curCorner + 1;
        }
    }

    public void submit(View v) {
        if(hallnumber.getText().toString().trim().length() == 0) {
            submitTV.setText("Hall Number cannot be empty!");
            return;
        }
        if(curCorner != 3) {
            submitTV.setText("Please scan 4 corners of the room!");
            return;
        }
        String keys[] = {"room_name", "gps_coord_1", "gps_coord_2", "gps_coord_3", "gps_coord_4"};
        String values[] = {String.valueOf(hallnumber.getText()), latlong[0], latlong[1], latlong[2], latlong[3]};

        JSONObject response = null;
        try {
            response = JSONRequest.sendRequest(keys, values, "create_room");
            Log.d("JSON response", ""+response);
            String msg = response.getString("message");
            Log.d("msg", msg);
            submitTV.setText(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
