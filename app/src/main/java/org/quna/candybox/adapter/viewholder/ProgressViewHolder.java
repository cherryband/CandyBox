package org.quna.candybox.adapter.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.quna.candybox.R;
import org.quna.candybox.data.PlaceHolder;

/**
 * Created by graphene on 2016-11-06.
 */

public class ProgressViewHolder extends BaseViewHolder<PlaceHolder> {
    public static final int ID = 1;
    public ProgressBar mProgress;

    public ProgressViewHolder(View v) {
        super(v);
        mProgress = (ProgressBar) v.findViewById(R.id.progress);
    }

    public static ProgressViewHolder newInstance(ViewGroup parent) {
        View progressView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.progress_viewer, parent, false);
        return new ProgressViewHolder(progressView);
    }

    @Override
    public void loadWith(PlaceHolder data) {
        mProgress.setIndeterminate(true);
    }
}
