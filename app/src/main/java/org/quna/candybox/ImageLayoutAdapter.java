package org.quna.candybox;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ImageLayoutAdapter extends RecyclerView.Adapter<ImageLayoutAdapter.ViewHolder> {
    private Image[] mDataset;

    public ImageLayoutAdapter(Image[] mDataset) {
        this.mDataset = mDataset;
    }

    @Override
    public ImageLayoutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_card, parent,
                false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Image image = mDataset[position];
        ImageView mImageView = holder.mImageView;
        mImageView.setContentDescription(image.getAlt());
        String imgLink = image.getSource();

        if (imgLink.contains(".gif")) {
            Glide
                    .with(mImageView.getContext())
                    .load(imgLink)
                    .asGif()
                    .crossFade()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mImageView);
        } else {
            Glide
                    .with(mImageView.getContext())
                    .load(imgLink)
                    .crossFade()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mImageView);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public ViewHolder(View v) {
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.thumbnail);
        }
    }
}
