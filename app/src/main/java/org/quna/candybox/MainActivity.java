package org.quna.candybox;

import android.app.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.support.v4.widget.*;
import android.support.v7.widget.*;
import android.view.*;
import com.malinskiy.materialicons.*;
import com.vlonjatg.progressactivity.*;
import java.util.*;
import org.quna.candybox.*;
import org.quna.candybox.listener.*;

public class MainActivity extends Activity {
	private static final String IMAGE_LIST = "images";
	private static final String PAGE_COUNT = "position";
	
	private RecyclerView mRecycler;
    private RecyclerView.LayoutManager mManager;
    private ImageLayoutAdapter mAdapter;
    private ArrayList<Image> mDataset;
    private ProgressActivity progressActivity;
	private SwipeRefreshLayout swipeRefresher;
	private int currentPos;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        progressActivity = (ProgressActivity) findViewById(R.id.main);
        mRecycler = (RecyclerView) findViewById(R.id.recycler_view);
		mDataset = new ArrayList<Image>();
		
		swipeRefresher = (SwipeRefreshLayout) findViewById(R.id.refresher);
		swipeRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
			@Override
			public void onRefresh(){
				mAdapter.requestRefresh(false);
			}
		});
        mManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mManager);

        try {
			currentPos = savedInstanceState.getInt(PAGE_COUNT, 1);
			mDataset = savedInstanceState.getParcelableArrayList(IMAGE_LIST);
            mAdapter = new ImageLayoutAdapter(mDataset, currentPos, mRecycler);
			setListeners();
        } catch (NullPointerException e) {
			mAdapter = new ImageLayoutAdapter(mRecycler);
			setListeners();
			mAdapter.requestRefresh(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(IMAGE_LIST, mAdapter.getData());
		outState.putInt(PAGE_COUNT, currentPos);
    }
	
	private void setListeners(){
		mAdapter.setOnErrorListener(new Listener(){
				public void invoke(){
					onError();
				}
			});
  		mAdapter.setOnUpdatedListener(new Listener(){
				public void invoke(){
					onLoadingFinished();
				}
			});
	}

    private void onLoadingFinished() {
		swipeRefresher.setRefreshing(false);
        progressActivity.showContent();
    }
	
	private void onError() {
		Drawable error = new IconDrawable(MainActivity.this,
			Iconify.IconValue.zmdi_wifi_off).colorRes(android.R.color.background_dark);
		progressActivity.showError(error, "Error while loading",
			"Something went wrong while loading images.\n" +
			"Check the internet connection and try again.", "Reload",
			new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					progressActivity.showContent();
					mAdapter.requestRefresh(! swipeRefresher.isRefreshing());
				}
			});
	}
}
