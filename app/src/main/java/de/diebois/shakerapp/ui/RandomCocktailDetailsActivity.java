package de.diebois.shakerapp.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.diebois.shakerapp.Cocktail;
import de.diebois.shakerapp.CocktailDatabase;
import de.diebois.shakerapp.Helper;
import de.diebois.shakerapp.Ingredient;
import de.diebois.shakerapp.MainActivity;
import de.diebois.shakerapp.Network;
import de.diebois.shakerapp.R;

public class RandomCocktailDetailsActivity extends AppCompatActivity {

    public static Cocktail cocktail;
    private RandomIngredientsRVAdapter adapter;
    private boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        cocktail = new Cocktail(0, "Loading", "");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_cocktail_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(cocktail.getStrDrink());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.cocktail_ingredients_rv);
        GridLayoutManager glm = new GridLayoutManager(getBaseContext(), 2);

        recyclerView.setLayoutManager(glm);
        recyclerView.setNestedScrollingEnabled(false);

        adapter = new RandomIngredientsRVAdapter(getBaseContext());
        adapter.setIngredientList(new LinkedHashMap<>());
        recyclerView.setAdapter(adapter);

        //Network.addFullCocktailInfo(cocktail, this::onCocktailInfoLoaded);
        //pdateCocktailImage(cocktail);

        findViewById(R.id.big_cocktail_interaction_share).setOnClickListener(v -> shareIntent(cocktail));

        ImageButton likeButton = findViewById(R.id.big_cocktail_interaction_like);
        CocktailDatabase db = new CocktailDatabase(this);
        if (db.isInDatabase(cocktail)) {
            likeButton.setImageResource(R.drawable.ic_heart_filled);
        } else {
            likeButton.setImageResource(R.drawable.ic_heart_border);
        }

        // Kopiert aus dem BigCocktailRVAdapter
        // TODO vereinheitlichen
        likeButton.setOnClickListener(v -> {
            if (db.isInDatabase(cocktail)) {
                db.deleteCocktail(cocktail);
                likeButton.setImageResource(R.drawable.ic_heart_border);
            } else {
                db.addCocktail(cocktail);
                likeButton.setImageResource(R.drawable.ic_heart_filled);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("I am in thread now");
                cocktail = Network.loadRandomCocktail();
                onCocktailInfoLoaded();
                loading = false;
            }
        }).start();

    }

    // Kopiert aus dem BigCocktailRVAdapter
    // TODO vereinheitlichen
    private void shareIntent(Cocktail cocktail) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Have a look at this nice cocktail!\nhttps://www.thecocktaildb.com/drink.php?c=" + cocktail.getID());
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getBaseContext().startActivity(shareIntent);
    }

    public void onCocktailInfoLoaded() {
        Log.wtf("RandomCocktailDetailsActivity",  "I got messaged that the cocktail is now fully loaded!");

        //Toolbar toolbar = findViewById(R.id.toolbar);
        //toolbar.setTitle(cocktail.getStrDrink());
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Helper.runOnUiThread(() ->{
            getSupportActionBar().setTitle(cocktail.getStrDrink());
        });

        LinkedHashMap<Integer, Ingredient> ingredientMap = new LinkedHashMap<>();
        ArrayList<String> ingredientList = cocktail.getIngredients();

        for (int i = 0; i < ingredientList.size(); i++) {
            ingredientMap.put(i, new Ingredient(ingredientList.get(i)));
        }

        adapter.setIngredientList(ingredientMap);
        Helper.notifyAdaperFromUi(adapter);

        // TODO Auf der TheCocktailDB-Seite wird hinter jedem Punkt ein Zeilenumbruch hinzugefügt
        TextView instructionView = findViewById(R.id.instructions_text);
        TextView glassView = findViewById(R.id.glass_text);

        Helper.runOnUiThread(() -> {
            instructionView.setText(cocktail.getInstruction());
            glassView.setText(cocktail.getGlass());
        });

        updateCocktailImage(cocktail);

        //updateCocktailImage();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateCocktailImage(Cocktail cocktail) {
        // TODO Das hier ist mehr oder weniger von CocktailRVAdapter kopiert - Kombinieren?
        // Abbrechen, wenn der Cocktail kein Bild hat
        // Was aktuell nur beim "Americano" der Fall ist
        if (cocktail.isImageMissing()) {
            return;
        }

        String url = cocktail.getImg_Url();
        String filename = url.substring(url.lastIndexOf('/')+1);

        File file = new File(MainActivity.localDir, filename);

        ImageView view = findViewById(R.id.cocktail_image);

        if (!file.exists()) {
            Network.downloadPic(filename, url, () -> {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                Helper.runOnUiThread(() -> view.setImageBitmap(bitmap));
            });
            view.setImageResource(R.drawable.ic_image_not_found);
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            view.setImageBitmap(bitmap);
        }
    }
}

