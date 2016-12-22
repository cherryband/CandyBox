package org.quna.candybox.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by graphene on 2016-11-11.
 */

public class PlaceHolder implements Data {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object[] newArray(int size) {
            return new PlaceHolder[size];
        }

        @Override
        public Object createFromParcel(Parcel source) {
            return new PlaceHolder(source);
        }
    };

    private final int holderId;

    public PlaceHolder(int id) {
        holderId = id;
    }

    public PlaceHolder(Parcel source) {
        this(source.readInt());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(getHolderId());
    }

    @Override
    public int getHolderId() {
        return holderId;
    }
}
