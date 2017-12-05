package com.songa.mandoline.core.detail.album;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.songa.mandoline.audio.entity.Album;
import com.songa.mandoline.audio.entity.Track;
import com.songa.mandoline.ui.viewholder.TrackViewholder;
import com.songa.mandoline.ui.listener.ItemEventListener;
import com.songa.mandoline.ui.listener.ViewholderListener;

import java.util.ArrayList;
import java.util.List;

public class AlbumDetailAdapter extends RecyclerView.Adapter implements ViewholderListener<TrackViewholder>
{
    private final @NonNull Album album;
    private final @NonNull List<Track> tracks = new ArrayList<>();

    private @Nullable ItemEventListener<Track> listener = null;

    public AlbumDetailAdapter(@NonNull Album album)
    {
        this.album = album;
    }

    @Override
    public int getItemViewType(int position)
    {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType) {
            //case 0 : return AlbumHeaderViewHolder.inflate(parent);
            default: return TrackViewholder.inflate(parent).clickListener(this);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        /*
        if (position==0) {
            AlbumHeaderViewHolder.bind(holder, album);

        } else if (position<tracksFromAlbum.size()) {
            TrackViewholder.bind(holder, tracksFromAlbum.get(position-1));
        }
        */

        if (position<tracks.size()) {
            TrackViewholder.bind(holder, tracks.get(position));
        }
    }

    @Override
    public int getItemCount()
    {
        return tracks.size();
    }

    @Override
    public void onClick(@NonNull TrackViewholder holder, @NonNull View view)
    {
        if (listener!=null && holder.getTrack()!=null) {
            listener.onClick(holder.getTrack(), view);
        }
    }

    @UiThread
    public void addTracks(@NonNull List<Track> items, boolean clearBefore)
    {
        if (clearBefore) { this.tracks.clear(); }
        this.tracks.addAll(items);
        notifyDataSetChanged();
    }

    public void setListener(@Nullable ItemEventListener<Track> listener)
    {
        this.listener = listener;
    }
}
