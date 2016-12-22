package org.quna.candybox.adapter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.quna.candybox.adapter.viewholder.BaseViewHolder;
import org.quna.candybox.adapter.viewholder.CommentViewHolder;
import org.quna.candybox.adapter.viewholder.ProgressViewHolder;
import org.quna.candybox.adapter.viewholder.ThumbnailViewHolder;
import org.quna.candybox.data.Data;
import org.quna.candybox.data.PlaceHolder;
import org.quna.candybox.listener.CallbackListener;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by graphene on 2016-11-06.
 */

public abstract class BaseAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final CallbackListener dummy = new CallbackListener() {
        @Override
        public void onLoadFinished() {
        }

        @Override
        public void onIOException(IOException e) {
        }

        @Override
        public void onEmptyResult() {
        }
    };

    private static final String ITEM_LIST = "images";
    protected final PlaceHolder progressBar = new PlaceHolder(ProgressViewHolder.ID);
    private CallbackListener callbackListener = dummy;
    private ArrayList<Data> mDataset;
    private boolean loading;

    public BaseAdapter(RecyclerView recycler) {
        mDataset = new ArrayList<Data>();
        recycler.setAdapter(this);
    }

    public BaseAdapter(Bundle savedInstanceState, RecyclerView mRecycler) {
        this(mRecycler);
        for (Parcelable p : savedInstanceState.getParcelableArray(ITEM_LIST)) {
            if (p != null) {
                getDataset().add((Data) p);
            }
        }
    }

    public void setCallbackListener(CallbackListener listener) {
        callbackListener = listener;
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(ITEM_LIST, getDataset());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return getDataset().size();
    }

    private void loaded() {
        setLoading(false);
        while (getDataset().contains(progressBar)) {
            int lastIndex = getDataset().lastIndexOf(progressBar);
            getDataset().remove(lastIndex);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.loadWith(getItem(position));
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case CommentViewHolder.ID:
                return CommentViewHolder.newInstance(parent);
            case ProgressViewHolder.ID:
                return ProgressViewHolder.newInstance(parent);
            case ThumbnailViewHolder.ID:
                return ThumbnailViewHolder.newInstance(parent);
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getHolderId();
    }

    protected boolean addItemAndNotify(Data data) {
        boolean inserted = mDataset.add(data);
        if (inserted) notifyItemInserted(getItemCount());
        return inserted;
    }

    protected Data removeItemAndNotify(int pos) {
        Data data = mDataset.remove(pos);
        this.notifyItemRemoved(pos);
        return data;
    }

    protected Data getItem(int pos) {
        return mDataset.get(pos);
    }

    protected boolean isLoading() {
        return loading;
    }

    protected void setLoading(boolean loading) {
        this.loading = loading;
    }

    protected ArrayList<Data> getDataset() {
        return mDataset;
    }

    public abstract class DownloadAsyncTask<F> extends AsyncTask<F, Void, ArrayList<Data>> {

        @Override
        protected ArrayList<Data> doInBackground(F... params) {
            loading = true;
            ArrayList<Data> datas = new ArrayList<Data>();
            try {
                datas = organizeUi(params);
            } catch (IOException e) {
                e.printStackTrace();
                callbackListener.onIOException(e);
            }
            return datas;
        }

        protected abstract ArrayList<Data> organizeUi(F... params) throws IOException;

        @Override
        protected void onPostExecute(ArrayList<Data> datas) {
            super.onPostExecute(datas);
            loaded();
            if (datas.size() == 0) //No Result
                callbackListener.onEmptyResult();
            else {
                for (Data data : datas)
                    addItemAndNotify(data);

                callbackListener.onLoadFinished();
            }
        }
    }
}
