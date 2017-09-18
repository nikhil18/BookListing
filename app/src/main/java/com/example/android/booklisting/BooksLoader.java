package com.example.android.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

public class BooksLoader extends AsyncTaskLoader<ArrayList<Books>> {

    private String mUrl;

    public BooksLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Books> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        ArrayList<Books> books = Utils.fetchBooksData(mUrl);
        return books;
    }
}

