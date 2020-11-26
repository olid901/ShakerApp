package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String Cocktails_URL = "https://www.thecocktaildb.com/api/json/v2/***REMOVED***/filter.php?a=Alcoholic";
    private static final String Ingredients_URL = "https://www.thecocktaildb.com/api/json/v2/***REMOVED***/list.php?i=list";
    public ArrayList<Cocktail> Cocktails;
    public ArrayList<Ingredient> Ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Cocktails laden
        Cocktails = new ArrayList<Cocktail>();
        Ingredients = new ArrayList<Ingredient>();

        Network Network1 = new Network();
        Network1.loadCocktails(Cocktails_URL, Cocktails);
        Network1.loadIngredients(Ingredients_URL, Ingredients);


        for (Cocktail c : Cocktails) {
            System.out.println(c);
        }
        for (Ingredient i : Ingredients) {
            System.out.println(i);
        }
    }
}