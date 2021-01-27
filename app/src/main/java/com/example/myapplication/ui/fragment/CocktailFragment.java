package com.example.myapplication.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.Cocktail;
import com.example.myapplication.ui.CocktailClickListener;
import com.example.myapplication.ui.CocktailDetailsActivity;
import com.example.myapplication.ui.adapter.CocktailRVAdapter;

import java.util.LinkedHashMap;

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
        //Toast.makeText(getContext(), "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        // TODO Vlt kann man einen Cocktail einfach in ne statische Variable packen und den dann im neuen Intent auslesen
        // Wäre besser, dann müsste man nicht erneut die Cocktail-Infos abfragen
        // Außer die Suche liefert nicht direkt alle infos über den Cocktail
        System.out.println();
        System.out.println("rtesersfdsfdsfsdfsdfsdfdsfffffffsdfdsfdsfsfd");
        Context context = getContext();
        Intent intent = new Intent(context, CocktailDetailsActivity.class);
        intent.putExtra("cocktailID", adapter.getItem(position).getID());
        System.out.println("Sent: " + adapter.getItem(position).getID());
        context.startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(getCurrentFragmentID(), container, false);

        LinkedHashMap<Integer, Cocktail> cocktailMap = new LinkedHashMap<>();

        RecyclerView recyclerView = view.findViewById(getCurrentRecViewID());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //adapter = createAdapter(cocktailMap);
        adapter = createAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(this);

        fetchAllCocktails();

        //Network.loadCocktails(getCocktailListURL(), cocktailMap, adapter);

        return view;
    }

    abstract public void fetchAllCocktails();
    abstract int getCurrentFragmentID();
    abstract int getCurrentRecViewID();
    abstract CocktailRVAdapter createAdapter();
}