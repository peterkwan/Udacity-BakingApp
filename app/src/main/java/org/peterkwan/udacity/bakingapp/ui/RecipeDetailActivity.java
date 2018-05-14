package org.peterkwan.udacity.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;

import org.peterkwan.udacity.bakingapp.R;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailActivity extends AppCompatActivity implements OnRecipeStepItemClickListener {

    private static final String RECIPE_ID = "recipeId";
    private static final int DEFAULT_RECIPE_ID = 1;

    private static final String STEP_ID = "stepId";
    private static final String RECIPE_NAME = "recipeName";

    private int recipeId = DEFAULT_RECIPE_ID;

    @BindView(R.id.detailFragmentContainer)
    View detailFragmentContainer;

    @BindView(R.id.stepFragmentContainer)
    View stepFragmentContainer;

    @BindBool(R.bool.two_pane_layout)
    boolean isTwoPaneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            recipeId = intent.getIntExtra(RECIPE_ID, DEFAULT_RECIPE_ID);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        ButterKnife.bind(this);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int activityWidth = metrics.widthPixels;

        initDetailFragment();

        if (isTwoPaneLayout) {
            detailFragmentContainer.getLayoutParams().width = activityWidth / 3;
            stepFragmentContainer.getLayoutParams().width = activityWidth * 2 / 3;
            stepFragmentContainer.setVisibility(View.VISIBLE);
        }
        else {
            detailFragmentContainer.getLayoutParams().width = activityWidth;
            stepFragmentContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStepItemClicked(int stepId, String recipeName) {
        Bundle args = new Bundle();
        args.putInt(RECIPE_ID, recipeId);
        args.putInt(STEP_ID, stepId);
        args.putString(RECIPE_NAME, recipeName);

        if (isTwoPaneLayout) {
            RecipeStepFragment fragment = new RecipeStepFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.stepFragmentContainer, fragment)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, RecipeStepActivity.class);
            intent.putExtras(args);
            startActivity(intent);
        }
    }

    private void initDetailFragment() {
        Bundle args = new Bundle();
        args.putInt(RECIPE_ID, recipeId);

        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detailFragmentContainer, fragment)
                .commit();
    }

}
