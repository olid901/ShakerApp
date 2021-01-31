package com.example.myapplication.ui.fragment;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.myapplication.Cocktail;
import com.example.myapplication.Network;
import com.example.myapplication.R;
import com.example.myapplication.ui.adapter.BigCocktailRVAdapter;
import com.example.myapplication.ui.adapter.CocktailRVAdapter;

import java.util.LinkedHashMap;

/**
 * Dieses Fragment zeigt die beliebtesten Cocktails groß an, ähnlich wie auf Instagram
 * TODO: Das kleine "Like"-Herz soll wechseln, wenn der Cocktail bereits geliked wurde
 * Überlegen, wie ich das dann noch mache:
 * Gehe ich den SAP-Weg und klatsch extension points rein?
 */
public class BigCocktailFragment extends CocktailFragment {

ImageButton shareButton;

    @Override
    public void fetchAllCocktails() {
        String apiURL = "https://www.thecocktaildb.com/api/json/v2/***REMOVED***/popular.php";
        LinkedHashMap<Integer, Cocktail> cocktailMap = new LinkedHashMap<>();
        adapter.setCocktailList(cocktailMap);
        Network.loadCocktails(apiURL, cocktailMap, adapter);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.wtf("Bitte Helfen sie mir", "Ich bin in Gefahr");

        View view =  super.onCreateView(inflater, container, savedInstanceState);



        ImageButton sharingButton = view.findViewById(R.id.big_cocktail_interaction_share);

        sharingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                shareIntent();

            }

        });

        return view;

    }



    private void shareIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Have a look at this nice cocktail:"+"LINK");
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
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