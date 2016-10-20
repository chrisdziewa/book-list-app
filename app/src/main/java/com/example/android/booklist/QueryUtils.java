package com.example.android.booklist;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 8/23/2016.
 */
public class QueryUtils {

    /**
     * @param urlString string for api call
     * @return list of book objects
     */
    public static List<Book> getBooks(String urlString, String subjectParam) {
        Log.i("QueryUtils", "Called getBooks");

        String jsonResponse = "";

        URL url = createUrl(urlString, subjectParam);

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("QueryUtils", "IOException: " + e);
        }

        return extractFromJSON(jsonResponse);
    }

    private static List<Book> extractFromJSON(String bookJSON) {
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        List<Book> books = new ArrayList<Book>();

        try {
            JSONObject jsonObject = new JSONObject(bookJSON);

            if (jsonObject.getInt("totalItems") < 1) {
                return null;
            }

            JSONArray bookArray = jsonObject.getJSONArray("items");

            for (int i = 0; i < bookArray.length(); i++) {
                JSONObject bookObject = bookArray.getJSONObject(i);
                JSONObject book = bookObject.getJSONObject("volumeInfo");

                // Define final book information variable
                String title = "";
                ArrayList<String> authors = new ArrayList<String>();
                String imageUrl = "";
                String description = "";
                String infoUrl = "";

                if (book.getString("title") != null) {
                    title = book.getString("title");
                }

                if (book.has("authors")) {
                    JSONArray authorsJSON = book.getJSONArray("authors");
                    authors = convertJSONArrayToList(authorsJSON);
                }

                if (book.has("imageLinks")) {
                    JSONObject imageLinks = book.getJSONObject("imageLinks");

                    if (imageLinks.has("thumbnail")) {
                        imageUrl = imageLinks.getString("thumbnail");
                    }
                }

                if (book.has("description")) {
                    description = book.getString("description");
                }

                if (book.has("infoLink")) {
                    infoUrl = book.getString("infoLink");
                }

                books.add(new Book(title, authors, imageUrl, description, infoUrl));
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing JSON: " + e);
            return null;
        }

        return books;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        // Set up connection
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200) {
                // Read from stream
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("QueryUtils", "Response code was " + statusCode);
            }

        } catch (IOException e) {
            Log.e("QueryUtils", "IOException: " + e);
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

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder resultString = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(streamReader);

            String line = bufferedReader.readLine();

            while (line != null) {
                resultString.append(line);
                line = bufferedReader.readLine();
            }
        }

        return resultString.toString();
    }

    private static URL createUrl(String urlString, String subjectParam) {
        URL url;
        String encodedParam;

        // Handles special characters such as spaces to encode url parameter
        try {
            encodedParam = URLEncoder.encode(subjectParam, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e("QueryUtils", "Error encoding URL param: " + e);
            return null;
        }

        try {
            url = new URL(urlString + encodedParam);
        } catch (MalformedURLException e) {
            Log.e("QueryUtils", "Error creating URL: " + e);
            return null;
        }

        return url;
    }

    /**
     * @param jsonArray a JSON Array
     * @return an Array list containing the elements from the JSON Array
     */
    private static ArrayList<String> convertJSONArrayToList(JSONArray jsonArray) {
        ArrayList<String> finalList = new ArrayList<String>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    finalList.add(jsonArray.get(i).toString());
                } catch (JSONException e) {
                    Log.e("Book", "Problem converting JSON to ArrayList: " + e);
                }
            }
        }

        return finalList;
    }
}
