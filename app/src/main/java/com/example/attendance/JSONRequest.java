package com.example.attendance;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JSONRequest {

    static JSONObject sendRequest(String[] keys, String[] values, String route) throws InterruptedException {
        JSONObject JSONForm = new JSONObject();
        try {
            for (int i=0; i<keys.length; i+=1) {
                JSONForm.put(keys[i], values[i]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
//            TODO: Add Toast or some other debug display
        }

//        OkHttpHandler okHttpHandler= new OkHttpHandler();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSONForm.toString());
//        okHttpHandler.execute(MainActivity.postUrl+route, body);

        return postRequest(MainActivity.postUrl+route, body);

    }



    public static JSONObject postRequest(String postUrl, RequestBody postBody) throws InterruptedException {
        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

//        final JSONObject[] response = new JSONObject[1];
        Log.d("START", "START");

        final JSONObject[] JSONresp = {null};
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Response response = client.newCall(request).execute()) {
                    JSONresp[0] = new JSONObject(response.body().string().trim());
                    String message = JSONresp[0].getString("error_message");
                    Log.v("IS IT WORKING", message);
                } catch (IOException e) {
                    String error_msg = "{'error_message':'Failed to Connect to Server. Please Try Again.'}";
                    try {
                        JSONresp[0] = new JSONObject(error_msg);
                        Log.v("Failure", ""+ JSONresp[0]);
                    } catch (JSONException er) {
                        er.printStackTrace();
                    }
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
        t.join();

        return JSONresp[0];
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                // Cancel the post on failure.
//                call.cancel();
//                Log.v("onFailure", "onFailure");
//                Log.v("FAIL", e.getMessage());
//                Log.v("test", "Failed to Connect to Server. Please Try Again.");
//                String error_msg = "{'error_message':'Failed to Connect to Server. Please Try Again.'}";
//
//                try {
//                    JSONObject resp = new JSONObject(error_msg);
//                    response[0] = resp;
//                    Log.v("response", ""+response[0]);
//                    Log.v("resp", ""+resp);
//                } catch (JSONException er) {
//                    er.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onResponse(Call call, final Response resp) throws IOException {
//                final String responseString = resp.body().string().trim();
//                JSONObject res = null;
//                try {
//                    res = new JSONObject(responseString);
//                    String message = res.getString("error_message");
//                    Log.v("IS IT WORKING", message);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        Log.d("END", "END");
    }
}

