package org.quna.candybox.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.quna.candybox.adapter.viewholder.CommentViewHolder;

import java.util.ArrayList;

/**
 * Created by graphene on 2016-11-06.
 */

public class Comment implements Data {
    public static final String COMMENT = "comment_class";
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object[] newArray(int size) {
            return new Comment[size];
        }

        @Override
        public Object createFromParcel(Parcel source) {
            return new Comment(source);
        }
    };
    private static final String BCI_MEMBER = "bci";
    private static final String UPLOADER = "uploader";
    private static final String ADMIN = "admin";
    private String author;
    private String content;
    private String dateTime;
    private ArrayList<String> userAttributes;

    public Comment(String author, String content, String dateTime,
                   boolean isBciMember, boolean isCreator, boolean isAdmin) {
        userAttributes = new ArrayList<String>();
        setAuthor(author);
        setContent(content);
        setDateTime(dateTime);
        setBciUser(isBciMember);
        setUploader(isCreator);
        setAdmin(isAdmin);
    }

    public Comment(Parcel in) {
        String[] stringData = new String[3];
        in.readStringArray(stringData);

        setAuthor(stringData[0]);
        setContent(stringData[1]);
        setDateTime(stringData[2]);

        userAttributes = (ArrayList<String>) in.readSerializable();
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
        return userAttributes.contains(BCI_MEMBER);
    }

    public void setBciUser(boolean bciMember) {
        setAttributes(bciMember, BCI_MEMBER);
    }

    public boolean isUploader() {
        return userAttributes.contains(UPLOADER);
    }

    public void setUploader(boolean uploader) {
        setAttributes(uploader, UPLOADER);
    }

    public boolean isAdmin() {
        return userAttributes.contains(ADMIN);
    }

    public void setAdmin(boolean admin) {
        setAttributes(admin, ADMIN);
    }

    private void setAttributes(boolean bool, String param) {
        if (bool)
            userAttributes.add(param);
        else
            userAttributes.remove(param);
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
        parcel.writeSerializable(userAttributes);
    }

    @Override
    public int getHolderId() {
        return CommentViewHolder.ID;
    }
}
