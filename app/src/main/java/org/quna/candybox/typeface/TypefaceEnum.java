package org.quna.candybox.typeface;

/**
 * Created by graphene on 2016-11-06.
 */

public enum TypefaceEnum {
    BOOK("fira-sans/FiraSans-Book.otf"), REGULAR("fira-sans/FiraSans-Regular.otf");

    private String path;

    TypefaceEnum(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
