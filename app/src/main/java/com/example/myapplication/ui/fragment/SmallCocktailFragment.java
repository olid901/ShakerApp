package com.example.myapplication.ui.fragment;

import com.example.myapplication.Cocktail;
import com.example.myapplication.R;
import com.example.myapplication.ui.adapter.CocktailRVAdapter;
import com.example.myapplication.ui.adapter.SmallCocktailRVAdapter;

import java.util.List;

/**
 * Dieses Fragment zeigt Cocktails klein an, was für größere Listen praktischer wird
 */
public class SmallCocktailFragment extends CocktailFragment {

    @Override
    int getCurrentFragmentID() {
        return R.layout.fragment_all_cocktails;
    }

    @Override
    int getCurrentRecViewID() {
        return R.id.small_cocktail_rv;
    }

    @Override
    String getCocktailListURL() {
        return "https://www.thecocktaildb.com/api/json/v2/9973533/filter.php?a=Alcoholic";
    }

    @Override
    CocktailRVAdapter getAdapter(List<Cocktail> cocktailList) {
        return new SmallCocktailRVAdapter(getContext(), cocktailList);
    }
}