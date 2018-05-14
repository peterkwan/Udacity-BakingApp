package org.peterkwan.udacity.bakingapp.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import org.peterkwan.udacity.bakingapp.R;
import org.peterkwan.udacity.bakingapp.architecture.INetworkFailureCallback;
import org.peterkwan.udacity.bakingapp.architecture.Resource;
import org.peterkwan.udacity.bakingapp.data.entity.Recipe;
import org.peterkwan.udacity.bakingapp.utils.DialogUtils;
import org.peterkwan.udacity.bakingapp.viewmodel.RecipeListViewModel;

import java.util.List;

import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static org.peterkwan.udacity.bakingapp.architecture.Resource.Status;
import static org.peterkwan.udacity.bakingapp.architecture.Resource.Status.ERROR;
import static org.peterkwan.udacity.bakingapp.architecture.Resource.Status.SUCCESS;

public class MainActivity extends AppCompatActivity implements RecipeListAdapter.OnItemClickListener, INetworkFailureCallback {

    private static final String RECIPE_ID = "recipeId";

    private RecipeListAdapter recipeListAdapter;

    @BindView(R.id.recipeListView)
    RecyclerView recipeListView;

    @BindInt(R.integer.grid_item_count)
    int gridItemCount;

    @BindDimen(R.dimen.grid_view_horizontal_padding)
    int gridHorizontalPadding;

    @BindDimen(R.dimen.grid_item_spacing)
    float gridItemSpacing;

    @BindString(R.string.load_data_error_message)
    String loadDataErrorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int imageWidth = (int) (metrics.widthPixels - gridItemSpacing * 2 - gridHorizontalPadding * 2) / gridItemCount;

        recipeListAdapter = new RecipeListAdapter(this, imageWidth);
        recipeListView.setAdapter(recipeListAdapter);
        recipeListView.setHasFixedSize(true);
        recipeListView.setLayoutManager(new GridLayoutManager(this, gridItemCount));

        RecipeListViewModel viewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);
        viewModel.retrieveRecipeData(this).observe(this, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
                if (listResource != null) {
                    Status status = listResource.getStatus();

                    if (SUCCESS.equals(status)) {
                        List<Recipe> recipeList = listResource.getData();
                        recipeListAdapter.setRecipeList(recipeList);
                    }
                    else if (ERROR.equals(status))
                        showErrorDialog();
                }
            }
        });
    }

    @Override
    public void onItemClick(int recipeId) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RECIPE_ID, recipeId);
        startActivity(intent);
    }

    @Override
    public void onNetworkFailure() {
        showErrorDialog();
    }

    private void showErrorDialog(){
        DialogUtils.showMessageDialog(this, R.string.load_data_error_title, loadDataErrorMsg, android.R.drawable.ic_dialog_alert);
    }
}
