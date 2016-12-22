package org.quna.candybox.typeface;

import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by graphene on 2016-11-12.
 */

public class CustomTypefaceSnackbar {
    public static void runSnackBarWithTypeface(Snackbar snackbar, Typeface typeface) {
        //Setting custom font to both info and action text in snackbar.
        View sbView = snackbar.getView();

        TextView textView = (TextView) sbView.findViewById
                (android.support.design.R.id.snackbar_text);
        TextView actionView = (TextView) sbView.findViewById
                (android.support.design.R.id.snackbar_action);

        textView.setTypeface(typeface);
        actionView.setTypeface(typeface);
        snackbar.show();
    }
}
