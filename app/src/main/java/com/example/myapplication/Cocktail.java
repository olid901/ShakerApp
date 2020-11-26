package com.example.myapplication;

import java.util.ArrayList;

public class Cocktail {


    private int ID;
    private String StrDrink;
    private String Img_Url;
    private ArrayList<String> Ingredients, Measures;
    private String Instruction, Category, Alcoholic, Glass, Tags;
    private boolean favorite;


    //GETTER
    public String getStrDrink() { return StrDrink; }

    public int getID() { return ID; }

    public String getImg_Url() { return Img_Url; }

    public ArrayList<String> getIngredients() { return Ingredients; }

    public ArrayList<String> getMeasures() { return Measures; }

    public String getInstruction() { return Instruction; }

    public String getCategory() { return Category; }

    public String getAlcoholic() { return Alcoholic; }

    public String getGlass() { return Glass; }

    public String getTags() { return Tags; }

    public boolean isFavorite() { return favorite; }

    //SETTER
    public void setFavorite(boolean favorite) { this.favorite = favorite; }

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

    public void addIngredient(String Ingr, String Measurement){
        Ingredients.add(Ingr);
        Measures.add(Measurement);
    }

    public void downloadPicture(){
        //TBA in Network! (Karte Bilder speichern)
        //Network.downloadPic(Img_Url);
    }

    @Override
    public String toString() {
        return "Cocktail{" +
                "ID=" + ID +
                ", StrDrink='" + StrDrink + '\'' +
                ", Img_Url='" + Img_Url + '\'' +
                ", Ingredients=" + Ingredients +
                ", Measures=" + Measures +
                ", Instruction='" + Instruction + '\'' +
                ", Category='" + Category + '\'' +
                ", Alcoholic='" + Alcoholic + '\'' +
                ", Glass='" + Glass + '\'' +
                ", Tags='" + Tags + '\'' +
                ", favorite=" + favorite +
                '}';
    }
}
