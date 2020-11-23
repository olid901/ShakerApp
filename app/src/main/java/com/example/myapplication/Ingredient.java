package com.example.myapplication;

public class Ingredient {

    private String StrIngredient, StrDescription, StrID, Prozent, Img_Url;
    private boolean atHome = false;

    public Ingredient(String StrIngredient){
        this.StrIngredient = StrIngredient;
    }

    public void downloadPicture(){
        //TBA in Network! (Karte Bilder speichern)
        Network.downloadPic(Img_Url);
    }
}
