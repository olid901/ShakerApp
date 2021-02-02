package com.example.myapplication.ui.adapter;

import android.content.Context;

import com.example.myapplication.Cocktail;
import com.example.myapplication.R;
import com.example.myapplication.ui.UICallback;

public class BigCocktailRVAdapter extends CocktailRVAdapter {

    public BigCocktailRVAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemLayoutID() {
        return R.layout.big_cocktail_item;
    }

    @Override
    public int getCocktailNameID() {
        return R.id.big_cocktail_layout_name;
    }

    @Override
    public int getCocktailImageID() {
        return R.id.big_cocktail_img;
    }

    @Override
    public String getCocktailImageURL(Cocktail cocktail) {
        // Hier nehmen wir das normale Bild, ohne "preview"
        return cocktail.getImg_Url();
    }

    @Override
    public String getCocktailImageFilename(Cocktail cocktail) {
        // Wir nehmen den Dateinamen, so wie er in der URL steht
        String url = cocktail.getImg_Url();
        return url.substring(url.lastIndexOf('/')+1);
    }
}