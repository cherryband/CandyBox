package org.quna.candybox.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.util.LruCache;

/**
 * Created by graphene on 2016-10-31.
 */

public class TypeFaceCache {
    private static LruCache<String, Typeface> sTypefaceCache =
            new LruCache<String, Typeface>(12);

    private static Typeface get(Context context, String path) {
        Typeface mTypeface = sTypefaceCache.get(path);
        if (mTypeface == null)
            mTypeface = TypeFaceCache.put(context, path);
        return mTypeface;
    }

    public static Typeface get(Context context, TypefaceEnum typefaceEnum) {
        return TypeFaceCache.get(context, typefaceEnum.getPath());
    }

    private static Typeface put(Context context, String path) {
        Typeface mTypeface = Typeface.createFromAsset(context.getApplicationContext()
                .getAssets(), String.format("fonts/%s", path));

        return sTypefaceCache.put(path, mTypeface);
    }
}
