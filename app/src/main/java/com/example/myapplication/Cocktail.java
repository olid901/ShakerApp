package com.example.myapplication;

import java.util.ArrayList;

public class Cocktail {
    public int getID() {
        return ID;
    }

    private int ID;
    private String StrDrink;
    private String Img_Url;
    private ArrayList<String> Ingredients, Measures;
    private String Instruction, Category, Alcoholic, Glass, Tags;

    public String getStrDrink() { return StrDrink; }

    public void setInstruction(String instruction) {
        this.Instruction = instruction;
    }

    public void setCategory(String category) {
        this.Category = category;
    }

    public void setAlcoholic(String alcoholic) {
        this.Alcoholic = alcoholic;
    }

    public void setGlass(String glass) {
        this.Glass = glass;
    }

    public void setTags(String tags) {
        this.Tags = tags;
    }



    public Cocktail(int ID, String StrDrink, String Img_Url){
        this.ID = ID;
        this.StrDrink = StrDrink;
        this.Img_Url= Img_Url;
        this.Instruction = "";
        this.Category = "";
        this.Alcoholic = "";
        this.Glass = "";
        this.Tags = "";
        this.Ingredients = new ArrayList<String>();
        this.Measures = new ArrayList<String>();
    }

    public void addIngredients(String rawData){
        //TBA: Siehe Karte Measurements Magic
    }

    @Override
    public String toString() {
        return "Cocktail{" +
                "ID=" + ID +
                ", StrDrink='" + StrDrink + '\'' +
                ", Instruction='" + Instruction + '\'' +
                ", Category='" + Category + '\'' +
                ", Alcoholic='" + Alcoholic + '\'' +
                ", Glass='" + Glass + '\'' +
                ", Tags='" + Tags + '\'' +
                '}';
    }

}
