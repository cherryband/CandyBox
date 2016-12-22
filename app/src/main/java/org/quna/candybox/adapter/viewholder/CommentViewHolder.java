package org.quna.candybox.adapter.viewholder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.quna.candybox.R;
import org.quna.candybox.data.Comment;
import org.quna.candybox.typeface.TypefaceCache;
import org.quna.candybox.typeface.TypefaceEnum;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static com.malinskiy.materialicons.Iconify.TAG;

/**
 * Created by graphene on 2016-11-06.
 */
public class CommentViewHolder extends BaseViewHolder<Comment> implements Html.ImageGetter {
    public static final int ID = 2;
    private static LruCache<String, Bitmap> stringBitmapLruCache =
            new LruCache<String, Bitmap>(4 * 1024 * 1024);
    public CardView mCardView;
    public TextView mAuthorText, mContentText, mDateTimeText;

    public CommentViewHolder(View v) {
        super(v);
        mCardView = (CardView) v.findViewById(R.id.comment_card);

        mAuthorText = (TextView) v.findViewById(R.id.creator_text);
        mContentText = (TextView) v.findViewById(R.id.comment_text);
        mDateTimeText = (TextView) v.findViewById(R.id.uploaded_date_time_text);

        Typeface regular = TypefaceCache.get(v.getContext(), TypefaceEnum.REGULAR);
        Typeface book = TypefaceCache.get(v.getContext(), TypefaceEnum.BOOK);

        mAuthorText.setTypeface(regular);
        mContentText.setTypeface(book);
        mDateTimeText.setTypeface(book);
    }

    public static CommentViewHolder newInstance(ViewGroup parent) {
        View commentView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_card, parent, false);
        return new CommentViewHolder(commentView);
    }

    @Override
    public void loadWith(Comment data) {
        mAuthorText.setText(data.getAuthor());

        if (data.isBciUser()) {
            mAuthorText.setTextColor(Color.parseColor("#AB7F20"));
        } else {
            mAuthorText.setTextColor(Color.parseColor("#222233"));
        }

        if (data.isAdmin()) {
            mCardView.setCardBackgroundColor(Color.parseColor("#D6E7D7"));
        } else if (data.isUploader()) {
            mCardView.setCardBackgroundColor(Color.parseColor("#D9D5E6"));
        } else {
            mCardView.setCardBackgroundColor(Color.parseColor("#EFF3F7"));
        }

        Spanned spannedHtml;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spannedHtml = Html.fromHtml(data.getContent(), Html.FROM_HTML_MODE_LEGACY, this, null);
        } else {
            spannedHtml = Html.fromHtml(data.getContent(), this, null);
        }
        mContentText.setText(spannedHtml, TextView.BufferType.SPANNABLE);

        mDateTimeText.setText(data.getDateTime());
    }

    @Override
    public Drawable getDrawable(String source) {
        LevelListDrawable d = new LevelListDrawable();
        new ImageLoader().execute(source, d);
        return d;
    }

    //Load image included in the comments (e.g. emoji.)
    class ImageLoader extends AsyncTask<Object, Void, Bitmap> {
        private LevelListDrawable mDrawable;

        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            mDrawable = (LevelListDrawable) params[1];
            Bitmap bitmap = stringBitmapLruCache.get(source);
            if (bitmap != null) {
                return bitmap;
            }
            try {
                InputStream is = new URL(source).openStream();
                bitmap = BitmapFactory.decodeStream(is);
                stringBitmapLruCache.put(source, bitmap);
                return bitmap;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d(TAG, "onPostExecute drawable " + mDrawable);
            Log.d(TAG, "onPostExecute bitmap " + bitmap);
            if (bitmap != null) {
                BitmapDrawable d = new BitmapDrawable(mContentText.getResources(), bitmap);
                mDrawable.addLevel(1, 1, d);
                mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                mDrawable.setLevel(1);

                // i don't know yet a better way to refresh TextView
                // mContentText.invalidate() doesn't work as expected
                CharSequence t = mContentText.getText();
                mContentText.setText(t);
            }
        }
    }
}
