package de.diebois.shakerapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import de.diebois.shakerapp.ui.UICallback;
import de.diebois.shakerapp.ui.adapter.CocktailRVAdapter;
import de.diebois.shakerapp.ui.adapter.IngredientRVAdapter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Network {

    private static final OkHttpClient okHttpClient;

    static {
        okHttpClient = new OkHttpClient();
    }

    public static String getBaseURL() {
        return "https://www.thecocktaildb.com/api/json/v2/" + BuildConfig.apikey;
    }

    //Wenn kein Filter gewünscht: Null als Filter übergeben
    public static void loadIngredients(String URL, String filter, List<Ingredient> ingredientList, IngredientRVAdapter adapter) {
        final Request request = new Request.Builder().url(URL).build();
        System.out.println("Filter is: " + filter);

        // use async method, to not block the UI thread
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("Network", "Could not fetch data! Message: " + e);
//                System.out.println("Something went wrong there");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String rawResponse = response.body().string();
//                    System.out.println("Ingredient response: "+rawResponse);
                    extractIngredients(rawResponse, ingredientList);

                    if (filter != null) {
                        List<Ingredient> toRemove = new ArrayList<>();
                        for (Ingredient i : ingredientList) {
                            String s = i.getStrIngredient();
                            if (!s.toLowerCase().contains(filter.toLowerCase())) {
                                //it.remove();
                                toRemove.add(i);
                            }
                        }


                        for (Ingredient i : toRemove) {
                            ingredientList.remove(i);
                        }

                    }

                    Helper.notifyAdaperFromUi(adapter);
                }
            }
        });


    }

    public static void multiIngredientSearch(LinkedHashMap<Integer, Cocktail> resultMap, List<Ingredient> ingredientsAtHome, CocktailRVAdapter adapter) {

        System.out.println("Start of MIS, the following ingredients are at home:");
        for (Ingredient i : ingredientsAtHome) {
            System.out.println(" - " + i.getStrIngredient());
        }

        new Thread(() -> {

            //Zeitmessung weil ich performance Bedenken bei vielen Zutaten habe!
            long startTime = System.nanoTime();

            HashMap<Integer, Integer> CocktailCount = new HashMap<>();
            ExecutorService Executor = Executors.newCachedThreadPool();

            for (Ingredient ing : ingredientsAtHome) {
                String ingredientName = ing.getStrIngredient();

                Executor.execute(() -> {
                    System.out.println("Running MIS for " + ingredientName);
                    MIScocktailCounter(CocktailCount, ingredientName);
                    System.out.println("MIS for " + ingredientName + " done!");
                });
            }

            try {
                Executor.shutdown();
                boolean finished = Executor.awaitTermination(1, TimeUnit.MINUTES);
                System.out.println("Finished MIS!");
            } catch (Exception e) {
                System.out.println("Something went wrong with the Executor service in MIS!");
            }


            //Es gibt keine Cocktails mit einer Zutat: Entferne Cocktails die nur bei einer Zutaten Abfrage auftauchen
            CocktailCount.entrySet().removeIf(cnt -> cnt.getValue() == 1);

            LinkedHashMap<Integer, Cocktail> candidates = new LinkedHashMap<>();

            ExecutorService Executor2 = Executors.newCachedThreadPool();

            for (int ID : CocktailCount.keySet()) {
                Executor2.execute(() -> candidates.put(ID, MIScocktailLoader(ID)));
            }

            try {
                Executor2.shutdown();
                boolean finished = Executor2.awaitTermination(1, TimeUnit.MINUTES);
            } catch (Exception e) {
                System.out.println("Something went wrong with the Executor service in MIS!");
            }

            for (int ID : candidates.keySet()) {

                boolean allAtHome = true;

                try {
                    for (String ingr : candidates.get(ID).getIngredients()) {
                        if (!ingredientsAtHome.contains(new Ingredient(ingr))) {
                            System.out.println("Drink with ID " + ID + " Contains Ingredient " + ingr + " which is not at home!");
                            allAtHome = false;
                            break;
                        }
                    }
                } catch (NullPointerException e) {
                    Log.wtf("Network/MIS", "There was a NullPointerException in this MIS. This is probably due to a remote DB Error and hence not our fault. Aborting this MIS and starting a new one with the same Parameters!");
                    resultMap.clear();
                    multiIngredientSearch(resultMap, ingredientsAtHome, adapter);
                    return;
                }


                if (allAtHome) {
                    System.out.println("Drink with ID = " + ID + " contains only Ingredients, that are at home!");
                    resultMap.put(ID, candidates.get(ID));
                }


            }

            long elapsedTimeMillis = (System.nanoTime() - startTime) / 1000000;
            System.out.println("MIS-test: \n - size = " + ingredientsAtHome.size() + "\n - time " + elapsedTimeMillis + " millis \n - Cocktails found: " + resultMap.size());
            System.out.println("Result of MIS: ");
            for (Cocktail c : resultMap.values()) {
                System.out.println(c);
            }

            Helper.notifyAdaperFromUi(adapter);

        }).start();

    }

    //Achtung:  Diese Methode verwendet eine SYNCHRONE Abfrage, da für Multiingredient Search Abfragen für jede Zutat gemacht werden müssen
    //          und auf die Antwort jeder Abfrage gewartet werden muss.
    //          Darf NIEMALS UND UNTER KEINEN UMSTÄNDEN AUF DEM UI THREAD AUSGEFÜHRT WERDEN!!! (Immer ein extra Thread machen!)
    // CocktailCount:   Zählt wie oft ein Cocktail in den einzelnen Abfragen vorkommt. Sinn des ganzen ist, dass Cocktails die nur in einer Abfrage vorkommen,
    //                  gedroppt werden können, da es keine Cocktails mit nur einer Zutat gibt. Bei jeder MIS Abfrage wird eine Map erstellt, die allen Zutatenabfragen mitgegeben wird
    private static void MIScocktailCounter(HashMap<Integer, Integer> CocktailCount, String Ingredient) {

        final Request request = new Request.Builder().url(Network.getBaseURL() + "/filter.php?i=" + Ingredient).build();

        try {
            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                String rawResponse = response.body().string();

                //do something with the response
                System.out.println("MIS Search response of Ingr. " + Ingredient + ": " + rawResponse);
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
                    System.out.println("There is probaly no cocktail that uses: " + Ingredient + " as an Ingredient");
                }

            }
        } catch (IOException e) {
            //System.out.println("There was a error with the loading of MIS Information");
        }

    }

    private static Cocktail MIScocktailLoader(int cocktailID) {
        final Request request = new Request.Builder().url(Network.getBaseURL() + "/lookup.php?i=" + cocktailID).build();
        try {
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
                } finally {
                    response.close();
                }

            }

        } catch (IOException e) {
            //Log.wtf("Network", "Hier kommt wohl null her!");
        }


        return null;
    }

    public static void downloadPic(String filename, String URL, UICallback uicb) {
        Request request = new Request.Builder().url(URL).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to download file: " + response);
                }

                InputStream inputStream = response.body().byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                System.out.println("Trying to save file");
                File file = new File(MainActivity.localDir, filename);

                // ???
                if (file.exists())
                    //noinspection ResultOfMethodCallIgnored
                    file.delete();

                try {
                    FileOutputStream out = new FileOutputStream(file);

                    // Ingredients werden als PNG-Dateien geliefert, da diese
                    // einen Alphakanal besitzen
                    if (file.getName().endsWith(".png"))
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    else
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("saved!");
                uicb.refreshView();
            }
        });
    }

    public static void loadCocktails(String URL, LinkedHashMap<Integer, Cocktail> CocktailMap, CocktailRVAdapter adapter) {

        final Request request = new Request.Builder().url(URL).build();

        // use async method, to not block the UI thread
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("Network", "Could not fetch data! Message: " + e);
//                System.out.println("Something went wrong there");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String rawResponse = response.body().string();
                    System.out.println("Cocktailsdata: " + rawResponse);

                    extractAndAddCocktails(rawResponse, CocktailMap);

                    // Für die einzelndarstellung des Cocktails brauchen wir keinen Adapter
                    // In so einem Fall geben wir einfach null mit
                    // TODO: Vlt. überladene Methode erstellen, damits schöner aussieht
                    // null-Übergaben werden allerdings sehr ungern gesehen
                    if (adapter != null) {
                        Helper.notifyAdaperFromUi(adapter);
                    }
                }
            }
        });
    }

    public static void addFullCocktailInfo(Cocktail c, UICallback uicb) {

        String URL = getBaseURL() + "/lookup.php?i=" + c.getID();
        final Request request = new Request.Builder().url(URL).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("Network", "Could not fetch data! Message: " + e);
//                System.out.println("Something went wrong there");
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

                        uicb.refreshView();

                    } catch (JSONException e) {
//                        System.out.println("Something went wrong here");
                        e.printStackTrace();
                    } finally {
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
//            System.out.println("Something went wrong here");
            e.printStackTrace();
        }
    }


    private static void extractIngredients(String rawResponse, List<Ingredient> ingredientList) {
        try {
            JSONObject responseObject = new JSONObject(rawResponse);
            JSONArray responseArray = responseObject.getJSONArray("drinks");
            for (int index = 0; index < responseArray.length(); index++) {
                JSONObject tempObject = responseArray.getJSONObject(index);
                String Name = tempObject.getString("strIngredient1");
                ingredientList.add(new Ingredient(Name));
            }
        } catch (JSONException e) {
//            System.out.println("Something went wrong here");
            e.printStackTrace();
        }
    }

    private static void extractAndAddIngredientsAndMeasurments(String rawResponse, Cocktail c) {
        try {
            JSONObject responseObject = new JSONObject(rawResponse);
            JSONArray responseArray = responseObject.getJSONArray("drinks");
            JSONObject tempObject = responseArray.getJSONObject(0);
            String Ingredient, Measurement;

            for (int i = 1; i <= 15; i++) {
                Ingredient = tempObject.getString("strIngredient" + i);
                Measurement = tempObject.getString("strMeasure" + i);
                if (!Ingredient.equals("null") && !Ingredient.equals(" ") && !Ingredient.equals("")) {
                    c.addIngredient(Ingredient, Measurement);
                } else {
                    return;
                }
            }
        } catch (JSONException e) {
//            System.out.println("Something went wrong here");
            e.printStackTrace();
        }
    }

    public static Cocktail loadRandomCocktail() {

        final Request request = new Request.Builder().url(Network.getBaseURL() + "/random.php").build();

        Cocktail c;

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String rawResponse = response.body().string();
            JSONObject responseObject = new JSONObject(rawResponse);
            JSONArray responseArray = responseObject.getJSONArray("drinks");
            JSONObject tempObject = responseArray.getJSONObject(0);

            int ID = Integer.parseInt(tempObject.getString("idDrink"));
            String strDrink = tempObject.getString("strDrink");
            String imgUrl = tempObject.getString("strDrinkThumb");

            c = new Cocktail(ID, strDrink, imgUrl);

            c.setAlcoholic(tempObject.getString("strAlcoholic"));
            c.setInstruction(tempObject.getString("strInstructions"));
            c.setCategory(tempObject.getString("strCategory"));
            c.setGlass(tempObject.getString("strGlass"));
            c.setTags(tempObject.getString("strGlass"));
            extractAndAddIngredientsAndMeasurments(rawResponse, c);

            return c;

        } catch (JSONException | IOException e) {
            Log.wtf("Network", "ERROR!!!");
        }
        return new Cocktail(0, "THERE WAS AN ERROR LOADING THIS COCKTAIL", "");
    }

}
