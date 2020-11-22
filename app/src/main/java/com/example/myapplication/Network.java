package com.example.myapplication;

import android.app.Activity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Network {

    public Network(){
        this.okHttpClient = new OkHttpClient();
    }

    private OkHttpClient okHttpClient;

    public void loadCocktails(String URL, ArrayList<Cocktail> CocktailList){

        //ArrayList<Cocktail> Cocktails;
        final Request request = new Request.Builder().url(URL).build();

        //this.okHttpClient = new OkHttpClient();


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

                    //GET ALL THE COCKTAILS INFORMATION (ONLY ID, NAME AND IMG_URL)
                    List<Cocktail> textInfo = extractCocktails(rawResponse);

                    for (Cocktail c : textInfo){
                        addFullCocktailInfo(c);
                        CocktailList.add(c);
                    }

                }
            }
        });
    }

    private void addFullCocktailInfo(Cocktail c){
        String URL = "https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i="+c.getID();
        final Request request = new Request.Builder().url(URL).build();
        //this.okHttpClient = new OkHttpClient();

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
                    try {
                        System.out.println("Getting all the Information of Cocktail "+c.getStrDrink());
                        JSONObject responseObject = new JSONObject(rawResponse);
                        JSONArray responseArray = responseObject.getJSONArray("drinks");
                        //System.out.println("Response: "+responseArray.toString());
                        JSONObject tempObject = responseArray.getJSONObject(0);
                        c.setAlcoholic(tempObject.getString("strAlcoholic"));
                        c.setInstruction(tempObject.getString("strInstructions"));
                        c.setCategory(tempObject.getString("strCategory"));
                        c.setGlass(tempObject.getString("strGlass"));
                        c.setTags(tempObject.getString("strGlass"));

                    } catch (JSONException e) {
                        System.out.println("Something went wrong here");
                    }finally {
                        response.close();
                    }
                }
            }
        });

    }

    private List<Cocktail> extractCocktails(String rawResponse) {
        List<Cocktail> results = new ArrayList<>();
        try {
            JSONObject responseObject = new JSONObject(rawResponse);
            JSONArray responseArray = responseObject.getJSONArray("drinks");
            for (int index = 0; index < responseArray.length(); index++) {
                JSONObject tempObject = responseArray.getJSONObject(index);
                String Name = tempObject.getString("strDrink");
                String Img_Url = tempObject.getString("strDrinkThumb");
                int ID = tempObject.getInt("idDrink");
                results.add(new Cocktail(ID, Name, Img_Url));
            }
        } catch (JSONException e) {
            System.out.println("Something went wrong here");
        }
        return results;
    }


}

    /*static String mResponse;

    public static String getRawResponse(String URL) {
        OkHttpClient okHttpClient;

        final Request request = new Request.Builder().url(URL).build();

        okHttpClient = new OkHttpClient();

        final String[] anwser = new String[1];
        //String mResponse;
        // use async method, to not block the UI thread
        String res = "123";
        Thread callThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String body = okHttpClient.newCall(request).execute().body().toString();
                    res = body;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return res;

        okHttpClient.newCall(request).enqueue(new Callback() {
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
                    mResponse = response.body().string();
                }
            }
        });
        return mResponse;
    }
    }

    private List<Cocktail> getTextInformation(String rawResponse) {
        List<Cocktail> results = new ArrayList<>();
        try {
            JSONObject responseObject = new JSONObject(rawResponse);
            JSONArray responseArray = responseObject.getJSONArray("drinks");
            for (int index = 0; index < responseArray.length(); index++) {
                JSONObject tempObject = responseArray.getJSONObject(index);
                String Name = tempObject.getString("strDrink");
                String Img_Url = tempObject.getString("strDrinkThumb");
                int ID = tempObject.getInt("idDrink");
                results.add(new Cocktail(ID, Name, Img_Url));
            }
        } catch (JSONException e) {
            System.out.println("Something went wrong here");
        }
        return results;
    }*/


