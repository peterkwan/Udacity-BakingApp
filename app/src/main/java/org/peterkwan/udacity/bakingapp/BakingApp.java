package org.peterkwan.udacity.bakingapp;

import android.app.Application;

import org.peterkwan.udacity.bakingapp.data.RecipeDatabase;
import org.peterkwan.udacity.bakingapp.utils.StethoUtils;

import lombok.Getter;

public class BakingApp extends Application {

    @Getter
    private AppExecutors executors;

    @Override
    public void onCreate() {
        super.onCreate();

        executors = new AppExecutors();

        StethoUtils.initStetho(this);
    }

    public RecipeDatabase getDatabase() {
        return RecipeDatabase.getInstance(this, executors);
    }
}
