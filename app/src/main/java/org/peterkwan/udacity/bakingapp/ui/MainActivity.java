package org.peterkwan.udacity.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.peterkwan.udacity.bakingapp.R;

public class MainActivity extends AppCompatActivity implements RecipeListFragment.OnListItemClickListener {

    private static final String RECIPE_ID = "recipeId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onListItemClick(int recipeId) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RECIPE_ID, recipeId);
        startActivity(intent);
    }
}
