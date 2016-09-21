package com.beesham.booklisting;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String BOOK_REQUEST_AUTHORITY = "https://www.googleapis.com/books/v1/volumes?q=";
    private String request_url;

    private ArrayList<Book> mBookList;
    private EditText searchEdittext;
    private Button searchButton;
    private ListView bookListview;
    private BookListAdapter bookListAdapter;
    private TextView defaultMessage;

    private String jsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        defaultMessage = (TextView) findViewById(R.id.defaultMessage_textview);

        mBookList = new ArrayList<>();

        searchEdittext = (EditText) findViewById(R.id.search_edittext);
        searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request_url = new StringBuilder()
                        .append(BOOK_REQUEST_AUTHORITY)
                        .append(searchEdittext.getText().toString().trim().replace(" ", "+"))
                        .toString();
                if(checkForInternet()) {
                    BookListingAsyncTask bookListingAsyncTask = new BookListingAsyncTask();
                    bookListingAsyncTask.execute();
                }else{
                    Toast.makeText(MainActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        bookListview = (ListView) findViewById(R.id.list);
        bookListAdapter = new BookListAdapter(this, mBookList);
        bookListview.setAdapter(bookListAdapter);

        bookListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mBookList.get(i).getmInfoLink()));
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivity(intent);
                }
            }
        });
    }

    private void updateUi(ArrayList bookList){

        //Clear the list of old data if any
        mBookList.clear();

        if(!bookList.isEmpty()) {
            //Add the new data to list
            mBookList.addAll(bookList);

            //Notify adapter of the new data
            bookListAdapter.notifyDataSetChanged();

            //Hide the no data default message
            defaultMessage.setVisibility(View.INVISIBLE);
        }else{
            defaultMessage.setVisibility(View.VISIBLE);

            //Notify adapter of data changed
            bookListAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("jsonResponse", jsonResponse);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState.containsKey("jsonResponse")) {
            jsonResponse = savedInstanceState.getString("jsonResponse");
            updateUi(QueryUtils.extractBooks(jsonResponse));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    /*
     * Checks if device has internet connection
     */
    private boolean checkForInternet(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private class BookListingAsyncTask extends AsyncTask<URL, Void, ArrayList>{

        @Override
        protected ArrayList<Book> doInBackground(URL... urls) {
            URL url = createUrl(request_url);

            jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error in http request: ", e);
                return null;
            }

            ArrayList<Book> bookArrayList = new ArrayList<>(QueryUtils.extractBooks(jsonResponse));

            return bookArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList bookList) {
            if(bookList == null){
                return;
            }
            updateUi(bookList);
        }

        private URL createUrl(String urlString){
            URL url = null;

            try{
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error creating URL: ", e);
                return null;
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try{
                if(url != null){
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setConnectTimeout(15000);
                    urlConnection.connect();

                    if(urlConnection.getResponseCode() == 200){
                        inputStream = urlConnection.getInputStream();
                        jsonResponse = readFromStream(inputStream);
                    }else{
                        Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                    }
                }
            }catch (IOException e){
                Log.e(LOG_TAG, "An I/O Exception occurred", e);
            }finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if(inputStream != null){
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException{
            StringBuilder output = new StringBuilder();
            if(inputStream != null){
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);

                String line = reader.readLine();

                while (line != null){
                    output.append(line);
                    line = reader.readLine();
                }

            }
            return output.toString();
        }

    }

}
