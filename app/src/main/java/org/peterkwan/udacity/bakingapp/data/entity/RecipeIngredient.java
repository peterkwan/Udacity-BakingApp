package org.peterkwan.udacity.bakingapp.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "recipe_ingredient")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RecipeIngredient implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "recipe_id")
    @ForeignKey(parentColumns = "id", childColumns = "recipe_id", entity = Recipe.class, deferred = true, onDelete = ForeignKey.CASCADE)
    private int recipeId;

    @NonNull
    @SerializedName("ingredient")
    @Expose
    private String name;

    @SerializedName("quantity")
    @Expose
    private String quantity;

    @SerializedName("measure")
    @Expose
    private String measure;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(recipeId);
        dest.writeString(name);
        dest.writeString(quantity);
        dest.writeString(measure);
    }

    public static final Parcelable.Creator<RecipeIngredient> CREATOR = new Parcelable.Creator<RecipeIngredient>() {

        @Override
        public RecipeIngredient[] newArray(int size) {
            return new RecipeIngredient[size];
        }

        @Override
        public RecipeIngredient createFromParcel(Parcel source) {
            return new RecipeIngredient(source);
        }
    };

    private RecipeIngredient(Parcel parcel) {
        id = parcel.readInt();
        recipeId = parcel.readInt();
        name = parcel.readString();
        quantity = parcel.readString();
        measure = parcel.readString();
    }
}
