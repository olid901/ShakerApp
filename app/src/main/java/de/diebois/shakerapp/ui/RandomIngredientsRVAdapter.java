package de.diebois.shakerapp.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import de.diebois.shakerapp.Helper;
import de.diebois.shakerapp.Ingredient;
import de.diebois.shakerapp.MainActivity;
import de.diebois.shakerapp.Network;
import de.diebois.shakerapp.R;

class RandomIngredientsRVAdapter extends RecyclerView.Adapter<RandomIngredientsRVAdapter.ViewHolder> {

    private LinkedHashMap<Integer, Ingredient> ingredientMap;
    private final LayoutInflater layoutInflater;

    private List<Ingredient> ingredientList() {
        return new ArrayList<>(ingredientMap.values());
    }

    public RandomIngredientsRVAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        ingredientMap = new LinkedHashMap<>(); // Prevent app from crashing
    }


    public void setIngredientList(LinkedHashMap<Integer, Ingredient> ingredientList) {
        this.ingredientMap = ingredientList;
    }

    @NotNull
    @Override
    public RandomIngredientsRVAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.ingredient_grid_item, parent, false));
    }

    /**
     * Hier wird der Cocktail-Name und das Bild zu jedem Eintrag im Recycler View gesetzt
     * TODO: Bild zu Ingredient anzeigen
     */
    @Override
    public void onBindViewHolder(@NotNull RandomIngredientsRVAdapter.ViewHolder holder, int position) {
        List<Ingredient> ingredientList = ingredientList();
        ArrayList<String> measures = RandomCocktailDetailsActivity.cocktail.getMeasures();

        if (position > ingredientList.size())
            return;

        Ingredient ingredient = ingredientList.get(position);
        String ingredientName = ingredient.getStrIngredient();
        String displayedText = ingredientName;

        if (position > measures.size() - 1)
            return;

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
        String filename = ingredient.getStrIngredient() + "-Medium.png";
        String url = "https://www.thecocktaildb.com/images/ingredients/" + filename;

        File file = new File(MainActivity.localDir, filename);

        if (!file.exists()) {
            Network.downloadPic(filename, url, () -> Helper.notifyAdaperFromUi(RandomIngredientsRVAdapter.this, position));
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