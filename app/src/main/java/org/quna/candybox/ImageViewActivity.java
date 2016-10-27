package org.quna.candybox;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.vlonjatg.progressactivity.ProgressActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ImageViewActivity extends Activity {
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
	
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    private ImageView mImageView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mImageView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
	private ProgressActivity progressActivity;
	private String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_view);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mImageView = (ImageView) findViewById(R.id.full_image);
		progressActivity = (ProgressActivity) findViewById(R.id.image_progress);


        // Set up the user interaction to manually show or hide the system UI.
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.comments_button).setOnTouchListener(mDelayHideTouchListener);
		Intent intent = getIntent();
		link = intent.getStringExtra(ImageLayoutAdapter.OPEN_IMAGE);
		progressActivity.showLoading();
		new DownloadAsyncTask().execute(link);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mImageView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
	
	private void onError() {
		Drawable error = new IconDrawable(ImageViewActivity.this,
			Iconify.IconValue.zmdi_wifi_off).colorRes(android.R.color.background_light);
		progressActivity.showError(error, "Error while loading",
			"Something went wrong while loading images.\n" +
			"Check the internet connection and try again.", "Reload",
			new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					progressActivity.showLoading();
					new DownloadAsyncTask().execute(link);
				}
			});
	}
	private void onLoaded(String rawLink){
		if (rawLink.contains(".gif")) {
			Glide
				.with(this)
				.load(rawLink)
				.asGif()
				.crossFade()
				.fitCenter()
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.into(mImageView);
		} else {
			Glide
				.with(this)
				.load(rawLink)
				.crossFade()
				.fitCenter()
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.into(mImageView);
		}
		progressActivity.showContent();
	}
	
	public class DownloadAsyncTask extends AsyncTask<String, Void, String> {
        private boolean isError;

        @Override
        protected String doInBackground(String... params) {
			int loaded = 0;
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
            if (isError){
				onError();
				return;
            }
			onLoaded(param);
        }
    }
}
