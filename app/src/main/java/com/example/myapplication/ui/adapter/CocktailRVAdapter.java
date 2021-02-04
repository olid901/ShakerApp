package com.example.myapplication.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Cocktail;
import com.example.myapplication.Helper;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Network;
import com.example.myapplication.R;
import com.example.myapplication.ui.CocktailClickListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class CocktailRVAdapter extends RecyclerView.Adapter<CocktailRVAdapter.ViewHolder> {

    private LinkedHashMap<Integer, Cocktail> cocktailMap;
    protected final LayoutInflater layoutInflater;
    private CocktailClickListener itemClickListener;

    protected List<Cocktail> cocktailList(){
        return new ArrayList<Cocktail>(cocktailMap.values());
    }

    public CocktailRVAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        cocktailMap = new LinkedHashMap<>(); // Prevent app from crashing
    }


    public void setCocktailList(LinkedHashMap<Integer, Cocktail> cocktailMap) {
        this.cocktailMap = cocktailMap;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(getItemLayoutID(), parent, false));
    }

    /**
     * Hier wird der Cocktail-Name und das Bild zu jedem Eintrag im Recycler View gesetzt
     */
    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        Cocktail cocktail = cocktailList().get(position);

        // TODO Return null ist böse!!! Sollte noch geändert werden bei Gelegenheit
        File file = updateCocktailImage(cocktail, position);
        if (file != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            holder.cocktailImgView.setImageBitmap(bitmap);
        } else {
            holder.cocktailImgView.setImageResource(R.drawable.ic_image_not_found);
        }

        holder.cocktailNameView.setText(cocktail.getStrDrink());
    }

    public File updateCocktailImage(Cocktail cocktail, int position) {
        // Abbrechen, wenn der Cocktail kein Bild hat
        // Was aktuell nur beim "Americano" der Fall ist
        if (!cocktail.hasImage()) {
            return null;
        }

        String url = getCocktailImageURL(cocktail);
        String filename = getCocktailImageFilename(cocktail);

        File file = new File(MainActivity.localDir, filename);

        // Wenn kein Bild gespeichert ist, dann lädt er eines runter und gibt dem Adapter bescheid
        // Danach durchläuft er erneut das onBindViewHolder inklusive dieser Methode hier
        if (!file.exists()) {
            Network.downloadPic(filename, url, () -> Helper.notifyAdaperFromUi(CocktailRVAdapter.this, position));
            return null;
        } else {
            return file;
        }
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
        final ImageView cocktailImgView;
        final ImageButton shareButtonView;

        ViewHolder(View itemView) {
            super(itemView);
            cocktailNameView = itemView.findViewById(getCocktailNameID());
            cocktailImgView = itemView.findViewById(getCocktailImageID());
            shareButtonView = itemView.findViewById(R.id.big_cocktail_interaction_share);
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

    /**
     * Hole die ID vom ImageView, welche das Bild des Cocktails beinhaltet
     */
    public abstract int getCocktailImageID();

    /**
     * Hole die Bild-URL vom Cocktail
     * Wird benötigt, um zwischen kleinen und großen Bildern unterscheiden zu können
     */
    public abstract String getCocktailImageURL(Cocktail cocktail);

    /**
     * Hole den Dateinamen des Cocktail-Bilds
     * Wird benötigt, um zwischen kleinen und großen Bildern unterscheiden zu können
     */
    public abstract String getCocktailImageFilename(Cocktail cocktail);
}
