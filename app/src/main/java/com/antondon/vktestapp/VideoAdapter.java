package com.antondon.vktestapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VkVideoArray;

import java.util.Locale;

/**
 * Created by anton on 11/23/17.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private VkVideoArray videos;
    private Context context;
    private RecyclerViewClickListener listener;

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageViewPhoto;
        TextView textViewTitle, textViewTime;
        RecyclerViewClickListener listener;

        ViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);

            imageViewPhoto = itemView.findViewById(R.id.image_view_photo);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewTime = itemView.findViewById(R.id.text_view_duration);

            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onClick(v, getAdapterPosition());
            }
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

    VideoAdapter(Context context, VkVideoArray videos, RecyclerViewClickListener listener) {
        this.context = context;
        this.videos = videos;
        this.listener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.video_feed_view, parent, false);

        return new ViewHolder(contactView, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VKApiVideo video = videos.get(position);

        holder.textViewTitle.setText(video.title);
        String formattedDuration = String.format(Locale.ENGLISH,"%02d:%02d",
        (video.duration / 60) % 60, video.duration % 60);
        holder.textViewTime.setText(formattedDuration);
        String photoUrl = "";
        if (!video.photo_640.isEmpty()) {
            photoUrl = video.photo_640;
        } else if (!video.photo_320.isEmpty()) {
            photoUrl = video.photo_320;
        } else if (!video.photo_130.isEmpty()) {
            photoUrl = video.photo_130;
        }
        if (!photoUrl.isEmpty()) {
            Picasso.with(context).load(photoUrl).fit().into(holder.imageViewPhoto);
        }
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    @Override
    public long getItemId(int position) {
        return videos.get(position).id;
    }


}
