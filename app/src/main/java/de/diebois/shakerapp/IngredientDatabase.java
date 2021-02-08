package de.diebois.shakerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class IngredientDatabase extends SQLiteOpenHelper {

    private static final String TAG = "SQLite";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "saved_ingredients.db";

    // Table name
    private static final String TABLE_INGREDIENTS = "Ingredients";

    private static final String COLUMN_INGREDIENT_NAME = "Ingredient";

    //    private static final String COLUMN_COCKTAIL_NAME ="Name";
//    private static final String COLUMN_COCKTAIL_IMAGE_URL ="IMG_URL";
    public IngredientDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // https://o7planning.org/10433/android-sqlite-database

    // Create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "IngredientDatabase.onCreate ... ");

        String script = "CREATE TABLE " + TABLE_INGREDIENTS + "(" + COLUMN_INGREDIENT_NAME + " TEXT PRIMARY KEY)";

        // Execute Script.
        db.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "IngredientDatabase.onUpgrade ... ");
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);

        // Create tables again
        onCreate(db);
    }

    public void addIngredient(Ingredient ingredient) {
        Log.i(TAG, "IngredientDatabase.addIngredient ... " + ingredient.getStrIngredient());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_INGREDIENT_NAME, ingredient.getStrIngredient());

        // Inserting Row
        db.insert(TABLE_INGREDIENTS, null, values);

        // Closing database connection
        db.close();
    }

    public boolean isInDatabase(Ingredient ingredient) {
        Log.i(TAG, "IngredientDatabase.isInDatabase ... " + ingredient.getStrIngredient());

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_INGREDIENTS,
                new String[]{COLUMN_INGREDIENT_NAME},
                COLUMN_INGREDIENT_NAME + "=?",
                new String[]{String.valueOf(ingredient.getStrIngredient())},
                null,
                null,
                null,
                null);

        boolean returnValue = cursor.getCount() == 1;
        cursor.close();
        return returnValue;
    }

    public List<Ingredient> getAllIngredients() {
        Log.i(TAG, "IngredientDatabase.getAllIngredients ... ");

        List<Ingredient> ingredientList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_INGREDIENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Ingredient ingredient = new Ingredient(cursor.getString(0));
                ingredientList.add(ingredient);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return ingredientList;
    }

    public void deleteIngredient(Ingredient ingredient) {
        Log.i(TAG, "IngredientDatabase.deleteIngredient ... " + ingredient.getStrIngredient());

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INGREDIENTS, COLUMN_INGREDIENT_NAME + " = ?", new String[]{"" + ingredient.getStrIngredient()});
        db.close();
    }
}