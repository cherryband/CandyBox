package org.quna.candybox.adapter.viewholder;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import org.quna.candybox.R;
import org.quna.candybox.data.Comment;

/**
 * Created by graphene on 2016-11-06.
 */
public class CommentViewHolder extends AbstractViewHolder<Comment> {
    public static final int VIEW_COMMENT = 2;
    public CardView mCardView;
    public ImageView mImageView;

    public CommentViewHolder(View v) {
        super(v);
        mCardView = (CardView) v.findViewById(R.id.card);
        mImageView = (ImageView) v.findViewById(R.id.thumbnail);
    }

    @Override
    public void loadWith(Comment data) {

    }
}
