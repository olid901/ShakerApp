package com.example.myapplication.ui.adapter;

import android.content.Context;

import com.example.myapplication.Cocktail;
import com.example.myapplication.R;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class SmallCocktailRVAdapter extends CocktailRVAdapter {

    public SmallCocktailRVAdapter(Context context, LinkedHashMap<Integer, Cocktail> data) {
        super(context, data);
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
