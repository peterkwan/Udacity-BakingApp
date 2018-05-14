package org.peterkwan.udacity.bakingapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.annotation.VisibleForTesting;

import org.peterkwan.udacity.bakingapp.data.entity.RecipeStep;
import org.peterkwan.udacity.bakingapp.repository.RecipeRepository;

import java.util.List;

public class RecipeStepViewModel extends BaseRecipeViewModel {

    private LiveData<List<RecipeStep>> recipeStepLiveData;

    public RecipeStepViewModel(final Application app) {
        super(app);
    }

    @VisibleForTesting
    public RecipeStepViewModel(final Application app, final RecipeRepository repository) {
        super(app);
        this.repository = repository;
    }

    public LiveData<List<RecipeStep>> retrieveRecipeSteps(int recipeId) {
        if (recipeStepLiveData == null)
            recipeStepLiveData = repository.retrieveRecipeSteps(recipeId);

        return recipeStepLiveData;
    }
}
