package com.example.myapplication.ui.fragment;

import com.example.myapplication.Cocktail;
import com.example.myapplication.Database;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Dieses Fragment zeigt die gespeicherten Cocktails groß an, ähnlich wie auf Instagram
 */
public class FavoriteCocktailFragment extends BigCocktailFragment{

    @Override
    public void fetchAllCocktails() {
        LinkedHashMap<Integer, Cocktail> cocktailMap = new LinkedHashMap<>();
        Database db = new Database(getContext());

        List<Cocktail> cocktails = db.getAllCocktails();
        for (Cocktail c : cocktails) {
            cocktailMap.put(c.getID(), c);
        }

        adapter.setCocktailList(cocktailMap);
    }
}
