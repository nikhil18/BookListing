package com.example.android.booklisting;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public final class Utils {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = Utils.class.getSimpleName();

    public static ArrayList fetchBooksData(String requestUrl) {

        Log.e(LOG_TAG, "fetch book data");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        ArrayList book = extractBooks(jsonResponse);

        // Return the {@link Event}
        return book;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Books JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Books} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Books> extractBooks(String bookJSON) {

        // Create an empty ArrayList that we can start adding books to
        ArrayList<Books> books = new ArrayList<>();

        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject baseJsonResponce = new JSONObject(bookJSON);
            if (!baseJsonResponce.isNull("items")) {
            JSONArray itemsArray = baseJsonResponce.getJSONArray("items");

            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject firstItem = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = firstItem.getJSONObject("volumeInfo");
                String title = volumeInfo.getString("title");
                // Some books don't have an authors node, use try/catch to prevent null pointers
                JSONArray bookAuthors = null;
                try {
                    bookAuthors = volumeInfo.getJSONArray("authors");
                } catch (JSONException ignored) {
                }
                // Convert the authors to a string
                String bookAuthorsString = "";
                // If the author is empty, set it as "Unknown"
                if (bookAuthors == null) {
                    bookAuthorsString = "Unknown";
                } else {
                    // Format the authors as "author1, author2, and author3"
                    int countAuthors = bookAuthors.length();
                    for (int e = 0; e < countAuthors; e++) {
                        String author = bookAuthors.getString(e);
                        if (bookAuthorsString.isEmpty()) {
                            bookAuthorsString = author;
                        } else if (e == countAuthors - 1) {
                            bookAuthorsString = bookAuthorsString + " and " + author;
                        } else {
                            bookAuthorsString = bookAuthorsString + ", " + author;
                        }
                    }
                }
                Books book = new Books(title, bookAuthorsString);
                books.add(book);
            }
            } else {
                books = null;
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("Utils", "Problem parsing the books JSON results", e);
        }

        // Return the list of books
        return books;
    }

}
