package org.quna.candybox.misc;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import org.quna.candybox.typeface.TypefaceCache;
import org.quna.candybox.typeface.TypefaceEnum;

/**
 * Created by graphene on 2016-11-12.
 */

public class CustomTypefaceSnackbar {
    public static Snackbar getSnackBar(Activity activity, int viewId, String text, String actionText, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar
                .make(activity.findViewById(viewId), text, Snackbar.LENGTH_INDEFINITE)
                .setAction(actionText, listener);

        //Setting custom font to both info and action text in snackbar.
        View sbView = snackbar.getView();

        TextView textView = (TextView) sbView.findViewById
                (android.support.design.R.id.snackbar_text);
        TextView actionView = (TextView) sbView.findViewById
                (android.support.design.R.id.snackbar_action);

        textView.setTypeface(TypefaceCache.get(activity, TypefaceEnum.BOOK));
        actionView.setTypeface(TypefaceCache.get(activity, TypefaceEnum.BOOK));
        return snackbar;
    }

    public static Snackbar getSnackBar(Activity activity, int viewId, String text) {
        Snackbar snackbar = Snackbar
                .make(activity.findViewById(viewId), text, Snackbar.LENGTH_INDEFINITE);

        //Setting custom font to both info and action text in snackbar.
        View sbView = snackbar.getView();

        TextView textView = (TextView) sbView.findViewById
                (android.support.design.R.id.snackbar_text);

        textView.setTypeface(TypefaceCache.get(activity, TypefaceEnum.BOOK));
        return snackbar;
    }
}
