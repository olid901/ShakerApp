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
import de.diebois.shakerapp.IngredientDatabase;
import de.diebois.shakerapp.MainActivity;
import de.diebois.shakerapp.Network;
import de.diebois.shakerapp.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class IngredientRVAdapter extends RecyclerView.Adapter<IngredientRVAdapter.ViewHolder> {

    public LinkedHashMap<String, Ingredient> IngredientMap;
    private final LayoutInflater layoutInflater;
    private final IngredientDatabase atHomeDB;
    private List<Ingredient> atHomeList;

    public LinkedHashMap<String, Ingredient> atHomeMap(){
        LinkedHashMap<String, Ingredient> returnMap = new LinkedHashMap<>();
        for(Ingredient i : atHomeList){
            returnMap.put(i.getStrIngredient(), i);
        }
        return returnMap;
    }

    public List<Ingredient> ingredientList(){

        List<Ingredient> returnList = new ArrayList<>(atHomeList);

        for(Ingredient i : IngredientMap.values()){
            if(!atHomeList.contains(i)){
                returnList.add(i);
            }
        }
        return returnList;
    }

    public IngredientRVAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        IngredientMap = new LinkedHashMap<>(); // Prevent app from crashing
        atHomeDB = new IngredientDatabase(context);
        atHomeList = atHomeDB.getAllIngredients();
    }


    public void setIngredientList(LinkedHashMap<String, Ingredient> IngredientMap) {
        this.IngredientMap = IngredientMap;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.small_ingredient_item, parent, false));
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

        holder.atHomeButtonView.setOnClickListener(v -> {

            if(atHomeDB.isInDatabase(ingredient)){
                atHomeDB.deleteIngredient(ingredient);
                atHomeList.remove(ingredient);
            }else{
                atHomeDB.addIngredient(ingredient);
                atHomeList.add(ingredient);
            }
            Helper.notifyAdaperFromUi(this, position);

        });

        if(atHomeList.contains(ingredient)){
            holder.atHomeButtonView.setImageResource(R.drawable.ic_checked);
        }else{
            holder.atHomeButtonView.setImageResource(R.drawable.ic_home);
        }
    }

    public File updateingredientImage(Ingredient ingredient, int position) {
        // Abbrechen, wenn der ingredient kein Bild hat
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
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView ingredientNameView;
        final ImageView ingredientImgView;
        final ImageButton atHomeButtonView;

        ViewHolder(View itemView) {
            super(itemView);
            ingredientNameView = itemView.findViewById(R.id.small_ingredient_layout_name);
            ingredientImgView = itemView.findViewById(R.id.small_ingredient_img);
            atHomeButtonView = itemView.findViewById(R.id.ingredient_atHome_button);
        }
    }

    public String getingredientImageURL(Ingredient ingredient) {
        return ingredient.Img_Url(Ingredient.SMALL);
    }

    /**
     * Hole den Dateinamen des Ingredient-Bilds
     */
    public String getingredientImageFilename(Ingredient ingredient) {
        String url = ingredient.Img_Url(Ingredient.SMALL);
        String filename = url.substring(url.lastIndexOf('/') + 1);

        // Wir verändern den Dateinamen so, dass er "<bezeichner>-small.<typ>" lautet
        int pos = filename.lastIndexOf(".");
        if (pos > -1) {
            return filename.substring(0, pos)
                    + "-small."
                    + filename.substring(pos + ".".length());
        } else {
            return filename;
        }
    }
}
