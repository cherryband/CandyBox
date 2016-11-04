package org.quna.candybox.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.util.LruCache;

/**
 * Created by graphene on 2016-10-31.
 */

public class TypefaceCache {
    private static LruCache<String, Typeface> sTypefaceCache =
            new LruCache<String, Typeface>(12);

    public static Typeface get(Context context, String path) {
        Typeface mTypeface = sTypefaceCache.get(path);
        if (mTypeface == null)
            mTypeface = TypefaceCache.put(context, path);
        return mTypeface;
    }

    private static Typeface put(Context context, String path) {
        Typeface mTypeface = Typeface.createFromAsset(context.getApplicationContext()
                .getAssets(), String.format("fonts/%s", path));

        return sTypefaceCache.put(path, mTypeface);
    }
}
