package org.quna.candybox.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;

import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.indicator.progresspie.ProgressPieIndicator;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.github.piasy.biv.view.BigImageView;

import org.quna.candybox.R;
import org.quna.candybox.data.Image;
import org.quna.candybox.data.Thumbnail;
import org.quna.candybox.typeface.CustomTypefaceSnackbar;
import org.quna.candybox.typeface.TypefaceCache;
import org.quna.candybox.typeface.TypefaceEnum;
import org.quna.candybox.typeface.TypefaceSpan;
import org.quna.candybox.util.CandybooruImagePageParser;

import java.io.IOException;

public class ImageViewerActivity extends AppCompatActivity {
    private BigImageView mImageView;
    private CoordinatorLayout coordinatorLayout;
    private Thumbnail thumbnail;
    private Image image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BigImageViewer.initialize(GlideImageLoader.with(this));
        setContentView(R.layout.activity_image_viewer);

        mImageView = (BigImageView) findViewById(R.id.full_image);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.image_viewer_coordinator);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.comment_fab);

        Intent intent = getIntent();
        thumbnail = intent.getParcelableExtra(Thumbnail.THUMBNAIL);

        // Update the action bar title with the TypefaceSpan instance

        SpannableString s = new SpannableString(thumbnail.getAlt());
        s.setSpan(new TypefaceSpan(this, TypefaceEnum.BOOK), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);
        actionBar.setDisplayHomeAsUpEnabled(true);

        loadImage(thumbnail.getPageLink());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open ComentViewerActivity
                Context context = view.getContext();
                Intent intent = new Intent(context, CommentViewerActivity.class);
                intent.putExtra(Thumbnail.THUMBNAIL, thumbnail);
                context.startActivity(intent);
            }
        });
    }

    private void loadImage(String link) {
        new ImageDownloadASyncTask().execute(link);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_to_gallery_menu:
                mImageView.saveImageIntoGallery();
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Download Started.", Snackbar.LENGTH_SHORT);

                Typeface book = TypefaceCache.get(this, TypefaceEnum.BOOK);
                CustomTypefaceSnackbar.runSnackBarWithTypeface(snackbar, book);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onIOError() { //Called when IOException is thrown, mostly occured by network problem.
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, R.string.err_load_failed, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.err_action_reload, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadImage(thumbnail.getPageLink());
                    }
                });
        Typeface book = TypefaceCache.get(this, TypefaceEnum.BOOK);
        CustomTypefaceSnackbar.runSnackBarWithTypeface(snackbar, book);
    }

    public class ImageDownloadASyncTask extends AsyncTask<String, Void, Image> {

        @Override
        protected Image doInBackground(String... strings) {
            try {
                return new CandybooruImagePageParser(strings[0]).getImage();
            } catch (IOException e) {
                e.printStackTrace();
                onIOError();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Image imageInfo) {
            super.onPostExecute(imageInfo);
            image = imageInfo;
            if (image != null) {
                String link = image.getImageLink();
                mImageView.showImage(Uri.parse(link));
                mImageView.setProgressIndicator(new ProgressPieIndicator());
            }
        }
    }
}