package org.peterkwan.udacity.bakingapp.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "recipe_step", primaryKeys = {"id", "recipe_id"})
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RecipeStep {

    @SerializedName("id")
    @Expose
    private int id;

    @ColumnInfo(name = "recipe_id")
    @ForeignKey(parentColumns = "id", childColumns = "recipe_id", entity = Recipe.class, deferred = true, onDelete = ForeignKey.CASCADE)
    private int recipeId;

    @NonNull
    @ColumnInfo(name = "short_description")
    @SerializedName("shortDescription")
    @Expose
    private String shortDescription;

    @SerializedName("description")
    @Expose
    private String description;

    @ColumnInfo(name = "video_url")
    @SerializedName("videoURL")
    @Expose
    private String videoUrl;

    @ColumnInfo(name = "thumbnail_url")
    @SerializedName("thumbnailURL")
    @Expose
    private String thumbnailUrl;
}
