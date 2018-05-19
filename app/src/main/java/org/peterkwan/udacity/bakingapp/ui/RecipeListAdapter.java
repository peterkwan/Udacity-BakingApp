package org.peterkwan.udacity.bakingapp.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.peterkwan.udacity.bakingapp.R;
import org.peterkwan.udacity.bakingapp.data.entity.Recipe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder> {

    private final OnItemClickListener clickListener;
    private List<Recipe> recipeList;

    private final int imageWidth;

    public RecipeListAdapter(OnItemClickListener clickListener, int imageWidth) {
        this.clickListener = clickListener;
        this.imageWidth = imageWidth;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final @NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        String imagePath = recipe.getImagePath();

        if (TextUtils.isEmpty(imagePath)) {
            Picasso.get()
                    .load(R.drawable.cupcake)
                    .into(holder.imageView);
        }
        else {
            Picasso.get()
                    .load(recipe.getImagePath())
                    .placeholder(R.drawable.cupcake)
                    .error(R.drawable.cupcake)
                    .into(holder.imageView);
        }

        String name = recipe.getName();

        holder.imageView.setContentDescription(name);
        holder.footerTextView.setText(name);

        holder.imageView.getLayoutParams().width = imageWidth;
        holder.footerTextView.getLayoutParams().width = imageWidth;
    }

    @Override
    public int getItemCount() {
        if (recipeList == null || recipeList.isEmpty())
            return 0;

        return recipeList.size();
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int recipeId);
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.recipeImageView)
        ImageView imageView;

        @BindView(R.id.recipeItemFooterTextView)
        TextView footerTextView;

        RecipeViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                Recipe recipe = recipeList.get(getAdapterPosition());
                clickListener.onItemClick(recipe.getId());
            }
        }
    }

}
