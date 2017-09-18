package com.example.android.booklisting;

/**
 * Created by nikhi on 17-09-2017.
 */

public class Books {

    private String mBooksName;

    private String mAuthorName;

    public Books(String booksname, String authorname) {
        this.mBooksName = booksname;
        this.mAuthorName = authorname;
    }

    public String getBooksName() {
        return mBooksName;
    }

    public String getAuthorName() {
        return mAuthorName;
    }
}
