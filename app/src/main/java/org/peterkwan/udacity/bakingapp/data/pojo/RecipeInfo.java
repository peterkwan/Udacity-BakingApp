package org.peterkwan.udacity.bakingapp.data.pojo;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import org.peterkwan.udacity.bakingapp.data.entity.Recipe;
import org.peterkwan.udacity.bakingapp.data.entity.RecipeIngredient;
import org.peterkwan.udacity.bakingapp.data.entity.RecipeStep;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RecipeInfo {

    @Embedded
    private Recipe recipe;

    @Relation(parentColumn = "id", entityColumn = "recipe_id", entity = RecipeIngredient.class)
    private List<RecipeIngredient> ingredientList;

    @Relation(parentColumn = "id", entityColumn = "recipe_id", entity = RecipeStep.class)
    private List<RecipeStep> stepList;
}
