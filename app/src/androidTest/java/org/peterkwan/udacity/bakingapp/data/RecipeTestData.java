package org.peterkwan.udacity.bakingapp.data;

import org.peterkwan.udacity.bakingapp.data.entity.Recipe;
import org.peterkwan.udacity.bakingapp.data.entity.RecipeIngredient;
import org.peterkwan.udacity.bakingapp.data.entity.RecipeStep;
import org.peterkwan.udacity.bakingapp.data.pojo.RecipeInfo;

import java.util.Arrays;

public final class RecipeTestData {

    public static final RecipeIngredient ingredientEntity11 = new RecipeIngredient(1, 1, "flour", "10", "g");
    public static final RecipeIngredient ingredientEntity12 = new RecipeIngredient(2, 1, "sugar", "2", "cup");
    public static final RecipeIngredient ingredientEntity13 = new RecipeIngredient(3, 1, "egg", "3", "");
    public static final RecipeStep stepEntity11 = new RecipeStep(1, 1, "Pour flour into a bowl", "Measure 10 gram of flour and pour it into a bowl", "", "");
    public static final RecipeStep stepEntity12 = new RecipeStep(2, 1, "Pour sugar into a bowl", "Pour 2 cups of sugar into the bowl", "", "https://test.org/step2.mp4");
    public static final RecipeStep stepEntity13 = new RecipeStep(3, 1, "Add egg", "Add 3 eggs into the bowl", "https://test.org/step3.mp4", "");
    public static final Recipe recipeEntity1 = new Recipe(1, "Yellow Cake", 8, "",
            Arrays.asList(ingredientEntity11, ingredientEntity12, ingredientEntity13), Arrays.asList(stepEntity11, stepEntity12, stepEntity13));

    public static final RecipeIngredient ingredientEntity21 = new RecipeIngredient(4, 2, "flour", "12", "g");
    public static final RecipeIngredient ingredientEntity22 = new RecipeIngredient(5, 2, "sugar", "2", "cup");
    public static final RecipeIngredient ingredientEntity23 = new RecipeIngredient(6, 2, "egg", "3", "");
    public static final RecipeStep stepEntity21 = new RecipeStep(1, 2, "Pour flour into a bowl", "Measure 12 gram of flour and pour it into a bowl", "", "");
    public static final RecipeStep stepEntity22 = new RecipeStep(2, 2, "Pour sugar into a bowl", "Pour 2 cups of sugar into the bowl", "", "https://test.org/step2.mp4");
    public static final Recipe recipeEntity2 = new Recipe(2, "Brownies", 8, "",
            Arrays.asList(ingredientEntity21, ingredientEntity22, ingredientEntity23), Arrays.asList(stepEntity21, stepEntity22));

    public static final RecipeInfo recipeInfo1 = new RecipeInfo(
            recipeEntity1,
            Arrays.asList(ingredientEntity11, ingredientEntity12, ingredientEntity13),
            Arrays.asList(stepEntity11, stepEntity12, stepEntity13)
    );
    public static final RecipeInfo recipeInfo2 = new RecipeInfo(
            recipeEntity2,
            Arrays.asList(ingredientEntity21, ingredientEntity22, ingredientEntity23),
            Arrays.asList(stepEntity21, stepEntity22)
    );

}
