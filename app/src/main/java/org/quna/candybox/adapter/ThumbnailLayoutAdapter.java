package org.quna.candybox.adapter;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import org.quna.candybox.adapter.viewholder.BaseViewHolder;
import org.quna.candybox.adapter.viewholder.ProgressViewHolder;
import org.quna.candybox.data.Data;
import org.quna.candybox.util.CandybooruMainPageParser;

import java.io.IOException;
import java.util.ArrayList;

public class ThumbnailLayoutAdapter extends BaseAdapter {
    private static final String BCB_URL = "https://www.bittersweetcandybowl.com";
    private static final String PAGE_URL = BCB_URL + "/candybooru/post/list/";
    private static final String PAGE_COUNT = "position";
    private int currentPos;
    private String url;
    private int visibleThreshold = 16;

    private RecyclerView.OnScrollListener onScroll = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recycler, int dx, int dy) {
            final StaggeredGridLayoutManager mManager = (StaggeredGridLayoutManager) recycler.getLayoutManager();
            super.onScrolled(recycler, dx, dy);
            recycler.post(new Runnable() {
                @Override
                public void run() {
                    int totalItemCount = mManager.getItemCount();
                    int[] into = new int[mManager.getSpanCount()];
                    mManager.findLastVisibleItemPositions(into);

                    int lastItemCount = 0;
                    for (int i : into) {
                        if (i >= lastItemCount) lastItemCount = i;
                    }

                    if (!isLoading() && totalItemCount >= currentPos * visibleThreshold && totalItemCount <= (lastItemCount + 1))
                        requestDataUpdate(true, false, ++currentPos);
                }
            });
        }
    };


    public ThumbnailLayoutAdapter(RecyclerView recycler) {
        super(recycler);
        url = PAGE_URL;
        currentPos = 1;
        recycler.addOnScrollListener(onScroll);
    }

    public ThumbnailLayoutAdapter(String query, RecyclerView recycler) {
        this(recycler);
        setQuery(query);
    }

    public ThumbnailLayoutAdapter(Bundle savedInstanceState, RecyclerView mRecycler) {
        super(savedInstanceState, mRecycler);
        currentPos = savedInstanceState.getInt(PAGE_COUNT);
        url = PAGE_URL;
    }

    public ThumbnailLayoutAdapter(String query, Bundle savedInstanceState, RecyclerView mRecycler) {
        this(savedInstanceState, mRecycler);
        url = PAGE_URL + query + "/";
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PAGE_COUNT, currentPos);
    }

    public boolean requestRefresh(boolean showProgressBar) {
        return requestDataUpdate(showProgressBar, true, 1);
    }

    public void setQuery(String query) {
        url = PAGE_URL + query + "/";
    }

    private boolean requestDataUpdate(boolean showProgressBar, boolean isRefresh, int start) {
        if (isLoading()) return false; //Data is already being processed so it's unnecessary call.
        while (isRefresh && getItemCount() > 0) {
            int lastIndex = getItemCount() - 1;
            removeItemAndNotify(lastIndex);
        }
        if (showProgressBar) addItemAndNotify(progressBar);
        new ImageListParser(url).execute(start);
        return true;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof ProgressViewHolder) {
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
        }
    }

    public class ImageListParser extends DownloadAsyncTask<Integer> {
        private CandybooruMainPageParser parser;

        public ImageListParser(String url) {
            parser = new CandybooruMainPageParser(url);
        }

        @Override
        protected ArrayList<Data> organizeUi(Integer... params) throws IOException {
            ArrayList<Data> datas = new ArrayList<Data>();
            datas.addAll(parser.getThumbnails(params[0]));
            return datas;
        }
    }
}
