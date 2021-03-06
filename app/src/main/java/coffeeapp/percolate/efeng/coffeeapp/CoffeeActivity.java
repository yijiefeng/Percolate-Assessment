package coffeeapp.percolate.efeng.coffeeapp;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class CoffeeActivity extends Activity {

    private final static String apiEndpoint = "https://coffeeapi.percolate.com/api/coffee/?api_key=";
    private final static String apiKey = "WuVbkuUsCXHPx3hsQzus4SE";

    private ListView lv;
    protected ArrayList<Coffee> coffeelist;
    private ListViewAdapter adapter;
    private ConnectivityManager connMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //format the actionbar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(actionBar.DISPLAY_SHOW_CUSTOM);
        View cView = getLayoutInflater().inflate(R.layout.actionbar, null);
        actionBar.setCustomView(cView);

        setContentView(R.layout.coffeelist_layout);

        //connectivity manager
        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //API GET Request
        String url = apiEndpoint + apiKey;
        new apiCallTask().execute(url);

        //set view objects and adapter
        coffeelist = new ArrayList<Coffee>();
        lv = (ListView) findViewById(R.id.listview);

        adapter = new ListViewAdapter(getApplicationContext(), R.layout.row, coffeelist);
        lv.setAdapter(adapter);


    }

    // AsyncTask for making a GET request
    protected class apiCallTask extends AsyncTask<String, Void, JSONArray> {

        protected JSONArray doInBackground(String... urls){

            //check that there is a working network connection
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                try{
                    // create a URL connection
                    URL request = new URL(urls[0]);
                    HttpURLConnection urlConnection = JsonUtil.createConnection(request);

                    // create JSONArray object from content
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    return new JSONArray(JsonUtil.getResponseText(in));

                }catch(Exception e){
                    e.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }
        }

        protected void onPostExecute(JSONArray result) {

            //check that the JSONArray is not null
            if(result != null){

                //store all values from JSONArray into coffeelist (an ArrayList of Coffee Objects)
                coffeelist = JsonUtil.JsonToArrayList(result, coffeelist);

                //notify the ListViewAdapter that the underlying data has changed
                adapter.notifyDataSetChanged();
            }else{
                Toast.makeText(getBaseContext(),
                    "Failed to retrieve data, check your network connection",
                    Toast.LENGTH_SHORT).show();
            }


        }
    }

}
