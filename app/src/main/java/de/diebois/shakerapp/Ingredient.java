package de.diebois.shakerapp;

public class Ingredient {

    private final String StrIngredient;
    private String StrDescription;
    private String StrID;
    private String Prozent;
    private boolean atHome = false;

    public boolean isAtHome() {
        return atHome;
    }

    public void setAtHome(boolean atHome) {
        this.atHome = atHome;
    }

    public static final short SMALL = 1, MEDIUM = 2, LARGE = 3;

    public Ingredient(String StrIngredient){
        this.StrIngredient = StrIngredient;
    }

    public String getStrIngredient() {
        return this.StrIngredient;
    }

    public boolean hasImage(){
        return true;
    }

    public String Img_Url(short size){
        if(size == SMALL)
            return "https://www.thecocktaildb.com/images/ingredients/"+StrIngredient+"-Small.png";
        else if(size == MEDIUM)
            return "https://www.thecocktaildb.com/images/ingredients/"+StrIngredient+"-Medium.png";
        else if (size == LARGE)
            return "https://www.thecocktaildb.com/images/ingredients/"+StrIngredient+".png";
        else //default return bei falscher size Angabe
            return "https://www.thecocktaildb.com/images/ingredients/"+StrIngredient+".png";
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "StrIngredient='" + StrIngredient + '\'' +
                ", atHome=" + atHome +
                '}';
    }
}
