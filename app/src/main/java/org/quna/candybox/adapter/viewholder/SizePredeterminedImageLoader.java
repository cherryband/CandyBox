package org.quna.candybox.adapter.viewholder;

import android.support.percent.PercentFrameLayout;
import android.view.View;

import org.quna.candybox.data.SizePredeterminedImage;

/**
 * Created by graphene on 2016-11-20.
 */

public class SizePredeterminedImageLoader {

    public static void initViewWithFixedSize(SizePredeterminedImage image,
                                             View v) {
        v.layout(0, 0, 0, 0);

        PercentFrameLayout.LayoutParams layoutParams =
                (PercentFrameLayout.LayoutParams) v.getLayoutParams();
        layoutParams.getPercentLayoutInfo().aspectRatio = getAspectRatio(image.getDimension());
        layoutParams.height = 0;
        v.setLayoutParams(layoutParams);
    }

    public static float getAspectRatio(int[] dimension) {
        float width = (float) dimension[0];
        float height = (float) dimension[1];
        return width / height;
    }

}
