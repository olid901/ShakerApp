package de.diebois.shakerapp.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import de.diebois.shakerapp.Ingredient;
import de.diebois.shakerapp.IngredientDatabase;
import de.diebois.shakerapp.Network;
import de.diebois.shakerapp.R;
import de.diebois.shakerapp.ui.CocktailClickListener;
import de.diebois.shakerapp.ui.adapter.IngredientRVAdapter;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Klasse für das Ingredient-Fragment
 */
public class IngredientFragment extends Fragment {

    private IngredientRVAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_ingredients, container, false);

        Button MISbtn = view.findViewById(R.id.MISbutton);
        MISbtn.setOnClickListener(v -> {
            Log.wtf("MISbtn", "I've been clicked!");

            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Fragment MISresultFragment = new MultiIngredientSearchFragment(Ingredient.atHomeList);
            fragmentTransaction.replace(R.id.nav_host_fragment, MISresultFragment);
            fragmentTransaction.addToBackStack(this.getClass().getName());
            fragmentTransaction.commit();

        });

        RecyclerView recyclerView = view.findViewById(R.id.small_ingredient_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new IngredientRVAdapter(getContext());
        recyclerView.setAdapter(adapter);

        fetchAllIngredients();

        EditText ingredientSearch = view.findViewById(R.id.ingredient_search_text);
        ingredientSearch.addTextChangedListener(new TextWatcher() {
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

    public void fetchAllIngredients() {
        String apiURL = "https://www.thecocktaildb.com/api/json/v2/9973533/list.php?i=list";
        List<Ingredient> ingList = new ArrayList<>();
        adapter.setIngredientList(ingList);
        Network.loadIngredients(apiURL, null, ingList, adapter);
    }

    // Standard-Implementierung für eine allgemeine Cocktail-Suche
    // TODO Eigentlich kann man das auch mit der fetchAllIngredients-Methode kombinieren
    private void filterIngredients(String filter) {
        String IngredientURL = "https://www.thecocktaildb.com/api/json/v2/9973533/list.php?i=list";
        List<Ingredient> ingList = new ArrayList<>();
        adapter.setIngredientList(ingList);
        Network.loadIngredients(IngredientURL, filter, ingList, adapter);
    }
}