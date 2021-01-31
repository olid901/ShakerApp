package com.example.myapplication.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.Cocktail;
import com.example.myapplication.Ingredient;
import com.example.myapplication.Network;
import com.example.myapplication.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CocktailDetailsActivity extends AppCompatActivity implements UICallback {

    public static Cocktail cocktail;
    private IngredientsRVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocktail_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(cocktail.getStrDrink());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        System.out.println(cocktail.getStrDrink());

        RecyclerView recyclerView = findViewById(R.id.cocktail_ingredients_rv);
        GridLayoutManager glm = new GridLayoutManager(getBaseContext(), 2);

        recyclerView.setLayoutManager(glm);
        recyclerView.setNestedScrollingEnabled(false);

        adapter = new IngredientsRVAdapter(getBaseContext());

        LinkedHashMap<Integer, Ingredient> ingredientMap = new LinkedHashMap<>();

        adapter.setIngredientList(ingredientMap);

        recyclerView.setAdapter(adapter);

        // TODO TEST
        Network.addFullCocktailInfo(cocktail, this);

        NestedScrollView sv = findViewById(R.id.cocktail_scroll_view);
        sv.scrollTo(0,0);

        //TODO Cocktail darstellen
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void refreshView() {
        RecyclerView recyclerView = findViewById(R.id.cocktail_ingredients_rv);

        LinkedHashMap<Integer, Ingredient> ingredientMap = new LinkedHashMap<>();
        ArrayList<String> ingredientList = cocktail.getIngredients();
        // TODO WHAT THE FUCK
        ArrayList<String> measuresList = cocktail.getMeasures();

        Log.d("MyActivity", "HALLO WELT");
        Log.d("MyActivity", ingredientList.size() + "");

        for (int i = 0; i < ingredientList.size(); i++) {
            ingredientMap.put(i, new Ingredient(ingredientList.get(i)));
            Log.d("MyActivity", ingredientList.get(i));
        }

        adapter.setIngredientList(ingredientMap);
        Network.notifyAdaperFromUi(adapter);

        // TODO Auf der TheCocktailDB-Seite wird hinter jedem Punkt ein Zeilenumbruch hinzugefügt
        TextView instructionView = findViewById(R.id.instructions_text);
        instructionView.setText(cocktail.getInstruction());

        TextView glassView = findViewById(R.id.glass_text);
        glassView.setText(cocktail.getGlass());
    }
}

class IngredientsRVAdapter extends RecyclerView.Adapter<IngredientsRVAdapter.ViewHolder> {

    private LinkedHashMap<Integer, Ingredient> ingredientMap;
    private final LayoutInflater layoutInflater;
    private CocktailClickListener itemClickListener;

    private List<Ingredient> ingredientList() {
        return new ArrayList(ingredientMap.values());
    }

    public IngredientsRVAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        ingredientMap = new LinkedHashMap<>(); // Prevent app from crashing
    }


    public void setIngredientList(LinkedHashMap<Integer, Ingredient> ingredientList) {
        this.ingredientMap = ingredientList;
    }

    @NotNull
    @Override
    public IngredientsRVAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.ingredient_grid_item, parent, false));
    }

    /**
     * Hier wird der Cocktail-Name und das Bild zu jedem Eintrag im Recycler View gesetzt
     * TODO: Bild zu Ingredient anzeigen
     */
    @Override
    public void onBindViewHolder(IngredientsRVAdapter.ViewHolder holder, int position) {
        List<Ingredient> ingredientList = ingredientList();
        ArrayList<String> measures = CocktailDetailsActivity.cocktail.getMeasures();
        String ingredientName = ingredientList.get(position).getStrIngredient();
        String displayedText = ingredientName;

        // Für den Fall, dass bei einer Zutat keine Mengenangabe gegeben ist
        if (measures.get(position) != "null")
            displayedText = measures.get(position) + ingredientName;

        holder.ingredientNameView.setText(displayedText);
    }

    /**
     * Get total number of rows
     */
    @Override
    public int getItemCount() {
        return ingredientMap.size();
    }

    /**
     * Stores and recycles views as they are scrolled off screen
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView ingredientNameView;

        ViewHolder(View itemView) {
            super(itemView);
            ingredientNameView = itemView.findViewById(R.id.ingredient_name);
            //itemView.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View view) {
//            if (itemClickListener != null)
//                itemClickListener.onItemClick(view, getAdapterPosition());
//        }
    }

    /**
     * Convenience method for getting data at click position
     */
    //wichtig: Es muss die cocktailList verwendet werden, da bei der Map sonst versucht wird das Item zu returnen, das auf id gemappt ist, nicht das an der Stelle id
    //Daher zur Verständnis auch mal "id" in "pos" umbenannt, damit es nicht zu Verwirrung kommt
//    public Ingredient getItem(int pos) {
//        return cocktailList().get(pos);
//    }

    /**
     * allows clicks events to be caught
     */
    public void setClickListener(CocktailClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}