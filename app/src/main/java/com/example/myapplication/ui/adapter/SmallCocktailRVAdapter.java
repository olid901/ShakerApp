package com.example.myapplication.ui.adapter;

import android.content.Context;

import com.example.myapplication.Cocktail;
import com.example.myapplication.R;

public class SmallCocktailRVAdapter extends CocktailRVAdapter {

    public SmallCocktailRVAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemLayoutID() {
        return R.layout.small_cocktail_item;
    }

    @Override
    public int getCocktailNameID() {
        return R.id.small_cocktail_layout_name;
    }

    @Override
    public int getCocktailImageID() {
        return R.id.small_cocktail_img;
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
