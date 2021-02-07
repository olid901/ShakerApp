package de.diebois.shakerapp.ui.adapter;

import android.content.Context;
import android.content.Intent;

import de.diebois.shakerapp.Cocktail;
import de.diebois.shakerapp.CocktailDatabase;
import de.diebois.shakerapp.Helper;
import de.diebois.shakerapp.R;

import org.jetbrains.annotations.NotNull;

public class BigCocktailRVAdapter extends CocktailRVAdapter {

    private final CocktailDatabase db;

    public BigCocktailRVAdapter(Context context) {
        super(context);
        this.db = new CocktailDatabase(context);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Cocktail cocktail = super.cocktailList().get(position);

        holder.shareButtonView.setOnClickListener(v -> shareIntent(cocktail));

        holder.favoriteButtonView.setOnClickListener(v -> {
            if (db.isInDatabase(cocktail)) {
                db.deleteCocktail(cocktail);
            } else {
                db.addCocktail(cocktail);
            }
            Helper.notifyAdaperFromUi(this, position);
        });

        boolean val = db.isInDatabase(cocktail);

        if (val) {
            holder.favoriteButtonView.setImageResource(R.drawable.ic_heart_filled);
        } else {
            holder.favoriteButtonView.setImageResource(R.drawable.ic_heart_border);
        }

    }

    private void shareIntent(Cocktail cocktail) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Have a look at this nice cocktail!\nhttps://www.thecocktaildb.com/drink.php?c=" + cocktail.getID());
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        super.layoutInflater.getContext().startActivity(shareIntent);
    }

    @Override
    public int getItemLayoutID() {
        return R.layout.big_cocktail_item;
    }

    @Override
    public int getCocktailNameID() {
        return R.id.big_cocktail_layout_name;
    }

    @Override
    public int getCocktailImageID() {
        return R.id.big_cocktail_img;
    }

    @Override
    public String getCocktailImageURL(Cocktail cocktail) {
        // Hier nehmen wir das normale Bild, ohne "preview"
        return cocktail.getImg_Url();
    }

    @Override
    public String getCocktailImageFilename(Cocktail cocktail) {
        // Wir nehmen den Dateinamen, so wie er in der URL steht
        String url = cocktail.getImg_Url();
        return url.substring(url.lastIndexOf('/')+1);
    }
}