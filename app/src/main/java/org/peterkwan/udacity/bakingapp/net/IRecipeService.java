package org.peterkwan.udacity.bakingapp.net;

import org.peterkwan.udacity.bakingapp.data.entity.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

import static org.peterkwan.udacity.bakingapp.net.NetConstants.RECIPE_JSON_URL;

public interface IRecipeService {

    @GET(RECIPE_JSON_URL)
    Call<List<Recipe>> retrieveRecipes();
}
