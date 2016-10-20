package com.example.android.booklist;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Chris on 8/23/2016.
 */
public class BookLoader extends AsyncTaskLoader<List<Book>> {

    // url for api request
    private String mApiUrl;
    private String mSearchTerm;

    public BookLoader(Context context, String apiURL, String searchTerm) {
        super(context);

        mApiUrl = apiURL;
        mSearchTerm = searchTerm;
    }

    @Override
    public List<Book> loadInBackground() {
        if (mSearchTerm == null) {
            return null;
        }

        List<Book> books = QueryUtils.getBooks(mApiUrl, mSearchTerm);

        return books;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
