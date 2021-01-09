package com.example.myapplication.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Cocktail;
import com.example.myapplication.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BigCocktailRVAdapter extends RecyclerView.Adapter<BigCocktailRVAdapter.ViewHolder> {

    private final List<Cocktail> cocktailList;
    private final LayoutInflater layoutInflater;
    private ItemClickListener itemClickListener;

    BigCocktailRVAdapter(Context context, List<Cocktail> data) {
        this.layoutInflater = LayoutInflater.from(context);
        this.cocktailList = data;
    }

    // inflates the row layout from xml when needed
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.big_cocktail_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    // Hier wird der Cocktail-Name und zukünftig auch das Bild zu jedem Cocktail gesetzt
    // TODO: Bild zu Cocktail anzeigen
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String cocktailName = cocktailList.get(position).getStrDrink();
        holder.cocktailNameView.setText(cocktailName);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return cocktailList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView cocktailNameView;

        ViewHolder(View itemView) {
            super(itemView);
            cocktailNameView = itemView.findViewById(R.id.big_cocktail_layout_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) itemClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Cocktail getItem(int id) {
        return cocktailList.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
