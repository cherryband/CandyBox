package org.quna.candybox;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.TextView;

import com.vlonjatg.progressactivity.ProgressActivity;

import org.quna.candybox.typeface.TypefaceCache;
import org.quna.candybox.typeface.TypefaceSpan;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String BOOK_FONT = "fira-sans/FiraSans-Book.otf";

    private RecyclerView mRecycler;
    private RecyclerView.LayoutManager mManager;
    private ImageLayoutAdapter mAdapter;
    private ArrayList<Image> mDataset;
    private ProgressActivity progressActivity;
	private SwipeRefreshLayout swipeRefresher;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        SpannableString s = new SpannableString("Recents");
        s.setSpan(new TypefaceSpan(this, BOOK_FONT), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);

        //init variables
        progressActivity = (ProgressActivity) findViewById(R.id.main);
        mRecycler = (RecyclerView) findViewById(R.id.recycler_view);
		mDataset = new ArrayList<Image>();
		
		swipeRefresher = (SwipeRefreshLayout) findViewById(R.id.refresher);
		swipeRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
			@Override
			public void onRefresh(){
                refresh(false);
            }
		});
        mManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mManager);

        try {
            //recover data saved from onSavedInstanceState
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
        Snackbar snackbar = Snackbar
                .make(mRecycler, "Loading Failed.", Snackbar.LENGTH_LONG)
                .setAction("Reload", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAdapter.requestRefresh(true);
                    }
                });

        //Setting custom font to both text and action text in snackbar.
        View sbView = snackbar.getView();

        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTypeface(TypefaceCache.get(this, BOOK_FONT));

        TextView actionView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_action);
        actionView.setTypeface(TypefaceCache.get(this, BOOK_FONT));

        snackbar.show();
    }
}
