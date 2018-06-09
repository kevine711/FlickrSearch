package com.kevinersoy.flickrsearch;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kevinersoy.flickrsearch.models.GalleryItem;
import com.kevinersoy.flickrsearch.models.GetPhotosResponse;
import com.kevinersoy.flickrsearch.models.PhotoMeta;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * Queue up searches as text in the search box changes.  Only process the latest query.
 */
public class SearchIntentService extends IntentService {


    final String method = "flickr.photos.search";
    final String api_key= "<FLICKR API KEY HERE>";
    final String format= "json";
    final String nojsoncallback = "1";
    final String extras= "url_s";

    //maintain request count and only process latest request
    private int requestCount = 0;

    private static final String ACTION_FLICKR_SEARCH = "com.kevinersoy.flickrsearch.action.SEARCH";

    private static final String EXTRA_SEARCH_TEXT = "com.kevinersoy.flickrsearch.extra.SEARCH_TEXT";
    private static final String EXTRA_MESSENGER = "com.kevinersoy.flickrsearch.extra.MESSENGER";

    public SearchIntentService() {
        super("SearchIntentService");
    }

    /**
     * Starts this service to perform action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void search(Context context, String searchText, Messenger messenger) {
        Intent intent = new Intent(context, SearchIntentService.class);
        intent.setAction(ACTION_FLICKR_SEARCH);
        intent.putExtra(EXTRA_SEARCH_TEXT, searchText);
        intent.putExtra(EXTRA_MESSENGER, messenger);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        requestCount++;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Decrement request count, if more requests pending, skip this request
        requestCount--;
        if(requestCount == 0) {
            if (intent != null) {
                final String action = intent.getAction();
                if (ACTION_FLICKR_SEARCH.equals(action)) {
                    final String searchText = intent.getStringExtra(EXTRA_SEARCH_TEXT);
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        final Messenger messenger = (Messenger) bundle.get(EXTRA_MESSENGER);
                        try {
                            handleActionSearch(searchText, messenger);
                        } catch (IOException e) {
                            Log.e("FlickrSearchService", "IO Exception" + e);
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * Handle action Search in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSearch(String text, Messenger messenger) throws IOException{
        if(text != null && text.length() > 0){
            //Build Uri for OkHTTP Request
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("api.flickr.com")
                    .appendPath("services")
                    .appendPath("rest")
                    .appendQueryParameter("method", method)
                    .appendQueryParameter("format", format)
                    .appendQueryParameter("nojsoncallback", nojsoncallback)
                    .appendQueryParameter("api_key", api_key)
                    .appendQueryParameter("extras", extras)
                    .appendQueryParameter("text", text);

            //Build OkHTTP Request
            Request request = new Request.Builder()
                    .url(builder.build().toString())
                    .build();
            OkHttpClient client = new OkHttpClient();
            Log.d("flickrlog", "REQUEST HERE: " + request.toString());
            Response response = client.newCall(request).execute();


            String json = response.body().string();

            Log.d("flickrlog", "JSON HERE: " + json);

            //Pack message and pass back to MainActivity to update RecyclerView
            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString(MainActivity.JSON_RESPONSE_MESSAGE, json);
            msg.setData(bundle);
            try{
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }


    }

}
