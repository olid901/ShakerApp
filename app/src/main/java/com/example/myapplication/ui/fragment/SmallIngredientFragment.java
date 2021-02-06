package com.example.myapplication.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.myapplication.Ingredient;
import com.example.myapplication.Network;
import com.example.myapplication.R;
import com.example.myapplication.ui.adapter.IngredientRVAdapter;
import com.example.myapplication.ui.adapter.SmallIngredientRVAdapter;

import java.util.LinkedHashMap;

/**
 * Dieses Fragment zeigt Cocktails klein an, was für größere Listen praktischer wird
 */
public class SmallIngredientFragment extends IngredientFragment {

    @Override
    int getCurrentFragmentID() {
        return R.layout.fragment_all_ingredients;
    }

    @Override
    int getCurrentRecViewID() {
        return R.id.small_ingredient_rv;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // TODO: Vermutlich muss hier die ID noch ausgetauscht werden
        EditText etValue = view.findViewById(R.id.ingredient_search_text);
        etValue.addTextChangedListener(new TextWatcher() {
            @Override // Brauchen wir nicht
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override // Brauchen wir nicht
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0)
                    fetchAllIngredients();
                else
                    filterIngredients(s.toString());
            }
        });
        return view;
    }

    @Override
    public void fetchAllIngredients() {
        String apiURL = "https://www.thecocktaildb.com/api/json/v2/9973533/list.php?i=list";
        LinkedHashMap<String, Ingredient> ingredientMap = new LinkedHashMap<>();
        adapter.setIngredientList(ingredientMap);
        Network.loadIngredients(apiURL, null, ingredientMap, adapter);
    }

    // Standard-Implementierung für eine allgemeine Cocktail-Suche
    // TODO Eigentlich kann man das auch mit der fetchAllCocktails-Methode kombinieren
    private void filterIngredients(String filter) {
        String IngredientURL = "https://www.thecocktaildb.com/api/json/v2/9973533/list.php?i=list";
        LinkedHashMap<String, Ingredient> ingredientMap = new LinkedHashMap<>();
        adapter.setIngredientList(ingredientMap);
        Network.loadIngredients(IngredientURL, filter, ingredientMap, adapter);
    }

    @Override
    IngredientRVAdapter createAdapter() {
        return new SmallIngredientRVAdapter(getContext());
    }
}