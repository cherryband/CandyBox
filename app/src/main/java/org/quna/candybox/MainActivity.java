package org.quna.candybox;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.vlonjatg.progressactivity.ProgressActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final String BCB_URL = "https://www.bittersweetcandybowl.com/";
    private RecyclerView mRecycler;
    private RecyclerView.LayoutManager mManager;
    private RecyclerView.Adapter mAdapter;
    private Image[] mDataset;
    private ProgressActivity progressActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        progressActivity = (ProgressActivity) findViewById(R.id.main);
        mRecycler = (RecyclerView) findViewById(R.id.recycler_view);

        try {
            mDataset = (Image[]) savedInstanceState.getParcelableArray("images");
            if (mDataset == null || mDataset.length == 0)
                startDownload();
            else onLoadingFinished();
        } catch (NullPointerException e) {
            startDownload();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray("images", mDataset);
    }

    private void startDownload() {
        progressActivity.showLoading();
        new DownloadAsyncTask().execute(BCB_URL + "candybooru");
    }

    private void onLoadingFinished() {
        mAdapter = new ImageLayoutAdapter(mDataset);
        mRecycler.setAdapter(mAdapter);

        mRecycler.addItemDecoration(new GridSpacingItemDecoration(2, 20, true));
        initRecyclerViewManager();
    }

    private void initRecyclerViewManager() {
        int column = 2;
        mManager = new GridLayoutManager(this, column);
        mRecycler.setLayoutManager(mManager);
    }

    public class DownloadAsyncTask extends AsyncTask<String, Void, Image[]> {
        private boolean isError;

        @Override
        protected Image[] doInBackground(String... params) {
            ArrayList<Image> links = new ArrayList<>();
            try { //Get thumbnails.
                Document page = Jsoup.connect(params[0]).get();
                Elements thumbnails = page.body().getElementsByClass("thumbnailcontainer");
                //Parse Image link and alt text then store.
                for (Element thumbnail : thumbnails) {
                    Element img = thumbnail.getElementsByTag("img").first();
                    String sourceLink = BCB_URL + img.attr("src");

                    Element link = thumbnail.getElementsByTag("a").first();
                    String pageLink = BCB_URL + link.attr("href");

                    Image image = new Image(sourceLink, img.attr("alt"), pageLink);
                    links.add(image);
                }
            } catch (IOException e) {
                e.printStackTrace();
                isError = true;
            }
            return links.toArray(new Image[links.size()]);
        }

        @Override
        protected void onPostExecute(Image[] images) {
            super.onPostExecute(images);
            if (isError) {
                Drawable error = new IconDrawable(MainActivity.this,
                        Iconify.IconValue.zmdi_wifi_off).colorRes(android.R.color.background_dark);
                progressActivity.showError(error, "Error while loading",
                        "Something went wrong while loading images.\n" +
                                "Check the internet connection and try again.", "Reload",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startDownload();
                            }
                        });
                return;
            }
            mDataset = images;
            progressActivity.showContent();
            onLoadingFinished();
        }
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}
