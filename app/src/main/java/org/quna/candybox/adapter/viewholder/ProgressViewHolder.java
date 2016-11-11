package org.quna.candybox.adapter.viewholder;

import android.view.View;
import android.widget.ProgressBar;

import org.quna.candybox.R;

/**
 * Created by graphene on 2016-11-06.
 */

public class ProgressViewHolder extends AbstractViewHolder {
    public static final int VIEW_PROGRESS = 1;
    public ProgressBar mProgress;

    public ProgressViewHolder(View v) {
        super(v);
        mProgress = (ProgressBar) v.findViewById(R.id.progress);
    }

    @Override
    public void loadWith(Object data) {
        mProgress.setIndeterminate(true);
    }
}
