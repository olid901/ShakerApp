package com.example.myapplication.ui.adapter;

import android.content.Context;
import android.content.Intent;

import com.example.myapplication.Cocktail;
import com.example.myapplication.R;

import org.jetbrains.annotations.NotNull;

public class BigCocktailRVAdapter extends CocktailRVAdapter {

    public BigCocktailRVAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Cocktail cocktail = super.cocktailList().get(position);

        holder.shareButtonView.setOnClickListener(v -> shareIntent(cocktail));
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