package de.diebois.shakerapp.ui.fragment;

import de.diebois.shakerapp.Cocktail;
import de.diebois.shakerapp.Network;
import de.diebois.shakerapp.R;
import de.diebois.shakerapp.ui.adapter.BigCocktailRVAdapter;
import de.diebois.shakerapp.ui.adapter.CocktailRVAdapter;

import java.util.LinkedHashMap;

/**
 * Dieses Fragment zeigt die beliebtesten Cocktails groß an, ähnlich wie auf Instagram
 * TODO: Das kleine "Like"-Herz soll wechseln, wenn der Cocktail bereits geliked wurde
 * Überlegen, wie ich das dann noch mache:
 * Gehe ich den SAP-Weg und klatsch extension points rein?
 */
public class BigCocktailFragment extends CocktailFragment {

    @Override
    public void fetchAllCocktails() {
        String apiURL = Network.getBaseURL() + "/popular.php";
        LinkedHashMap<Integer, Cocktail> cocktailMap = new LinkedHashMap<>();
        adapter.setCocktailList(cocktailMap);
        Network.loadCocktails(apiURL, cocktailMap, adapter);
    }

    @Override
    int getCurrentFragmentID() {
        return R.layout.fragment_home;
    }

    @Override
    int getCurrentRecViewID() {
        return R.id.big_cocktail_rv;
    }

    @Override
    CocktailRVAdapter createAdapter() {
        return new BigCocktailRVAdapter(getContext());
    }
}