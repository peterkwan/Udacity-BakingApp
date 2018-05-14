package org.peterkwan.udacity.bakingapp.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.peterkwan.udacity.bakingapp.R;
import org.peterkwan.udacity.bakingapp.data.entity.RecipeStep;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeStepListAdapter extends RecyclerView.Adapter<RecipeStepListAdapter.RecipeStepViewHolder> {

    private final OnItemClickListener listener;
    private List<RecipeStep> recipeStepList;

    @BindString(R.string.recipe_step_intro_format)
    String stepIntroFormat;

    public RecipeStepListAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_step_item, parent, false);

        ButterKnife.bind(this, rootView);

        return new RecipeStepViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeStepViewHolder holder, int position) {
        RecipeStep step = recipeStepList.get(position);
        holder.itemTextView.setText(String.format(stepIntroFormat, step.getId(), step.getShortDescription()));
    }

    @Override
    public int getItemCount() {
        if (recipeStepList == null || recipeStepList.isEmpty())
            return 0;

        return recipeStepList.size();
    }

    public void setRecipeStepList(List<RecipeStep> stepList) {
        this.recipeStepList = stepList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClicked(int stepId);
    }

    public class RecipeStepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.stepItemTextView)
        TextView itemTextView;

        RecipeStepViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                RecipeStep step = recipeStepList.get(position);
                listener.onItemClicked(step.getId());
            }
        }
    }
}
