package com.example.myapplication;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public void addIngredient(String Ingredient, String Measurement){

        Pattern patternAllOz = Pattern.compile("([0-9]+ )?([0-9].)?[0-9] oz");
        Pattern patternAllCups = Pattern.compile("([0-9]+ )?([0-9].)?[0-9] [C|c]ups?");
        Pattern patternBruchOz = Pattern.compile("[0-9]/[0-9](?= oz)");
        Pattern patternBruchCups = Pattern.compile("[0-9]/[0-9](?= [C|c]ups?)");
        Pattern patternDezimalzahlOz = Pattern.compile("[0-9]+\\.?[0-9]?(?= +oz)");
        Pattern patternDezimalzahlCups = Pattern.compile("[0-9]+\\.?[0-9]?(?= +[C|c]ups?)");
        Matcher matcherAllOz = patternAllOz.matcher(Measurement);
        if (matcherAllOz.find()){
            double totalOz = 0;
            String all = matcherAllOz.group(0);
            String all_replaced = all;
            Matcher matcherBruchOz = patternBruchOz.matcher(all);
            if (matcherBruchOz.find()){
                String Bruch = matcherBruchOz.group(0);
                double DezimalBruch = ((double) Integer.parseInt(String.valueOf(Bruch.charAt(0))))/Integer.parseInt(String.valueOf(Bruch.charAt(2)));
                all_replaced = all.replace(Bruch, "");
                totalOz += DezimalBruch;
            }
            Matcher matcherDezimalOz = patternDezimalzahlOz.matcher(all_replaced);
            if (matcherDezimalOz.find()){
                String Dezizahl = matcherDezimalOz.group(0);
                totalOz += Double.parseDouble(Dezizahl);
            }
            String replacement = ozToCl(totalOz)+" cl";
            Measurement = Measurement.replace(all, replacement);
            //System.out.println("Regex replacement: "+all+" --> "+replacement);

        }

        Matcher matcherAllCups = patternAllCups.matcher(Measurement);
        if (matcherAllCups.find()){
            double totalCups = 0;
            String all = matcherAllCups.group(0);
            String all_replaced = all;
            Matcher matcherBruchCups = patternBruchCups.matcher(all);
            if (matcherBruchCups.find()){
                String Bruch = matcherBruchCups.group(0);
                double DezimalBruch = ((double) Integer.parseInt(String.valueOf(Bruch.charAt(0))))/Integer.parseInt(String.valueOf(Bruch.charAt(2)));
                all_replaced = all.replace(Bruch, "");
                totalCups += DezimalBruch;
            }
            Matcher matcherDezimalCups = patternDezimalzahlCups.matcher(all_replaced);
            if (matcherDezimalCups.find()){
                String Dezizahl = matcherDezimalCups.group(0);
                totalCups += Double.parseDouble(Dezizahl);
            }
            String replacement = cupsToMl(totalCups)+" ml";
            Measurement = Measurement.replace(all, replacement);
            //System.out.println("Regex replacement: "+all+" --> "+replacement);

        }

        Ingredients.add(Ingredient);
        Measures.add(Measurement);
    }

    private static double ozToCl(double oz){
        return Math.round(2*oz*2.95735)/2.0;
    }
    private static int cupsToMl(double cups){ return (int)Math.round(cups*240); }

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
