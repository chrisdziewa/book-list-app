package com.example.android.booklist;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    // base Google books api url
    private static final String mGoogleBooksApiUrl = "https://www.googleapis.com/books/v1/volumes?q=subject:";

    // Books array adapter
    private BookAdapter mAdapter;

    // the search term
    private String mSearchTerm;

    // For when there is no connection or no results
    private TextView mEmptyStateTextView;

    // Progress bar
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        // Search function
        Button searchButton = (Button) findViewById(R.id.search_button);

        // Text field
        final EditText textInput = (EditText) findViewById(R.id.search_field);

        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        // Restore state on orientation change/activity recreation
        if ((savedInstanceState != null)) {
            // Restore book list
            ArrayList<Book> books = savedInstanceState.getParcelableArrayList("books");
            mAdapter.addAll(books);

            mSearchTerm = savedInstanceState.getString("searchTerm");

            mAdapter.notifyDataSetChanged();
        }

        ListView bookList = (ListView) findViewById(R.id.book_list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_list);
        bookList.setEmptyView(mEmptyStateTextView);

        bookList.setAdapter(mAdapter);

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Book currentBook = mAdapter.getItem(position);
                Uri bookUrl = Uri.parse(currentBook.getInfoUrl());

                Intent urlIntent = new Intent(Intent.ACTION_VIEW, bookUrl);

                if (urlIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(urlIntent);
                }

            }
        });

        final LoaderManager bookLoaderManager = getLoaderManager();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isConnected = checkConnection();

                if (!isConnected) {
                    mAdapter.clear();
                    mEmptyStateTextView.setVisibility(View.VISIBLE);
                    mEmptyStateTextView.setText("No internet connection");
                    mSearchTerm = null;
                    return;
                } else {
                    mEmptyStateTextView.setText("");
                }

                String searchString = textInput.getText().toString();

                if (searchString.length() > 0 && (mSearchTerm == null ||
                        !mSearchTerm.toLowerCase().equals(searchString.toLowerCase()))) {
                    mAdapter.clear();

                    mSearchTerm = searchString;

                    bookLoaderManager.restartLoader(0, null, BookActivity.this);
                } else {
                    // Nothing was changed - search results are the same
                    if (searchString.length() != 0) {
                        Toast.makeText(BookActivity.this,
                                "Already displaying results for " + searchString,
                                Toast.LENGTH_SHORT).show();
                    }
                }

                InputUtils.hideSoftKeyboard(BookActivity.this);
            }
        });

        // Check initial connection
        boolean isConnected = checkConnection();

        if (!isConnected) {
            mEmptyStateTextView.setText(R.string.no_connection);
            return;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedState) {
        Log.v("BookActivity", "Called onSaveInstanceState");

        if (mAdapter.getBookList() != null) {
            savedState.putParcelableArrayList("books", mAdapter.getBookList());
            savedState.putString("searchTerm", mSearchTerm);
        }

        super.onSaveInstanceState(savedState);
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        mProgressBar.setVisibility(View.VISIBLE);
        return new BookLoader(BookActivity.this, mGoogleBooksApiUrl, mSearchTerm);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        mProgressBar.setVisibility(View.GONE);
        mAdapter.clear();

        if (books == null) {
            mEmptyStateTextView.setText(R.string.no_results);
            return;
        }

        mAdapter.addAll(books);
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
    }

    private boolean checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) BookActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
