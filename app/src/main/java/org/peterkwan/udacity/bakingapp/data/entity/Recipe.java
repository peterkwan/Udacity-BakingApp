package org.peterkwan.udacity.bakingapp.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "recipe")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Recipe {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private int id;

    @NonNull
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("servings")
    @Expose
    private int servings;

    @ColumnInfo(name = "image_path")
    @SerializedName("image")
    @Expose
    private String imagePath;

    @Ignore
    @SerializedName("ingredients")
    @Expose
    private List<RecipeIngredient> ingredientList;

    @Ignore
    @SerializedName("steps")
    @Expose
    private List<RecipeStep> stepList;
}
