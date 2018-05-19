package org.peterkwan.udacity.bakingapp.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import butterknife.Unbinder;
import lombok.NoArgsConstructor;

import static org.peterkwan.udacity.bakingapp.architecture.Resource.Status.ERROR;
import static org.peterkwan.udacity.bakingapp.architecture.Resource.Status.SUCCESS;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnListItemClickListener} interface
 * to handle interaction events.
 */
@NoArgsConstructor
public class RecipeListFragment extends Fragment implements RecipeListAdapter.OnItemClickListener, INetworkFailureCallback {

    private RecipeListAdapter recipeListAdapter;
    private Context context;

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

    private OnListItemClickListener mListener;
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        unbinder = ButterKnife.bind(this, rootView);
        context = getContext();

        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        int imageWidth = (int) (metrics.widthPixels - gridItemSpacing * 2 - gridHorizontalPadding * 2) / gridItemCount;

        recipeListAdapter = new RecipeListAdapter(this, imageWidth);
        recipeListView.setAdapter(recipeListAdapter);
        recipeListView.setHasFixedSize(true);
        recipeListView.setLayoutManager(new GridLayoutManager(context, gridItemCount));

        RecipeListViewModel viewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);
        viewModel.retrieveRecipeData(this).observe(this, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
                if (listResource != null) {
                    Resource.Status status = listResource.getStatus();

                    if (SUCCESS.equals(status)) {
                        List<Recipe> recipeList = listResource.getData();
                        recipeListAdapter.setRecipeList(recipeList);
                    }
                    else if (ERROR.equals(status))
                        showErrorDialog();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListItemClickListener) {
            mListener = (OnListItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListItemClickListener");
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListItemClickListener {
        void onListItemClick(int recipeId);
    }

    @Override
    public void onItemClick(int recipeId) {
        if (mListener != null)
            mListener.onListItemClick(recipeId);
    }

    @Override
    public void onNetworkFailure() {
        showErrorDialog();
    }

    private void showErrorDialog(){
        DialogUtils.showMessageDialog(context, R.string.load_data_error_title, loadDataErrorMsg, android.R.drawable.ic_dialog_alert);
    }

}
