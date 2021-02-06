package de.diebois.shakerapp.ui.adapter;

import android.content.Context;

import de.diebois.shakerapp.Ingredient;
import de.diebois.shakerapp.R;

import java.util.List;

import androidx.annotation.NonNull;

public class SmallIngredientRVAdapter extends IngredientRVAdapter {

    public SmallIngredientRVAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        Ingredient ingr = super.ingredientList().get(position);
        holder.atHomeButtonView.setOnClickListener(v -> {
            ingr.setAtHome(!ingr.isAtHome());
            System.out.println("Ingredient "+ingr.toString()+" is at home: "+ingr.isAtHome());
        });
    }

    @Override
    public int getItemLayoutID() {
        return R.layout.small_ingredient_item;
    }

    @Override
    public int getingredientNameID() {
        return R.id.small_ingredient_layout_name;
    }

    @Override
    public int getingredientImageID() {
        return R.id.small_ingredient_img;
    }

    @Override
    public String getingredientImageURL(Ingredient ingredient) {
        return ingredient.Img_Url(Ingredient.SMALL);
    }

    @Override
    public String getingredientImageFilename(Ingredient ingredient) {
        String url = ingredient.Img_Url(Ingredient.SMALL);
        String filename = url.substring(url.lastIndexOf('/') + 1);

        // Wir verändern den Dateinamen so, dass er "<bezeichner>-preview.<typ>" lautet
        int pos = filename.lastIndexOf(".");
        if (pos > -1) {
            return filename.substring(0, pos)
                    + "-preview."
                    + filename.substring(pos + ".".length());
        } else {
            return filename;
        }
    }
    
}
