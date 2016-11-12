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
    private boolean isBciUser;
    private boolean isCreator;

    public Comment(String author, String content, String dateTime, boolean isBciMember, boolean isCreator) {
        setHolderClass(CommentViewHolder.class);
        setAuthor(author);
        setContent(content);
        setDateTime(dateTime);
        setBciUser(isBciMember);
        setCreator(isCreator);
    }

    public Comment(Parcel in) {
        setHolderClass(CommentViewHolder.class);

        String[] stringData = new String[3];
        in.readStringArray(stringData);

        setAuthor(stringData[0]);
        setContent(stringData[1]);
        setDateTime(stringData[2]);

        boolean[] boolData = new boolean[2];
        in.readBooleanArray(boolData);

        setBciUser(boolData[0]);
        setCreator(boolData[1]);
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

    public boolean isBciUser() {
        return isBciUser;
    }

    public void setBciUser(boolean bciUser) {
        isBciUser = bciUser;
    }

    public boolean isCreator() {
        return isCreator;
    }

    public void setCreator(boolean author) {
        isCreator = author;
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
        parcel.writeBooleanArray(new boolean[]{
                this.isBciUser(),
                this.isCreator()
        });
    }
}
