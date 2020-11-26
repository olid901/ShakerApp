package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.io.File;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {

    private static final String Cocktails_URL = "https://www.thecocktaildb.com/api/json/v2/***REMOVED***/filter.php?a=Alcoholic";
    private static final String Ingredients_URL = "https://www.thecocktaildb.com/api/json/v2/***REMOVED***/list.php?i=list";
    public ArrayList<Cocktail> Cocktails;
    public ArrayList<Ingredient> Ingredients;
    public static File localDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        localDir = getFilesDir();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Cocktails laden
        Cocktails = new ArrayList<Cocktail>();
        Ingredients = new ArrayList<Ingredient>();


        Network.loadCocktails(Cocktails_URL, Cocktails);
        Network.loadIngredients(Ingredients_URL, Ingredients);
        Network.downloadPic("test2.jpg", "https://www.thecocktaildb.com/images/media/drink/vrwquq1478252802.jpg");



        for (Cocktail c : Cocktails) {
            System.out.println(c);
        }
        for (Ingredient i : Ingredients) {
            System.out.println(i);
        }
    }
}