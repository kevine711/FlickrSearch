package com.kevinersoy.flickrsearch;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kevinersoy.flickrsearch.models.GalleryItem;
import com.kevinersoy.flickrsearch.models.GetPhotosResponse;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String JSON_RESPONSE_MESSAGE = "com.kevinersoy.FlickrSearch.jsonresponsemessage";
    ImagesRecyclerAdapter mImagesRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up RecyclerView
        initializeContent();

        //Listen for changes to the search text
        EditText editText = (EditText)findViewById(R.id.text_search);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                flickrSearch(s.toString());

            }
        });


    }

    /* Issue the network call to search here
     * Call intent service to queue up a query
     * Intent service will pass a bundle back here to process the JSON
    */
    private void flickrSearch(String searchText){
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle reply = msg.getData();
                String json = (String)reply.get(JSON_RESPONSE_MESSAGE);

                Log.d("MainActivityJSON", "JSON: " + json);
                Gson gson = new GsonBuilder().create();
                GetPhotosResponse photosResponse = gson.fromJson(json, GetPhotosResponse.class);
                try {
                    List<GalleryItem> list = photosResponse.getPhotoMeta().getGalleryItems();
                    mImagesRecyclerAdapter.updateList(list);
                } catch (NullPointerException e) {
                    Log.e("MainActivityHandler", "Issue parsing objects - null");
                    e.printStackTrace();
                }
            }
        };

        //When empty search text, we should clear the list
        if(searchText != null && searchText.length() > 0) {
            SearchIntentService.search(this, searchText, new Messenger(handler));
        } else {
            mImagesRecyclerAdapter.updateList(null);
        }
    }

    /*
     *  Set up RecyclerView with layout manager and adapter
     */
    private void initializeContent() {
        //Set up RecyclerView with a layout manager
        final RecyclerView recyclerImages = (RecyclerView) findViewById(R.id.list_images);
        final GridLayoutManager imagesLayoutManager = new GridLayoutManager(this, 3); //3 columns
        recyclerImages.setLayoutManager(imagesLayoutManager);

        //Set field for our recycler adapter
        mImagesRecyclerAdapter = new ImagesRecyclerAdapter(this, null);
        recyclerImages.setAdapter(mImagesRecyclerAdapter);
    }
}
