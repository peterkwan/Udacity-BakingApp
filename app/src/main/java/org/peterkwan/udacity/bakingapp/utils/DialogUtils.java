package org.peterkwan.udacity.bakingapp.utils;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AlertDialog;

import org.peterkwan.udacity.bakingapp.R;

public final class DialogUtils {

    public static void showMessageDialog(Context context, int titleId, String message, int iconId) {
        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        }
        else {
            builder = new AlertDialog.Builder(context);
        }

        builder.setTitle(titleId)
                .setMessage(message)
                .setIcon(iconId)
                .setNeutralButton(R.string.close, null)
                .show();
    }
}
