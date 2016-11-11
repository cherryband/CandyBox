package org.quna.candybox.data;

import android.os.Parcelable;

/**
 * Created by graphene on 2016-11-11.
 */

public abstract class Data implements Parcelable {
    private Class holderClass;

    public Class getHolderClass() {
        return holderClass;
    }

    public void setHolderClass(Class holderClass) {
        this.holderClass = holderClass;
    }
}
