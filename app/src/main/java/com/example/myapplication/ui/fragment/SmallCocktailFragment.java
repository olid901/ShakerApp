package com.example.myapplication.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.myapplication.Cocktail;
import com.example.myapplication.Network;
import com.example.myapplication.R;
import com.example.myapplication.ui.adapter.CocktailRVAdapter;
import com.example.myapplication.ui.adapter.SmallCocktailRVAdapter;

import java.util.LinkedHashMap;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // TODO: Vermutlich muss hier die ID noch ausgetauscht werden
        EditText etValue = view.findViewById(R.id.cocktail_search_text);
        etValue.addTextChangedListener(new TextWatcher() {
            @Override // Brauchen wir nicht
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override // Brauchen wir nicht
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0)
                    fetchAllCocktails();
                else
                    filterCocktails(s.toString());
            }
        });
        return view;
    }

    @Override
    public void fetchAllCocktails() {
        String apiURL = "https://www.thecocktaildb.com/api/json/v2/9973533/filter.php?a=Alcoholic";
        LinkedHashMap<Integer, Cocktail> cocktailMap = new LinkedHashMap<>();
        adapter.setCocktailList(cocktailMap);
        Network.loadCocktails(apiURL, cocktailMap, adapter);
    }

    // Standard-Implementierung für eine allgemeine Cocktail-Suche
    // TODO Eigentlich kann man das auch mit der fetchAllCocktails-Methode kombinieren
    private void filterCocktails(String filter) {
        String cocktailURL = "https://www.thecocktaildb.com/api/json/v2/9973533/search.php?s=" + filter;
        LinkedHashMap<Integer, Cocktail> cocktailMap = new LinkedHashMap<>();
        adapter.setCocktailList(cocktailMap);
        Network.loadCocktails(cocktailURL, cocktailMap, adapter);
    }

    @Override
    CocktailRVAdapter createAdapter() {
        return new SmallCocktailRVAdapter(getContext());
    }
}