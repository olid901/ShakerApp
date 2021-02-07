package de.diebois.shakerapp.ui.fragment;

import de.diebois.shakerapp.Cocktail;
import de.diebois.shakerapp.CocktailDatabase;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Dieses Fragment zeigt die gespeicherten Cocktails groß an, ähnlich wie auf Instagram
 */
public class FavoriteCocktailFragment extends BigCocktailFragment{

    @Override
    public void fetchAllCocktails() {
        LinkedHashMap<Integer, Cocktail> cocktailMap = new LinkedHashMap<>();
        CocktailDatabase db = new CocktailDatabase(getContext());

        List<Cocktail> cocktails = db.getAllCocktails();
        for (Cocktail c : cocktails) {
            cocktailMap.put(c.getID(), c);
        }

        adapter.setCocktailList(cocktailMap);
    }
}
