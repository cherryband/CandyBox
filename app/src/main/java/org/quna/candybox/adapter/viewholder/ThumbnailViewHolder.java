package org.quna.candybox.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;

import org.quna.candybox.R;
import org.quna.candybox.activity.ImageViewerActivity;
import org.quna.candybox.data.Thumbnail;

/**
 * Created by graphene on 2016-11-06.
 */

public class ThumbnailViewHolder extends BaseViewHolder<Thumbnail> {
    public static final int ID = 0;
    public RoundedImageView mImageView;

    public ThumbnailViewHolder(View v) {
        super(v);
        mImageView = (RoundedImageView) v.findViewById(R.id.thumbnail);
    }

    public static ThumbnailViewHolder newInstance(ViewGroup parent) {
        View thumbnailView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.thumbnail_card, parent, false);
        return new ThumbnailViewHolder(thumbnailView);
    }

    @Override
    public void loadWith(final Thumbnail thumbnail) {
        String imgLink = thumbnail.getImageLink();
        Context context = mImageView.getContext();

        SizePredeterminedImageLoader.initViewWithFixedSize(thumbnail, mImageView);

        Glide
                .with(context)
                .load(imgLink)
                .placeholder(Color.GRAY)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mImageView);

        if (thumbnail.getAlt().contains("excellent ")) {
            mImageView.setBorderColor(Color.parseColor("#FFD700"));
        }

        mImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Open ImageViewerActivity
                Context context = view.getContext();
                Intent intent = new Intent(context, ImageViewerActivity.class);
                intent.putExtra(Thumbnail.THUMBNAIL, thumbnail);
                context.startActivity(intent);
            }
        });
    }
}
