package org.quna.candybox.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.quna.candybox.data.Data;

/**
 * Created by graphene on 2016-11-11.
 */

public abstract class BaseViewHolder<T extends Data> extends RecyclerView.ViewHolder {
    public static int ID;

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void loadWith(T data); //Load views with provided data.
}
