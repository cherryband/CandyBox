package org.quna.candybox.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.quna.candybox.adapter.viewholder.CommentViewHolder;

/**
 * Created by graphene on 2016-11-06.
 */

public class Comment extends Data implements Parcelable {
    public static final String COMMENT = "comment_class";
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object[] newArray(int size) {
            return new Image[size];
        }

        @Override
        public Object createFromParcel(Parcel source) {
            return new Comment(source);
        }
    };
    private String author;
    private String content;
    private String dateTime;

    public Comment(String author, String content, String dateTime) {
        setHolderClass(CommentViewHolder.class);
        setAuthor(author);
        setContent(content);
        setDateTime(dateTime);
    }

    public Comment(Parcel in) {
        setHolderClass(CommentViewHolder.class);
        String[] data = new String[3];
        in.readStringArray(data);
        setAuthor(data[0]);
        setContent(data[1]);
        setDateTime(data[2]);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeStringArray(new String[]{
                this.getAuthor(),
                this.getContent(),
                this.getDateTime()
        });
    }
}
