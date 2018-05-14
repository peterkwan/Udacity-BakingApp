package org.peterkwan.udacity.bakingapp.ui;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.peterkwan.udacity.bakingapp.R;
import org.peterkwan.udacity.bakingapp.architecture.INetworkFailureCallback;
import org.peterkwan.udacity.bakingapp.architecture.Resource;
import org.peterkwan.udacity.bakingapp.data.entity.Recipe;
import org.peterkwan.udacity.bakingapp.viewmodel.RecipeListViewModel;
import org.peterkwan.udacity.bakingapp.widget.RecipeWidgetIntentService;

import java.util.List;

import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

import static org.peterkwan.udacity.bakingapp.architecture.Resource.Status.ERROR;
import static org.peterkwan.udacity.bakingapp.architecture.Resource.Status.SUCCESS;

public class RecipeAppWidgetConfigureActivity extends AppCompatActivity implements RecipeListAdapter.OnItemClickListener, INetworkFailureCallback {

    private static final String PREFS_NAME = "org.peterkwan.udacity.bakingapp.ui.RecipeAppWidget";
    private static final String PREF_KEY = "appwidget_recipe";
    private static final int DEFAULT_RECIPE_ID = -1;

    private RecipeListAdapter recipeListAdapter;

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @BindView(R.id.widgetRecipeListView)
    RecyclerView recipeListView;

    @BindView(R.id.widgetErrorMessageTextView)
    TextView errorMessageTextView;

    @BindInt(R.integer.grid_item_count)
    int gridItemCount;

    @BindDimen(R.dimen.widget_grid_view_horizontal_padding)
    int gridHorizontalPadding;

    @BindDimen(R.dimen.widget_grid_item_spacing)
    float gridItemSpacing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(RESULT_CANCELED);

        setContentView(R.layout.activity_recipe_widget_configure);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        int recipeId = loadSelectedRecipePref(this, mAppWidgetId);
        if (recipeId != DEFAULT_RECIPE_ID) {
            updateAppWidget(recipeId);
            return;
        }

        getWindow().getDecorView().measure(0, 0);
        int imageWidth = (int) (getWindow().getDecorView().getMeasuredWidth() - gridItemSpacing * 2 - gridHorizontalPadding * 2) / gridItemCount;

        recipeListAdapter = new RecipeListAdapter(this, imageWidth);
        recipeListView.setAdapter(recipeListAdapter);
        recipeListView.setHasFixedSize(true);
        recipeListView.setLayoutManager(new GridLayoutManager(this, gridItemCount));

        RecipeListViewModel viewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);
        viewModel.retrieveRecipeData(this).observe(this, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
                if (listResource != null) {
                    Resource.Status status = listResource.getStatus();

                    if (SUCCESS.equals(status)) {
                        List<Recipe> recipeList = listResource.getData();
                        recipeListAdapter.setRecipeList(recipeList);
                        errorMessageTextView.setVisibility(View.GONE);
                    }
                    else if (ERROR.equals(status)) {
                        recipeListView.setVisibility(View.GONE);
                        errorMessageTextView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void onNetworkFailure() {
        recipeListView.setVisibility(View.GONE);
        errorMessageTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(int recipeId) {
        saveSelectedRecipePref(this, mAppWidgetId, recipeId);
        updateAppWidget(recipeId);
    }

    private static void saveSelectedRecipePref(Context context, int appWidgetId, int recipeId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        prefs.putInt(PREF_KEY,  recipeId);
        prefs.apply();
    }

    private static int loadSelectedRecipePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(PREF_KEY, DEFAULT_RECIPE_ID);
    }

    private void updateAppWidget(int recipeId) {
        RecipeWidgetIntentService.startActionUpdateWidgets(this, recipeId);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}
