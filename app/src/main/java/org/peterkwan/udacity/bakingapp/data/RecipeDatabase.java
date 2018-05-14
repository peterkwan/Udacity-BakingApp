package org.peterkwan.udacity.bakingapp.data;

import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.peterkwan.udacity.bakingapp.AppExecutors;
import org.peterkwan.udacity.bakingapp.data.dao.RecipeDao;
import org.peterkwan.udacity.bakingapp.data.entity.Recipe;
import org.peterkwan.udacity.bakingapp.data.entity.RecipeIngredient;
import org.peterkwan.udacity.bakingapp.data.entity.RecipeStep;

@Database(entities = { Recipe.class, RecipeIngredient.class, RecipeStep.class}, version = 1, exportSchema = false)
public abstract class RecipeDatabase extends RoomDatabase {

    @VisibleForTesting
    public static final String DATABASE_NAME = "baking_recipe";

    private static RecipeDatabase db;

    private final MutableLiveData<Boolean> isDatabaseCreated = new MutableLiveData<>();

    public static RecipeDatabase getInstance(final Context context, final AppExecutors executors) {
        if (db == null) {
            synchronized (RecipeDatabase.class) {
                if (db == null) {
                    Context appContext = context.getApplicationContext();
                    db = createDatabase(appContext, executors);
                    db.updateDatabaseCreated(appContext);
                }
            }
        }

        return db;
    }

    public abstract RecipeDao recipeDao();

    private static RecipeDatabase createDatabase(final Context context, final AppExecutors executors) {
        return Room.databaseBuilder(context, RecipeDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);

                        executors.getDiskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                RecipeDatabase database = RecipeDatabase.getInstance(context, executors);
                                database.setDatabaseCreated();
                            }
                        });
                    }
                })
                .build();
    }

    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists())
            setDatabaseCreated();
    }

    private void setDatabaseCreated() {
        isDatabaseCreated.postValue(true);
    }
}
