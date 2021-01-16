package com.example.myapplication.ui.fragment;

import com.example.myapplication.Cocktail;
import com.example.myapplication.R;
import com.example.myapplication.ui.adapter.BigCocktailRVAdapter;
import com.example.myapplication.ui.adapter.CocktailRVAdapter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Dieses Fragment zeigt die beliebtesten Cocktails groß an, ähnlich wie auf Instagram
 * TODO: Das kleine "Like"-Herz soll wechseln, wenn der Cocktail bereits geliked wurde
 * Überlegen, wie ich das dann noch mache:
 * Gehe ich den SAP-Weg und klatsch extension points rein?
 */
public class BigCocktailFragment extends CocktailFragment {

    @Override
    int getCurrentFragmentID() {
        return R.layout.fragment_home;
    }

    @Override
    int getCurrentRecViewID() {
        return R.id.big_cocktail_rv;
    }

    @Override
    String getCocktailListURL() {
        return "https://www.thecocktaildb.com/api/json/v2/***REMOVED***/popular.php";
    }


    @Override
    CocktailRVAdapter getAdapter(LinkedHashMap<Integer, Cocktail> cocktailMap) {
        return new BigCocktailRVAdapter(getContext(), cocktailMap);
    }
}