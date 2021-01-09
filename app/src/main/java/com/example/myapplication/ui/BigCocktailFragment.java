package com.example.myapplication.ui;

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
import com.example.myapplication.R;

import java.util.ArrayList;

/**
 * Dieses Fragment zeigt die beliebtesten Cocktails groß an, ähnlich wie auf Instagram
 * TODO: Eine einheitliche Fragment-Klasse für die Groß- und Kleinansicht für Cocktails?
 */
public class BigCocktailFragment extends Fragment implements BigCocktailRVAdapter.ItemClickListener {

    BigCocktailRVAdapter adapter;

    public BigCocktailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onItemClick(View view, int position) {
        // TODO: Allgemeinen CocktailClickListener schreiben
        // Ob man ja auf einen Cocktail in der großen oder kleinen Ansicht drückt ist ja egal
        // Am Ende wird der Cocktail einzeln groß dargestgellt, weswegen man das verallgemeinern kann
        Toast.makeText(getContext(), "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ArrayList<Cocktail> cocktailList = new ArrayList<>();

        Network.loadCocktails("https://www.thecocktaildb.com/api/json/v2/9973533/popular.php", cocktailList);

        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.big_cocktail_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BigCocktailRVAdapter(getContext(), cocktailList);

        // TODO: Das darf auf keinen Fall so bleiben!
        // Ohne sleep wird im Main-Fragment kein Cocktail angezeigt, weil diese dann noch nicht geladen sind
        // Stattdessen die Network-Klasse umkrempeln und das ganze mit einem Callback lösen
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        return view;
    }
}