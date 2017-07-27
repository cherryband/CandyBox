package org.quna.candybox.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.quna.candybox.R;
import org.quna.candybox.adapter.ThumbnailLayoutAdapter;
import org.quna.candybox.listener.CallbackListener;
import org.quna.candybox.typeface.CustomTypefaceSnackbar;
import org.quna.candybox.typeface.TypeFaceCache;
import org.quna.candybox.typeface.TypefaceEnum;
import org.quna.candybox.typeface.TypefaceSpan;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecycler; //List of thumbnails
    private StaggeredGridLayoutManager mManager; //RecyclerView LayoutManager instance
    private ThumbnailLayoutAdapter mAdapter; //Custom RecyclerView Adapter
    private SwipeRefreshLayout swipeRefresher; //SwipeRefresher for manually refreshing RecyclerView
    private int spanCount = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SpannableString s = new SpannableString("Recents");
        s.setSpan(new TypefaceSpan(this, TypefaceEnum.BOOK), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);

        //init variables
        mRecycler = (RecyclerView) findViewById(R.id.recycler_view);

        swipeRefresher = (SwipeRefreshLayout) findViewById(R.id.refresher);
        swipeRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh(false);
            }
        });

        mManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(mManager);

        try { //recover data saved from onSavedInstanceState
            mAdapter = new ThumbnailLayoutAdapter(savedInstanceState, mRecycler);
            setListeners();
        } catch (NullPointerException e) {
            mAdapter = new ThumbnailLayoutAdapter(mRecycler);
            setListeners();
            mAdapter.requestRefresh(true);
        }
    }

    private void refresh(boolean showProgressBar) {
        if (!mAdapter.requestRefresh(showProgressBar))
            onLoadingFinished();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.onSaveInstanceState(outState);
    }

    private void setListeners() { //Set listeners for adapter
        mAdapter.setCallbackListener(new CallbackListener() {
            @Override
            public void onLoadFinished() {
                onLoadingFinished();
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

    private void onLoadingFinished() { //Called when RecyclerView is loaded/updated/refreshed.
        swipeRefresher.setRefreshing(false);

    }

    private void onError() { //Called when IOException is thrown, mostly occured by network problem.
        Snackbar snackbar = Snackbar
                .make(mRecycler, R.string.err_load_failed, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.err_action_reload, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refresh(true);
                    }
                });
        Typeface book = TypeFaceCache.get(this, TypefaceEnum.BOOK);
        CustomTypefaceSnackbar.runSnackBarWithTypeface(snackbar, book);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        }
    }
}
