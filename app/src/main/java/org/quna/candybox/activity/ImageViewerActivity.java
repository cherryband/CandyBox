package org.quna.candybox.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.vlonjatg.progressactivity.ProgressActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.quna.candybox.R;
import org.quna.candybox.data.Image;
import org.quna.candybox.typeface.TypefaceCache;
import org.quna.candybox.typeface.TypefaceEnum;
import org.quna.candybox.typeface.TypefaceSpan;

import java.io.IOException;

import uk.co.senab.photoview.PhotoView;

public class ImageViewerActivity extends AppCompatActivity {
    private ProgressActivity progressActivity;
    private Image image;
    private PhotoView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        mImageView = (PhotoView) findViewById(R.id.full_image);
        progressActivity = (ProgressActivity) findViewById(R.id.image_progress);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.comment_fab);

        Intent intent = getIntent();
        image = intent.getParcelableExtra(Image.IMAGE);

        // Update the action bar title with the TypefaceSpan instance
        SpannableString s = new SpannableString(image.getAlt());
        s.setSpan(new TypefaceSpan(this, TypefaceEnum.BOOK.getPath()), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressActivity.showLoading();

        new DownloadAsyncTask().execute(image.getLink());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open CommentViewerActivity
                Context context = view.getContext();
                Intent intent = new Intent(context, CommentViewerActivity.class);
                intent.putExtra(Image.IMAGE, image);
                context.startActivity(intent);
            }
        });
    }

    private void onError() {
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.activity_image_view), "Loading Failed",
                        Snackbar.LENGTH_INDEFINITE)
                .setAction("Reload", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new DownloadAsyncTask().execute(image.getLink());
                        progressActivity.showLoading();
                    }
                });

        //Setting custom font to both text and action text in snackbar.
        View sbView = snackbar.getView();

        TextView textView = (TextView) sbView.findViewById
                (android.support.design.R.id.snackbar_text);
        textView.setTypeface(TypefaceCache.get(this, TypefaceEnum.BOOK.getPath()));

        TextView actionView = (TextView) sbView.findViewById
                (android.support.design.R.id.snackbar_action);
        actionView.setTypeface(TypefaceCache.get(this, TypefaceEnum.BOOK.getPath()));

        snackbar.show();
    }

    private void onLoaded(String rawLink) {
        if (rawLink.contains(".gif")) {
            Glide
                    .with(this)
                    .load(rawLink)
                    .asGif() //Loading GIF in animated form.
                    .listener(new RequestListener<String, GifDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model,
                                                   Target<GifDrawable> target,
                                                   boolean isFirstResource) {
                            e.printStackTrace();
                            onError();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GifDrawable resource, String model,
                                                       Target<GifDrawable> target,
                                                       boolean isFromMemoryCache,
                                                       boolean isFirstResource) {
                            return false;
                        }
                    })
                    .crossFade()
                    .thumbnail(.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mImageView);
        } else {
            Glide
                    .with(this)
                    .load(rawLink)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model,
                                                   Target<GlideDrawable> target,
                                                   boolean isFirstResource) {
                            e.printStackTrace();
                            onError();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model,
                                                       Target<GlideDrawable> target,
                                                       boolean isFromMemoryCache,
                                                       boolean isFirstResource) {
                            return false;
                        }
                    })
                    .crossFade()
                    .thumbnail(.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mImageView);
        }
        progressActivity.showContent();
    }

    public class DownloadAsyncTask extends AsyncTask<String, Void, String> {
        private boolean isError;

        @Override
        protected String doInBackground(String... params) {
            try { //Get thumbnails.
                Document doc = Jsoup.connect(params[0]).timeout(0).get();
                Element page = doc.body();
                Element image = page.getElementById("Imagemain");

                return image.getElementsByTag("img").attr("src");
            } catch (IOException e) {
                e.printStackTrace();
                isError = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String param) {
            super.onPostExecute(param);
            if (isError)
                onError();
            else onLoaded(param);
        }
    }
}
