package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import okhttp3.*;


import android.os.Bundle;
import java.io.File;
import android.util.Log;
import android.view.Menu;
import com.google.android.material.navigation.NavigationView;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {


    //Network Constructor muss ein mal ausgeführt werden
    Network n = new Network();

    private AppBarConfiguration mAppBarConfiguration;

    private static final String Cocktails_URL = "https://www.thecocktaildb.com/api/json/v2/9973533/filter.php?a=Alcoholic";
    private static final String Ingredients_URL = "https://www.thecocktaildb.com/api/json/v2/9973533/list.php?i=list";
    public ArrayList<Cocktail> Cocktails;
    public ArrayList<Ingredient> Ingredients;
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
                R.id.nav_popular, R.id.nav_favorite, R.id.nav_all_drinks, R.id.nav_ingredients, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Cocktails laden
        Cocktails = new ArrayList<Cocktail>();
        Ingredients = new ArrayList<Ingredient>();

        Network.loadCocktails(Cocktails_URL, Cocktails);
        Network.loadIngredients(Ingredients_URL, Ingredients);

        //Network.downloadPic("test2.jpg", "https://www.thecocktaildb.com/images/media/drink/vrwquq1478252802.jpg");

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}