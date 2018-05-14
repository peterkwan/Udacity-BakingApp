package org.peterkwan.udacity.bakingapp.utils;

import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

import org.peterkwan.udacity.bakingapp.BakingApp;

public class BakingAppTestRunner extends AndroidJUnitRunner {

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, BakingApp.class.getName(), context);
    }
}
