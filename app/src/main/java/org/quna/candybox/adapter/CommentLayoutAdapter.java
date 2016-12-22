package org.quna.candybox.adapter;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import org.quna.candybox.data.Data;
import org.quna.candybox.util.CandybooruImagePageParser;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by graphene on 2016-11-16.
 */

public class CommentLayoutAdapter extends BaseAdapter {
    private String url;

    public CommentLayoutAdapter(String url, RecyclerView recycler) {
        super(recycler);
        this.url = url;
    }

    public CommentLayoutAdapter(String url, Bundle savedInstanceState, RecyclerView mRecycler) {
        super(savedInstanceState, mRecycler);
        this.url = url;
    }

    public boolean requestRefresh() {
        if (isLoading()) return false; //Data is already being processed so it's unnecessary call.
        while (getItemCount() > 0) {
            int lastIndex = getItemCount() - 1;
            removeItemAndNotify(lastIndex);
        }
        addItemAndNotify(progressBar);
        new CommentsParser().execute();
        return true;
    }

    //Custom page parser for Candybooru.
    public class CommentsParser extends BaseAdapter.DownloadAsyncTask<Void> {

        @Override
        protected ArrayList<Data> organizeUi(Void... params) throws IOException {
            ArrayList<Data> datas = new ArrayList<Data>();
            CandybooruImagePageParser parser = new CandybooruImagePageParser(url);
            datas.addAll(parser.getComments());
            return datas;
        }

    }
}
