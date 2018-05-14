package org.peterkwan.udacity.bakingapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.VisibleForTesting;

import org.peterkwan.udacity.bakingapp.architecture.INetworkFailureCallback;
import org.peterkwan.udacity.bakingapp.architecture.Resource;
import org.peterkwan.udacity.bakingapp.data.entity.Recipe;
import org.peterkwan.udacity.bakingapp.repository.RecipeRepository;

import java.util.List;

public class RecipeListViewModel extends BaseRecipeViewModel {

    private MediatorLiveData<Resource<List<Recipe>>> recipeListCache;

    public RecipeListViewModel(final Application app) {
        super(app);
    }

    @VisibleForTesting
    public RecipeListViewModel(final Application app, RecipeRepository repository) {
        super(app);
        this.repository = repository;
    }

    public LiveData<Resource<List<Recipe>>> retrieveRecipeData(final INetworkFailureCallback callback) {
        if (recipeListCache == null)
            recipeListCache = repository.retrieveRecipeList(callback);

        return recipeListCache;
    }

}
