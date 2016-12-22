package org.quna.candybox.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.quna.candybox.adapter.viewholder.ThumbnailViewHolder;

/**
 * Created by graphene on 2016-12-22.
 */

public class Thumbnail implements SizePredeterminedImage {
    public static final String THUMBNAIL = "thumbnail_class";
    public static final Parcelable.Creator CREATOR = new Creator<Thumbnail>() {
        @Override
        public Thumbnail[] newArray(int size) {
            return new Thumbnail[size];
        }

        @Override
        public Thumbnail createFromParcel(Parcel source) {
            return new Thumbnail(source);
        }
    };
    private String imageLink; //Link of a image
    private String pageLink; //Link of original page
    private String alt; //Alt text of a image
    private int width;
    private int height;

    public Thumbnail(String imageLink, String pageLink, String alt, int width, int height) {
        setImageLink(imageLink);
        setPageLink(pageLink);
        setAlt(alt);
        setWidth(width);
        setHeight(height);
    }

    public Thumbnail(Parcel in) {
        String[] stringData = new String[3];
        in.readStringArray(stringData);

        setImageLink(stringData[0]);
        setPageLink(stringData[1]);
        setAlt(stringData[2]);

        int[] intData = new int[2];
        in.readIntArray(intData);
        setDimension(intData);
    }


    @Override
    public int getHolderId() {
        return ThumbnailViewHolder.ID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{
                getImageLink(),
                getPageLink(),
                getAlt()
        });
        parcel.writeIntArray(getDimension());
    }

    public int[] getDimension() {
        return new int[]{width, height};
    }

    public void setDimension(int... dimension) {
        int width = dimension[0];
        int height = dimension[1];
        setDimension(width, height);
    }

    public void setDimension(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getPageLink() {
        return pageLink;
    }

    public void setPageLink(String pageLink) {
        this.pageLink = pageLink;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }
}
