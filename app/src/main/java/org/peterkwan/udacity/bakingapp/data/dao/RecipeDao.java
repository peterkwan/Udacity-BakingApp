package org.peterkwan.udacity.bakingapp.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.peterkwan.udacity.bakingapp.data.entity.Recipe;
import org.peterkwan.udacity.bakingapp.data.entity.RecipeIngredient;
import org.peterkwan.udacity.bakingapp.data.entity.RecipeStep;
import org.peterkwan.udacity.bakingapp.data.pojo.RecipeInfo;

import java.util.List;

@Dao
public abstract class RecipeDao {

    @Transaction
    @Query("SELECT * FROM recipe")
    public abstract LiveData<List<Recipe>> findAllRecipes();

    @Transaction
    @Query("SELECT * FROM recipe WHERE id = :recipeId")
    public abstract LiveData<RecipeInfo> findRecipeById(int recipeId);

    @Query("SELECT * from recipe_step WHERE recipe_id = :recipeId")
    public abstract LiveData<List<RecipeStep>> findRecipeStepByRecipeId(int recipeId);

    @Query("DELETE FROM recipe")
    abstract void deleteAllRecipes();

    @Insert
    abstract void insertRecipe(Recipe recipe);

    @Insert
    abstract void insertRecipeIngredient(RecipeIngredient ingredient);

    @Insert
    abstract void insertRecipeStep(RecipeStep step);

    @Transaction
    public void refreshAllRecipes(List<RecipeInfo> recipeList) {
        deleteAllRecipes();
        insertAllRecipes(recipeList);
    }

    private void insertAllRecipes(List<RecipeInfo> recipeList) {
        if (recipeList != null && !recipeList.isEmpty()) {
            for (RecipeInfo recipe : recipeList) {
                insertRecipe(recipe.getRecipe());

                int recipeId = recipe.getRecipe().getId();

                if (recipe.getIngredientList() != null) {
                    for (RecipeIngredient ingredient : recipe.getIngredientList()) {
                        ingredient.setRecipeId(recipeId);
                        insertRecipeIngredient(ingredient);
                    }
                }

                if (recipe.getStepList() != null) {
                    for (RecipeStep step : recipe.getStepList()) {
                        step.setRecipeId(recipeId);
                        insertRecipeStep(step);
                    }
                }
            }
        }
    }

}
