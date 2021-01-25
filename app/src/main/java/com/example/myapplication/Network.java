package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.example.myapplication.ui.adapter.CocktailRVAdapter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Network{

    static private OkHttpClient okHttpClient;

    // TODO: Da hier vermutlich sowieso alles static sein wird -> Konstruktor durch static-Block ersetzen
    public Network(){
        this.okHttpClient = new OkHttpClient();
    }



    public static void loadIngredients(String URL, LinkedHashMap<String, Ingredient> IngredientMap, CocktailRVAdapter adapter){
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

        notifyAdaperFromUi(adapter);

    }



    public static void addFullIngredientInfo(Ingredient i){
        //TBA: Siehe Karte Zutaten abfragen
    }

    //Multi Ingredient Search (kurz MIS)
    public static void multiIngredientSearch(LinkedHashMap<Integer, Cocktail> resultMap, LinkedHashMap<String, Ingredient> ingredientsAtHome){

        new Thread(new Runnable() {
            @Override
            public void run() {

                //Zeitmessung weil ich performance Bedenken bei vielen Zutaten habe!
                long startTime = System.nanoTime();

                HashMap<Integer, Integer> CocktailCount = new HashMap<Integer, Integer>();
                ExecutorService Executor = Executors.newCachedThreadPool();

                for(String ingredientName : ingredientsAtHome.keySet()){

                    Executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Running MIS for "+ingredientName);
                            MIScocktailCounter(CocktailCount, ingredientName);
                            System.out.println("MIS for "+ingredientName+" done!");
                        }
                    });
                }

                try{
                    Executor.shutdown();
                    boolean finished = Executor.awaitTermination(1, TimeUnit.MINUTES);
                    System.out.println("Finished MIS!");
                }catch(Exception e){
                    System.out.println("Something went wrong with the Executor service in MIS!");
                }


                //Es gibt keine Cocktails mit einer Zutat: Entferne Cocktails die nur bei einer Zutaten Abfrage auftauchen
                CocktailCount.entrySet().removeIf(cnt -> cnt.getValue() == 1);

                LinkedHashMap<Integer, Cocktail> candidates = new LinkedHashMap<Integer, Cocktail>();

                ExecutorService Executor2 = Executors.newCachedThreadPool();

                for(int ID : CocktailCount.keySet()){

                    Executor2.execute(new Runnable() {
                        @Override
                        public void run() {
                            candidates.put(ID, MIScocktailLoader(ID));
                        }
                    });
                }

                try{
                    Executor2.shutdown();
                    boolean finished = Executor2.awaitTermination(1, TimeUnit.MINUTES);
                    //System.out.println("Finished Candidate loading!");
                }catch(Exception e){
                    System.out.println("Something went wrong with the Executor service in MIS!");
                }

                for(int ID: candidates.keySet()){

                    boolean allAtHome = true;

                    for(String ingr : candidates.get(ID).getIngredients()){
                        if(!ingredientsAtHome.keySet().contains(ingr)){
                            //System.out.println("Drink with ID "+ID+" Contains Ingredient "+ingr+" which is not at home!");
                            allAtHome = false;
                        }

                    }
                    if(allAtHome){
                        System.out.println("Drink with ID = "+ID+" contains only Ingredients, that are at home!");
                        resultMap.put(ID, candidates.get(ID));
                    }


                }

                long elapsedTimeMillis = (System.nanoTime() - startTime)/1000000;
                System.out.println("MIS-test: \n - size = "+ingredientsAtHome.size()+"\n - time "+ elapsedTimeMillis+" millis \n - Cocktails found: "+resultMap.size());
                System.out.println("Result of MIS: ");
                for(Cocktail c : resultMap.values()){
                    System.out.println(c);
                }

            }
        }).start();

    }

    //Achtung:  Diese Methode verwendet eine SYNCHRONE Abfrage, da für Multiingredient Search Abfragen für jede Zutat gemacht werden müssen
    //          und auf die Antwort jeder Abfrage gewartet werden muss.
    //          Darf NIEMALS UND UNTER KEINEN UMSTÄNDEN AUF DEM UI THREAD AUSGEFÜHRT WERDEN!!! (Immer ein extra Thread machen!)
    // CocktailCount:   Zählt wie oft ein Cocktail in den einzelnen Abfragen vorkommt. Sinn des ganzen ist, dass Cocktails die nur in einer Abfrage vorkommen,
    //                  gedroppt werden können, da es keine Cocktails mit nur einer Zutat gibt. Bei jeder MIS Abfrage wird eine Map erstellt, die allen Zutatenabfragen mitgegeben wird
    private static void MIScocktailCounter(HashMap<Integer, Integer> CocktailCount, String Ingredient){

            final Request request = new Request.Builder().url("https://www.thecocktaildb.com/api/json/v2/9973533/filter.php?i="+Ingredient).build();

            try{
                try (Response response = okHttpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    String rawResponse = response.body().string();

                    //do something with the response
                    System.out.println("MIS Search response of Ingr. "+Ingredient+": "+rawResponse);
                    try {
                        JSONObject responseObject = new JSONObject(rawResponse);
                        JSONArray responseArray = responseObject.getJSONArray("drinks");
                        for (int index = 0; index < responseArray.length(); index++) {
                            JSONObject tempObject = responseArray.getJSONObject(index);
                            int ID = tempObject.getInt("idDrink");
                            //int cnt = CocktailCount.containsKey(ID) ? CocktailCount.get(ID) : 0;
                            //CocktailCount.put(ID, cnt + 1);
                            CocktailCount.merge(ID, 1, Integer::sum);
                        }
                    } catch (JSONException e) {
                        System.out.println("There is probaly no cocktail that uses: "+Ingredient+" as an Ingredient");
                    }

                }
            }
            catch (IOException e){
                //System.out.println("There was a error with the loading of MIS Information");
            }

    }

    private static Cocktail MIScocktailLoader(int cocktailID){
        final Request request = new Request.Builder().url("https://www.thecocktaildb.com/api/json/v2/9973533/lookup.php?i="+cocktailID).build();
        try{
            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                String rawResponse = response.body().string();
                try {
                    //System.out.println("Getting all the Information of Cocktail "+c.getStrDrink());
                    JSONObject responseObject = new JSONObject(rawResponse);
                    JSONArray responseArray = responseObject.getJSONArray("drinks");
                    JSONObject tempObject = responseArray.getJSONObject(0);

                    String strDrink = tempObject.getString("strDrink");
                    String imgUrl = tempObject.getString("strDrinkThumb");

                    Cocktail c = new Cocktail(cocktailID, strDrink, imgUrl);

                    c.setAlcoholic(tempObject.getString("strAlcoholic"));
                    c.setInstruction(tempObject.getString("strInstructions"));
                    c.setCategory(tempObject.getString("strCategory"));
                    c.setGlass(tempObject.getString("strGlass"));
                    c.setTags(tempObject.getString("strGlass"));
                    extractAndAddIngredientsAndMeasurments(rawResponse, c);

                    return c;



                } catch (JSONException e) {
                    System.out.println("Something went wrong here");
                }finally {
                    response.close();
                }

            }

        }
        catch (IOException e){
            //System.out.println("There was a error with the loading of MIS Information");
        }
        return null;
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
                if (file.exists())
                    file.delete();

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

    public static void loadCocktails(String URL, LinkedHashMap<Integer, Cocktail> CocktailMap, CocktailRVAdapter adapter) {

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

                    // Für die einzelndarstellung des Cocktails brauchen wir keinen Adapter
                    // In so einem Fall geben wir einfach null mit
                    // TODO: Vlt. überladene Methode erstellen, damits schöner aussieht
                    // null-Übergaben werden allerdings sehr ungern gesehen
                    if (adapter != null) {
                        notifyAdaperFromUi(adapter);
                    }
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

    private static void extractAndAddCocktails(String rawResponse, LinkedHashMap<Integer, Cocktail> Cocktails) {

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

    private static void extractIngredients(String rawResponse, LinkedHashMap<String, Ingredient> IngredientMap) {
        try {
            JSONObject responseObject = new JSONObject(rawResponse);
            JSONArray responseArray = responseObject.getJSONArray("drinks");
            for (int index = 0; index < responseArray.length(); index++) {
                JSONObject tempObject = responseArray.getJSONObject(index);
                String Name = tempObject.getString("strIngredient1");
                IngredientMap.put(Name, new Ingredient(Name));
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


    //Gibt einen Adapter bescheid, dass sich seine Daten geändert haben. Das muss auf dem Ui Thread ausgeführt werden.
    private static void notifyAdaperFromUi(CocktailRVAdapter adapter){
        if (adapter != null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    //Diese Methode führt eine Task auf dem Ui Thread aus. Ist notwendig um in irgendeiner Weise mit Views zu interagieren
    //(bspw. Adapter Bescheid geben, dass die Daten sich geändert haben)
    private static void runOnUiThread(Runnable task) {
        new Handler(Looper.getMainLooper()).post(task);
    }
}
