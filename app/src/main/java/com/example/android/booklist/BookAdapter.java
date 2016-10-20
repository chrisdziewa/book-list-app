package com.example.android.booklist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 8/23/2016.
 */
public class BookAdapter extends ArrayAdapter<Book> {
    // String for loading image url
    private String mImageUrl;
    // String for name of image src
    private String mImageSrc;
    // current list item
    private View mListItem;

    // All books for saving instance
    private List<Book> mBookList;

    /**
     * @param context context for inflating the layout
     * @param books
     */
    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);

        // Set global book list variable
        mBookList = books;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listViewItem = convertView;
        mListItem = listViewItem;

        if (listViewItem == null) {
            listViewItem = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item, parent, false);
        }

        Book currentBook = getItem(position);

        // Set the titleSnow
        TextView titleView = (TextView) listViewItem.findViewById(R.id.title);
        titleView.setText(currentBook.getBookTitle());

        // Set the author(s)
        TextView authorsView = (TextView) listViewItem.findViewById(R.id.authors);
        authorsView.setText(getContext().getString(R.string.by_author) + currentBook.getAuthorsString());

        // Set the image resource
        ImageView imageView = (ImageView) listViewItem.findViewById(R.id.photo);

        // Set the description
        TextView descriptionView = (TextView) listViewItem.findViewById(R.id.description_text_view);
        descriptionView.setText(currentBook.getDescription());

        // Load image
        if (currentBook.getImageUrl() != "") {
            Picasso.with(getContext()).load(currentBook.getImageUrl()).resize(100, 150).into(imageView);
        }


        return listViewItem;
    }

    /**
     * Used for saving the instance
     *
     * @return list of books in the adapter
     */
    public ArrayList<Book> getBookList() {
        return (ArrayList<Book>) mBookList;
    }
}
