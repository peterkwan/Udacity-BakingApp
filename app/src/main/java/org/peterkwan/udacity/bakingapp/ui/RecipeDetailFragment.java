package org.peterkwan.udacity.bakingapp.ui;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.peterkwan.udacity.bakingapp.R;
import org.peterkwan.udacity.bakingapp.data.entity.RecipeIngredient;
import org.peterkwan.udacity.bakingapp.data.entity.RecipeStep;
import org.peterkwan.udacity.bakingapp.data.pojo.RecipeInfo;
import org.peterkwan.udacity.bakingapp.viewmodel.RecipeViewModel;
import org.peterkwan.udacity.bakingapp.widget.RecipeWidgetProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnRecipeStepItemClickListener} interface
 * to handle interaction events.
 */
public class RecipeDetailFragment extends Fragment implements RecipeStepListAdapter.OnItemClickListener {

    private static final String RECIPE_ID = "recipeId";
    private static final String RECIPE_NAME = "recipeName";
    private static final String INGREDIENTS = "ingredients";
    private static final String PREFS_NAME = "org.peterkwan.udacity.bakingapp.ui.RecipeAppWidget";
    private static final String PREF_KEY = "appwidget_recipe";
    private static final int DEFAULT_RECIPE_ID = 1;

    private OnRecipeStepItemClickListener mListener;
    private Unbinder unbinder;
    private int recipeId = DEFAULT_RECIPE_ID;
    private RecipeStepListAdapter listAdapter;
    private String recipeName;

    @BindView(R.id.ingredientTextView)
    TextView ingredientTextView;

    @BindView(R.id.stepListView)
    RecyclerView stepListView;

    @BindString(R.string.ingredient_format)
    String ingredientFormatString;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null && args.containsKey(RECIPE_ID))
            recipeId = args.getInt(RECIPE_ID);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        listAdapter = new RecipeStepListAdapter(this);
        stepListView.setAdapter(listAdapter);
        stepListView.setHasFixedSize(true);
        stepListView.addItemDecoration(new DividerItemDecoration(stepListView.getContext(), DividerItemDecoration.VERTICAL));

        RecipeViewModel viewModel = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);
        viewModel.retrieveRecipe(recipeId).observe(this, new Observer<RecipeInfo>() {
            @Override
            public void onChanged(@Nullable RecipeInfo recipeInfo) {
                if (recipeInfo != null) {
                    List<RecipeStep> stepList = recipeInfo.getStepList();
                    listAdapter.setRecipeStepList(stepList);

                    Activity activity = getActivity();
                    Context context = getActivity();

                    recipeName = recipeInfo.getRecipe().getName();

                    if (activity != null)
                        activity.setTitle(recipeName);

                    List<RecipeIngredient> ingredientList = recipeInfo.getIngredientList();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (RecipeIngredient ingredient : ingredientList)
                        stringBuilder.append(String.format(ingredientFormatString, ingredient.getQuantity(), ingredient.getMeasure().toLowerCase(), ingredient.getName())).append("\n");

                    if (stringBuilder.length() > 1)
                        ingredientTextView.setText(stringBuilder.substring(0, stringBuilder.length() - 1));

                    saveSelectedRecipePref(context);
                    updateAppWidgets(context, ingredientList);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecipeStepItemClickListener) {
            mListener = (OnRecipeStepItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecipeStepItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClicked(int stepId) {
        if (mListener != null)
            mListener.onStepItemClicked(stepId, recipeName);
    }

    private void updateAppWidgets(Context context, List<RecipeIngredient> ingredientList) {
        Bundle bundle = new Bundle();
        bundle.putInt(RECIPE_ID, recipeId);
        bundle.putString(RECIPE_NAME, recipeName);
        bundle.putParcelableArrayList(INGREDIENTS, (ArrayList<RecipeIngredient>) ingredientList);

        Intent intent = new Intent(context, RecipeWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtras(bundle);
        context.sendBroadcast(intent);
    }

    private void saveSelectedRecipePref(Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        prefs.putInt(PREF_KEY,  recipeId);
        prefs.apply();
    }
}
