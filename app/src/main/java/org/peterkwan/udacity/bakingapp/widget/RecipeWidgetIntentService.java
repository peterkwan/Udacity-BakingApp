package org.peterkwan.udacity.bakingapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.peterkwan.udacity.bakingapp.BakingApp;
import org.peterkwan.udacity.bakingapp.R;
import org.peterkwan.udacity.bakingapp.data.entity.Recipe;
import org.peterkwan.udacity.bakingapp.data.entity.RecipeIngredient;
import org.peterkwan.udacity.bakingapp.data.pojo.RecipeInfo;
import org.peterkwan.udacity.bakingapp.repository.RecipeRepository;

import java.util.List;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class RecipeWidgetIntentService extends IntentService {

    private static final String SERVICE_NAME = RecipeWidgetIntentService.class.getSimpleName();
    private static final String ACTION_UPDATE_WIDGETS = "org.peterkwan.udacity.bakingapp.widget.actions.update_widget";

    private static final String RECIPE_ID = "recipeId";
    private static final int DEFAULT_RECIPE_ID = 1;

    private static RecipeRepository repository;

    public RecipeWidgetIntentService() {
        super(SERVICE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (repository == null)
            repository = RecipeRepository.getInstance((BakingApp) getApplication());
    }

    public static void startActionUpdateWidgets(Context context, int recipeId) {
        Intent intent = new Intent(context, RecipeWidgetIntentService.class);
        intent.setAction(ACTION_UPDATE_WIDGETS);
        intent.putExtra(RECIPE_ID, recipeId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_WIDGETS.equals(action) && intent.getExtras() != null) {
                int recipeId = intent.getIntExtra(RECIPE_ID, DEFAULT_RECIPE_ID);
                handleUpdateAppWidget(recipeId);
            }
        }
    }

    private void handleUpdateAppWidget(final int recipeId) {
        LiveData<RecipeInfo> recipeInfoLiveData = repository.retrieveRecipe(recipeId);
        if (recipeInfoLiveData != null) {
            recipeInfoLiveData.observeForever(new Observer<RecipeInfo>() {

                @Override
                public void onChanged(@Nullable RecipeInfo recipeInfo) {
                    if (recipeInfo != null) {
                        Recipe recipe = recipeInfo.getRecipe();
                        String recipeName = recipe.getName();
                        List<RecipeIngredient> ingredientList = recipeInfo.getIngredientList();

                        Context context = RecipeWidgetIntentService.this;
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, RecipeWidgetProvider.class));
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.ingredientTextView);

                        RecipeWidgetProvider.updateWidgets(context, appWidgetManager, appWidgetIds, recipeId, recipeName, ingredientList);
                    }
                }
            });

        }
    }
}
