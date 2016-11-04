package org.quna.candybox;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class ImageLayoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String OPEN_IMAGE = "openImage";
    public static final int VIEW_ITEM = 0;
    public static final int VIEW_PROGRESS = 1;
    private static final String BCB_URL = "https://www.bittersweetcandybowl.com";
    private static final String PAGE_URL = BCB_URL + "/candybooru/post/list/";
    private static final String IMAGE_LIST = "images";
    private static final String PAGE_COUNT = "position";
    private static final Listener dummy = new Listener() {
        public void invoke() {
            return;
        }
    };
    private int currentPos;
    private String url;
    private ArrayList<Image> mDataset;

    private Listener onUpdateRequest = dummy;
    private Listener onUpdated = dummy;
    private Listener onError = dummy;
    private Listener onEmpty = dummy;

    private boolean loading;
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
                    if (!loading && totalItemCount >= currentPos * visibleThreshold && totalItemCount <= (lastVisibleItem + 1))
                        requestDataUpdate(true, false, currentPos + 1, 1);
                }
            });
        }
    };

    public ImageLayoutAdapter(String query, RecyclerView recycler) {
        this(recycler);
        url = PAGE_URL + query + "/";
    }

    public ImageLayoutAdapter(RecyclerView recycler) {
        url = PAGE_URL;
        currentPos = 1;

        recycler.addOnScrollListener(onScroll);

        mDataset = new ArrayList<Image>();
        recycler.setAdapter(this);
    }

    public ImageLayoutAdapter(ArrayList<Parcelable> savedInstance, int currentPos, RecyclerView recycler) {
        this(recycler);
        this.currentPos = currentPos;
        for (Parcelable p : savedInstance) {
            if (p != null && p instanceof Image) {
                Image i = (Image) p;
                mDataset.add(i);
                this.notifyItemInserted(mDataset.size());
            }
        }
    }

    public ImageLayoutAdapter(String query, ArrayList<Parcelable> savedInstance, int currentPos, RecyclerView recycler) {
        this(savedInstance, currentPos, recycler);
        url = PAGE_URL + query + "/";
        this.currentPos = currentPos;
    }

    public ImageLayoutAdapter(Bundle savedInstanceState, RecyclerView mRecycler) {
        this(savedInstanceState.getParcelableArrayList(IMAGE_LIST),
                savedInstanceState.getInt(PAGE_COUNT), mRecycler);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_card,
                    parent, false);
            return new ImageViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_viewer, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    public void setOnUpdateRequestedListener(Listener l) {
        onUpdateRequest = l;
    }

    public void setOnUpdatedListener(Listener l) {
        onUpdated = l;
    }

    public void setOnErrorListener(Listener l) {
        onError = l;
    }

    public void setOnEmptyListener(Listener l) {
        onEmpty = l;
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataset.get(position) != null)//The item is in the dataset
            return VIEW_ITEM;
        return VIEW_PROGRESS;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageViewHolder) {
            ImageViewHolder imageHolder = (ImageViewHolder) holder;
            final Image image = mDataset.get(position);
            ImageView mImageView = imageHolder.mImageView;
            mImageView.setContentDescription(image.getAlt());
            String imgLink = image.getSource();
            Context context = mImageView.getContext();

            Glide
                    .with(context)
                    .load(imgLink)
                    .crossFade()
                    .fitCenter()
                    .thumbnail(.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mImageView);

            mImageView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    //Open ImageViewerActivity
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ImageViewerActivity.class);
                    intent.putExtra(OPEN_IMAGE, image);
                    context.startActivity(intent);
                }
            });
        } else if (holder instanceof ProgressViewHolder) {
            ProgressViewHolder progressHolder = (ProgressViewHolder) holder;
            ProgressBar progress = progressHolder.mProgress;
            progress.setIndeterminate(true);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public boolean requestRefresh(boolean showProgressBar) {
        return requestDataUpdate(showProgressBar, true, 1, 1);
    }

    public boolean requestDataUpdate(boolean showProgressBar, boolean isRefresh, int start, int pageCount) {
        if (loading) return false; //Data is already being processed so it's unnecessary call.
        while (isRefresh && mDataset.size() > 0) {
            int lastIndex = mDataset.size() - 1;
            mDataset.remove(lastIndex);
            this.notifyItemRemoved(lastIndex);
        }
        if (showProgressBar) mDataset.add(null);
        new DownloadAsyncTask().execute(start, pageCount);
        return true;
    }

    private void loaded() {
        loading = false;
        while (mDataset.contains(null)) {
            int lastIndex = mDataset.lastIndexOf(null);
            mDataset.remove(lastIndex);
            this.notifyItemRemoved(lastIndex);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(IMAGE_LIST, mDataset);
        outState.putInt(PAGE_COUNT, currentPos);
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public CardView mCardView;
        public ImageView mImageView;

        public ImageViewHolder(View v) {
            super(v);
            mCardView = (CardView) v.findViewById(R.id.card);
            mImageView = (ImageView) v.findViewById(R.id.thumbnail);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar mProgress;

        public ProgressViewHolder(View v) {
            super(v);
            mProgress = (ProgressBar) v.findViewById(R.id.progress);
        }
    }

    //Custom page parser for Candybooru.
    public class DownloadAsyncTask extends AsyncTask<Integer, Void, ArrayList<Image>> {
        private static final String NO_RESULT = "No_Images_Foundmain";
        private static final String THUMBNAIL_CONTAINER = "thumbnailcontainer";
        private boolean isError;

        @Override
        protected ArrayList<Image> doInBackground(Integer... params) {
            loading = true;
            ArrayList<Image> images = new ArrayList<Image>();
            try { //Parse thumbnails.
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
            } catch (IOException e) {
                e.printStackTrace();
                isError = true;
            }
            return images;
        }

        @Override
        protected void onPostExecute(ArrayList<Image> images) {
            super.onPostExecute(images);
            loaded();
            if (isError)
                onError.invoke();
            else if (images.size() == 0) //No Result
                onEmpty.invoke();
            else {
                for (Image image : images) {
                    if (mDataset.add(image)) {
                        //This method has to be called from UI thread.
                        ImageLayoutAdapter.this.notifyItemInserted(mDataset.size());
                    }
                }
                onUpdated.invoke();
            }
        }
    }
}
