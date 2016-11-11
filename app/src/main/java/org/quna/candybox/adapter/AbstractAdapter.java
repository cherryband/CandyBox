package org.quna.candybox.adapter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;

import org.quna.candybox.adapter.viewholder.AbstractViewHolder;
import org.quna.candybox.data.Data;
import org.quna.candybox.misc.Listener;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by graphene on 2016-11-06.
 */

public abstract class AbstractAdapter<T extends Data> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final Listener dummy = new Listener() {
        public void invoke() {
            return;
        }
    };

    private static final String ITEM_LIST = "images";
    private Listener onUpdateRequest = dummy;
    private Listener onUpdated = dummy;
    private Listener onError = dummy;
    private Listener onEmpty = dummy;

    private ArrayList<T> mDataset;
    private boolean loading;

    public AbstractAdapter(RecyclerView recycler) {
        mDataset = new ArrayList<T>();
        recycler.setAdapter(this);
    }

    public AbstractAdapter(Bundle savedInstanceState, RecyclerView mRecycler) {
        this(mRecycler);
        for (Parcelable p : savedInstanceState.getParcelableArray(ITEM_LIST)) {
            if (p != null) {
                try {
                    getDataset().add((T) p);
                    this.notifyItemInserted(getDataset().size());
                } catch (ClassCastException e) { //This shouldn't happen.
                    e.printStackTrace();
                }
            }
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
        while (getDataset().contains(null)) {
            int lastIndex = getDataset().lastIndexOf(null);
            getDataset().remove(lastIndex);
            this.notifyItemRemoved(lastIndex);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AbstractViewHolder) {
            AbstractViewHolder viewHolder = (AbstractViewHolder) holder;
            viewHolder.loadWith(get(position));
        }
    }

    protected boolean add(T t) {
        return mDataset.add(t);
    }

    protected T remove(int pos) {
        return mDataset.remove(pos);
    }

    protected T get(int pos) {
        return mDataset.get(pos);
    }

    protected boolean isLoading() {
        return loading;
    }

    protected void setLoading(boolean loading) {
        this.loading = loading;
    }

    protected ArrayList<T> getDataset() {
        return mDataset;
    }

    public abstract class DownloadAsyncTask<F> extends AsyncTask<F, Void, ArrayList<T>> {
        private boolean isError;

        @Override
        protected ArrayList<T> doInBackground(F... params) {
            loading = true;
            ArrayList<T> datas = new ArrayList<T>();
            try {
                datas = doNetworkTask(params);
            } catch (IOException e) {
                e.printStackTrace();
                isError = true;
            }
            return datas;
        }

        protected abstract ArrayList<T> doNetworkTask(F... params) throws IOException;

        @Override
        protected void onPostExecute(ArrayList<T> datas) {
            super.onPostExecute(datas);
            loaded();
            if (isError)
                onError.invoke();
            else if (datas.size() == 0) //No Result
                onEmpty.invoke();
            else {
                for (T data : datas) {
                    if (add(data)) {
                        //This method has to be called from UI thread.
                        AbstractAdapter.this.notifyItemInserted(getItemCount());
                    }
                }
                onUpdated.invoke();
            }
        }
    }
}
