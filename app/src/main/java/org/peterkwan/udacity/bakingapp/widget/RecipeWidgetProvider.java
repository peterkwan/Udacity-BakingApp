package org.peterkwan.udacity.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import org.peterkwan.udacity.bakingapp.R;
import org.peterkwan.udacity.bakingapp.data.entity.RecipeIngredient;
import org.peterkwan.udacity.bakingapp.ui.RecipeDetailActivity;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class RecipeWidgetProvider extends AppWidgetProvider {

    private static final String RECIPE_ID = "recipeId";
    private static final String RECIPE_NAME = "recipeName";
    private static final String INGREDIENTS = "ingredients";
    private static final String PREFS_NAME = "org.peterkwan.udacity.bakingapp.ui.RecipeAppWidget";
    private static final String PREF_KEY = "appwidget_recipe";
    private static final int DEFAULT_RECIPE_ID = 1;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int recipeId = sharedPreferences.getInt(PREF_KEY, DEFAULT_RECIPE_ID);
        RecipeWidgetIntentService.startActionUpdateWidgets(context, recipeId);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction()) && intent.getExtras() != null) {
            int recipeId = intent.getIntExtra(RECIPE_ID, DEFAULT_RECIPE_ID);
            String recipeName = intent.getStringExtra(RECIPE_NAME);
            List<RecipeIngredient> ingredientList = intent.getParcelableArrayListExtra(INGREDIENTS);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, RecipeWidgetProvider.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.ingredientTextView);

            updateWidgets(context, appWidgetManager, appWidgetIds, recipeId, recipeName, ingredientList);
        }
    }

    public static void updateWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, int recipeId, String recipeName, List<RecipeIngredient> ingredientList) {
        for (int widgetId : appWidgetIds)
            updateWidget(context, appWidgetManager, widgetId, recipeId, recipeName, ingredientList);
    }

    private static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, int recipeId, String recipeName, List<RecipeIngredient> ingredientList) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_ingredients);

        if (ingredientList != null)
            views.setTextViewText(R.id.ingredientTextView, formatIngredientString(context, ingredientList));

        views.setTextViewText(R.id.ingredientLabelView, context.getString(R.string.ingredients) + String.format(" (%s)", recipeName));

        Intent intent = new Intent(context, RecipeDetailActivity.class);
        intent.putExtra(RECIPE_ID, recipeId);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.ingredientTextView, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    private static String formatIngredientString(Context context, List<RecipeIngredient> ingredientList) {
        String ingredientFormatString = context.getString(R.string.ingredient_format);
        StringBuilder stringBuilder = new StringBuilder();
        for (RecipeIngredient ingredient : ingredientList) {
            stringBuilder.append(String.format(ingredientFormatString, ingredient.getQuantity(), ingredient.getMeasure().toLowerCase(), ingredient.getName())).append("\n");
        }

        if (stringBuilder.length() > 1)
            return stringBuilder.substring(0, stringBuilder.length() - 1);
        else
            return "";
    }

}
