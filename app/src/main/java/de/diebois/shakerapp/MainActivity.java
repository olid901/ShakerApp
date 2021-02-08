package de.diebois.shakerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import de.diebois.shakerapp.ui.RandomCocktailDetailsActivity;

import com.squareup.seismic.ShakeDetector;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;

import java.io.File;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    public static File localDir;
    private static boolean shakeable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        IngredientDatabase IngredientDB = new IngredientDatabase(this);
        Ingredient.atHomeList = new ArrayList<Ingredient>();
        Ingredient.atHomeList = IngredientDB.getAllIngredients();

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
                R.id.nav_popular, R.id.nav_favorite, R.id.nav_all_drinks, R.id.nav_ingredients, R.id.nav_random)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        /*  Eine Shaker-App MUSS eine Funktion haben um mittels Handy schütteln einen (random) COcktail aufzurufen.
            Dies wir hier mit folgender Gradle Bibliothek implmentiert: 'com.squareup:seismic:1.0.2'
         */
        final SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector detector = new ShakeDetector(new ShakeDetector.Listener() {
            @Override
            public void hearShake() {
                if (shakeable) {
                    Intent myIntent = new Intent(MainActivity.this, RandomCocktailDetailsActivity.class);
                    MainActivity.this.startActivity(myIntent);
                    shakeable = false;
                    Timer buttonTimer = new Timer();
                    buttonTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            shakeable = true;
                        }
                    }, 1000);
                }
            }
        });
        detector.start(mSensorManager);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}