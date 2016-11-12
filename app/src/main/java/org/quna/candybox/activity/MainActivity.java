package org.quna.candybox.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;

import org.quna.candybox.R;
import org.quna.candybox.adapter.ImageLayoutAdapter;
import org.quna.candybox.adapter.viewholder.ImageViewHolder;
import org.quna.candybox.adapter.viewholder.ProgressViewHolder;
import org.quna.candybox.misc.CustomTypefaceSnackbar;
import org.quna.candybox.misc.Listener;
import org.quna.candybox.typeface.TypefaceEnum;
import org.quna.candybox.typeface.TypefaceSpan;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecycler; //List of thumbnails
    private GridLayoutManager mManager; //RecyclerView LayoutManager instance
    private ImageLayoutAdapter mAdapter; //Custom RecyclerView Adapter
    private SwipeRefreshLayout swipeRefresher; //SwipeRefresher for manually refreshing RecyclerView
    private int spanCount = 2;

    private GridLayoutManager.SpanSizeLookup sizeLookup = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            switch (mAdapter.getItemViewType(position)) {
                case ImageViewHolder.VIEW_IMAGE:
                    return 1;
                case ProgressViewHolder.VIEW_PROGRESS:
                    return spanCount; //number of columns of the grid
                default:
                    return -1;
            }
        }
    };

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
        mManager = new GridLayoutManager(this, spanCount);
        mManager.setSpanSizeLookup(sizeLookup);
        mRecycler.setLayoutManager(mManager);

        try { //recover data saved from onSavedInstanceState
            mAdapter = new ImageLayoutAdapter(savedInstanceState, mRecycler);
            setListeners();
        } catch (NullPointerException e) {
            mAdapter = new ImageLayoutAdapter(mRecycler);
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
    }

    private void onLoadingFinished() { //Called when RecyclerView is loaded/updated/refreshed.
        swipeRefresher.setRefreshing(false);
    }

    private void onError() { //Called when IOException is thrown, mostly occured by network problem.
        String title = getResources().getString(R.string.err_load_failed);
        String actionReload = getResources().getString(R.string.err_action_reload);

        CustomTypefaceSnackbar.getSnackBar(this, R.id.main, title, actionReload,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refresh(true);
                    }
                }).show();
    }
}
