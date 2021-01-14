package com.example.myapplication.ui.adapter;

import android.content.Context;

import com.example.myapplication.Cocktail;
import com.example.myapplication.R;

import java.util.List;

public class BigCocktailRVAdapter extends CocktailRVAdapter {

    public BigCocktailRVAdapter(Context context, List<Cocktail> data) {
        super(context, data);
    }

    @Override
    public int getItemLayoutID() {
        return R.layout.big_cocktail_item;
    }

    @Override
    public int getCocktailNameID() {
        return R.id.big_cocktail_layout_name;
    }
}
