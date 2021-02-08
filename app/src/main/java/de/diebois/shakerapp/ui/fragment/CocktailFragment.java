package de.diebois.shakerapp.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.diebois.shakerapp.ui.CocktailClickListener;
import de.diebois.shakerapp.ui.CocktailDetailsActivity;
import de.diebois.shakerapp.ui.adapter.CocktailRVAdapter;

/**
 * Allgemeine Cocktail-Fragment-Klasse, da wir vermutlich mehrere
 * Klassen brauchen die alle mehr oder weniger das gleiche machen
 * TODO: Rausfinden, wie ich die Cocktail-Fragment generischer machen kann
 * Vor allem in Hinsicht auf den jeweiligen RV-Adapter
 */
public abstract class CocktailFragment extends Fragment implements CocktailClickListener {

    protected CocktailRVAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Was getan werden soll, wenn auf ein jeweiliges Cocktail-Item in einer Liste geklickt wird
     * -> Cocktail öffnen und Zeug wie Zutaten in Großansicht darstellen
     */
    @Override
    public void onItemClick(View view, int position) {
        Context context = getContext();
        Intent intent = new Intent(context, CocktailDetailsActivity.class);

        // Wir weisen einem Cocktail direkt das Cocktail-Objekt zu, so haben wir
        // direkt die Cocktail-Daten da und sparen uns die Netzwerkabfrage
        CocktailDetailsActivity.cocktail = adapter.getItem(position);

        context.startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(getCurrentFragmentID(), container, false);

        RecyclerView recyclerView = view.findViewById(getCurrentRecViewID());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = createAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(this);

        fetchAllCocktails();
        return view;
    }

    abstract public void fetchAllCocktails();

    abstract int getCurrentFragmentID();

    abstract int getCurrentRecViewID();

    abstract CocktailRVAdapter createAdapter();
}