package org.quna.candybox;

import android.os.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.*;
import com.bumptech.glide.load.engine.*;
import java.io.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.quna.candybox.listener.*;

public class ImageLayoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final String BCB_URL = "https://www.bittersweetcandybowl.com";
	private static final String PAGE_URL = BCB_URL + "/candybooru/post/list/";
	private static final int VIEW_ITEM = 0;
	private static final int VIEW_PROGRESS =  1;
	private static final Listener dummy = new Listener(){
		public void invoke(){
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
		@Override public void onScrolled(RecyclerView recycler, int dx, int dy) {
			LinearLayoutManager mManager = (LinearLayoutManager) recycler.getLayoutManager();
			super.onScrolled(recycler, dx, dy);
			int totalItemCount = mManager.getItemCount();
			int lastVisibleItem = mManager.findLastVisibleItemPosition();
			if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
				mDataset.add(null);
				ImageLayoutAdapter.this.notifyItemInserted(mDataset.size());
				new DownloadAsyncTask().execute(currentPos + 1, 1);
				onUpdateRequest.invoke();
			}
		}
	};

    public ImageLayoutAdapter(String query, RecyclerView recycler) {
        this(recycler);
		url = PAGE_URL + query + "/";
    }
	
	public ImageLayoutAdapter(RecyclerView recycler){
		url = PAGE_URL;
		currentPos = 1;
		
		recycler.addOnScrollListener(onScroll);
		mDataset = new ArrayList<Image>();
		recycler.setAdapter(this);
	}
	
	public ImageLayoutAdapter (ArrayList<Image> savedInstance,int currentPos, RecyclerView recycler){
		this(recycler);
		this.currentPos = currentPos;
		for (Image i:savedInstance){
			mDataset.add(i);
			this.notifyItemInserted(mDataset.size());
		}
	}
	
	public ImageLayoutAdapter (String query, ArrayList<Image> savedInstance,int currentPos, RecyclerView recycler){
		this(savedInstance, currentPos, recycler);
		url = PAGE_URL + query + "/";
		this.currentPos = currentPos;
	}
	
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_card,
				parent,false);
			return new ImageViewHolder(v);
		} else {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_viewer,parent,false);
			return new ProgressViewHolder(v);
		}
    }
	
	public void setOnUpdateRequestedListener(Listener l){
		onUpdateRequest = l;
	}
	
	public void setOnUpdatedListener(Listener l){
		onUpdated = l;
	}
	
	public void setOnErrorListener(Listener l){
		onError = l;
	}
	
	public void setOnEmptyListener (Listener l){
		onEmpty = l;
	}
	
	public ArrayList<Image> getData(){
		return mDataset;
	}
	
	@Override public int getItemViewType(int position) {
		if (mDataset.get(position) != null)//The item is in the dataset
			return VIEW_ITEM;
		return VIEW_PROGRESS;
	}

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof ImageViewHolder) {
			ImageViewHolder imageHolder = (ImageViewHolder) holder;
        	Image image = mDataset.get(position);
			ImageView mImageView = imageHolder.mImageView;
        	mImageView.setContentDescription(image.getAlt());
        	String imgLink = image.getSource();

        	if (imgLink.contains(".gif")) {
            	Glide
                    .with(mImageView.getContext())
                    .load(imgLink)
                    .asGif()
                    .crossFade()
					.fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mImageView);
			} else {
          		Glide
                    .with(mImageView.getContext())
                    .load(imgLink)
                    .crossFade()
					.fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mImageView);
			}
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
	
	public void requestRefresh(boolean showProgressBar) {
		while (mDataset.size() > 0) {
			int lastIndex = mDataset.size()-1;
			mDataset.remove(lastIndex);
			this.notifyItemRemoved(lastIndex);
		}
		if (showProgressBar) mDataset.add(null);
		new DownloadAsyncTask().execute(1, 1);
	}
	
	//Custom page parser for Candybooru.
    public class DownloadAsyncTask extends AsyncTask<Integer, Void, Void> {
        private boolean isError;
		private boolean isEmpty;

        @Override
        protected Void doInBackground(Integer... params) {
			loading = true;
			int loaded = 0;
            try { //Get thumbnails.
				for (int index = params[0]; loaded < params[1] * 16; index ++){
					String url = PAGE_URL + Integer.toString(index);
                	Document doc = Jsoup.connect(url).get();
					Element page = doc.body();
					if (page.getElementById("No_Images_Foundmain") != null) {
						isEmpty = true;
						return null;
					}
					
                	Elements thumbnails = page.getElementsByClass("thumbnailcontainer");
                	//Parse Image link and alt text then store.
                	for (Element thumbnail : thumbnails) {
                	    Element img = thumbnail.getElementsByTag("img").first();
                	    String sourceLink = BCB_URL + img.attr("src");

	                    Element link = thumbnail.getElementsByTag("a").first();
    	                String pageLink = BCB_URL + link.attr("href");
						String alt = img.attr("alt");

						Image image = new Image(sourceLink, alt, pageLink);
						if (mDataset.add(image)) {
							loaded++;
							try{
								ImageLayoutAdapter.this.notifyItemInserted(mDataset.size());
							} catch (Exception e){
								e.printStackTrace();
								Log.e("suitcase", e.getMessage());
							}
						}
        	        }
					currentPos = index;
				}
            } catch (IOException e) {
                e.printStackTrace();
                isError = true;
            }
			return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            super.onPostExecute(param);
			loaded();
            if (isError)
                onError.invoke();
            else if (isEmpty)
				onEmpty.invoke();
			else {
            	onUpdated.invoke();
			}
        }
    }
	
	private void loaded(){
		loading = false;
		while (mDataset.contains(null)){
			int lastIndex = mDataset.lastIndexOf(null);
			mDataset.remove(lastIndex);
			this.notifyItemRemoved(lastIndex);
		}
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
		
		public ProgressViewHolder (View v){
			super(v);
			mProgress = (ProgressBar) v.findViewById(R.id.progress);
		}
	}
}
