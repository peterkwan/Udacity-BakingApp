package org.peterkwan.udacity.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.peterkwan.udacity.bakingapp.R;

import butterknife.BindBool;
import butterknife.BindString;
import butterknife.ButterKnife;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class RecipeStepActivity extends AppCompatActivity implements OnRecipeStepItemClickListener {

    private static final String RECIPE_ID = "recipeId";
    private static final String STEP_ID = "stepId";
    private static final String RECIPE_NAME = "recipeName";

    private static final int DEFAULT_RECIPE_ID = 1;
    private static final int DEFAULT_STEP_ID = 0;

    private int recipeId = DEFAULT_RECIPE_ID;
    private int stepId = DEFAULT_STEP_ID;
    private String recipeName;

    @BindBool(R.bool.two_pane_layout)
    boolean isTwoPaneLayout;

    @BindString(R.string.making_of_recipe)
    String makingOfTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            recipeId = intent.getIntExtra(RECIPE_ID, DEFAULT_RECIPE_ID);
            stepId = intent.getIntExtra(STEP_ID, DEFAULT_STEP_ID);
            recipeName = intent.getStringExtra(RECIPE_NAME);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(STEP_ID))
            stepId = savedInstanceState.getInt(STEP_ID);

        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        if (isTwoPaneLayout) {
            finish();
            return;
        }

        if (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null)
                actionBar.hide();
        }

        setContentView(R.layout.activity_recipe_step);
        setTitle(String.format(makingOfTitle, recipeName));

        if (savedInstanceState == null)
            initStepFragment();
    }

    @Override
    public void onStepItemClicked(int stepId, String recipeName) {
        this.stepId = stepId;
        this.recipeName = recipeName;
        initStepFragment();
    }

    private void initStepFragment() {
        Bundle args = new Bundle();
        args.putInt(RECIPE_ID, recipeId);
        args.putInt(STEP_ID, stepId);
        args.putString(RECIPE_NAME, recipeName);

        RecipeStepFragment fragment = new RecipeStepFragment();
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.stepFragmentContainer, fragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STEP_ID, stepId);
        super.onSaveInstanceState(outState);
    }
}
