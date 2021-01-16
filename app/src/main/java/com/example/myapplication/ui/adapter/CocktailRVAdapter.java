package com.example.myapplication.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Cocktail;
import com.example.myapplication.ui.CocktailClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class CocktailRVAdapter extends RecyclerView.Adapter<CocktailRVAdapter.ViewHolder> {

    private final LinkedHashMap<Integer, Cocktail> cocktailMap;
    private final LayoutInflater layoutInflater;
    private CocktailClickListener itemClickListener;

    private List<Cocktail> cocktailList(){
        return new ArrayList(cocktailMap.values());
    }

    public CocktailRVAdapter(Context context, LinkedHashMap<Integer, Cocktail> data) {
        this.layoutInflater = LayoutInflater.from(context);
        this.cocktailMap = data;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(getItemLayoutID(), parent, false));
    }

    /**
     * Hier wird der Cocktail-Name und das Bild zu jedem Eintrag im Recycler View gesetzt
     * TODO: Bild zu Cocktail anzeigen
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //List<Cocktail> cocktailList = new ArrayList(cocktailMap.values());
        String cocktailName = cocktailList().get(position).getStrDrink();
        holder.cocktailNameView.setText(cocktailName);
    }

    /**
     * Get total number of rows
     */
    @Override
    public int getItemCount() {
        return cocktailMap.size();
    }

    /**
     * Stores and recycles views as they are scrolled off screen
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView cocktailNameView;

        ViewHolder(View itemView) {
            super(itemView);
            cocktailNameView = itemView.findViewById(getCocktailNameID());
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) itemClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    /**
     * Convenience method for getting data at click position
     */
    //wichtig: Es muss die cocktailList verwendet werden, da bei der Map sonst versucht wird das Item zu returnen, das auf id gemappt ist, nicht das an der Stelle id
    //Daher zur Verständnis auch mal "id" in "pos" umbenannt, damit es nicht zu Verwirrung kommt
    public Cocktail getItem(int pos) {
        return cocktailList().get(pos);
    }

    /**
     * allows clicks events to be caught
     */
    public void setClickListener(CocktailClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /**
     * Hole die ID vom jeweiligen Layout eines einzelnen Recycler View Eintrags
     */
    public abstract int getItemLayoutID();

    /**
     * Hole die ID vom TextView, welches den Namen des Cocktails beinhaltet
     */
    public abstract int getCocktailNameID();

}
