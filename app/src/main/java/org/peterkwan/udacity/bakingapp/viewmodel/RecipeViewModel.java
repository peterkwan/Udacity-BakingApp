package org.peterkwan.udacity.bakingapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.annotation.VisibleForTesting;

import org.peterkwan.udacity.bakingapp.data.pojo.RecipeInfo;
import org.peterkwan.udacity.bakingapp.repository.RecipeRepository;

public class RecipeViewModel extends BaseRecipeViewModel {

    private LiveData<RecipeInfo> recipeLiveData;

    public RecipeViewModel(final Application app) {
        super(app);
    }

    @VisibleForTesting
    public RecipeViewModel(final Application app, final RecipeRepository repository) {
        super(app);
        this.repository = repository;
    }

    public LiveData<RecipeInfo> retrieveRecipe(int recipeId) {
        if (recipeLiveData == null)
            recipeLiveData = repository.retrieveRecipe(recipeId);

        return recipeLiveData;
    }
}
