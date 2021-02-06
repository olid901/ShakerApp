package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import android.os.Bundle;
import java.io.File;

import com.google.android.material.navigation.NavigationView;

import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private static final String Cocktails_URL = "https://www.thecocktaildb.com/api/json/v2/9973533/filter.php?a=Alcoholic";
    private static final String Ingredients_URL = "https://www.thecocktaildb.com/api/json/v2/9973533/list.php?i=list";
    public LinkedHashMap<Integer, Cocktail> Cocktails;
    public LinkedHashMap<String, Ingredient> Ingredients;
    public static File localDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        localDir = getFilesDir();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_popular, R.id.nav_favorite, R.id.nav_all_drinks, R.id.nav_ingredients)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Cocktails laden
        Cocktails = new LinkedHashMap<Integer, Cocktail>();
        Ingredients = new LinkedHashMap<String, Ingredient>();

        LinkedHashMap<Integer, Cocktail> resMap = new LinkedHashMap<Integer, Cocktail>();

        //test MIS
        LinkedHashMap<String, Ingredient> atHome = new LinkedHashMap<String, Ingredient>();

        atHome.put("Gin", new Ingredient("Gin"));
        atHome.put("Vodka", new Ingredient("Vodka"));
        atHome.put("Rum", new Ingredient("Rum"));
        atHome.put("Ice", new Ingredient("Ice"));
        atHome.put("Bourbon", new Ingredient("Bourbon"));
        atHome.put("Water", new Ingredient("Water"));
        atHome.put("Light rum", new Ingredient("Light rum"));
        atHome.put("Coca-Cola", new Ingredient("Coca-Cola"));
        atHome.put("Lime", new Ingredient("Lime"));
        atHome.put("Lemon", new Ingredient("Lemon"));
        atHome.put("Tequila", new Ingredient("Tequila"));
        Network.multiIngredientSearch(resMap, atHome);

        //Testweise alles Laden, passiert später durch die einzelnen Fragments
        Network.loadCocktails(Cocktails_URL, Cocktails, null);
        Network.loadIngredients(Ingredients_URL, Ingredients, null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}