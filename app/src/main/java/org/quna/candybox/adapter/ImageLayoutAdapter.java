package org.quna.candybox.adapter;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quna.candybox.R;
import org.quna.candybox.adapter.viewholder.ImageViewHolder;
import org.quna.candybox.adapter.viewholder.ProgressViewHolder;
import org.quna.candybox.data.Image;

import java.io.IOException;
import java.util.ArrayList;

public class ImageLayoutAdapter extends AbstractAdapter<Image> {
    private static final String BCB_URL = "https://www.bittersweetcandybowl.com";
    private static final String PAGE_URL = BCB_URL + "/candybooru/post/list/";
    private static final String PAGE_COUNT = "position";
    private int currentPos;
    private String url;
    private int visibleThreshold = 16;

    private RecyclerView.OnScrollListener onScroll = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recycler, int dx, int dy) {
            final LinearLayoutManager mManager = (LinearLayoutManager) recycler.getLayoutManager();
            super.onScrolled(recycler, dx, dy);
            recycler.post(new Runnable() {
                @Override
                public void run() {
                    int totalItemCount = mManager.getItemCount();
                    int lastVisibleItem = mManager.findLastVisibleItemPosition();
                    if (!isLoading() && totalItemCount >= currentPos * visibleThreshold && totalItemCount <= (lastVisibleItem + 1))
                        requestDataUpdate(true, false, currentPos + 1, 1);
                }
            });
        }
    };


    public ImageLayoutAdapter(RecyclerView recycler) {
        super(recycler);
        url = PAGE_URL;
        currentPos = 1;
        recycler.addOnScrollListener(onScroll);
    }

    public ImageLayoutAdapter(String query, RecyclerView recycler) {
        this(recycler);
        url = PAGE_URL + query + "/";
    }

    public ImageLayoutAdapter(Bundle savedInstanceState, RecyclerView mRecycler) {
        super(savedInstanceState, mRecycler);
        currentPos = savedInstanceState.getInt(PAGE_COUNT);
        url = PAGE_URL;
    }

    public ImageLayoutAdapter(String query, Bundle savedInstanceState, RecyclerView mRecycler) {
        this(savedInstanceState, mRecycler);
        url = PAGE_URL + query + "/";
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PAGE_COUNT, currentPos);
    }

    public boolean requestRefresh(boolean showProgressBar) {
        return requestDataUpdate(showProgressBar, true, 1, 1);
    }

    public boolean requestDataUpdate(boolean showProgressBar, boolean isRefresh, int start, int pageCount) {
        if (isLoading()) return false; //Data is already being processed so it's unnecessary call.
        while (isRefresh && getItemCount() > 0) {
            int lastIndex = getItemCount() - 1;
            remove(lastIndex);
        }
        if (showProgressBar) add(null);
        new CandybooruParser().execute(start, pageCount);
        return true;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ImageViewHolder.VIEW_IMAGE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_card,
                    parent, false);
            return new ImageViewHolder(v);
        } else {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_viewer,
                    parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (get(position) != null)//The item is in the dataset
            return ImageViewHolder.VIEW_IMAGE;
        return ProgressViewHolder.VIEW_PROGRESS;
    }

    //Custom page parser for Candybooru.
    public class CandybooruParser extends DownloadAsyncTask<Integer> {
        private static final String NO_RESULT = "No_Images_Foundmain";
        private static final String THUMBNAIL_CONTAINER = "thumbnailcontainer";

        @Override
        protected ArrayList<Image> doNetworkTask(Integer... params) throws IOException {
            ArrayList<Image> images = new ArrayList<Image>();
            for (int index = params[0]; images.size() < params[1] * visibleThreshold; index++) {
                String url = ImageLayoutAdapter.this.url + Integer.toString(index);
                Document doc = Jsoup.connect(url).timeout(0).get();
                Element page = doc.body();
                if (page.getElementById(NO_RESULT) != null) //No Result
                    break;

                Elements thumbnails = page.getElementsByClass(THUMBNAIL_CONTAINER);
                //Parse Image link(src), post link(href), and alt text(alt) then store.
                for (Element thumbnail : thumbnails) {
                    Element img = thumbnail.getElementsByTag("img").first();
                    String sourceLink = BCB_URL + img.attr("src");

                    Element link = thumbnail.getElementsByTag("a").first();
                    String pageLink = BCB_URL + link.attr("href");
                    String alt = img.attr("alt");

                    Image image = new Image(sourceLink, alt, pageLink);
                    images.add(image);
                }
                currentPos = index;
            }
            return images;
        }
    }
}
