package org.peterkwan.udacity.bakingapp.repository;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.peterkwan.udacity.bakingapp.AppExecutors;
import org.peterkwan.udacity.bakingapp.BakingApp;
import org.peterkwan.udacity.bakingapp.architecture.IApiResponse;
import org.peterkwan.udacity.bakingapp.architecture.INetworkFailureCallback;
import org.peterkwan.udacity.bakingapp.architecture.NetworkBoundResource;
import org.peterkwan.udacity.bakingapp.architecture.Resource;
import org.peterkwan.udacity.bakingapp.data.dao.RecipeDao;
import org.peterkwan.udacity.bakingapp.data.entity.Recipe;
import org.peterkwan.udacity.bakingapp.data.entity.RecipeStep;
import org.peterkwan.udacity.bakingapp.data.pojo.RecipeInfo;
import org.peterkwan.udacity.bakingapp.net.IRecipeService;
import org.peterkwan.udacity.bakingapp.net.RecipeServiceLocator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeRepository {

    private static final String LOG_TAG = RecipeRepository.class.getSimpleName();

    private static RecipeRepository repository;

    private final AppExecutors executors;
    private final RecipeDao recipeDao;
    private final IRecipeService webService;
    private INetworkFailureCallback callback;

    private RecipeRepository(final BakingApp app) {
        webService = RecipeServiceLocator.getService(app);
        recipeDao = app.getDatabase().recipeDao();
        executors = app.getExecutors();
    }

    public synchronized static RecipeRepository getInstance(final BakingApp app) {
        if (repository == null) {
            synchronized (RecipeRepository.class) {
                if (repository == null)
                    repository = new RecipeRepository(app);
            }
        }

        return repository;
    }

    public MediatorLiveData<Resource<List<Recipe>>> retrieveRecipeList(final INetworkFailureCallback callback) {
        this.callback = callback;

        return new NetworkBoundResource<List<Recipe>, List<RecipeInfo>>() {

            @Override
            protected void saveCallResult(@NonNull List<RecipeInfo> item) {
                recipeDao.refreshAllRecipes(item);
            }

            @Override
            protected boolean shouldFetchFromNetwork(@Nullable List<Recipe> data) {
                return data == null || data.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<List<Recipe>> loadFromDatabase() {
                return recipeDao.findAllRecipes();
            }

            @NonNull
            @Override
            protected LiveData<IApiResponse<List<RecipeInfo>>> createNetworkCall() {
                final MediatorLiveData<IApiResponse<List<RecipeInfo>>> result = new MediatorLiveData<>();

                executors.getNetworkIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        MediatorLiveData<IApiResponse<List<Recipe>>> netResult = retrieveDataFromNetwork();
                        LiveData<IApiResponse<List<RecipeInfo>>> netResult2 = Transformations.map(netResult, transformResponse());

                        result.addSource(netResult2, new Observer<IApiResponse<List<RecipeInfo>>>() {
                            @Override
                            public void onChanged(@Nullable IApiResponse<List<RecipeInfo>> listIApiResponse) {
                                result.postValue(listIApiResponse);
                            }
                        });
                    }
                });

                return result;
            }

            @Override
            protected void onFetchFailed() {
                Log.e(LOG_TAG, "Fetch data failed");
            }

        }.getAsLiveData();
    }

    public LiveData<RecipeInfo> retrieveRecipe(int recipeId) {
        return recipeDao.findRecipeById(recipeId);
    }

    public LiveData<List<RecipeStep>> retrieveRecipeSteps(int recipeId) {
        return recipeDao.findRecipeStepByRecipeId(recipeId);
    }

    private MediatorLiveData<IApiResponse<List<Recipe>>> retrieveDataFromNetwork() {
        final MediatorLiveData<IApiResponse<List<Recipe>>> result = new MediatorLiveData<>();

        webService.retrieveRecipes().enqueue(new Callback<List<Recipe>>() {

            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull final Response<List<Recipe>> response) {

                IApiResponse<List<Recipe>> apiResponse = new IApiResponse<List<Recipe>>() {
                    @Override
                    public List<Recipe> getBody() {
                        return response.body();
                    }

                    @Override
                    public String getErrorMessage() {
                        return response.message();
                    }

                    @Override
                    public boolean isSuccessful() {
                        return response.isSuccessful();
                    }
                };

                result.postValue(apiResponse);
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull final Throwable t)  {
                Log.e(LOG_TAG, "Failed to retrieve data from network", t);
                if (callback != null)
                    callback.onNetworkFailure();
            }
        });

        return result;
    }

    private Function<IApiResponse<List<Recipe>>, IApiResponse<List<RecipeInfo>>> transformResponse() {
        return new Function<IApiResponse<List<Recipe>>, IApiResponse<List<RecipeInfo>>>() {
            @Override
            public IApiResponse<List<RecipeInfo>> apply(final IApiResponse<List<Recipe>> input) {

                return new IApiResponse<List<RecipeInfo>>() {
                    @Override
                    public List<RecipeInfo> getBody() {
                        List<Recipe> recipeList = input.getBody();
                        List<RecipeInfo> outputList = new ArrayList<>();

                        if (recipeList != null) {
                            for (Recipe recipe : recipeList) {
                                RecipeInfo recipeInfo = new RecipeInfo();
                                recipeInfo.setRecipe(recipe);
                                recipeInfo.setIngredientList(recipe.getIngredientList());
                                recipeInfo.setStepList(recipe.getStepList());

                                outputList.add(recipeInfo);
                            }
                        }

                        return outputList;
                    }

                    @Override
                    public String getErrorMessage() {
                        return input.getErrorMessage();
                    }

                    @Override
                    public boolean isSuccessful() {
                        return input.isSuccessful();
                    }
                };
            }
        };
    }

}
