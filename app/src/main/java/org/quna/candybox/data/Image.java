package org.quna.candybox.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

import java.util.ArrayList;

/**
 * Created by graphene on 2016-11-16.
 */

public class Image extends SugarRecord implements Parcelable {
    public static final String IMAGE = "image_class";

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

    private String imageLink; //Link of a image
    private String creator; //Actual creator of the image. Can be null.
    private String uploader; //Uploader of the image
    private String dateTime; //Date and time the image was uploaded
    private String sourceLink; //Original image source. Can be null.

    private int width;
    private int height;

    private ArrayList<String> favourited;
    private ArrayList<String> tags;

    public Image() {
        imageLink = "";
        creator = "";
        uploader = "";
        dateTime = "";
        sourceLink = "";
        favourited = new ArrayList<String>(1);
        tags = new ArrayList<String>(1);
    }

    public Image(String imageLink, int width, int height, String uploader, String dateTime, ArrayList tags) {
        this();
        setImageLink(imageLink);
        setWidth(width);
        setHeight(height);
        setUploader(uploader);
        setDateTime(dateTime);
        setTags(tags);
    }

    public Image(Parcel in) {
        this();
        String[] stringData = new String[5];
        in.readStringArray(stringData);

        setCreator(stringData[0]);
        setDateTime(stringData[1]);
        setImageLink(stringData[2]);
        setSourceLink(stringData[4]);
        setUploader(stringData[5]);

        setFavourited((ArrayList<String>) in.readSerializable());
        setTags((ArrayList<String>) in.readSerializable());

        int[] intData = new int[2];
        in.readIntArray(intData);
        setDimension(intData);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.getCreator(),
                this.getDateTime(),
                this.getImageLink(),
                this.getSourceLink(),
                this.getUploader()
        });

        dest.writeSerializable(getFavourited());
        dest.writeSerializable(getTags());
        dest.writeIntArray(getDimension());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //Getters and setters
    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public ArrayList<String> getFavourited() {
        return favourited;
    }

    public void setFavourited(ArrayList<String> favourited) {
        this.favourited = favourited;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getSourceLink() {
        return sourceLink;
    }

    public void setSourceLink(String sourceLink) {
        this.sourceLink = sourceLink;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
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

}
