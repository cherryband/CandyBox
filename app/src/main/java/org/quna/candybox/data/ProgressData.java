package org.quna.candybox.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.quna.candybox.adapter.viewholder.ProgressViewHolder;

/**
 * Created by graphene on 2016-11-11.
 */

public class ProgressData extends Data {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object[] newArray(int size) {
            return new ProgressData[size];
        }

        @Override
        public Object createFromParcel(Parcel source) {
            return new ProgressData(source);
        }
    };

    public ProgressData() {
        setHolderClass(ProgressViewHolder.class);
    }

    public ProgressData(Parcel source) {
        super();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(null);
    }
}
