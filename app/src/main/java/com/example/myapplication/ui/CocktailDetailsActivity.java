package com.example.myapplication.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.myapplication.Cocktail;
import com.example.myapplication.Network;
import com.example.myapplication.R;

import java.util.LinkedHashMap;

public class CocktailDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocktail_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final int cocktailID = getIntent().getIntExtra("cocktailID", -1);
        System.out.println("Received: " + cocktailID);

        String apiURL = "https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i=" + cocktailID;
        LinkedHashMap<Integer, Cocktail> cocktailMap = new LinkedHashMap<>();
        Network.loadCocktails(apiURL, cocktailMap, null);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Cocktail c = cocktailMap.get(cocktailID);
        System.out.println(c.getStrDrink());

        //TODO Aufräumen & Cocktail darstellen
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}