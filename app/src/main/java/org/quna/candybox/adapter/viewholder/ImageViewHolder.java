package org.quna.candybox.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.quna.candybox.R;
import org.quna.candybox.activity.ImageViewerActivity;
import org.quna.candybox.data.Image;

/**
 * Created by graphene on 2016-11-06.
 */

public class ImageViewHolder extends AbstractViewHolder<Image> {
    public static final int VIEW_IMAGE = 0;
    public CardView mCardView;
    public ImageView mImageView;

    public ImageViewHolder(View v) {
        super(v);
        mCardView = (CardView) v.findViewById(R.id.thumbnail_card);
        mImageView = (ImageView) v.findViewById(R.id.thumbnail);
    }

    @Override
    public void loadWith(final Image image) {
        mImageView.setContentDescription(image.getAlt());
        String imgLink = image.getSource();
        Context context = mImageView.getContext();

        Glide
                .with(context)
                .load(imgLink)
                .crossFade()
                .fitCenter()
                .thumbnail(.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mImageView);

        mCardView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Open ImageViewerActivity
                Context context = view.getContext();
                Intent intent = new Intent(context, ImageViewerActivity.class);
                intent.putExtra(Image.IMAGE, image);
                context.startActivity(intent);
            }
        });
    }
}
