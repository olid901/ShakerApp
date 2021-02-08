package de.diebois.shakerapp.ui.fragment;

import java.util.LinkedHashMap;
import java.util.List;

import de.diebois.shakerapp.Cocktail;
import de.diebois.shakerapp.CocktailDatabase;
import de.diebois.shakerapp.Ingredient;
import de.diebois.shakerapp.Network;

/**
 * Dieses Fragment zeigt die gespeicherten Cocktails groß an, ähnlich wie auf Instagram
 */
public class MultiIngredientSearchFragment extends BigCocktailFragment {

    private List<Ingredient> ingredientsAtHome;

    public MultiIngredientSearchFragment(List<Ingredient> ingredientsAtHome) {
        this.ingredientsAtHome = ingredientsAtHome;
    }

    @Override
    public void fetchAllCocktails() {
        LinkedHashMap<Integer, Cocktail> cocktailMap = new LinkedHashMap<>();

        Network.multiIngredientSearch(cocktailMap, ingredientsAtHome, adapter);

        adapter.setCocktailList(cocktailMap);
    }
}
