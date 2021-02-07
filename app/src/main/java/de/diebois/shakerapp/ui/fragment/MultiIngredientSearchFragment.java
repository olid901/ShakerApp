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
public class MultiIngredientSearchFragment extends BigCocktailFragment{

    LinkedHashMap<String, Ingredient> IngredientsAtHome;

    public MultiIngredientSearchFragment(LinkedHashMap<String, Ingredient> IngredientsAtHome){
        this.IngredientsAtHome = IngredientsAtHome;
    }

    @Override
    public void fetchAllCocktails() {
        LinkedHashMap<Integer, Cocktail> cocktailMap = new LinkedHashMap<>();

        Network.multiIngredientSearch(cocktailMap, IngredientsAtHome, adapter);

        adapter.setCocktailList(cocktailMap);
    }
}
