package org.quna.candybox;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by qtwye on 2016-10-22.
 */

class Image implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object[] newArray(int size) {
            return new Image[size];
        }

        @Override
        public Object createFromParcel(Parcel source) {
            return new Image(source);
        }
    };
    private String source; //link of a image
    private String alt; //Alt text for the visually impaired
    private String link; //link to a page of the image

    public Image(String source, String alt, String link) {
        setSource(source);
        setLink(link);
        setAlt(alt);
    }

    public Image(Parcel in) {
        String[] data = new String[3];
        in.readStringArray(data);
        setSource(data[0]);
        setAlt(data[1]);
        setLink(data[2]);
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.getSource(),
                this.getAlt(),
                this.getLink()
        });
    }
}
