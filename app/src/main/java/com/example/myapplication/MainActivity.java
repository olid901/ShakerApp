package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String URL = "https://www.thecocktaildb.com/api/json/v2/***REMOVED***/filter.php?a=Alcoholic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ArrayList<Cocktail> Cocktails = new ArrayList<Cocktail>();
        Network Network1 = new Network();
        Network1.loadCocktails(URL, Cocktails);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Cocktail c : Cocktails) {
            System.out.println(c);
        }
    }
}