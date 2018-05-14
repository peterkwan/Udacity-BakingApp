package org.peterkwan.udacity.bakingapp.utils;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class StethoUtils {

    public static void initStetho(final Application app) {
        Stetho.initializeWithDefaults(app);
    }
}
