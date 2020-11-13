package com.example.myapplication;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class Cocktail {
    private int ID;
    private String StrDrink;
    private String Img_Url;
    //private ArrayList<String> Ingredients, Measures;

    public Cocktail(int ID, String StrDrink, String Img_Url){
        this.ID = ID;
        this.StrDrink = StrDrink;
        this.Img_Url= Img_Url;
        //this.category = category;
        //this.Ingredients = Ingredients;
        //this.Measures = Measures;
    }

    @NonNull
    @Override
    public String toString() {
        return("Cocktail information: \n"+"ID = "+ID+"\nName = "+StrDrink+"\n");
        //return super.toString();
    }
}
