package org.peterkwan.udacity.bakingapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import org.peterkwan.udacity.bakingapp.BakingApp;
import org.peterkwan.udacity.bakingapp.repository.RecipeRepository;

abstract class BaseRecipeViewModel extends AndroidViewModel {

    RecipeRepository repository;

    BaseRecipeViewModel(final Application app) {
        super(app);
        repository = RecipeRepository.getInstance((BakingApp) app);
    }
}
