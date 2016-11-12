package org.quna.candybox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import org.quna.candybox.data.Image;
import org.quna.candybox.misc.CustomTypefaceSnackbar;
import org.quna.candybox.misc.Listener;
import org.quna.candybox.typeface.TypefaceEnum;
import org.quna.candybox.typeface.TypefaceSpan;

public class CommentViewerActivity extends AppCompatActivity {
    private RecyclerView mRecycler; //List of comments
    private LinearLayoutManager mManager; //RecyclerView LayoutManager instance
    private CommentLayoutAdapter mAdapter; //Custom RecyclerView Adapter
    private SwipeRefreshLayout swipeRefresher; //SwipeRefresher for manually refreshing RecyclerView

    private Image image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_viewer);

        SpannableString s = new SpannableString("Comments");
        s.setSpan(new TypefaceSpan(this, TypefaceEnum.BOOK), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        image = intent.getParcelableExtra(Image.IMAGE);

        swipeRefresher = (SwipeRefreshLayout) findViewById(R.id.refresher);
        swipeRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        mRecycler = (RecyclerView) findViewById(R.id.recycler_view);
        mManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mManager);

        String link = image.getLink();

        try { //recover data saved from onSavedInstanceState
            mAdapter = new CommentLayoutAdapter(link, savedInstanceState, mRecycler);
            setListeners();
        } catch (NullPointerException e) {
            mAdapter = new CommentLayoutAdapter(link, mRecycler);
            setListeners();
            mAdapter.requestRefresh();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.onSaveInstanceState(outState);
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
        mAdapter.setOnErrorListener(new Listener() {
            public void invoke() {
                onError();
            }
        });
        mAdapter.setOnUpdatedListener(new Listener() {
            public void invoke() {
                onLoadingFinished();
            }
        });
        mAdapter.setOnEmptyListener(new Listener() {
            @Override
            public void invoke() {
                onEmpty();
            }
        });
    }

    private void onEmpty() {
        CustomTypefaceSnackbar.getSnackBar(this, R.id.comment_viewer_coordinator, "There's no comment. Spooky.").show();
    }

    private void onLoadingFinished() { //Called when RecyclerView is loaded/updated/refreshed.
        swipeRefresher.setRefreshing(false);
    }

    private void refresh() {
        if (!mAdapter.requestRefresh())
            onLoadingFinished();
    }

    private void onError() { //Called when IOException is thrown, mostly occured by network problem.
        String title = getResources().getString(R.string.err_load_failed);
        String actionReload = getResources().getString(R.string.err_action_reload);

        CustomTypefaceSnackbar.getSnackBar(this, R.id.comment_viewer_coordinator, title, actionReload,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refresh();
                    }
                }).show();
    }

}
