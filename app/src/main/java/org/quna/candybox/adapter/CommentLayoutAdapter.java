package org.quna.candybox.adapter;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quna.candybox.R;
import org.quna.candybox.adapter.viewholder.CommentViewHolder;
import org.quna.candybox.adapter.viewholder.ProgressViewHolder;
import org.quna.candybox.data.Comment;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by graphene on 2016-11-06.
 */

public class CommentLayoutAdapter extends AbstractAdapter<Comment> {
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
        add(null);
        new CommentParser().execute();
        return true;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CommentViewHolder.VIEW_COMMENT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_card,
                    parent, false);
            return new CommentViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_viewer,
                    parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (get(position) != null) //The item is in the dataset
            return CommentViewHolder.VIEW_COMMENT;
        return ProgressViewHolder.VIEW_PROGRESS;
    }

    //Custom page parser for Candybooru.
    public class CommentParser extends DownloadAsyncTask<Void> {
        private static final String COMMENT_CONTAINER = "Commentsmain";
        private static final String AUTHOR_DETAILS = "authordetails";
        private static final String CONTENT = "text";

        @Override
        protected ArrayList<Comment> doNetworkTask(Void... params) throws IOException {
            ArrayList<Comment> comments = new ArrayList<Comment>();
            Document doc = Jsoup.connect(url).timeout(0).get();
            Element page = doc.body();

            Elements commentContainer = page.getElementsByClass(COMMENT_CONTAINER);
            for (Element element : commentContainer) {
                if (element.tagName() != "form") {
                    Elements details = element.getElementsByClass(AUTHOR_DETAILS);
                    Element content = element.getElementsByClass(CONTENT).first();
                }
            }

            return comments;
        }
    }
}
