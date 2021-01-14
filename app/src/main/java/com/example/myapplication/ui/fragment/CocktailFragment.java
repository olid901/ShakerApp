package com.example.myapplication.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.Cocktail;
import com.example.myapplication.Network;
import com.example.myapplication.ui.CocktailClickListener;
import com.example.myapplication.ui.adapter.CocktailRVAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Allgemeine Cocktail-Fragment-Klasse, da wir vermutlich mehrere
 * Klassen brauchen die alle mehr oder weniger das gleiche machen
 * TODO: Rausfinden, wie ich die Cocktail-Fragment generischer machen kann
 * Vor allem in Hinsicht auf den jeweiligen RV-Adapter
 */
public abstract class CocktailFragment extends Fragment implements CocktailClickListener {

    CocktailRVAdapter adapter;

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
        Toast.makeText(getContext(), "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getCurrentFragmentID(), container, false);

        ArrayList<Cocktail> cocktailList = new ArrayList<>();
        Network.loadCocktails(getCocktailListURL(), cocktailList);

        RecyclerView recyclerView = view.findViewById(getCurrentRecViewID());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = getAdapter(cocktailList);

        // TODO: Das darf auf keinen Fall so bleiben!
        // Ohne sleep wird im Main-Fragment kein Cocktail angezeigt, weil diese dann noch nicht geladen sind
        // Stattdessen die Network-Klasse umkrempeln und das ganze mit einem Callback lösen?
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    abstract int getCurrentFragmentID();
    abstract int getCurrentRecViewID();
    abstract String getCocktailListURL();
    abstract CocktailRVAdapter getAdapter(List<Cocktail> cocktailList);
}