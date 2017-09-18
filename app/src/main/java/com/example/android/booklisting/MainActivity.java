package com.example.android.booklisting;


import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Books>> {

    ListView listView;
    ProgressBar mProgressBar;
    BooksAdapter mAdapter;
    EditText queryEditText;
    TextView emptyTextView;
    private static final int BOOK_LOADER_ID = 1;

    public static final String LOG_TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list_items);

        mAdapter = new BooksAdapter(this, new ArrayList<Books>());

        listView.setAdapter(mAdapter);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        queryEditText = (EditText) findViewById(R.id.edit_text);

        emptyTextView = (TextView) findViewById(R.id.no_book_or_internet_found);

        listView.setEmptyView(emptyTextView);

        // Setup loader
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(BOOK_LOADER_ID, null, this);


        Button search = (Button) findViewById(R.id.seach_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                mAdapter.clear();
                if (queryEditText.getText().toString().trim().matches("")) { //if search box is empty and button is clicked
                    emptyTextView.setVisibility(View.VISIBLE);
                    emptyTextView.setText(R.string.please_enter);
                } else {
                    if (networkInfo != null && networkInfo.isConnected()) {                     //if search box is NOT empty and network is connected
                        getLoaderManager().restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
                    } else {                                        ////if search box is NOT empty and network is disconnected
                        emptyTextView.setVisibility(View.VISIBLE);
                        emptyTextView.setText(R.string.no_internet_found);
                    }
                }
            }
        });
    }

    @Override
    public Loader<ArrayList<Books>> onCreateLoader(int id, Bundle args) {

        String query = queryEditText.getText().toString();
        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error url encoding: ", e);
            query = "";
        }
        if (queryEditText.getText().toString().trim().matches("")) {
            emptyTextView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        } else {
            emptyTextView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        String url = "https://www.googleapis.com/books/v1/volumes?q=" + query + "&maxResults=10";
        return new BooksLoader(MainActivity.this, url);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Books>> loader, ArrayList<Books> books) {
        mAdapter.clear();

        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
            emptyTextView.setVisibility(View.GONE);
            listView.setAdapter(mAdapter);
            listView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        } else {
            if (queryEditText.getText().toString().trim().matches("")) {
                emptyTextView.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
            } else {
                emptyTextView.setText(getString(R.string.nothing_found));
                emptyTextView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.GONE);

            }
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Books>> loader) {
        mAdapter.clear();
    }

}

