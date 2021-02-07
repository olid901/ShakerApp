package de.diebois.shakerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class CocktailDatabase extends SQLiteOpenHelper {

    private static final String TAG = "SQLite";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "fav_cocktails.db";

    // Table name
    private static final String TABLE_COCKTAILS = "Cocktails";

    private static final String COLUMN_COCKTAIL_ID ="ID";
    private static final String COLUMN_COCKTAIL_NAME ="Name";
    private static final String COLUMN_COCKTAIL_IMAGE_URL ="IMG_URL";

    public CocktailDatabase(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // https://o7planning.org/10433/android-sqlite-database

    // Create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Database.onCreate ... ");
        // Script.

        String script = "CREATE TABLE " + TABLE_COCKTAILS + "("
                + COLUMN_COCKTAIL_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_COCKTAIL_NAME + " TEXT,"
                + COLUMN_COCKTAIL_IMAGE_URL + " TEXT" + ")";

        // Execute Script.
        db.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i(TAG, "Database.onUpgrade ... ");
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COCKTAILS);

        // Create tables again
        onCreate(db);
    }

    public void addCocktail(Cocktail cocktail) {
        Log.i(TAG, "Database.addCocktail ... " + cocktail.getStrDrink());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_COCKTAIL_ID, cocktail.getID());
        values.put(COLUMN_COCKTAIL_NAME, cocktail.getStrDrink());
        values.put(COLUMN_COCKTAIL_IMAGE_URL, cocktail.getImg_Url());

        // Inserting Row
        db.insert(TABLE_COCKTAILS, null, values);

        // Closing database connection
        db.close();
    }

    public boolean isInDatabase(Cocktail cocktail) {
        Log.i(TAG, "MyDatabaseHelper.isInDatabase ... " + cocktail.getID());

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_COCKTAILS,
                new String[] { COLUMN_COCKTAIL_ID },
                COLUMN_COCKTAIL_ID + "=?",
                new String[] { String.valueOf(cocktail.getID()) },
                null,
                null,
                null,
                null);

        boolean returnValue = cursor.getCount() == 1;
        cursor.close();
        return returnValue;
    }

    public List<Cocktail> getAllCocktails() {
        Log.i(TAG, "Database.getAllNotes ... " );

        List<Cocktail> cocktailList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_COCKTAILS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Cocktail cocktail = new Cocktail(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
                cocktailList.add(cocktail);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return cocktailList;
    }

    public void deleteCocktail(Cocktail cocktail) {
        Log.i(TAG, "Database.deleteCocktail ... " + cocktail.getStrDrink() );

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COCKTAILS, COLUMN_COCKTAIL_ID + " = ?", new String[] { "" + cocktail.getID() });
        db.close();
    }
}