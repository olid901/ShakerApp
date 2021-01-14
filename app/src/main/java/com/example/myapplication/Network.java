package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;


public class Network {

    static private OkHttpClient okHttpClient;

    public Network(){
        this.okHttpClient = new OkHttpClient();
    }



    public static void loadIngredients(String URL, HashMap<String, Ingredient> IngredientMap){
        final Request request = new Request.Builder().url(URL).build();

        // use async method, to not block the UI thread
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
                    System.out.println("Ingredient response: "+rawResponse);
                    extractIngredients(rawResponse, IngredientMap);
                }
            }
        });

    }



    public static void addFullIngredientInfo(Ingredient i){
        //TBA: Siehe Karte Zutaten abfragen
    }


    public static void downloadPic(String Filename, String URL){
        Request request = new Request.Builder().url(URL).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to download file: " + response);
                }

                InputStream inputStream = response.body().byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                System.out.println("Trying to save file");
                //File file = new File (android.os.Environment.getExternalStorageDirectory(),"test2.jpg");
                File file = new File(MainActivity.localDir, "Filename");

                // ???
                if (file.exists ()) file.delete ();
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("saved!");
            }
        });
    }

    public static void loadCocktails(String URL, HashMap<Integer, Cocktail> CocktailMap) {

        final Request request = new Request.Builder().url(URL).build();

        // use async method, to not block the UI thread
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
                    System.out.println("Cocktailsdata: "+rawResponse);

                    extractAndAddCocktails(rawResponse, CocktailMap);


                }
            }
        });
    }


    public static void addFullCocktailInfo(Cocktail c){

        String URL = "https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i="+c.getID();
        final Request request = new Request.Builder().url(URL).build();

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
                    try {
                        //System.out.println("Getting all the Information of Cocktail "+c.getStrDrink());
                        JSONObject responseObject = new JSONObject(rawResponse);
                        JSONArray responseArray = responseObject.getJSONArray("drinks");
                        JSONObject tempObject = responseArray.getJSONObject(0);
                        c.setAlcoholic(tempObject.getString("strAlcoholic"));
                        c.setInstruction(tempObject.getString("strInstructions"));
                        c.setCategory(tempObject.getString("strCategory"));
                        c.setGlass(tempObject.getString("strGlass"));
                        c.setTags(tempObject.getString("strGlass"));
                        extractAndAddIngredientsAndMeasurments(rawResponse, c);

                    } catch (JSONException e) {
                        System.out.println("Something went wrong here");
                    }finally {
                        response.close();
                    }
                }
            }
        });

    }

    private static void extractAndAddCocktails(String rawResponse, HashMap<Integer, Cocktail> Cocktails) {
        //List<Cocktail> results = new ArrayList<>();
        try {
            JSONObject responseObject = new JSONObject(rawResponse);
            JSONArray responseArray = responseObject.getJSONArray("drinks");
            for (int index = 0; index < responseArray.length(); index++) {
                JSONObject tempObject = responseArray.getJSONObject(index);
                String Name = tempObject.getString("strDrink");
                String Img_Url = tempObject.getString("strDrinkThumb");
                int ID = tempObject.getInt("idDrink");
                Cocktails.put(ID, new Cocktail(ID, Name, Img_Url));
            }
        } catch (JSONException e) {
            System.out.println("Something went wrong here");
        }

    }

    private static void extractIngredients(String rawResponse, HashMap<String, Ingredient> IngredientList) {
        try {
            JSONObject responseObject = new JSONObject(rawResponse);
            JSONArray responseArray = responseObject.getJSONArray("drinks");
            for (int index = 0; index < responseArray.length(); index++) {
                JSONObject tempObject = responseArray.getJSONObject(index);
                String Name = tempObject.getString("strIngredient1");
                IngredientList.put(Name, new Ingredient(Name));
            }
        } catch (JSONException e) {
            System.out.println("Something went wrong here");
        }
    }

    private static void extractAndAddIngredientsAndMeasurments(String rawResponse, Cocktail c){
        try {

            JSONObject responseObject = new JSONObject(rawResponse);
            JSONArray responseArray = responseObject.getJSONArray("drinks");
            JSONObject tempObject = responseArray.getJSONObject(0);
            String Ingredient, Measurement;

            for(int i = 1; i <= 15; i++){
                Ingredient = tempObject.getString("strIngredient"+i);
                Measurement = tempObject.getString("strMeasure"+i);
                if(!Ingredient.equals("null") && !Ingredient.equals(" ")){
                    c.addIngredient(Ingredient, Measurement);
                }else{
                    return;
                }

            }

        } catch (JSONException e) {
            System.out.println("Something went wrong here");
        }
    }


}
