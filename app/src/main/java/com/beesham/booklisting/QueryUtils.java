package com.beesham.booklisting;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Beesham on 9/20/2016.
 */
public class QueryUtils {

    private QueryUtils() {
    }

    public static ArrayList<Book> extractBooks(String booksJSONResponse){

        ArrayList<Book> bookArrayList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(booksJSONResponse);
            JSONArray items = jsonObject.getJSONArray("items");

            for(int i = 0; i < items.length(); i++){

                JSONObject item = items.getJSONObject(i);
                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                String title = volumeInfo.getString("title");

                JSONArray authorsJSONArray = volumeInfo.getJSONArray("authors");
                String []authors = new String[authorsJSONArray.length()];
                for(int j=0; j<authorsJSONArray.length(); j++){
                    authors[j] = authorsJSONArray.getString(j);
                }

                String infoLink = volumeInfo.getString("infoLink");

                bookArrayList.add(new Book(title, authors, infoLink));
            }


        }catch (JSONException e){
            Log.e("QueryUtils", "Error parsing JSON", e);
        }
        return bookArrayList;
    }

}
