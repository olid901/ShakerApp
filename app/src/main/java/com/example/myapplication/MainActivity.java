package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.*;


import android.os.Bundle;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private OkHttpClient okHttpClient;

    private static final String URL = "https://www.thecocktaildb.com/api/json/v1/1/random.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Request request = new Request.Builder().url(URL).build();

        this.okHttpClient = new OkHttpClient();


        // use async method, to not block the UI thread
        this.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //Log.e(TAG, "Could not fetch data! Message: " + e);
                System.out.println("Something went wrong there");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String rawResponse = response.body().string();
                    System.out.println(rawResponse);
                    //ist<String> textInfo = getTextInformation(rawResponse);
                    //System.out.println("Response: " + textInfo);
                }
            }
        });
    }

    private List<String> getTextInformation(String rawResponse) {
        List<String> results = new ArrayList<>();
        try {
            JSONObject responseObject = new JSONObject(rawResponse);
            JSONArray responseArray = responseObject.getJSONArray("all");
            for (int index = 0; index < responseArray.length(); index++) {
                JSONObject tempObject = responseArray.getJSONObject(index);
                String text = tempObject.getString("text");
                results.add(text);
            }
        } catch (JSONException e) {
            System.out.println("Something went wrong here");
        }
        return results;
    }

}