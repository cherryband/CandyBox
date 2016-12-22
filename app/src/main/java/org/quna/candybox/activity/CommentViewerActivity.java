package org.quna.candybox.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;

import org.quna.candybox.R;
import org.quna.candybox.adapter.CommentLayoutAdapter;
import org.quna.candybox.data.Thumbnail;
import org.quna.candybox.listener.CallbackListener;
import org.quna.candybox.typeface.CustomTypefaceSnackbar;
import org.quna.candybox.typeface.TypefaceCache;
import org.quna.candybox.typeface.TypefaceEnum;
import org.quna.candybox.typeface.TypefaceSpan;

import java.io.IOException;

public class CommentViewerActivity extends AppCompatActivity {
    private RecyclerView mRecycler; //List for comments and images
    private LinearLayoutManager mManager; //RecyclerView LayoutManager instance
    private CommentLayoutAdapter mAdapter; //Custom RecyclerView Adapter
    private Thumbnail thumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_viewer);

        mRecycler = (RecyclerView) findViewById(R.id.recycler_view);
        mManager = new LinearLayoutManager(this);

        Intent intent = getIntent();
        thumbnail = intent.getParcelableExtra(Thumbnail.THUMBNAIL);

        // Update the action bar title with the TypefaceSpan instance
        SpannableString s = new SpannableString("Comments");
        s.setSpan(new TypefaceSpan(this, TypefaceEnum.BOOK), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mRecycler.setLayoutManager(mManager);

        try { //recover data saved from onSavedInstanceState
            mAdapter = new CommentLayoutAdapter(thumbnail.getPageLink(), savedInstanceState, mRecycler);
            setListeners();
        } catch (NullPointerException e) {
            mAdapter = new CommentLayoutAdapter(thumbnail.getPageLink(), mRecycler);
            setListeners();
            requestUpdate();
        }
    }

    private void requestUpdate() {
        mAdapter.requestRefresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }

    private void setListeners() { //Set listeners for adapter
        mAdapter.setCallbackListener(new CallbackListener() {
            @Override
            public void onLoadFinished() {
            }

            @Override
            public void onIOException(IOException e) {
                onError();
            }

            @Override
            public void onEmptyResult() {

            }
        });
    }

    private void onError() { //Called when IOException is thrown, mostly occured by network problem.
        Snackbar snackbar = Snackbar
                .make(mRecycler, R.string.err_load_failed, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.err_action_reload, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAdapter.requestRefresh();
                    }
                });
        Typeface book = TypefaceCache.get(this, TypefaceEnum.BOOK);
        CustomTypefaceSnackbar.runSnackBarWithTypeface(snackbar, book);
    }
}
