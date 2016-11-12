package org.quna.candybox.adapter.viewholder;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import org.quna.candybox.R;
import org.quna.candybox.data.Comment;
import org.quna.candybox.typeface.TypefaceCache;
import org.quna.candybox.typeface.TypefaceEnum;

/**
 * Created by graphene on 2016-11-06.
 */
public class CommentViewHolder extends AbstractViewHolder<Comment> {
    public static final int VIEW_COMMENT = 2;
    public CardView mCardView;
    public TextView mAuthorText, mContentText, mDateTimeText;

    public CommentViewHolder(View v) {
        super(v);
        mCardView = (CardView) v.findViewById(R.id.comment_card);

        mAuthorText = (TextView) v.findViewById(R.id.author_text);
        mContentText = (TextView) v.findViewById(R.id.comment_text);
        mDateTimeText = (TextView) v.findViewById(R.id.date_time_text);

        mAuthorText.setTypeface(TypefaceCache.get(v.getContext(), TypefaceEnum.REGULAR));
        mContentText.setTypeface(TypefaceCache.get(v.getContext(), TypefaceEnum.BOOK));
        mDateTimeText.setTypeface(TypefaceCache.get(v.getContext(), TypefaceEnum.BOOK));
    }

    @Override
    public void loadWith(Comment data) {
        mAuthorText.setText(data.getAuthor());

        if (data.isBciUser()) {
            mAuthorText.setTextColor(Color.parseColor("#AB7F20"));
        } else {
            mAuthorText.setTextColor(Color.parseColor("#222233"));
        }

        if (data.isCreator()) {
            mCardView.setCardBackgroundColor(Color.parseColor("#D9D5E6"));
        } else {
            mCardView.setCardBackgroundColor(Color.parseColor("#EFF3F7"));
        }

        Spanned spannedHtml;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spannedHtml = Html.fromHtml(data.getContent(), Html.FROM_HTML_MODE_LEGACY);
        } else {
            spannedHtml = Html.fromHtml(data.getContent());
        }
        mContentText.setText(spannedHtml, TextView.BufferType.SPANNABLE);

        mDateTimeText.setText(data.getDateTime());
    }
}
