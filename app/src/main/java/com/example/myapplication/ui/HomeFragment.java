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
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * Dieses Fragment zeigt die beliebtesten Cocktails groß an, ähnlich wie auf Instagram
 * TODO: Das ganze Parameter-Zeug kommt vermutlich ja noch weg
 */
public class HomeFragment extends Fragment implements BigCocktailRVAdapter.ItemClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    BigCocktailRVAdapter adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getContext(), "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        ArrayList<Cocktail> cocktailNames = new ArrayList<>();

        Network.loadCocktails("https://www.thecocktaildb.com/api/json/v2/***REMOVED***/popular.php", cocktailNames);
        /*cocktailNames.add("Radioactive Long Island Icetea");
        cocktailNames.add("Daiquiri");
        cocktailNames.add("Old Fashioned");
        cocktailNames.add("Margarita");
        cocktailNames.add("SPAGHETTIOS");*/

        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.big_cocktail_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BigCocktailRVAdapter(getContext(), cocktailNames);

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