package com.beesham.booklisting;

/**
 * Created by Beesham on 9/20/2016.
 */
public class Book {

    private String mTitle;
    private String[] mAuthors;
    private String mInfoLink;

    public Book(String mTitle, String[] mAuthors, String mInfoLink) {
        this.mTitle = mTitle;
        this.mAuthors = mAuthors;
        this.mInfoLink = mInfoLink;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String[] getmAuthors() {
        return mAuthors;
    }

    public String getmInfoLink() {
        return mInfoLink;
    }


}
