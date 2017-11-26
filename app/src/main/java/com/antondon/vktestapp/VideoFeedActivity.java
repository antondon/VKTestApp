package com.antondon.vktestapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VkVideoArray;

import org.json.JSONArray;
import org.json.JSONException;

public class VideoFeedActivity extends AppCompatActivity {

    private static final String TAG = "VideoFeedActivity";
    private static final String RECYCLER_VIEW_STATE = "recycler_view_state";
    private static final String VIDEOS = "adapter_items";
    private String newsFeedStartFrom = "";
    private VkVideoArray videos = new VkVideoArray();
    private VideoAdapter adapter;
    private RecyclerView recyclerViewFeed;
    private Parcelable restoredState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_feed);

        //load videos only when activity created first time (not recreated)
        if (savedInstanceState == null) {
            requestVideos();
        } else {
            if (savedInstanceState.containsKey(VIDEOS)) {
                videos = savedInstanceState.getParcelable(VIDEOS);
            }
            if (savedInstanceState.containsKey(RECYCLER_VIEW_STATE)) {
                restoredState = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE);
            }
        }

        recyclerViewFeed = findViewById(R.id.recycler_view_video_feed);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewFeed.setLayoutManager(layoutManager);

        adapter = new VideoAdapter(this, videos, new VideoAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                startVideoPlayer(videos.get(position));
            }
        });
        adapter.setHasStableIds(true);
        recyclerViewFeed.setAdapter(adapter);

        recyclerViewFeed.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                Log.d(TAG, "onLoadMore: page " + page + ", totalItemsCount: " + totalItemsCount);
                requestVideos();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (restoredState != null) {
            recyclerViewFeed.getLayoutManager().onRestoreInstanceState(restoredState);
        }
    }

    void startVideoPlayer(VKApiVideo video) {
        Intent intent = new Intent();
        intent.setClass(this, VideoPlayerActivity.class);
        intent.putExtra("video", video);
        startActivity(intent);
    }

    void requestVideos() {
        VKRequest feedVideoRequest = getFeedVideoRequest();
        feedVideoRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                try {
                    JSONArray responseJsonArray = response.json.getJSONArray("response");
                    JSONArray videosJsonArray = responseJsonArray.getJSONObject(0).getJSONArray("items");
                    newsFeedStartFrom = responseJsonArray.getString(1);
                    int previousNum = videos.size();
                    for (int i = 0; i < videosJsonArray.length(); i++) {
                        videos.add(new VKApiVideo(videosJsonArray.getJSONObject(i)));
                    }
                    if (videos.size() != previousNum) {
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                // TODO: 11/26/17 show message about unavailability
                Log.d(TAG, "onError: " + error.toString());
            }
        });
    }

    VKRequest getFeedVideoRequest(){
        String newsFeedCode = newsFeedStartFrom.isEmpty() ?
                "var newsfeed = API.newsfeed.get({\"filters\" : \"post\"});\n" :
                "var newsfeed = API.newsfeed.get({\"filters\" : \"post\", \"start_from\" : \"" + newsFeedStartFrom + "\"});\n";
        String code =
                newsFeedCode +
                "var postAttachments = newsfeed.items@.attachments;\n" +
                "var videoIds;\n" +
                "var i = 0, j = 0;\n" +
                "while (i < postAttachments.length){\n" +
                    "while (j < postAttachments[i].length){\n" +
                        "if (postAttachments[i][j].type == \"video\"){\n" +
                            "videoIds = videoIds + postAttachments[i][j].video.owner_id + \"_\" + postAttachments[i][j].video.id;\n" +
                            "if (j < postAttachments[i].length - 1 || i < postAttachments.length - 1) {\n" +
                                "videoIds = videoIds + \",\";\n" +
                            "}\n" +
                        "}\n" +
                        "j = j + 1;\n" +
                    "}\n" +
                    "j = 0;\n" +
                    "i = i + 1;\n" +
                "}\n" +
                "var videos = API.video.get({\"videos\" : videoIds});\n" + // + "\"offset\" : " + String.valueOf(offset) + "});" +
                "return [videos, newsfeed.next_from];";
        return new VKRequest("execute", VKParameters.from("code", code));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (recyclerViewFeed != null) {
            Parcelable state = recyclerViewFeed.getLayoutManager().onSaveInstanceState();
            outState.putParcelable(RECYCLER_VIEW_STATE, state);
        }
        if (videos != null) {
            outState.putParcelable(VIDEOS, videos);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                if (VKSdk.isLoggedIn()) {
                    VKSdk.logout();
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
