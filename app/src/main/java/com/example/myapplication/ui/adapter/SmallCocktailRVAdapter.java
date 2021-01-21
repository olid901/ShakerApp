package com.example.myapplication.ui.adapter;

import android.content.Context;

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
}
