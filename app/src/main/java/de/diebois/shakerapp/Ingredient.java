package de.diebois.shakerapp;

import java.util.Objects;

public class Ingredient {

    private final String StrIngredient;
    private String StrDescription;
    private String StrID;
    private String Prozent;

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
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return Objects.equals(StrIngredient, that.StrIngredient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(StrIngredient);
    }
}
