package de.diebois.shakerapp.ui.adapter;

import android.content.Context;

import de.diebois.shakerapp.Cocktail;
import de.diebois.shakerapp.R;

import org.jetbrains.annotations.NotNull;

public class SmallCocktailRVAdapter extends CocktailRVAdapter {

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    public SmallCocktailRVAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemLayoutID() {
        return R.layout.small_cocktail_item;
    }

    @Override
    public int getCocktailNameID() {
        return R.id.small_ingredient_layout_name;
    }

    @Override
    public int getCocktailImageID() {
        return R.id.small_ingredient_img;
    }

    @Override
    public String getCocktailImageURL(Cocktail cocktail) {
        // Hier nehmen wir das kleinere Bild, mit "preview"
        return cocktail.getImg_Url() + "/preview";
    }

    @Override
    public String getCocktailImageFilename(Cocktail cocktail) {
        String url = cocktail.getImg_Url();
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
