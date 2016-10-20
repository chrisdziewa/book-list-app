package com.example.android.booklist;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Chris on 8/23/2016.
 * Stores information about a book
 */
public class Book implements Parcelable {

    private String mBookTitle;
    private ArrayList<String> mAuthors = new ArrayList<String>();
    private String mImageUrl;
    private String mDescription;
    private String mInfoUrl;

    /**
     * @param title       book title
     * @param authors     JSONArray
     * @param imageUrl    thumbnail
     * @param description of the book
     * @param infoUrl     to navigate to page
     */
    public Book(String title, ArrayList<String> authors, String imageUrl, String description, String infoUrl) {
        mBookTitle = title;
        mAuthors = authors;
        mImageUrl = imageUrl;
        mDescription = description;
        mInfoUrl = infoUrl;
    }

    // Parcelable constructor
    public Book(Parcel parcel) {
        mBookTitle = parcel.readString();
        mAuthors = (ArrayList<String>) parcel.readSerializable();
        mImageUrl = parcel.readString();
        mDescription = parcel.readString();
        mInfoUrl = parcel.readString();
    }

    public String getBookTitle() {
        return mBookTitle;
    }

    /**
     * @return a formatted string of the author(s)
     */
    public String getAuthorsString() {
        StringBuilder authorsStringBuilder = new StringBuilder();

        for (int i = 0; i < mAuthors.size(); i++) {
            if (i == mAuthors.size() - 1 && mAuthors.size() > 1) {
                authorsStringBuilder.append(" and ");
            } else if (i != 0) {
                authorsStringBuilder.append(", ");
            }
            // Add the array string object
            authorsStringBuilder.append(mAuthors.get(i));
        }

        return authorsStringBuilder.toString();
    }

    public ArrayList<String> getAuthors() {
        return mAuthors;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getInfoUrl() {
        return mInfoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getBookTitle());
        parcel.writeSerializable(getAuthors());
        parcel.writeString(getImageUrl());
        parcel.writeString(getDescription());
        parcel.writeString(getInfoUrl());
    }

    public static final Parcelable.Creator<Book> CREATOR =
            new Parcelable.Creator<Book>() {

                @Override
                public Book createFromParcel(Parcel parcel) {
                    return new Book(parcel);
                }

                @Override
                public Book[] newArray(int size) {
                    return new Book[size];
                }
            };
}


