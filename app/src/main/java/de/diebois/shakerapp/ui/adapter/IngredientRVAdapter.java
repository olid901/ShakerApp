package de.diebois.shakerapp.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import de.diebois.shakerapp.Helper;
import de.diebois.shakerapp.Ingredient;
import de.diebois.shakerapp.MainActivity;
import de.diebois.shakerapp.Network;
import de.diebois.shakerapp.R;
import de.diebois.shakerapp.ui.CocktailClickListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public abstract class IngredientRVAdapter extends RecyclerView.Adapter<IngredientRVAdapter.ViewHolder> {

    private LinkedHashMap<String, Ingredient> IngredientMap;
    protected final LayoutInflater layoutInflater;
    private CocktailClickListener itemClickListener;

    protected List<Ingredient> ingredientList(){
        return new ArrayList<>(IngredientMap.values());
    }

    public IngredientRVAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        IngredientMap = new LinkedHashMap<>(); // Prevent app from crashing
    }


    public void setIngredientList(LinkedHashMap<String, Ingredient> IngredientMap) {
        this.IngredientMap = IngredientMap;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(getItemLayoutID(), parent, false));
    }

    /**
     * Hier wird der ingredient-Name und das Bild zu jedem Eintrag im Recycler View gesetzt
     */
    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        Ingredient ingredient = ingredientList().get(position);

        // TODO Return null ist böse!!! Sollte noch geändert werden bei Gelegenheit
        File file = updateingredientImage(ingredient, position);
        if (file != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            holder.ingredientImgView.setImageBitmap(bitmap);
        } else {
            holder.ingredientImgView.setImageResource(R.drawable.ic_image_not_found);
        }

        holder.ingredientNameView.setText(ingredient.getStrIngredient());
    }

    public File updateingredientImage(Ingredient ingredient, int position) {
        // Abbrechen, wenn der ingredient kein Bild hat
        // Was aktuell nur beim "Americano" der Fall ist
        if (!ingredient.hasImage()) {
            return null;
        }

        String url = getingredientImageURL(ingredient);
        String filename = getingredientImageFilename(ingredient);

        File file = new File(MainActivity.localDir, filename);

        // Wenn kein Bild gespeichert ist, dann lädt er eines runter und gibt dem Adapter bescheid
        // Danach durchläuft er erneut das onBindViewHolder inklusive dieser Methode hier
        if (!file.exists()) {
            Network.downloadPic(filename, url, () -> Helper.notifyAdaperFromUi(IngredientRVAdapter.this, position));
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
        return IngredientMap.size();
    }

    /**
     * Stores and recycles views as they are scrolled off screen
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView ingredientNameView;
        final ImageView ingredientImgView;
        final ImageButton atHomeButtonView;
       //final ImageButton favoriteButtonView;

        ViewHolder(View itemView) {
            super(itemView);
            ingredientNameView = itemView.findViewById(getingredientNameID());
            ingredientImgView = itemView.findViewById(getingredientImageID());

            // Die beiden Buttons sind im Prinzip nur bei der Großansicht wichtig
            // In der kleinen Ansicht werden die mit "null" initialisiert
            // Aber bei der kleinen Ansicht macht das nichts aus
            atHomeButtonView = itemView.findViewById(R.id.ingredient_atHome_button);
            //favoriteButtonView = itemView.findViewById(R.id.big_ingredient_interaction_like);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) itemClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    /**
     * Convenience method for getting data at click position
     * wichtig: Es muss die ingredientList verwendet werden, da bei der Map sonst versucht
     * wird das Item zu returnen, das auf id gemappt ist, nicht das an der Stelle id
     * Daher zur Verständnis auch mal "id" in "pos" umbenannt, damit es nicht zu Verwirrung kommt
     */
    public Ingredient getItem(int pos) {
        return ingredientList().get(pos);
    }

    /**
     * allows clicks events to be caught
     */
    public void setClickListener(CocktailClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /**
     * Hole die ID vom jeweiligen Layout eines einzelnen Recycler View Eintrags
     */
    public abstract int getItemLayoutID();

    /**
     * Hole die ID vom TextView, welches den Namen des ingredients beinhaltet
     */
    public abstract int getingredientNameID();

    /**
     * Hole die ID vom ImageView, welche das Bild des ingredients beinhaltet
     */
    public abstract int getingredientImageID();

    /**
     * Hole die Bild-URL vom ingredient
     * Wird benötigt, um zwischen kleinen und großen Bildern unterscheiden zu können
     */
    public abstract String getingredientImageURL(Ingredient ingredient);

    /**
     * Hole den Dateinamen des ingredient-Bilds
     * Wird benötigt, um zwischen kleinen und großen Bildern unterscheiden zu können
     */
    public abstract String getingredientImageFilename(Ingredient ingredient);
}
