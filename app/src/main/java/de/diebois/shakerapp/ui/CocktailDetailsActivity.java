package de.diebois.shakerapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import de.diebois.shakerapp.Cocktail;
import de.diebois.shakerapp.Database;
import de.diebois.shakerapp.Helper;
import de.diebois.shakerapp.Ingredient;
import de.diebois.shakerapp.MainActivity;
import de.diebois.shakerapp.Network;
import de.diebois.shakerapp.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CocktailDetailsActivity extends AppCompatActivity {

    public static Cocktail cocktail;
    private IngredientsRVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocktail_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(cocktail.getStrDrink());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.cocktail_ingredients_rv);
        GridLayoutManager glm = new GridLayoutManager(getBaseContext(), 2);

        recyclerView.setLayoutManager(glm);
        recyclerView.setNestedScrollingEnabled(false);

        adapter = new IngredientsRVAdapter(getBaseContext());
        adapter.setIngredientList(new LinkedHashMap<>());
        recyclerView.setAdapter(adapter);

        Network.addFullCocktailInfo(cocktail, this::onCocktailInfoLoaded);
        updateCocktailImage(cocktail);

        findViewById(R.id.big_cocktail_interaction_share).setOnClickListener(v -> shareIntent(cocktail));

        ImageButton likeButton = findViewById(R.id.big_cocktail_interaction_like);
        Database db = new Database(this);
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

class IngredientsRVAdapter extends RecyclerView.Adapter<IngredientsRVAdapter.ViewHolder> {

    private LinkedHashMap<Integer, Ingredient> ingredientMap;
    private final LayoutInflater layoutInflater;

    private List<Ingredient> ingredientList() {
        return new ArrayList<>(ingredientMap.values());
    }

    public IngredientsRVAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        ingredientMap = new LinkedHashMap<>(); // Prevent app from crashing
    }


    public void setIngredientList(LinkedHashMap<Integer, Ingredient> ingredientList) {
        this.ingredientMap = ingredientList;
    }

    @NotNull
    @Override
    public IngredientsRVAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.ingredient_grid_item, parent, false));
    }

    /**
     * Hier wird der Cocktail-Name und das Bild zu jedem Eintrag im Recycler View gesetzt
     * TODO: Bild zu Ingredient anzeigen
     */
    @Override
    public void onBindViewHolder(@NotNull IngredientsRVAdapter.ViewHolder holder, int position) {
        List<Ingredient> ingredientList = ingredientList();
        ArrayList<String> measures = CocktailDetailsActivity.cocktail.getMeasures();

        Ingredient ingredient = ingredientList.get(position);
        String ingredientName = ingredient.getStrIngredient();
        String displayedText = ingredientName;

        // Für den Fall, dass bei einer Zutat keine Mengenangabe gegeben ist
        if (!measures.get(position).equals("null")) {
            // Damit immer ein Leerzeichen zwischen den Measurements und dem Ingredient sind
            if (!(measures.get(position).endsWith(" ") || ingredientName.startsWith(" ")))
                ingredientName = " " + ingredientName;
            displayedText = measures.get(position) + ingredientName;
        }

        holder.ingredientNameView.setText(displayedText);

        File file = updateIngredientImage(ingredient, position);
        if (file != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            holder.ingredientImageView.setImageBitmap(bitmap);
        } else {
            holder.ingredientImageView.setImageResource(R.drawable.ic_image_not_found);
        }
    }

    public File updateIngredientImage(Ingredient ingredient, int position) {
        String filename = ingredient.getStrIngredient() +  "-Medium.png";
        String url = "https://www.thecocktaildb.com/images/ingredients/" + filename;

        File file = new File(MainActivity.localDir, filename);

        if (!file.exists()) {
            Network.downloadPic(filename, url, () -> Helper.notifyAdaperFromUi(IngredientsRVAdapter.this, position));
            return null;
        } else {
            return file;
        }
    }

    /**
     * Get total number of rows
     */
    @Override
    public int getItemCount() {
        return ingredientMap.size();
    }

    /**
     * Stores and recycles views as they are scrolled off screen
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView ingredientNameView;
        final ImageView ingredientImageView;

        ViewHolder(View itemView) {
            super(itemView);
            ingredientNameView = itemView.findViewById(R.id.ingredient_name);
            ingredientImageView = itemView.findViewById(R.id.ingredient_image);
        }
    }
}